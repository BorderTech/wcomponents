<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.registrationScripts.coreRegistrationScripts.xsl"/>
	<xsl:import href="wc.common.registrationScripts.requiredLibraries.xsl"/>
	<xsl:import href="wc.common.registrationScripts.impl_registration.xsl"/>

	<!--
		Common bootstrapping scripts

		Template for outputting JavaScript necessary for wiring up components and
		including required libraries based on the content of the WApplication or
		AjaxResponse. This is internal to the UI and does not have a WComponent
		Java analogue. It is required by all components which have UI artefacts and
		JavaScript requirements, which is pretty much everything.

		This named template is called from wc.ui.root.n.includeJs.xsl AND
		wc.ui.ajaxTarget.xsl.

		There should be little or no need to override this template. See the
		documentation for the various helper templates.
	-->
	<xsl:template name="registrationScripts">
		<xsl:variable name="rego">
			<xsl:call-template name="coreRegistrationScripts"/>
			<xsl:call-template name="requiredLibraries"/>
		</xsl:variable>
		<xsl:if test="$rego ne '' or self::ui:root">
			<script type="text/javascript" class="registrationScripts">
				<xsl:text>require(["wc/compat/compat!"], function(){</xsl:text>
				<xsl:text>require(["wc/i18n/i18n!"], function(){</xsl:text>
				<xsl:text>require(["wc/common"], function(){</xsl:text>
				<xsl:if test="self::ui:root">
					<!--
						This looks strange, so here's what it's doing:
						1. wc.fixes is loaded, it calculates what fix modules are needed and provides this as an array.
						2. The array of module names is then loaded via require, each module is a fix which "does stuff" once loaded.
					-->
					<xsl:text>require(["wc/fixes"], function(f){require(f);});</xsl:text>
					<xsl:text>require(["wc/loader/style"], function(s){s.load();});</xsl:text>
				</xsl:if>
				<xsl:if test="number($isDebug) eq 1">
					<xsl:text>require(["wc/debug/common"]);</xsl:text>
				</xsl:if>
				<xsl:if test="$rego!=''">
					<xsl:value-of select="$rego"/>
				</xsl:if>
				<xsl:text>});});});</xsl:text>
			</script>
		</xsl:if>

		<!-- Placeholder to allow implementations to add scripts after registration -->
		<xsl:call-template name="impl_registration"/>
	</xsl:template>
</xsl:stylesheet>
