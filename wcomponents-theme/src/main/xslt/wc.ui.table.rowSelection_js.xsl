<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.selectToggle.xsl"/>
	<!--
		Outputs a comma separated list of JSON objects required for registering
		the selection controls. See wc.common.registrationScripts.xsl.
	-->
	<xsl:template match="ui:rowselection" mode="JS">
		<xsl:text>{"identifier":"</xsl:text>
		<xsl:value-of select="concat(../@id,'${wc.ui.table.id.body.suffix}','${wc.ui.selectToggle.id.suffix}')"/>
		<xsl:text>","groupName":"</xsl:text>
		<xsl:value-of select="concat(../@id,'${wc.ui.table.id.body.suffix}')"/>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
