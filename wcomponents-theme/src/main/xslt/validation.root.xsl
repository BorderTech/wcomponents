<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Client side validation plugin.

		To use this plugin you will need to include a call to the named template "plugin_validation" from somewhere in
		your root transform after requirejs has been added. The suggested way to turn on this plugin is to override the
		named template impl_registration (wc.common.registrationScripts.impl_registration) and call the named template
		thus:
			<xsl:call-template name="plugin_validation"/>"

		Alternatives:
		* add "wc/ui/validation/all" to localRequiredLibraries.xsl
		* to use client side validation per view add a script element to the WApplication using addJsFile where your 
		  JsFile has something like:
		   require(["wc/compat/compat!"], function(){require(["wc/ui/validation/all"]);});

	-->
	<xsl:template name="plugin_validation">
		<script type="text/javascript">
			<xsl:text>require(["wc/compat/compat!"], function(){require(["wc/ui/validation/all"]);});</xsl:text>
		</script>
	</xsl:template>
</xsl:stylesheet>