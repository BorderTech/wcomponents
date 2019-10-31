
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">

	<xsl:template match="ui:skiplinks">
		<nav class="wc-skiplinks" aria-hidden="true"></nav>
	</xsl:template>
</xsl:stylesheet>
