<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<!--
	Applies indentation to the first cell (th or td) in each row. Called from
	transforms of ui:td and ui:th. For info re params see transform of
	ui:table in wc.ui.table.xsl.
	-->
	<xsl:template name="firstRowCellIndentationHelper">
		<xsl:param name="myTable"/>
		<xsl:param name="maxIndent" select="0"/>
		<xsl:variable name="myTableId" select="$myTable/@id"/>
		<xsl:variable name="alreadyIndented">
			<xsl:value-of select="count(ancestor::ui:subTr[ancestor::ui:table[1]/@id=$myTableId])"/>
		</xsl:variable>
		<xsl:if test="$maxIndent &gt; $alreadyIndented">
			<xsl:attribute name="colspan">
				<xsl:value-of select="$maxIndent - $alreadyIndented"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>