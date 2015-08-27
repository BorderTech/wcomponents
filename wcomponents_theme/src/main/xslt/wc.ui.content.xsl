<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		ui:content is a child node of a number of components in its most basic form it
		merely passes through. Some components have their own content implementation:

		Generic template for unmoded content elements. Pass content through without any
		form of wrapper.
	-->
	<xsl:template match="ui:content">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
