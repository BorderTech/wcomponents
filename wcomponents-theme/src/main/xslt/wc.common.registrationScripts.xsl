<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.registrationScripts.coreRegistrationScripts.xsl"/>
	<xsl:import href="wc.common.registrationScripts.requiredLibraries.xsl"/>
	<!-- Common bootstrapping scripts -->
	<xsl:template name="registrationScripts">
		<xsl:variable name="rego">
			<xsl:call-template name="coreRegistrationScripts"/>
			<xsl:call-template name="requiredLibraries"/>
		</xsl:variable>
		<xsl:if test="$rego ne '' or self::ui:root">
			<xsl:variable name="scriptId" select="generate-id()"/>
			<script type="text/javascript" class="registrationScripts" id="{$scriptId}">
				<xsl:text>require(["wc/compat/compat!"], function(){</xsl:text>
				<xsl:text>require(["wc/i18n/i18n!"], function(){</xsl:text>
				<xsl:text>require(["wc/common"], function(c){if(c){try{</xsl:text>
				<xsl:if test="self::ui:root">
					<!--
						This looks strange, so here's what it's doing:
						1. wc.fixes is loaded, it calculates what fix modules are needed and provides this as an array.
						2. The array of module names is then loaded via require, each module is a fix which "does stuff" once loaded.
					-->
					<xsl:text>require(["wc/fixes"], function(f){require(f);});</xsl:text>
				</xsl:if>
				<xsl:if test="$rego ne ''">
					<xsl:value-of select="$rego"/>
				</xsl:if>
				<xsl:text>}finally{require(["wc/dom/removeElement"],function(r){r("</xsl:text>
				<xsl:value-of select="$scriptId"/>
				<xsl:text>",250);});}}});</xsl:text>
				<xsl:text>});});</xsl:text>
			</script>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
