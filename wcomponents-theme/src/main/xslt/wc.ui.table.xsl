<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>
	<!--
		WTable (and WDataTable)
	
		This is long but reasonably straight-forward generation of HTML tables.
	
		There are two modes of table which differ in how nested rows (ui:subTrs)
		are treated. Type "table" (assumed if attribute not present) makes all rows sit
		directly under each other. Type "hierarchic" indents child rows. This causes a
		few issues since the rows are not actual children but siblings.
	
		The HTML TABLE element is actually wrapped in a DIV. This is to provide
		somewhere to attach messages as a WTable can be in an error state (yes, really).


		This is the base transform for WTable. The component root HTML element is a DIV.
		This allows us to add error messaging to the component.

		Common XSLT parameters

		Individual element transforms may require to reference the ancestor table. To
		facilitate this without doing an ancestor:: lookup for each cell we pass certain
		information about the table down through all descendant element transforms.

		maxIndent
		If the table is of type HEIRARCHIC this paramter indicates the maximum depth of
		the table content preceding the first content column. THis is used to determine
		the colspan of each row's first cell.

		addCols
		This is the number of columns in the table in addition to the content columns.
		This is an integor from 0 to 1 and represents the sum of the existance of row
		selection and row expansion columns.

		NOTE: there is a current bug in Chrome which is very interesting. If the transform is done in
		javascript xsl:number value="count(nodeList)" returns nothing but xsl:value-of select="count(nodeList)"
		returns the expected value. This needs further investigation when we have time. So where we are counting
		we use value-of and where we have simple numbers we use number.
	-->
	<xsl:template match="ui:table">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="isError" select="key('errorKey',$id)"/>

		<xsl:variable name="disabled">
			<xsl:if test="@disabled">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		
		<xsl:element name="div">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<!--
				Error state

				Now it is pretty plain that a table cannot be in an error mode. The table is
				not, after all, intrinsically interactive. The error indicator is used to
				provide visual indication that there is an error somewhere in the table. As
				such it is pretty appalling!
			-->
			<xsl:attribute name="class">
				<xsl:text>table</xsl:text>
				<xsl:if test="$isError">
					<xsl:text> wc_error</xsl:text>
				</xsl:if>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
				<xsl:call-template name="WTableAdditionalContainerClass"/>
			</xsl:attribute>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:if test="ui:pagination[@mode='dynamic' or @mode='client'] or ui:rowExpansion[@mode='lazy' or @mode='dynamic'] or ui:sort[@mode='dynamic'] or key('targetKey',$id) or parent::ui:ajaxTarget[@action='replace']">
				<xsl:call-template name="setARIALive"/>
			</xsl:if>
			<xsl:apply-templates select="ui:margin"/>
			<!--
				Disabled state

				The disabled state is not strictly required on the table wrapper since we do
				not do ancestor-or-self lookups in determining disabled controls. It is used to
				disable table functionality: actions, rowExpansion, sorting and rowSelection.
			-->
			<xsl:call-template name="disabledElement"/>
			<!--
				This is a hook to the on load filters applied to a table. A filter applied to
				a table will show (on the client) only those rows with a matching filter
				property.
			-->
			<xsl:if test="@activeFilters">
				<xsl:attribute name="${wc.ui.table.rowFilter.attribute.tableFilter}">
					<xsl:value-of select="@activeFilters"/>
				</xsl:attribute>
			</xsl:if>
			
			<xsl:variable name="isHierarchic">
				<xsl:choose>
					<xsl:when test="@type='hierarchic'">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="rowExpansion">
				<xsl:choose>
					<xsl:when test="ui:rowExpansion">
						<xsl:value-of select="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="hierarchicWithExpansion">
				<xsl:choose>
					<xsl:when test="($isHierarchic + $rowExpansion = 2)  and .//ui:subTr[ancestor::ui:table[1]/@id=$id]">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			
			<!--
					Holds the maximum number of indentation columns required for the table,
					not just this row. This is a heavy calculation which came up as a
					major problem during stress-testing tree tables.
				-->
			<xsl:variable name="maxIndent">
				<xsl:choose>
					<xsl:when test="$hierarchicWithExpansion=1">
						<xsl:call-template name="calculateMaxDepth">
							<xsl:with-param name="thisTable" select="."/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="rowSelection">
				<xsl:choose>
					<xsl:when test="ui:rowSelection">
						<xsl:value-of select="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="staticCols">
				<xsl:choose>
					<xsl:when test="$maxIndent=0">
						<!-- when mode is hierarchic the row expansion column(s) is(are) part of the maxIndent if a page has expandable rows -->
						<xsl:value-of select="$rowSelection + $rowExpansion"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$rowSelection"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<!--<xsl:variable name="colGroupStaticColspan" select="$staticCols - 1"/>-->
			
			<xsl:variable name="firstDataColSorted">
				<xsl:if test="ui:sort[@col = '0']">
					<xsl:number value="1"/>
				</xsl:if>
			</xsl:variable>
			
			<!-- The table element is then the basic functional component.-->
			<xsl:element name="table">
				<xsl:variable name="class">
					<xsl:if test="ui:thead/ui:th[@width]">
						<xsl:text>wc_table_fix</xsl:text>
					</xsl:if>
					<xsl:if test="$isHierarchic=1">
						<xsl:text> hierarchic</xsl:text>
						<xsl:if test="$firstDataColSorted=1 and $rowSelection=0 and $hierarchicWithExpansion=1">
							<xsl:text> wc_table_sort_nxt</xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:variable>
				<xsl:if test="$class != ''">
					<xsl:attribute name="class">
						<xsl:value-of select="normalize-space($class)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:pagination">
					<xsl:attribute name="data-wc-rpp">
						<xsl:choose>
							<xsl:when test="ui:pagination/@rowsPerPage">
								<xsl:value-of select="ui:pagination/@rowsPerPage"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="ui:pagination/@rows"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@caption">
					<xsl:element name="caption">
						<xsl:value-of select="@caption"/>
					</xsl:element>
				</xsl:if>

				<xsl:element name="colgroup">
					<xsl:if test="@separators='both' or @separators='vertical'">
						<xsl:attribute name="class">
							<xsl:text>wc_table_colsep</xsl:text>
						</xsl:attribute>
					</xsl:if>
					
					<xsl:if test="$rowSelection=1">
						<xsl:element name="col">
							<xsl:attribute name="class">
								<xsl:text>wc_table_colauto</xsl:text>
								<xsl:if test="$firstDataColSorted=1 and $hierarchicWithExpansion=1">
									<xsl:text> wc_table_sort_nxt</xsl:text>
								</xsl:if>
							</xsl:attribute>
							<xsl:if test="$isDebug=1">
								<xsl:comment>row selection column</xsl:comment>
							</xsl:if>
						</xsl:element>
					</xsl:if>
					
					<xsl:if test="$rowExpansion=1">
						<xsl:choose>
							<xsl:when test="$hierarchicWithExpansion=0">
								<xsl:element name="col">
									<xsl:attribute name="class">
										<xsl:text>wc_table_colauto</xsl:text>
									</xsl:attribute>
									<xsl:if test="$isDebug=1">
										<xsl:comment>row expansion column</xsl:comment>
									</xsl:if>
								</xsl:element>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="subTrToCol">
									<xsl:with-param name="sort" select="$firstDataColSorted"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="ui:thead/ui:th">
							<xsl:apply-templates select="ui:thead/ui:th" mode="col">
								<xsl:with-param name="stripe">
									<xsl:if test="@striping='cols'">
										<xsl:value-of select="1"/>
									</xsl:if>
								</xsl:with-param>
								<xsl:with-param name="sortCol" select="ui:sort/@col"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="ui:tbody/ui:tr[1]/ui:th|ui:tbody/ui:tr[1]/ui:td" mode="col">
								<xsl:with-param name="stripe">
									<xsl:if test="@striping='cols'">
										<xsl:value-of select="1"/>
									</xsl:if>
								</xsl:with-param>
								<xsl:with-param name="sortCol" select="ui:sort/@col"/>
							</xsl:apply-templates>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>

				<!--
					we have to calculate the total number of columns for the header and footer so that
					the selectAll, expansion, actions and pagination controls span the correct number of
					columns.

					It is a quirk of the heirarchich type that the row expansion static column is also included in the
					calculation of maxIndent (to allow for an indentation on the LAST indented row actually) so we
					have to reduce the count of static cols by 1 to remove this.

					TODO: When I have more of a life I will see if I can work out a better way to do this.
				-->
				<xsl:apply-templates select="ui:thead">
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
					<xsl:with-param name="addCols" select="$staticCols"/>
					<xsl:with-param name="disabled" select="$disabled"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="ui:tbody">
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
					<xsl:with-param name="addCols" select="$staticCols"/>
					<xsl:with-param name="disabled" select="$disabled"/>
				</xsl:apply-templates>
				<xsl:call-template name="tfoot">
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
					<xsl:with-param name="addCols" select="$staticCols"/>
					<xsl:with-param name="disabled" select="$disabled"/>
				</xsl:call-template>
			</xsl:element>
			<xsl:call-template name="inlineError">
				<xsl:with-param name="errors" select="$isError"/>
			</xsl:call-template>
			<xsl:call-template name="hField"/>
		</xsl:element>
	</xsl:template>



	<!--
		WARNING: DO NOT TAMPLER BELOW HERE UNLESS YOU ARE VERY SURE!

		This template returns the maximum depth of ui:subTr elements in the table. 
		Called ONLY from the transform for ui:table - we really do not want to have to do this calculation too many times.
		The calculated value is then passed through all the descendants that need it.

		NOTE: this assumes we have already tested for hierarchic.

		DO NOT CHANGE THIS TEMPLATE!!!!
		
		Well, OK, you can if you find a genuine bug but it is a monster and I don't want to hear from you if you break something.
		
		
		param thisTable: The table whose depth we are calculating. 
			Remember, in a hierarchic table we include the row expansion control in the span of the first data cell (so 
			it is always under the column header) This means our actual colspan will be one bigger than we expect.
		
	-->
	<xsl:template name="calculateMaxDepth">
		<xsl:variable name="tableId" select="@id"/>
		<xsl:choose>
			<xsl:when test="not(ui:rowExpansion and(ui:tbody/ui:tr/ui:subTr))">
				<xsl:value-of select="0"/>
			</xsl:when>
			<xsl:otherwise><!-- if we have at least one subTr, count the subTr depth-->
				<xsl:apply-templates select=".//ui:subTr[not(ui:tr/ui:subTr) and ancestor::ui:table[1]/@id=$tableId]" mode="subRowDepth">
					<xsl:sort select="count(ancestor::ui:subTr[ancestor::ui:table[1]/@id=$tableId])" data-type="number" order="descending"/>
					<xsl:with-param name="tableId" select="$tableId"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- used only in above template: do not change -->
	<xsl:template match="ui:subTr" mode="subRowDepth">
		<xsl:param name="tableId"/>
		<xsl:if test="position()=1">
			<xsl:variable name="countSubTr" select="count(ancestor-or-self::ui:subTr[ancestor::ui:table[1]/@id=$tableId])"/>
			<xsl:choose>
				<!-- 
					Each ui:subTr forms a level of indentation for its expand/collapse control.
					If:
					* the current node has content or 
					* the current ui:subTr's parent ui:tr has a (preceding or we wouldn't be the last) sibling ui:tr with a ui:subTr with content or
					* the nearest ui:tbody ancestor has a ui:subTr descendant at the same level which has content
					then we have to add an extra level of indent for the content (yes I know all of the conditions are the same and 
					only the last is actually needed but the first two are MUCH quicker and the OR will only be calculated if 
					necessary (unlike |)). This is normally the case but if all of the ui:subTrs at a given level are closed 
					and dynamic/lazy then we do not have the extra node for content.
				-->
				<xsl:when test="* or ../../ui:tr/ui:subTr/* or (ancestor::ui:table[1]/ui:tbody//ui:subTr[ancestor::ui:table[1]/@id=$tableId and count(ancestor-or-self::ui:subTr[ancestor::ui:table[1]/@id=$tableId])=$countSubTr]/*)">
					<xsl:value-of select="$countSubTr + 1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$countSubTr"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="subTrToCol">
		<xsl:param name="sort"/>
		<xsl:variable name="tableId" select="@id"/>
		
		<xsl:if test="ui:rowExpansion and ui:tbody/ui:tr/ui:subTr">
			<xsl:apply-templates select=".//ui:subTr[not(ui:tr/ui:subTr) and ancestor::ui:table[1]/@id=$tableId]" mode="subTrToCol">
				<xsl:sort select="count(ancestor::ui:subTr[ancestor::ui:table[1]/@id=$tableId])" data-type="number" order="descending"/>
				<xsl:with-param name="tableId" select="$tableId"/>
				<xsl:with-param name="sort" select="$sort"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="ui:subTr" mode="subTrToCol">
		<xsl:param name="tableId"/>
		<xsl:param name="sort"/>
		<xsl:param name="extraCol" select="1"/>
		<xsl:if test="position()=1">
			<xsl:if test="$extraCol = 1">
				<xsl:variable name="countSubTr" select="count(ancestor-or-self::ui:subTr[ancestor::ui:table[1]/@id=$tableId])"/>
				<xsl:if test="* or ../../ui:tr/ui:subTr/* or (ancestor::ui:table[1]/ui:tbody//ui:subTr[ancestor::ui:table[1]/@id=$tableId and count(ancestor-or-self::ui:subTr[ancestor::ui:table[1]/@id=$tableId])=$countSubTr]/*)">
					<col>
						<xsl:attribute name="class">
							<xsl:text>wc_table_colauto</xsl:text>
							<xsl:if test="$sort=1">
								<xsl:text> wc_table_sort_sorted</xsl:text>
							</xsl:if>
						</xsl:attribute>
					</col>
					<xsl:if test="$isDebug=1">
						<xsl:comment>spare</xsl:comment>
					</xsl:if>
				</xsl:if>
			</xsl:if>
			<col>
				<xsl:attribute name="class">
					<xsl:text>wc_table_colauto</xsl:text>
					<xsl:if test="$sort=1">
						<xsl:text> wc_table_sort_sorted</xsl:text>
					</xsl:if>
				</xsl:attribute>
			</col>
			<xsl:if test="$isDebug=1">
				<xsl:comment>subTr</xsl:comment>
			</xsl:if>
			<xsl:apply-templates select="ancestor::ui:subTr[ancestor::ui:table[1]/@id=$tableId][1]" mode="subTrToCol">
				<xsl:with-param name="tableId" select="$tableId"/>
				<xsl:with-param name="sort" select="$sort"/>
				<xsl:with-param name="extraCol" select="0"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
