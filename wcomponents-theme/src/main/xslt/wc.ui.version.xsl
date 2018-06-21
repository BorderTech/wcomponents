
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		Transform to output version information. Used as an aid for application debugging.

		Outputs the buildNumber constant. If the themes build number is not the same as the server version then the server version is also output.
    TO BE REMOVED : NOT PART OF SCHEMA, NEVER OFFICIAL.
	-->
	<xsl:template match="ui:version"/>
</xsl:stylesheet>
