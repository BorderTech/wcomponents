<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Template match="ui:selecttoggle" mode="JS"

		This template creates JSON objects required to register named group 
		controllers.
	-->
	<xsl:template match="ui:selecttoggle" mode="JS">
		<xsl:text>{"identifier":"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>","groupName":"</xsl:text>
		<xsl:value-of select="@target"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
