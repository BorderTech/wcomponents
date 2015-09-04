<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	
	<!-- template to write a comma separatedlist of JSON objects -->
	<xsl:template match="ui:param" mode="analytics">
		<xsl:text>{"name":"</xsl:text>
		<xsl:value-of select="@name"/>
		<xsl:text>","value":"</xsl:text>
		<xsl:value-of select="@value"/>
		<xsl:text>","type":"</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>