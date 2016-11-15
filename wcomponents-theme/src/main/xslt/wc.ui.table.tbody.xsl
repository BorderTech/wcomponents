<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>

	<!-- 
		Transform of ui:tbody to tbody.

		Structural: do not override.
	-->
	<xsl:template match="ui:tbody">
		<tbody id="{concat(../@id,'_tb')}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="../@type">
						<xsl:value-of select="concat('wc_tbl_', ../@type)"/>
					</xsl:if>
					<xsl:if test="../@separators eq 'both' or ../@separators eq 'horizontal'">
						<xsl:text> wc_table_rowsep</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="ui:tr">
				<xsl:with-param name="myTable" select=".."/>
			</xsl:apply-templates>
		</tbody>
	</xsl:template>
</xsl:stylesheet>
