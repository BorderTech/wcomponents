<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Client side validation plugin.

		To use this plugin you will need to include a call to the named template "plugin_validation" from somewhere in
		your root transform after requirejs has been added. The suggested way to turn on this plugin is to override the
		named template impl_registration (wc.common.registrationScripts.impl_registration) and call the named template
		thus:
			<xsl:call-template name="plugin_validation"/>"

		Alternatives:
		* add "wc/ui/validation/all" (or whatever you decide to use) to localRequiredLibraries.xsl
		* to use client side validation per view add a script element to the WApplication using addJsFile where your JsFile
		has something like:
		   require(["wc/compat/compat!"], function(){require(["wc/ui/validation/all"]);});

	-->
	<xsl:template name="plugin_validation">
		<script type="text/javascript">
			<xsl:text>require(["wc/compat/compat!"], function(){require(["wc/ui/validation/all"]);});</xsl:text>
		</script>
	</xsl:template>

	<!--
	Or if you are really neat:
	
	<xsl:import href="validation.includes.xsl"/>
	<xsl:template name="plugin_validation">
		<xsl:variable name="scriptId" select="concat(generate-id(), '-validationscript')"/>
		<script type="text/javascript" id="{$scriptId}">
			<xsl:text>require(["wc/compat/compat!"], function(){try{</xsl:text>
			<xsl:call-template name="plugin_validation_includes"/>
			<xsl:text>}finally{require(["wc/dom/removeElement"],function(r){r("</xsl:text>
			<xsl:value-of select="$scriptId"/>
			<xsl:text>",250);});}</xsl:text>
			<xsl:text>});</xsl:text>
		</script>
	</xsl:template>
	-->
</xsl:stylesheet>