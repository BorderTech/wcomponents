<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.root.n.addHeadMetaBeforeTitle.xsl"/>
	<xsl:import href="wc.ui.root.n.makeIE8CompatScripts.xsl"/>
	<xsl:import href="wc.ui.root.n.makeRequireConfig.xsl"/>
	<xsl:import href="wc.ui.root.n.externalScript.xsl"/>
	<xsl:import href="wc.common.registrationScripts.xsl"/>
	<xsl:import href="wc.ui.root.n.wcBodyClass.xsl"/>
	<!--
		Some meta elements have to be VERY early to work reliably. Put them here.

		NOTE: If you need the old XSLT which enabled a WComponent application to be nested inside an existing HTML
		structure you will need to either rewrite it or retrieve it from the archives. It has gone because it was slow
		and no-one needs it anymore.

		$lang is a global attribute injected by the WComponent server application.
	-->
	<xsl:strip-space elements="*"/>
	<xsl:template match="ui:root">
		<html lang="{$lang}">
			<head>
				<xsl:call-template name="addHeadMetaBeforeTitle"/>
				<title>
					<xsl:value-of select="@title"/>
				</title>

				<link type="text/css" id="${wc_css_main_id}" rel="stylesheet">
					<xsl:attribute name="href">
						<xsl:value-of select="$resourceRoot"/>
						<xsl:text>${css.target.dir.name}/${css.target.file.name}</xsl:text>
						<xsl:if test="$isDebug=1">
							<xsl:text>${debug.target.file.name.suffix}</xsl:text>
						</xsl:if>
						<xsl:text>.css?</xsl:text>
						<xsl:value-of select="$cacheBuster"/>
					</xsl:attribute>
				</link>
				
				<xsl:apply-templates select="ui:application/ui:css" mode="inHead"/>
				<xsl:apply-templates select=".//html:link[@rel='stylesheet']" mode="inHead"/>

				<!--
					We need to set up the require config very early.
				-->
				<xsl:call-template name="makeRequireConfig"/>

				<!--
					non-AMD compatible fixes for IE: things that need to be fixed before we can require anything but
					have to be added after we have included requirejs/require.
				-->
				<xsl:call-template name="makeIE8CompatScripts"/>

				<xsl:call-template name="externalScript">
					<xsl:with-param name="scriptName" select="'lib/require'"/>
				</xsl:call-template>

				<!-- We can delete some script nodes after they have been used. To do this we need the script element to have an ID. -->
				<xsl:variable name="scriptId" select="generate-id()"/>
				<!-- We want to load up the CSS as soon as we can, so do it immediately after loading require. -->
				<xsl:variable name="styleLoaderId" select="concat($scriptId,'-styleloader')"/>
				<script type="text/javascript" id="{$styleLoaderId}">
					<xsl:text>require(["wc/compat/compat!"], function() {</xsl:text>
					<xsl:text>require(["wc/loader/style", "wc/dom/removeElement"</xsl:text>
					<xsl:if test="$isDebug=1">
						<xsl:text>,"wc/debug/consoleColor", "wc/debug/a11y", "wc/debug/indicator"</xsl:text>
					</xsl:if>
					<xsl:text>], function(s, r){try{s.load();}finally{r("</xsl:text>
					<xsl:value-of select="$styleLoaderId"/>
					<xsl:text>", 250);}});</xsl:text>
					<xsl:text>});</xsl:text>
				</script>

				<xsl:call-template name="registrationScripts"/>
				<!--
					We grab all base, meta and link elements from the content and place
					them in the head where they belong.
				-->
				<xsl:apply-templates select="ui:application/ui:js" mode="inHead"/>
				<xsl:apply-templates select=".//html:base|.//html:link[not(@rel='icon' or @rel='shortcut icon' or @rel='stylesheet')]|.//html:meta" mode="inHead"/>
			</head>
			<xsl:variable name="bodyClass">
				<xsl:call-template name="wcBodyClass"/>
			</xsl:variable>
			<body>
				<xsl:if test="$bodyClass!=''">
					<xsl:attribute name="class">
						<xsl:value-of select="normalize-space($bodyClass)"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$isDebug=1">
					<xsl:comment>
						XSLT processor: <xsl:value-of select="system-property('xsl:vendor')"/>
						base-uri available? <xsl:value-of select="function-available('base-uri')"/>
					</xsl:comment>
				</xsl:if>
				<!--
					loading indicator and shim
					We show a loading indicator as we construct the page then remove it as part of post-initialisation.
					This helps to prevent users interacting with a page before it is ready. The modal shim is part of
					the page level loading indicator.
				-->
				<div id="wc_shim" class="wc_shim_loading">
					<xsl:text>&#xa0;</xsl:text>
					<noscript>
						<p>
							<xsl:value-of select="$$${wc.ui.root.i18n.noscript.message}"/>
						</p>
					</noscript>
				</div>
				<div id="wc_ui_loading">
					<div tabindex="0">
						<xsl:value-of select="$$${wc.ui.loading.loadMessage}"/>
					</div>
				</div>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
