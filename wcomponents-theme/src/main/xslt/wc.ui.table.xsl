<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.ui.table.n.className.xsl"/>
	<xsl:import href="wc.ui.table.n.caption.xsl"/>
	<xsl:import href="wc.ui.table.n.tableBottomControls.xsl"/>
	<xsl:import href="wc.ui.table.n.topControls.xsl"/>

	<!--
		WTable (and WDataTable)

		This is long but reasonably straight-forward generation of HTML tables.

		The HTML TABLE element itself is wrapped in a DIV. This is to provide somewhere to attach messages as a WTable
		can be in an error state (yes, really). As a side-effect it makes it really easy to create more-or-les 
		accessible horizontal scrolling.

		Structural: do not override.
	-->
	<xsl:template match="ui:table">
		<xsl:variable name="id" select="@id"/>
		<!--
			NOTE: Error state

			Now it is pretty plain that a table cannot be in an error mode. The table is not, after all, intrinsically
			interactive. The error indicator is used to provide visual indication that there is an error somewhere in
			the table. As such it is pretty appalling!
			
			It is, therefore, assumed that a table will be in an error state only if the rowselection is in an 
			error state.
		-->
		<xsl:variable name="isError" select="key('errorKey',$id)"/>
		
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

		<div id="{$id}">
			<xsl:call-template name="wtableClassName"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>

			<xsl:if test="ui:pagination[@mode eq 'dynamic' or @mode eq 'client'] or 
				ui:rowexpansion[@mode eq 'lazy' or @mode eq 'dynamic'] or 
				ui:sort[@mode eq 'dynamic'] or 
				key('targetKey',$id) or 
				parent::ui:ajaxtarget[@action eq 'replace']">
				<xsl:call-template name="setARIALive"/>
			</xsl:if>
			
			<xsl:if test="number($rowExpansion) eq 1">
				<xsl:variable name="expMode" select="ui:rowexpansion/@mode"/>
				<xsl:attribute name="data-wc-expmode">	
					<xsl:choose>
						<xsl:when test="($expMode eq 'lazy' or $expMode eq 'eager') and ui:subtr/@open">
							<xsl:text>client</xsl:text>
						</xsl:when>
						<xsl:when test="$expMode eq 'eager'">
							<xsl:text>lazy</xsl:text>
						</xsl:when>
						<xsl:when test="$expMode">
							<xsl:value-of select="$expMode"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>client</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="ui:pagination">
				<xsl:attribute name="data-wc-pagemode">
					<xsl:choose>
						<xsl:when test="ui:pagination/@mode">
							<xsl:value-of select="ui:pagination/@mode"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>client</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>

			<!-- THIS IS WHERE THE DIV's CONTENT STARTS NO MORE ATTRIBUTES AFTER THIS POINT THANK YOU! -->
			<!--
				Add table controls which do not form part of the table structure but which control and reference the 
				table. The default are the select/deselect all, expand/collapse all and pagination controls (if position
				is TOP or BOTH).
			-->
			<xsl:call-template name="topControls"/>

			<xsl:variable name="tableClass">
				<xsl:if test="number($rowExpansion) eq 1">
					<xsl:text>wc_tbl_expansion</xsl:text>
				</xsl:if>
				<xsl:if test="ui:thead/ui:th[@width]">
					<xsl:text> wc_table_fix</xsl:text>
				</xsl:if>
			</xsl:variable>
	
			<table>
				<xsl:if test="$tableClass ne ''">
					<xsl:attribute name="class">
						<xsl:value-of select="normalize-space($tableClass)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$isError">
					<xsl:call-template name="invalid"/>
				</xsl:if>
				<xsl:if test="number($rowExpansion) + number($rowSelection) gt 0">
					<xsl:if test="number($rowSelection) eq 1">
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
					<xsl:if test="@separators eq 'both' or @separators eq 'vertical'">
						<xsl:attribute name="class">
							<xsl:text>wc_table_colsep</xsl:text>
						</xsl:attribute>
					</xsl:if>

					<xsl:if test="number($rowSelection) eq 1">
						<col class="wc_table_colauto"></col>
					</xsl:if>

					<xsl:if test="number($rowExpansion) eq 1">
						<col class="wc_table_colauto"></col>
					</xsl:if>

					<xsl:choose>
						<xsl:when test="ui:thead/ui:th">
							<xsl:apply-templates select="ui:thead/ui:th" mode="col">
								<xsl:with-param name="stripe">
									<xsl:choose>
										<xsl:when test="@striping eq 'cols'">
											<xsl:value-of select="1"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:number value="0"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="sortCol" select="ui:sort/@col"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="ui:tbody/ui:tr[1]/ui:th|ui:tbody/ui:tr[1]/ui:td" mode="col">
								<xsl:with-param name="stripe">
									<xsl:choose>
										<xsl:when test="@striping eq 'cols'">
											<xsl:value-of select="1"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:number value="0"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="sortCol" select="ui:sort/@col"/>
							</xsl:apply-templates>
						</xsl:otherwise>
					</xsl:choose>
				</colgroup>

				<xsl:apply-templates select="ui:thead"/>
				<xsl:apply-templates select="ui:tbody"/>
				
			</table>
			<!--
				Add table controls which do not form part of the table structure but which control and reference the 
				table. The default are actions and the pagination controls (if position is unsset, BOTTOM or BOTH).
			-->
			<xsl:call-template name="tableBottomControls"/>
			<xsl:call-template name="inlineError">
				<xsl:with-param name="errors" select="$isError"/>
			</xsl:call-template>
			<xsl:call-template name="hField"/>
		</div>
	</xsl:template>

	<!--
		Transform for the noData child of a tbody. This is a String so just needs to be wrapped up properly.
	-->
	<xsl:template match="ui:nodata">
		<div class="wc-nodata">
			<xsl:value-of select="."/>
		</div>
	</xsl:template>

	<!--
		Table Actions
	-->
	<xsl:template match="ui:actions">
		<xsl:apply-templates select="ui:action"/>
	</xsl:template>

	<xsl:template match="ui:action">
		<xsl:apply-templates select="ui:button"/>
	</xsl:template>

	<!--
		Outputs a comma separated list of JSON objects stored in a button attribute which is used to determine whether 
		the action's conditions are met before undertaking the action.
	-->
	<xsl:template match="ui:condition" mode="action">
		<xsl:text>{"min":"</xsl:text>
		<xsl:value-of select="@minSelectedRows"/>
		<xsl:text>","max":"</xsl:text>
		<xsl:value-of select="@maxSelectedRows"/>
		<xsl:text>","type":"</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>","message":"</xsl:text>
		<xsl:value-of select="@message"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<!--
		1. Guard wrapper for action conditions. These are not output in place but are part of the sibling button's 
		attribute data. The ui:action part of the match is to differentiate from ui:subordinate's ui:condition.
		
		2. Null template for ui:sort. The sort indicators are generated in the ui:table column generation and sort 
		controls in ui:thead/ui:th. The ui:sort element itself contains metaData only and does not generate a usable 
		HTML artefact.
	-->
	<xsl:template match="ui:action/ui:condition|ui:sort"/>

	<!--
		Creates each col element in the colgroup created in the transform of the table.
	
		param stripe: 1 if the table has column striping.
		param sortCol: The 0 indexed column which is currently sorted (if any).
	-->
	<xsl:template match="ui:th|ui:td" mode="col">
		<xsl:param name="stripe" select="0"/>
		<xsl:param name="sortCol" select="-1"/>
		<xsl:variable name="class">
			<xsl:if test="number($stripe) eq 1 and position() mod 2 eq 0">
				<xsl:text>wc_table_stripe</xsl:text>
			</xsl:if>
			<xsl:if test="$sortCol and number($sortCol) ge 0 and position() eq number($sortCol) + 1">
				<xsl:text> wc_table_sort_sorted</xsl:text>
			</xsl:if>
		</xsl:variable>
		<col>
			<xsl:if test="$class ne ''">
				<xsl:attribute name="class">
					<xsl:value-of select="normalize-space($class)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@width">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:',@width,'%')"/>
				</xsl:attribute>
			</xsl:if>
		</col>
	</xsl:template>
</xsl:stylesheet>
