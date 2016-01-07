<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		This is a convenience template. It computes the classname for the BODY element.
	-->
	<xsl:template name="wcBodyClass">
		<xsl:if test="$isDebug=1">
			<xsl:text>wc_debug</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
