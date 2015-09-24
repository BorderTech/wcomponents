<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Implementation specific requireJs config.
		
		If you use this template in your theme overrides it should contain config settings which are accessed by
		module.config() of the named module. This template will often contain only text. It MUST start with a text 
		comma (,) if it has any content at all as it is applied directly to a JavaScript object in 
		wc.ui.root.n.makeRequireConfig.xsl.

		###############################################################################################################
		###############################################################################################################
		WARNING: if not empty then **MUST** start with a comma!!!
		###############################################################################################################
		###############################################################################################################
	-->
	<xsl:template name="localConfig"/>
	<!--
	Here is an example which turns off statusbars in all timyMCE instances. The configuration conent has been spaced 
	out to make it readable but this is unnecessary to the functioning of the template.
	
	NOTE THE COMMA AT THE BEGINNING!!!!
	<xsl:template name="localConfig">
		<xsl:text>,
	"wc/ui/rtf": {
			"initObj": {statusbar:false}
			}
</xsl:text>
	</xsl:template>-->
</xsl:stylesheet>
