<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="https://github.com/bordertech/wcomponents/namespace/ui/dummy" version="2.0">
	<xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl" />
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
	<xsl:param name="includeUrl" />
	<!-- To be injected by caller.
		Do NOT include a querystring - in order to make server side
		transforms feasible when the xsl is not fetched over http it
		is safer not to include a querystring.
	-->
	<xsl:template match="properties">
		<x:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml"
			version="1.0" exclude-result-prefixes="xsl ui html">
			<x:output encoding="UTF-8" indent="no" method="html" doctype-system="about:legacy-compat" omit-xml-declaration="yes" />
			<x:strip-space elements="*" />
			<xsl:apply-templates select="property" />
			<x:include href="{$includeUrl}" />
		</x:stylesheet>
	</xsl:template>

	<xsl:template match="property">
		<x:param name="{@name}">
			<x:text>
				<xsl:value-of select="@value" />
			</x:text>
		</x:param>
	</xsl:template>
</xsl:stylesheet>
