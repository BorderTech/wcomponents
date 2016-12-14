<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Alternatives:
		* add "wc/ui/validation/all" to common.js
		* to use client side validation per view add a script element to the WApplication using addJsFile where your  JsFile has something like:
		   require(["wc/compat/compat!"], function(){require(["wc/ui/validation/all"]);});
	-->
	<xsl:template name="plugin_validation">
		<script type="text/javascript">
			<xsl:text>require(["wc/compat/compat!"], function(){require(["wc/ui/validation/all"]);});</xsl:text>
		</script>
	</xsl:template>
</xsl:stylesheet>