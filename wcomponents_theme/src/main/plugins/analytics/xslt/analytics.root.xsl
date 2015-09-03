<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		This XSLT writes JavaScript to set up the web analytics plugin. It should be called directly inside a transform
		of ui:root within the HTML HEAD element and	after requirejs is added.

		The suggested way to turn on this plugin is to override the named template impl_registration
		(wc.common.registrationScripts.impl_registration) and call the named template thus:
			<xsl:call-template name="plugin_analytics"/>"
	-->
	<xsl:import href="../../../xslt/wc.constants.xsl"/>
	<xsl:import href="analytics.ajaxTarget.xsl"/>

	<xsl:template name="plugin_analytics">
		<!-- NOTE: the ajaxTarget case in this test will ONLY work as expected if the originating ui:root element was set up
			to turn on tracking in its child ui:application.
		-->
		<xsl:if test="(self::ui:root and ui:application/ui:analytic) or (self::ui:ajaxTarget and (.//ui:tracking or descendant-or-self::*[@track=$t]))">
			<xsl:variable name="scriptId" select="concat(generate-id(),'-trackscript')"/>
			<script type="text/javascript" id="{$scriptId}">
				<xsl:text>require(["wc/compat/compat!"], function(){</xsl:text>
				<xsl:text>try{if(window.navigator.doNotTrack !== "1"){</xsl:text>
				<!-- config -->
				<xsl:apply-templates select="ui:application" mode="analyticsconfig"/>

				<!-- tracking -->
				<xsl:choose>
					<xsl:when test="self::ui:root">
						<xsl:apply-templates select="ui:application" mode="analytics"/>
					</xsl:when>
					<xsl:otherwise>
						<!-- ui:ajaxTarget -->
						<xsl:call-template name="analytics_ajaxTarget"/>
					</xsl:otherwise>
				</xsl:choose>
				<!-- clean up -->
				<xsl:text>}}finally{require(["wc/dom/removeElement"],function(r){r("</xsl:text>
				<xsl:value-of select="$scriptId"/>
				<xsl:text>",250);});}</xsl:text>
				<xsl:text>});</xsl:text>
			</script>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>