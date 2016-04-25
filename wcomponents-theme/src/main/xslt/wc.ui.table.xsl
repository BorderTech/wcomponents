<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.ui.table.n.caption.xsl"/>
	<xsl:import href="wc.ui.table.n.tableBottomControls.xsl"/>
	<xsl:import href="wc.ui.table.n.topControls.xsl"/>
	<xsl:import href="wc.ui.table.n.autocol.xsl"/>
	
	<!--
		WTable (and WDataTable)

		This is long but reasonably straight-forward generation of HTML tables.

		There are two modes of table which differ in how nested rows (ui:subtrs)
		are treated. Type "table" (assumed if attribute not present) makes all rows sit
		directly under each other. Type "hierarchic" indents child rows. This causes a
		few issues since the rows are not actual children but siblings.

		The HTML TABLE element is actually wrapped in a DIV. This is to provide
		somewhere to attach messages as a WTable can be in an error state (yes, really).

		Common XSLT parameters

		Individual element transforms may require to reference the ancestor table. To
		facilitate this without doing an ancestor:: lookup for each cell we pass certain
		information about the table down through all descendant element transforms.

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
		<!--
			NOTE: Error state

			Now it is pretty plain that a table cannot be in an error mode. The table is not, after all, intrinsically
			interactive. The error indicator is used to provide visual indication that there is an error somewhere in
			the table. As such it is pretty appalling!
		-->
		<xsl:variable name="isError" select="key('errorKey',$id)"/>

		<div id="{$id}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>

			<xsl:if test="ui:pagination[@mode='dynamic' or @mode='client'] or ui:rowexpansion[@mode='lazy' or @mode='dynamic'] or ui:sort[@mode='dynamic'] or key('targetKey',$id) or parent::ui:ajaxtarget[@action='replace']">
				<xsl:call-template name="setARIALive"/>
			</xsl:if>

			<xsl:apply-templates select="ui:margin"/>
			<!--
				Disabled state: WDataTable only

				The disabled state is not strictly required on the table wrapper since we do
				not do ancestor-or-self lookups in determining disabled controls. It is used to
				disable table functionality: actions, rowExpansion, sorting and rowSelection.
			<xsl:call-template name="disabledElement"/>
			-->

			<!-- THIS IS WHERE THE DIV's CONTENT STARTS NO MORE ATTRIBUTES AFTER THIS POINT THANK YOU! -->

			<xsl:choose>
				<xsl:when test="ui:tbody/ui:nodata">
					<!-- short-circuit a whole pile of pain if we have nothing to show. -->
					<xsl:apply-templates select="ui:tbody/ui:nodata"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="rowExpansion">
						<xsl:choose>
							<xsl:when test="ui:rowexpansion">
								<xsl:value-of select="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="rowSelection">
						<xsl:choose>
							<xsl:when test="ui:rowselection">
								<xsl:value-of select="1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="0"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="hasRole" select="$rowExpansion + $rowSelection"/>

					<!--
						Add table controls which do not form part of the table structure but which control and reference
						the table.
					-->
					<xsl:call-template name="topControls"/>

					<table>
						<xsl:if test="$hasRole &gt; 0">
							<xsl:attribute name="role">
								<xsl:choose>
									<xsl:when test="$rowExpansion=1">
										<xsl:text>treegrid</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>grid</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:attribute name="aria-readonly">true</xsl:attribute>
							<xsl:if test="$isError">
								<xsl:call-template name="invalid"/>
							</xsl:if>
							<xsl:if test="$rowSelection=1">
								<xsl:attribute name="aria-multiselectable">
									<xsl:choose>
										<xsl:when test="ui:rowselection/@multiple">
											<xsl:text>true</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>false</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</xsl:if>
						</xsl:if>
						<xsl:if test="ui:thead/ui:th[@width]">
							<xsl:attribute name="class">
								<xsl:text>wc_table_fix</xsl:text>
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
						<xsl:if test="ui:sort">
							<xsl:attribute name="sortable">sortable</xsl:attribute>
						</xsl:if>

						<xsl:call-template name="caption" />

						<colgroup>
							<xsl:if test="@separators='both' or @separators='vertical'">
								<xsl:attribute name="class">
									<xsl:text>wc_table_colsep</xsl:text>
								</xsl:attribute>
							</xsl:if>

							<xsl:if test="$rowSelection=1">
								<xsl:call-template name="autocol"/>
							</xsl:if>

							<xsl:if test="$rowExpansion=1">
								<xsl:call-template name="autocol"/>
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
						</colgroup>

						<xsl:apply-templates select="ui:thead">
							<xsl:with-param name="hasRole" select="$hasRole"/>
						</xsl:apply-templates>

						<xsl:apply-templates select="ui:tbody">
							<xsl:with-param name="hasRole" select="$hasRole"/>
						</xsl:apply-templates>
					</table>

					<xsl:call-template name="tableBottomControls">
						<xsl:with-param name="addCols" select="$hasRole"/>
					</xsl:call-template>
					<xsl:call-template name="inlineError">
						<xsl:with-param name="errors" select="$isError"/>
					</xsl:call-template>
					<xsl:call-template name="hField"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
</xsl:stylesheet>
