<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x="https://github.com/bordertech/wcomponents/namespace/ui/dummy"
	xmlns:doc="http://www.oxygenxml.com/ns/doc/xsl"
	version="2.0">
	<!--
		Part of building the output is to build xslt. We cannot output xsl:stylesheet directly from within an
		xsl:stylesheet. We could build an xsl:element name="stylesheet" with a namespace but we need to add other
		namespace attributes and one cannot use xsl:attribute with a name of xmlns or xmlns:foo. Therefore we have to
		write an xsl:stylesheet element directly to the output stream. We cannot have an xsl:stylesheet within an
		xsl:stylesheet so we have to use a namespace alias to do this for us.
	-->
	<xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl" />

	<!-- The final output is another xslt stylesheet so needs to be xml. The indent is so that the debug version is human readable.-->
	<xsl:output method="xml" indent="yes" omit-xml-declaration="no" />

	<!-- Generic match template for all unmatches nodes and attributes. Copies the input to the output. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<!--
		Output the value of the text node. We use the priority here to prevent processor warnings about ambiguous
		matches caused by the generic @*|node() match above. xsl:value-of is more efficient for text nodes than copy and
		apply-templates.
	-->
	<xsl:template match="text()" priority="5">
		<xsl:value-of select="." />
	</xsl:template>

	<!--
		The interim XML file has a root element of concat. This transforms to the required output xsl:stylesheet
		structure using the namespace alias defined above.
	-->
	<xsl:template match="concat">
		<x:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml"
					  version="2.0" exclude-result-prefixes="xsl ui html doc">
			<x:output encoding="UTF-8" indent="no" method="html" doctype-system="about:legacy-compat" omit-xml-declaration="yes" />
			<x:strip-space elements="*" />
			<xsl:apply-templates select=".//xsl:param[parent::xsl:stylesheet]" />
			<xsl:apply-templates select=".//xsl:variable[parent::xsl:stylesheet]" />
			<xsl:apply-templates select=".//xsl:template" />
		</x:stylesheet>
	</xsl:template>

	<!-- Remove all XSLT documentation elements.
		<xsl:template match="doc:*" /> -->
</xsl:stylesheet>
