
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:output encoding="UTF-8" indent="no" method="html"
		doctype-system="about:legacy-compat" omit-xml-declaration="yes" />
	<xsl:strip-space elements="*" />
	<xsl:include href="wc.html.ie9minus.xsl"/>

	<!--
		Debug flag. This is a global parameter as it is pulled out of the
		compressed XSLT and we do not want it renamed.
	-->
	<xsl:param name="isDebug" select="0" />

	<!--
		Used to calculate the path to the libs based on the stylesheet
		processing instruction stripped is used in the config object. Param so it can
		be overridden if necessary.
	-->
	<xsl:param name="xslPath"
		select="substring-before(replace(substring-after(//processing-instruction('xml-stylesheet'), 'href=&quot;'), '&amp;amp;', '&amp;'), '&quot;')" />

	<!--
		this is the absolute or server relative path to the resources used to
		build the site calculated from the XSLT
		processing instruction.
	-->
	<xsl:variable name="resourceRoot">
		<xsl:value-of select="substring-before($xslPath, 'xslt')" />
	</xsl:variable>

	<!--
		This string is used to build a query string on all resources requested
		as part of a page.
	-->
	<xsl:variable name="cacheBuster">
		<xsl:value-of select="substring-after($xslPath, '?')" />
	</xsl:variable>

	<xsl:template match="ui:root">
		<xsl:variable name="scriptDir">
			<xsl:choose>
				<xsl:when test="number($isDebug) eq 1">
					<xsl:text>scripts_debug</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>scripts</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="registeredComponents">
			<xsl:call-template name="registrationScripts" />
		</xsl:variable>

		<html lang="{@lang}">
			<head>
				<!-- Favicon works more reliably if it is first -->
				<xsl:choose>
					<xsl:when test="ui:application/@icon">
						<xsl:call-template name="faviconHelper">
							<xsl:with-param name="href">
								<xsl:value-of
									select="ui:application[@icon][1]/@icon" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when
						test="//html:link[@rel eq 'shortcut icon' or @rel eq 'icon']">
						<xsl:apply-templates
							select="//html:link[contains(@rel, 'icon')][1]" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="faviconHelper">
							<xsl:with-param name="href"
								select="concat($resourceRoot, 'resource/favicon.ico')"
							 />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				<!--
					The format-detection is needed to work around issues in some
					very popular mobile browsers that will convert "numbers"
					into phone links (a elements) if they appear to be phone
					numbers, even if those numbers are the content of buttons or
					links. This breaks important stuff if you, for example, want
					to link or submit using a number identifier.

					If you want a phone number link in these (or any) browser
					use WPhoneNumberField set to read-only.
				-->
				<meta name="format-detection" content="telephone=no" />
				<meta name="viewport" content="initial-scale=1" />
				<title>
					<xsl:value-of select="@title" />
				</title>

				<!--
					CSS before JavaScript for performance.
				-->
				<xsl:variable name="mainCssUrl">
					<xsl:call-template name="cssUrl">
						<xsl:with-param name="filename" select="'wc'" />
					</xsl:call-template>
				</xsl:variable>
				<!-- the id and data-attributes are used by the style loader js -->
				<link type="text/css" rel="stylesheet" id="wc_css_screen" data-wc-loader="style"
					href="{$mainCssUrl}" media="screen" />
				<xsl:if test="$isDebug = 1">
					<xsl:variable name="debugCssUrl">
						<xsl:call-template name="cssUrl">
							<xsl:with-param name="filename" select="'wcdebug'"
							 />
						</xsl:call-template>
					</xsl:variable>
					<link type="text/css" rel="stylesheet" href="{$debugCssUrl}" media="screen" data-wc-loader="style" />
				</xsl:if>
				<link type="text/css" rel="stylesheet" href="{concat($resourceRoot, 'resource/fontawesome/css/font-awesome.min.css')}" media="screen" />
				<!--
					We need to set up the require config very early. This mess
					constructs the require config which is necessary to commence
					inclusion and bootstrapping of WComponent JavaScript. This
					must be included before a script element to include
					require.js (or whichever AMD loader you are using).

					You really don't want to be here.
				-->
				<xsl:variable name="wcScriptDir"
					select="concat($scriptDir, '/wc')" />
				<xsl:variable name="libScriptDir"
					select="concat($scriptDir, '/lib')" />
				<script type="text/javascript">
					<!--
							Yes, we are defining a global require here.We are not
							using window.require = {
							}
							because the doco says this can
							cause problems in IE.//-->
					<xsl:text>(function(){
	var wcconfig, timing,
		config = {
				paths: {
					"wc": "</xsl:text><xsl:value-of select="$wcScriptDir" /><xsl:text>",
					"lib": "</xsl:text><xsl:value-of select="$libScriptDir" /><xsl:text>",
					"lib/sprintf": "</xsl:text><xsl:value-of select="$libScriptDir" /><xsl:text>/sprintf.min",
					"mailcheck": "</xsl:text><xsl:value-of select="$libScriptDir" /><xsl:text>/mailcheck",
					"tinyMCE": "</xsl:text><xsl:value-of select="$libScriptDir" /><xsl:text>/tinymce/tinymce.min",
					axs: "</xsl:text><xsl:value-of select="$libScriptDir" /><xsl:text>/axs_testing",
					axe: "</xsl:text><xsl:value-of select="$libScriptDir" /><xsl:text>/axe.min"
				},
				shim: {
					tinyMCE: {
						exports: "tinyMCE",
						init: function () {
							this.tinyMCE.DOM.events.domLoaded = true;
							return this.tinyMCE;
						}},
					axs: {exports: "axs"},
					axe: {exports: "axe"},
					mailcheck: {exports: "Mailcheck"}
				},
				deps:[],&#10;
			</xsl:text>
					<xsl:value-of select="concat('baseUrl:&quot;', normalize-space($resourceRoot), '&quot;,&#10;')" />
					<xsl:value-of select="concat('urlArgs:&quot;', $cacheBuster, '&quot;&#10;')" />
					<xsl:text>};&#10;wcconfig = {"wc/loader/resource": {</xsl:text>
					<xsl:value-of select="concat('resourceBaseUrl:&quot;', normalize-space($resourceRoot), 'resource/&quot;,&#10;')" />
					<xsl:value-of select="concat('cachebuster:&quot;', $cacheBuster, '&quot;')" />
					<xsl:text>}&#10;};&#10;</xsl:text>
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
				<script id="wc-config" type="application/json">
					<xsl:text>{ "wc/loader/resource": {</xsl:text>
					<xsl:value-of select="concat('&quot;resourceBaseUrl&quot;:&quot;', normalize-space($resourceRoot), 'resource/&quot;,&#10;')" />
					<xsl:value-of select="concat('&quot;cachebuster&quot;:&quot;', $cacheBuster, '&quot;')" />
					<xsl:text>}&#10;}&#10;</xsl:text>
				</script>
				<script type="importmap">
					<xsl:text>
					{
						"imports": {
							"wc/": "</xsl:text><xsl:value-of select="concat(normalize-space($resourceRoot), $wcScriptDir)" />/<xsl:text>",
							"fabric/": "</xsl:text><xsl:value-of select="concat(normalize-space($resourceRoot), $libScriptDir, '/fabric')" />/<xsl:text>",
							"mailcheck/": "</xsl:text><xsl:value-of select="concat(normalize-space($resourceRoot), $libScriptDir, '/mailcheck')" />/<xsl:text>",
							"tinymce/": "</xsl:text><xsl:value-of select="concat(normalize-space($resourceRoot), $libScriptDir, '/tinymce')" />/<xsl:text>",
							"socket.io/": "</xsl:text><xsl:value-of select="concat(normalize-space($resourceRoot), $libScriptDir, '/socket.io')" />/<xsl:text>",
							"i18next": "</xsl:text><xsl:value-of select="concat(normalize-space($resourceRoot), $libScriptDir, '/i18next/dist/esm/i18next.js')" /><xsl:text>",
							"sprintf-js/": "</xsl:text><xsl:value-of select="concat(normalize-space($resourceRoot), $libScriptDir, '/sprintf-js')" />/<xsl:text>"
						}
					}
					</xsl:text>
				</script>

				<script type="module" src="{concat($resourceRoot, $scriptDir, '/wc/common.mjs?', $cacheBuster)}" />

				<script type="module" class="registrationScripts">
					<xsl:text>import("wc/common.mjs").then(({ default: c }) => {</xsl:text>
					<xsl:if test="$registeredComponents ne ''">
						<xsl:value-of select="$registeredComponents" />
					</xsl:if>
					<xsl:text>import("wc/loader/style.mjs").then(({ default: s }) => {s.load();</xsl:text>
					<xsl:apply-templates select="ui:application/ui:css" mode="inHead" />
					<xsl:apply-templates select=".//html:link[@rel eq 'stylesheet']" mode="inHead" />
					<xsl:text>});</xsl:text><!--
						end style loader//-->
					<xsl:text>});</xsl:text><!--
						end common//-->
				</script>

				<!--
					We grab all base, meta and link elements from the content
					and place them in the head where they belong.
				-->
				<xsl:apply-templates select="ui:application/ui:js" mode="inHead" />
				<xsl:apply-templates
					select=".//html:base | .//html:link[not(contains(@rel, 'icon') or @rel eq 'stylesheet')] | .//html:meta"
					mode="inHead" />
			</head>
			<xsl:variable name="domready">
				<xsl:choose>
					<xsl:when test="$registeredComponents = ''">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<body data-wc-domready="{$domready}">
				<noscript>
					<p>You must have JavaScript enabled to use this application.</p>
				</noscript>
				<xsl:apply-templates>
					<xsl:with-param name="nojs">
						<xsl:choose>
							<xsl:when test="$registeredComponents ne ''">
								<xsl:number value="0" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="1" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:apply-templates>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="faviconHelper">
		<xsl:param name="href" select="''" />
		<xsl:if test="$href ne ''">
			<!-- rel value invalid but the only cross browser option -->
			<link rel="shortcut icon" href="{$href}" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="cssUrl">
		<xsl:param name="filename" />
		<xsl:value-of select="$resourceRoot" />
		<xsl:text>style/</xsl:text>
		<xsl:value-of select="$filename" />
		<xsl:text>.css?</xsl:text>
		<xsl:value-of select="$cacheBuster" />
	</xsl:template>

	<!--
		The root element of a response to an ajax request.
	-->
	<xsl:template match="ui:ajaxresponse">
		<xsl:choose>
			<xsl:when test="ui:ajaxtarget/node()[not(self::ui:file)]">
				<div class="wc-ajaxresponse">
					<xsl:if test="@defaultFocusId">
						<xsl:attribute name="data-focusid">
							<xsl:value-of select="@defaultFocusId" />
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates select="*" />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<html lang="en">
					<!-- The lang is hardcodeed but the pseudo ajax stuff is pretty much dead -->
					<head>
						<title>
							<xsl:text>Pseudo AJAX iframe</xsl:text>
						</title>
					</head>
					<body>
						<xsl:apply-templates mode="pseudoAjax" />
					</body>
				</html>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		ui:ajaxtarget is a child of ui:ajaxresponse.

		The main point of this template is a simple pass-through to output the
		contained elements and run the registration scripts to wire up new
		onload functionality.
	-->
	<xsl:template match="ui:ajaxtarget">
		<div class="wc-ajaxtarget" data-id="{@id}" data-action="{@action}">
			<xsl:apply-templates />
			<xsl:call-template name="registrationScripts" />
		</div>
	</xsl:template>

	<!--
		This mode is invoked in the faux-ajax used to do inline multifileupload.
	-->
	<xsl:template match="ui:ajaxtarget" mode="pseudoAjax">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Common bootstrapping scripts -->
	<xsl:template name="registrationScripts">
		<xsl:variable name="dataListCombos" select=".//ui:dropdown[@data and @type and not(@readOnly)] | .//ui:suggestions[@data]" />
		<xsl:variable name="dataListComponents"
			select=".//ui:dropdown[@data and not(@type) and not(@readOnly)] | .//ui:listbox[@data and not(@readOnly)] | .//ui:shuffler[@data and not(@readOnly)]" />
		<xsl:variable name="filedrops" select=".//ui:multifileupload[@ajax or @dropzone]" />
		<xsl:variable name="multiDDData" select=".//ui:multidropdown[@data and not(@readOnly)]" />
		<xsl:variable name="eagerness" select="//*[@mode eq 'eager']" />
		<xsl:variable name="editors" select=".//html:wc-imageedit" />
		<xsl:variable name="tableActions" select=".//ui:table/ui:actions/ui:action" />

		<xsl:variable name="libs">
			<!-- .mjs extension will be added to these -->
			<!--
				The following are in alphabetical order of local-name of the first element in the test, then in alphabetical order of attribute. Just
				because we think they are less common does not mean they should not be in wc.common.js.
			-->
			<xsl:if test=".//html:img[@data-wc-editor]">
				<xsl:text>"wc/ui/img",</xsl:text>
			</xsl:if>

			<!-- Autofocus fix -->
			<xsl:if test="$isDebug = 1">
				<xsl:text>"wc/debug/common",</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="normalizedLibs" select="normalize-space($libs)" />
		<xsl:variable name="rego">
			<xsl:if test="$tableActions">
				<xsl:text>import("wc/ui/table/action.mjs").then(({ default: c }) => {c.register([</xsl:text>
				<xsl:apply-templates select="$tableActions" mode="JS" />
				<xsl:text>]);});</xsl:text>
			</xsl:if>
			<xsl:if test="$editors">
				<xsl:text>import("wc/ui/imageEdit.mjs").then(({ default: c }) => {c.register([</xsl:text>
				<xsl:apply-templates select="$editors" mode="JS" />
				<xsl:text>]);});</xsl:text>
			</xsl:if>
			<xsl:if test="$dataListCombos">
				<xsl:text>import("wc/ui/comboLoader.mjs").then(({ default: c }) => {c.register([</xsl:text>
				<xsl:apply-templates select="$dataListCombos" mode="registerIds" />
				<xsl:text>]);});</xsl:text>
			</xsl:if>
			<xsl:if test="$dataListComponents">
				<xsl:text>import("wc/ui/selectLoader.mjs").then(({ default: c }) => {c.register([</xsl:text>
				<xsl:apply-templates select="$dataListComponents"
					mode="registerIds" />
				<xsl:text>]);});</xsl:text>
			</xsl:if>
			<xsl:if test="$filedrops">
				<xsl:text>import("wc/ui/multiFileUploader.mjs").then(({ default: c }) => {c.register([</xsl:text>
				<xsl:apply-templates select="$filedrops" mode="registerIds" />
				<xsl:text>]);});</xsl:text>
			</xsl:if>
			<xsl:if test="$multiDDData">
				<xsl:text>import("wc/ui/multiFormComponent.mjs").then(({ default: c }) => {c.register([</xsl:text>
				<xsl:apply-templates select="$multiDDData" mode="registerIds" />
				<xsl:text>]);});</xsl:text>
			</xsl:if>
			<xsl:if test="$eagerness">
				<xsl:text>import("wc/ui/containerload.mjs").then(({ default: c }) => {c.register([</xsl:text>
				<xsl:apply-templates select="$eagerness" mode="registerIds" />
				<xsl:text>]);});</xsl:text>
			</xsl:if>
			<xsl:if test="//@defaultFocusId">
				<xsl:text>import("wc/ui/onloadFocusControl.mjs").then(({ default: c }) => {c.register("</xsl:text>
				<xsl:value-of select="//@defaultFocusId[1]" />
				<xsl:text>");});</xsl:text>
			</xsl:if>
			<xsl:if test="$normalizedLibs ne ''">
				<xsl:text>[</xsl:text>
				<xsl:value-of
					select="substring($normalizedLibs, 1, string-length($normalizedLibs) - 1)" />
				<xsl:text>].forEach(dep => import(`${dep}.mjs`));</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$rego ne ''">
			<xsl:choose>
				<xsl:when test="self::ui:root">
					<xsl:value-of select="$rego" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="scriptId"
						select="concat('wcscript_', generate-id())" />
					<script type="text/javascript" class="registrationScripts" id="{$scriptId}">
						<xsl:text>import("wc/common.mjs").then(() => {</xsl:text>
						<xsl:value-of select="$rego" />
						<xsl:text>import("wc/dom/removeElement.mjs").then(({ default: r }) => { r("</xsl:text>
							<xsl:value-of select="$scriptId" />
						<xsl:text>", true);});</xsl:text>
						<xsl:text>});</xsl:text>
					</script>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!--
		Any component which requires a registration id list.
	-->
	<xsl:template match="*" mode="registerIds">
		<xsl:text>"</xsl:text>
		<xsl:choose>
			<xsl:when test="self::ui:tab">
				<xsl:value-of select="ui:tabcontent/@id" />
			</xsl:when>
			<xsl:when test="self::ui:collapsible or self::ui:submenu">
				<xsl:value-of select="ui:content/@id" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@id" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>"</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- templates which transform to nothing

		* Consume comments and do not pass them through
		* Remove link, base and meta elements completely from flow. These are
		  all hoisted into the HTML head element in ui:root.
		* Do not put a HTML form inside any WApplication.
		* ui:additionalParameters no longer used or part of the Java API but
		  still in the schema.
		* ui:analytic|ui:tracking|ui:debugInfo|ui:debug are no longer used.
		-->
	<xsl:template
		match="
			comment() | ui:comment | html:link | html:base | html:meta |
			html:form | ui:additionalParameters | ui:analytic | ui:tracking | ui:debugInfo |
			ui:debug" />

	<!--
		Generic utility templates.

		These are templates for text nodes, unmatched elements and unmatched attributes. You will often see example templates like this:

		``` xml
		<xsl:template match="*|@*|node()">
			<xsl:copy>
				<xsl:apply-templates select="@*|node()/>
			</xsl:copy
		</xsl:template>
		```

		whereas we split these into separate templates. This because in XSLT 2 a node with no children cannot apply templates. Using a single template
		like that above is also a performance issue, making a copy of a text node is slower than outputting its value.

		There is also one more caveat with element nodes:

		Template for unmatched elements. Make a copy of the element. We make an element using local-name() rather than the more obvious xsl:copy
		because copy will retain the namespace attributes..
	-->
	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!--
		Unmatched attributes: make a copy of the attribute.
	-->
	<xsl:template match="@*">
		<xsl:copy />
	</xsl:template>

	<!--
		For text nodes we use value-of rather than apply-templates on text nodes
		as this provides improved performance. This is actually redundant as it
		is the default rule, but I have seen too many variations on
		`match node(), copy, apply templates` as in the above comment to leave
		this to chance!
	-->
	<xsl:template match="text()">
		<xsl:value-of select="." />
	</xsl:template>


	<!--
		html:link can appear in a `ui:ajaxtarget` and in this case cannot be moved
		to a HEAD element, so we just output it in-situ.
	-->
	<xsl:template match="html:link[ancestor::ui:ajaxtarget]">
		<xsl:choose>
			<xsl:when test="@rel = 'stylesheet'">
				<script type="text/javascript">
					<xsl:text>import("wc/loader/style.mjs").then(({ default: s }) => {s.add("</xsl:text>
					<xsl:value-of select="@href" />
					<xsl:text>","</xsl:text>
					<xsl:if test="@media">
						<xsl:value-of select="@media" />
					</xsl:if>
					<xsl:text>", true);});</xsl:text>
				</script>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="." />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="html:link[@rel = 'stylesheet']" mode="inHead">
		<xsl:text>s.add("</xsl:text>
		<xsl:value-of select="@href" />
		<xsl:if test="@media">
			<xsl:text>","</xsl:text>
			<xsl:value-of select="@media" />
		</xsl:if>
		<xsl:text>");</xsl:text>
	</xsl:template>

	<!--
		Copy link, base and meta elements in the head.
		Copy without XML namespaces, prevents double output of XHTML
		self-closing elements.
	-->
	<xsl:template match="html:link | html:base | html:meta" mode="inHead">
		<xsl:element name="{local-name(.)}">
			<xsl:apply-templates select="@*" />
		</xsl:element>
	</xsl:template>

	<!--
		Copy without XML namespaces, prevents double output of XHTML
		self-closing elements.
	-->
	<xsl:template match="html:input | html:img | html:br | html:hr">
		<xsl:element name="{local-name(.)}">
			<xsl:apply-templates select="@*" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
