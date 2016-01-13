<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.root.variables.xsl"/>
	<xsl:import href="wc.ui.root.n.styleLoaderConfig.xsl"/>
	<xsl:import href="wc.ui.root.n.localConfig.xsl"/>
	<!--
		Constructs the require config which is necessary to commence inclusion
		and bootstrapping of WComponent JavaScript. This must be included before
		a script element to include require.js (or whichever AMD loader you are
		using).

		You really don't want to be here.
	-->
	<xsl:template name="makeRequireConfig">
		<xsl:element name="script">
			<xsl:attribute name="type">
				<xsl:text>text/javascript</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>${wc.global.config}</xsl:text>
			</xsl:attribute>
			<!-- Yes, we are defining a global require here. We are not using window.require = {} because the doco says this can cause problems in IE. -->
			<xsl:text>(function(){
	var wcconfig, timing,
		config = {
					paths: {
						tinyMCE: "lib/tinymce/tinymce.min",
						Promise: "lib/Promise.min",
						fabric: "lib/fabric",
						Mustache: "lib/mustache/mustache.min",
						axs: "lib/axs_testing",
						axe: "lib/axe.min"
					},
					shim: {
						tinyMCE: {
							exports: "tinyMCE",
							init: function () {
								this.tinyMCE.DOM.events.domLoaded = true;
								return this.tinyMCE;
							}
						},
						Promise: {
							exports: "Promise"
						},
						fabric: {
							exports: "fabric"
						},
						axs: {
							exports: "axs"
						},
						axe: {
							exports: "axe"
						}
					},
					deps:[],&#10;
			</xsl:text>
			<xsl:value-of select="concat('baseUrl:&quot;', normalize-space($resourceRoot), $scriptDir, '/&quot;,&#10;')"/>
			<xsl:value-of select="concat('urlArgs:&quot;', $cacheBuster, '&quot;&#10;')"/>
			<xsl:text>};&#10;wcconfig = {"wc/xml/xslTransform": {</xsl:text>
			<xsl:value-of select="concat('xslEngine:&quot;', system-property('xsl:vendor'), '&quot;,&#10;')"/>
			<!-- Used for testing purposes -->
			<xsl:value-of select="concat('xslUrl:&quot;', normalize-space($xslPath), '&quot;')"/>
			<xsl:text>},&#10;"wc/i18n/i18n": {</xsl:text>
			<xsl:value-of select="concat('i18nBundleUrl:&quot;', normalize-space($xslPath), '&quot;,')"/>
			<xsl:value-of select="concat('locale:&quot;', normalize-space($locale), '&quot;')"/>
			<xsl:text>},&#10;"wc/loader/resource": {</xsl:text>
			<xsl:value-of select="concat('xmlBaseUrl:&quot;', normalize-space($resourceRoot), '${xml.target.dir.name}/&quot;,&#10;')"/>
			<xsl:value-of select="concat('cachebuster:&quot;', $cacheBuster, '&quot;')"/>
			<xsl:text>},&#10;"wc/loader/style":{</xsl:text>
			<xsl:value-of select="concat('cssBaseUrl:&quot;', normalize-space($resourceRoot), '${css.target.dir.name}/&quot;,&#10;')"/>
			<xsl:value-of select="concat('cachebuster:&quot;', $cacheBuster, '&quot;')"/>
			<xsl:if test="$isDebug=1">
				<xsl:text>,debug:1</xsl:text>
			</xsl:if>
			<xsl:call-template name="styleLoaderConfig"/>
			<xsl:text>}</xsl:text>
			<xsl:call-template name="localConfig" />
			<xsl:text>};&#10;</xsl:text>
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
				if(window.requirejs) window.requirejs.config({"config":{"wc/compat/navigationTiming":{"timing": timing}}});
			};
		wcconfig["wc/compat/navigationTiming"] = {"timing": timing};
	}
	catch(ex){}
	config.config = wcconfig;
	if(window.requirejs) window.requirejs.config(config);
	else require = config;
})();</xsl:text>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
