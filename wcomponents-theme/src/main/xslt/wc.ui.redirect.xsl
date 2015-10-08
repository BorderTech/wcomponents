<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Client redirects
	
		Transform for a server initiated redirect. This is a client side redirect and
		is a bit clunky. You would hope the application would be able to work out real
		URLs without having to send redirects. This will cause an immediate redirect so
		we simply output the required JavaScript inline.
	
		Null template as ui:redirect has no artifact in the UI.
	-->
	<xsl:template match="ui:redirect"/>
	
	<!--
		Template match="ui:redirect" mode="JS"
		Writes a script element with a script to cause an immediate redirect in
		the top level window.
 	-->
	<xsl:template match="ui:redirect" mode="JS">
		<xsl:text>"</xsl:text>
		<xsl:value-of select="@url" />
		<xsl:text>"</xsl:text>
	</xsl:template>
</xsl:stylesheet>
