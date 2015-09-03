<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.debugInfo.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<!--
		Debug mode template for ui:application.
	-->
	<xsl:template name="application-debug">
		<xsl:call-template name="debugAttributes"/>
		<!--
			This is really unlikely: it would happen only if a WApplication was included in an external templating 
			system and the outer template was poorly formed.
		-->
		<xsl:call-template name="thisIsNotAllowedHere-debug">
			<xsl:with-param name="testForPhraseOnly" select="1"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
