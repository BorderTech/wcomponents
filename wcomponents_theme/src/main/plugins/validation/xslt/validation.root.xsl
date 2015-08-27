<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Client side validation plugin.

		To use this plugin you will need to include a call to the named template "plugin_validation" from somewhere in
		your root transform after requirejs has been added. The suggested way to turn on this plugin is to override the
		named template impl_registration (wc.common.registrationScripts.impl_registration) and call the named template
		thus:
			<xsl:call-template name="plugin_validation"/>"
	-->
	<xsl:import href="validation.includes.xsl"/>

	<xsl:template name="plugin_validation">
		<xsl:variable name="scriptId" select="concat(generate-id(), '-validationscript')"/>
		<script type="text/javascript" id="{$scriptId}">
			<xsl:text>require(["wc/compat/compat!"], function(){</xsl:text>
			<!-- config -->
			<xsl:text>try{requirejs.config({paths: {${validation.core.path.name}: "${validation.core.path.path}"}});</xsl:text>
			<!-- requires -->
			<xsl:call-template name="plugin_validation_includes"/>
			<!-- clean up this script element after use. -->
			<xsl:text>}finally{require(["wc/dom/removeElement"],function(r){r("</xsl:text>
			<xsl:value-of select="$scriptId"/>
			<xsl:text>",250);});}</xsl:text>
			<xsl:text>});</xsl:text>
		</script>
	</xsl:template>
</xsl:stylesheet>