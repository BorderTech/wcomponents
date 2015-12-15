<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
<!--
 If the table has single row selection then each selectable row is a radio and the tbody is the grouping element.
-->
	<xsl:template match="ui:tbody">
		<xsl:param name="addCols" select="0"/>
		<xsl:element name="tbody">
			<xsl:attribute name="id">
				<xsl:value-of select="concat(../@id,'${wc.ui.table.id.body.suffix}')"/>
			</xsl:attribute>
			<xsl:if test="../@separators='both' or ../@separators='horizontal'">
				<xsl:attribute name="class">
					<xsl:text>wc_table_rowsep</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="../ui:rowSelection[not(@multiple=$t)]">
				<xsl:attribute name="role">
					<xsl:text>radiogroup</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="myTable" select="parent::ui:table"/>
				<xsl:with-param name="addCols" select="$addCols"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
