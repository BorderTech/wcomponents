<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.root.variables.xsl"/>
	<!--
		IE 8 and below needs a helper to recognise HTML5 elemnts as HTML
		elements. This needs to happen so very early that we cannot use
		require to load it. We can use an IE conditional comment to limit
		this code to IE8 and before. We have tested for IE so that we
		do not event output the conditional comment in other browsers. It still
		needs to be in a conditional comment so that we do not apply to IE > 8.
	-->
	<xsl:template name="makeIE8CompatScripts">
		<xsl:comment>[if lte IE 8] &gt;
&lt;script type="text/javascript"&gt;
(function(){
	var i, el=["details","datalist","aside","dialog","summary","section","header","nav","footer","meter","output","progress","audio","video","source","time","track","figcaption","figure"];
	if (window.require &amp;&amp; require.config) require.config["wc/fix/html5Fix_ie8"] = { elements: el };
	for (i = 0; i &lt; el.length; i++){ document.createElement(el[i]); } })();
&lt;/script&gt;
&lt;![endif]</xsl:comment>
	</xsl:template>
</xsl:stylesheet>
