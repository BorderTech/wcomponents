<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.root.variables.xsl"/>
	<xsl:import href="wc.ui.root.n.styleLoaderConfig.xsl"/>
	<xsl:import href="wc.ui.root.n.localConfig.xsl"/>
	<xsl:import href="wc.ui.root.n.tinyMCEConfig.xsl"/>
	<!--
		Constructs the require config which is necessary to commence inclusion
		and bootstrapping of WComponent JavaScript. This must be included before
		a script element to include require.js (or whichever AMD loader you are
		using).

		You really don't want to be here.
	-->
	<xsl:template name="makeRequireConfig">
		<script type="text/javascript" class="wcconfig"><!-- todo: I think this class is no longer required -->
			<!-- Yes, we are defining a global require here. We are not using window.require = {} because the doco says this can cause problems in IE. -->
			<xsl:text>(function(){
	var wcconfig, timing,
		config = {
					paths: {
						tinyMCE: "lib/tinymce/tinymce.min",
						Promise: "lib/Promise.min",
						fabric: "lib/fabric",
						ccv: "lib/ccv",
						face: "lib/face",
						tracking: "lib/tracking/build/tracking-min",
						getUserMedia: "lib/getusermedia-js/getUserMedia.min",
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
						tracking: {
							exports: "tracking"
						},
						Promise: {
							exports: "Promise"
						},
						fabric: {
							exports: "fabric"
						},
						ccv: {
							exports: "ccv"
						},
						face: {
							exports: "cascade"
						},
						getUserMedia: {
							exports: "getUserMedia"
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
			<xsl:value-of select="concat('baseURL:&quot;', normalize-space($resourceRoot), $scriptDir, '/&quot;,&#10;')"/>
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
			<xsl:if test="number($isDebug) eq 1">
				<xsl:text>,debug:1</xsl:text>
			</xsl:if>
			<xsl:call-template name="styleLoaderConfig"/>
			<xsl:text>}</xsl:text>
			<xsl:call-template name="tinyMCEConfig"/>
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
				if(window.requirejs &amp;&amp; window.requirejs.config) window.requirejs.config({"config":{"wc/compat/navigationTiming":{"timing": timing}}});
			};
		wcconfig["wc/compat/navigationTiming"] = {"timing": timing};
		wcconfig["wc/config"] = { "dehydrated": JSON.stringify(wcconfig) };
	}
	catch(ex){}
	config.config = wcconfig;
	if(window.SystemJS) {
				wcconfig.meta = { "*": { format: "amd", scriptLoad: false } };
				wcconfig.packages = { ".": { defaultExtension: "js" } };
				window.SystemJS.pluginFirst = wcconfig.pluginFirst = true;
				window.SystemJS.defaultJSExtensions = config.defaultJSExtensions = true;
				window.SystemJS.config(config);
	}
	else if(window.requirejs) window.requirejs.config(config);
	else require = config;
})();</xsl:text>
		</script>
	</xsl:template>
</xsl:stylesheet>
