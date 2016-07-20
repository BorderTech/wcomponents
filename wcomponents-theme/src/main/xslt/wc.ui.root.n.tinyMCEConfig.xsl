<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.root.variables.xsl"/>
	<!--
		Sample tinyMCE configuration 
		**** NOTE: your config MUST start with a comma. ****
	-->
	<xsl:template name="tinyMCEConfig">,
	"wc/ui/rtf": {
		"initObj": {
			content_css: "<xsl:value-of select="$cssFilePath"/>",
			plugins: 'autolink link image lists print preview paste'
		}
	}</xsl:template>
</xsl:stylesheet>
