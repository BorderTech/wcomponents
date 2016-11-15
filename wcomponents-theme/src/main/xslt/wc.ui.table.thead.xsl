<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.offscreenSpan.xsl"/>

	<!--
		Template for ui:thead. Simple generation of the HTML thead element. If the table needs row selection or 
		expansion columns these are added then the ui:th elements are applied.
		
		Structural: do not override.
	-->
	<xsl:template match="ui:thead">
		<thead>
			<tr>
				<xsl:if test="../ui:rowselection">
					<th class="wc_table_sel_wrapper" scope="col" aria-hidden="true">
						<xsl:text>&#xa0;</xsl:text>
					</th>
				</xsl:if>
				<xsl:if test="../ui:rowexpansion">
					<th class="wc_table_rowexp_container" scope="col">
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text"><xsl:text>{{t 'table_rowExpansion_toggleAll'}}</xsl:text></xsl:with-param>
						</xsl:call-template>
					</th>
				</xsl:if>
				<xsl:apply-templates select="ui:th" mode="thead"/>
				
			</tr>
		</thead>
	</xsl:template>
</xsl:stylesheet>
