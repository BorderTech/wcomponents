<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>

	<xsl:template match="ui:tbody">
		<xsl:param name="hasRole" select="0"/>
		<tbody id="{concat(../@id,'${wc.ui.table.id.body.suffix}')}">
			<xsl:if test="../@separators='both' or ../@separators='horizontal'">
				<xsl:attribute name="class">wc_table_rowsep</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:tr">
				<xsl:with-param name="myTable" select=".."/>
				<xsl:with-param name="hasRole" select="$hasRole"/>
			</xsl:apply-templates>
		</tbody>
	</xsl:template>
</xsl:stylesheet>
