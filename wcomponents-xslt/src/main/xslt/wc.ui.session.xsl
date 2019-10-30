
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">

	<xsl:template match="ui:session[count(.|((//ui:session)[1])) eq 1]">
		<div id="wc_session_container" class="wc_session" role="alert" hidden="hiddden" aria-live="polite"></div>
	</xsl:template>

	<xsl:template match="ui:session"/>
</xsl:stylesheet>
