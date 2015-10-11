<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Transform to output version information. Used as an aid for application debugging.
		
		
		Outputs the buildNumber constant. If the themes build number is not the same
		as the server version then the server version is also output.
	-->
	<xsl:template match="ui:version">
		<xsl:variable name="buildNumber">
			<xsl:text>${build.number}</xsl:text>
		</xsl:variable>
		<xsl:value-of select="$buildNumber"/>
		<xsl:variable name="serv" select="@server"/>
		<xsl:if test="$serv and $serv != $buildNumber">
			<xsl:value-of select="concat(' (Server version: ',$serv,')')"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
