<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.registrationScripts.xsl"/>

	<!--
		Used to calculate the path to the libs based on the stylesheet processing instruction stripped is used in the config object. Param so it can
		be overridden if necessary.
	-->
	<xsl:param name="xslPath" select="substring-before(replace(substring-after(//processing-instruction('xml-stylesheet'), 'href=&quot;'), '&amp;amp;', '&amp;'), '&quot;')"/>

	<!--
		this is the absolute or server relative path to the resources used to build the site calculated from the XSLT
		processing instruction.
	-->
	<xsl:variable name="resourceRoot">
		<xsl:value-of select="substring-before($xslPath, 'xslt')"/>
	</xsl:variable>

	<!--
		This string is used to build a query string on all resources requested as part of a page.
	-->
	<xsl:variable name="cacheBuster">
		<xsl:value-of select="substring-after($xslPath, '?')"/>
	</xsl:variable>

	<xsl:template match="ui:root">
		<xsl:variable name="scriptDir">
			<xsl:choose>
				<xsl:when test="number($isDebug) eq 1">
					<xsl:text>${script.debug.target.dir.name}</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>${script.target.dir.name}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="registeredComponents">
			<xsl:call-template name="registrationScripts"/>
		</xsl:variable>

		<html lang="{@lang}">
			<head>
				<!-- Favicon works more reliably if it is first -->
				<xsl:choose>
					<xsl:when test="ui:application/@icon">
						<xsl:call-template name="faviconHelper">
							<xsl:with-param name="href">
								<xsl:value-of select="ui:application[@icon][1]/@icon"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="//html:link[@rel eq 'shortcut icon' or @rel eq 'icon']">
						<xsl:apply-templates select="//html:link[contains(@rel, 'icon')][1]"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="faviconHelper">
							<xsl:with-param name="href" select="concat($resourceRoot,'images/favicon.ico')"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				<!--
					The format-detection is needed to work around issues in some very popular mobile browsers that will convert "numbers" into phone
					links (a elements) if they appear to be phone numbers, even if those numbers are the content of buttons or links. This breaks
					important stuff if you, for example, want to link or submit using a number identifier.

					If you want a phone number link in these (or any) browser use WPhoneNumberField set to read-only.
				-->
				<meta name="format-detection" content="telephone=no"/>
				<meta name="viewport" content="initial-scale=1"/>
				<title><xsl:value-of select="@title"/></title>

				<!--
					CSS before JavaScript for performance.
				-->
				<xsl:variable name="mainCssUrl">
					<xsl:call-template name="cssUrl">
						<xsl:with-param name="filename" select="'${css.target.file.name}'"/>
					</xsl:call-template>
				</xsl:variable>
				<link type="text/css" rel="stylesheet" id="wc_css_screen" href="{$mainCssUrl}"/><!-- te id is used by the style loader js -->

				<xsl:if test="$isDebug = 1">
					<!-- Load debug CSS -->
					<xsl:variable name="debugCssUrl">
						<xsl:call-template name="cssUrl">
							<xsl:with-param name="filename" select="'wcdebug'"/>
						</xsl:call-template>
					</xsl:variable>
					<link type="text/css" rel="stylesheet" href="{$debugCssUrl}"/>
				</xsl:if>
				<xsl:apply-templates select=".//html:link[@rel eq 'stylesheet']" mode="inHead"/>
				<xsl:apply-templates select="ui:application/ui:css" mode="inHead"/>

				<!--
					We need to set up the require config very early. This mess constructs the require config which is necessary to commence inclusion
					and bootstrapping of WComponent JavaScript. This must be included before a script element to include require.js (or whichever AMD
					loader you are using).

					You really don't want to be here.
				-->
				<xsl:variable name="wcScriptDir" select="concat($scriptDir, '/wc')"/>
				<xsl:variable name="libScriptDir" select="concat($scriptDir, '/lib')"/>
				<script type="text/javascript">
					<!--
						Yes, we are defining a global require here. We are not using window.require = {} because the doco says this can cause problems
						in IE.
					-->
					<xsl:text>(function(){
	var wcconfig, timing,
		config = {
				paths: {
					wc: "</xsl:text><xsl:value-of select="$wcScriptDir"/><xsl:text>",
					lib: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>",
					tinyMCE: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/tinymce/tinymce.min",
					Promise: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/Promise.min",
					fabric: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/fabric",
					ccv: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/ccv",
					face: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/face",
					tracking: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/tracking/build/tracking-min",
					getUserMedia: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/getusermedia-js/getUserMedia.min",
					axs: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/axs_testing",
					axe: "</xsl:text><xsl:value-of select="$libScriptDir"/><xsl:text>/axe.min"
				},
				shim: {
					tinyMCE: {
						exports: "tinyMCE",
						init: function () {
							this.tinyMCE.DOM.events.domLoaded = true;
							return this.tinyMCE;
						}},
					tracking: {exports: "tracking"},
					Promise: {exports: "Promise"},
					fabric: {exports: "fabric"},
					ccv: {exports: "ccv"},
					face: {exports: "cascade"},
					getUserMedia: {exports: "getUserMedia"},
					axs: {exports: "axs"},
					axe: {exports: "axe"}
				},
				deps:[],&#10;
			</xsl:text>
					<xsl:value-of select="concat('baseUrl:&quot;', normalize-space($resourceRoot), '&quot;,&#10;')"/>
					<xsl:value-of select="concat('urlArgs:&quot;', $cacheBuster, '&quot;&#10;')"/>
					<xsl:text>};&#10;wcconfig = {"wc/i18n/i18n": { </xsl:text>
					<xsl:text>options:{ backend: {</xsl:text>
					<xsl:value-of select="concat('cachebuster:&quot;', $cacheBuster, '&quot;')"/>
					<xsl:text>} } },&#10;"wc/loader/resource": {</xsl:text>
					<xsl:value-of select="concat('resourceBaseUrl:&quot;', normalize-space($resourceRoot), '${resource.target.dir.name}/&quot;,&#10;')"/>
					<xsl:value-of select="concat('cachebuster:&quot;', $cacheBuster, '&quot;')"/>
					<xsl:text>},&#10;"wc/loader/style":{</xsl:text>
					<xsl:value-of select="concat('cssBaseUrl:&quot;', normalize-space($resourceRoot), '${css.target.dir.name}/&quot;,&#10;')"/>
					<xsl:value-of select="concat('cachebuster:&quot;', $cacheBuster, '&quot;')"/>
					<xsl:text>}};&#10;</xsl:text>
					<!--
						The timings must be collected as early as possible in the page lifecycle
						and since this is the very first script that runs we need to put it here.
						This is also why we build the config BEFORE we load the AMD loader.
					-->
					<xsl:text>
	try{
		timing = {};
		timing[document.readyState] = (new Date()).getTime();
		document.onreadystatechange = function(){
				timing[document.readyState] = (new Date()).getTime();
				if(window.requirejs &amp;&amp; window.requirejs.config) window.requirejs.config({"config":{"wc/compat/navigationTiming":{"timing": timing}}});
			};
		wcconfig["wc/compat/navigationTiming"] = {"timing": timing};
		wcconfig["wc/config"] = { "dehydrated": JSON.stringify(wcconfig) };
	}
	catch(ex){}
	config.config = wcconfig;
	if(window.requirejs) window.requirejs.config(config);
	else require = config;
})();</xsl:text>
				</script>

				<!--
					non-AMD compatible fixes for IE: things that need to be fixed before we can require anything but
					have to be added after we have included requirejs/require.
				-->
				<xsl:call-template name="makeIE8CompatScripts"/>
				<!--
					Load requirejs
				-->
				<script type="text/javascript" src="{concat($resourceRoot, $scriptDir, '/lib/require.js?', $cacheBuster)}"></script>

				<!--<xsl:if test="concat('${ie.css.list}','${css.pattern.list}') ne ''">
					<script type="text/javascript">
						<xsl:text>require(["wc/compat/compat!"], function(){</xsl:text>
						<xsl:text>require(["wc/loader/style"],function(s){s.load();});</xsl:text>
						<xsl:text>});</xsl:text>
					</script>
				</xsl:if>-->

				<xsl:if test="$registeredComponents ne '' or concat('${ie.css.list}','${css.pattern.list}') ne ''">
					<script type="text/javascript" class="registrationScripts">
						<xsl:text>require(["wc/compat/compat!"], function(){</xsl:text>
						<!--
							This looks strange, so here's what it's doing:
							1. wc.fixes is loaded, it calculates what fix modules are needed and provides this as an array.
							2. The array of module names is then loaded via require, each module is a fix which "does stuff" once loaded.
						-->
						<xsl:if test="concat('${ie.css.list}','${css.pattern.list}') ne ''">
							<xsl:text>
								require(["wc/loader/style"],function(s){s.load();});</xsl:text>
						</xsl:if>
						<xsl:if test="$registeredComponents ne ''">
							<xsl:text>
								require(["wc/common"], function(){
							</xsl:text>
							<xsl:value-of select="$registeredComponents"/>
							<xsl:text>
								});
							</xsl:text>
						</xsl:if>
						<xsl:text>});</xsl:text>
					</script>
				</xsl:if>

				<!--
					We grab all base, meta and link elements from the content and place
					them in the head where they belong.
				-->
				<xsl:apply-templates select="ui:application/ui:js" mode="inHead"/>
				<xsl:apply-templates select=".//html:base|.//html:link[not(contains(@rel,'icon') or @rel eq 'stylesheet')]|.//html:meta" mode="inHead"/>
			</head>
			<xsl:variable name="domready">
				<xsl:choose>
					<xsl:when test="$registeredComponents=''">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<body data-wc-domready="{$domready}">
				<xsl:if test="$registeredComponents!=''">
					<div id="wc-shim" class="wc_shim_loading">
						<xsl:text>&#xa0;</xsl:text>
						<noscript>
							<p>You must have JavaScript enabled to use this application.</p>
						</noscript>
					</div>
					<div id="wc-ui-loading">
						<div tabindex="0" class="fa fa-spinner fa-spin">&#x200b;</div>
					</div>
				</xsl:if>
				<xsl:apply-templates >
					<xsl:with-param name="nojs">
						<xsl:choose>
							<xsl:when test="$registeredComponents ne ''">
								<xsl:number value="0"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:apply-templates>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="faviconHelper">
		<xsl:param name="href" select="''"/>
		<xsl:if test="$href ne ''">
			<!-- rel value invalid but the only cross browser option -->
			<link rel="shortcut icon" href="{$href}"/>
		</xsl:if>
	</xsl:template>
	<!--
		IE 8 and below needs a helper to recognise HTML5 elemnts as HTML elements. This needs to happen so very early that we cannot use require to
		load it. We can use an IE conditional comment to limit this code to IE8 and before.
	-->
	<xsl:template name="makeIE8CompatScripts">
		<xsl:comment>[if lte IE 8] &gt;
&lt;script type="text/javascript"&gt;
(function(){
	var i, el=["details","datalist","aside","dialog","summary","section","header","nav","footer","meter","output","progress","audio","video","source","time","track","figcaption","figure"];
	for (i = 0; i &lt; el.length; i++){ document.createElement(el[i]); } })();
&lt;/script&gt;
&lt;![endif]</xsl:comment>
	</xsl:template>

	<xsl:template name="cssUrl">
		<xsl:param name="filename"/>
		<xsl:value-of select="$resourceRoot"/>
		<xsl:text>${css.target.dir.name}/</xsl:text>
		<xsl:value-of select="$filename"/>
		<xsl:if test="$isDebug = 1">
			<xsl:text>${debug.target.file.name.suffix}</xsl:text>
		</xsl:if>
		<xsl:text>.css?</xsl:text>
		<xsl:value-of select="$cacheBuster"/>
	</xsl:template>
</xsl:stylesheet>
