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

	<!-- The final output is another xslt stylesheet so needs to be xml. The indent is so that the debug version is
		human readable.-->
	<xsl:output method="xml" indent="yes" omit-xml-declaration="no" />

	<!--
		Used to flag that we should include all xsl:includes in the output. This currently only includes the debug info
		xslt but that may change. This should be set explicitly to something other than '1' for building the debug
		mode XSLT (debug.xsl).
	-->
	<xsl:param name="includeIncludes" select="'1'" />

	<!-- Generic match template for all unmatches nodes and attributes. Copies the input to the output. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<!--
		Null template used to remove the priority attribute from templates, to completely ignore the xsl:stylesheet
		elements and to remove other extraneous gubbins.

		Since the xsl:stylesheets are the child nodes of the concat this effectively removes inline template matching.
		Why do we do this? I am glad you asked. We want to output the xslt in a particular order and only the template
		with the highest priority for any match&mode/match without mode/name xsl:template grouping (eg a group is all
		templates with the same name or with the same match and mode).

		The priority is required for determining which version of a named template to output (when overridden in an
		implementation) but priority on a named template causes errors in some XSLT2 processors so should be removed and
		since we are here we may as well remove the priority from matched templates because we only output the one with
		the highest priority and it saves us a few bytes.

		If you have a legitimate need for prioritised matches you will have to completely change the way we concatenate
		the source XSLT.

		The rest of the stuff removed are things which are in the source for completeness and to help XSLT aware IDEs
		work with content completion, error checking etc. We rebuild any which are necessary in the concat match (see
		below).

		The priority on this template prevents ambiguous match issues with @*|node().
	-->
	<xsl:template match="@priority|xsl:stylesheet|xsl:import|xsl:include|xsl:strip-space|xsl:output|processing-instruction()|comment()" priority="2" />

	<!--
		Output the value of the text node. We use the priority here to prevent processor warnings about ambiguous
		matches caused by the generic @*|node() match above. xsl:value-of is more efficient for text nodes than copy and
		apply-templates.
	-->
	<xsl:template match="text()" priority="5">
		<xsl:value-of select="." />
	</xsl:template>

	<!--
		Transform to convert interim XML file of concatenated XSLT source to a single, final, well formed XSLT.

		We output the components of the source XSLT in the follwoing order (which is not actually important but I like
		to be neat):

		1. Any xsl:param elements which are immediate child elements of an xsl:stylesheet element. These are global
		params;

		2. All xsl:key elements;

		3. Any xsl:variable elements which are immediate child elements of an xsl:stylesheet element. These are
		global variables;

		4. All xsl:templates with a match but no mode. Where more than one template has the same match only the one with
		the highest priority will be output;

		5. All xsl:templates with a match and a mode. Where more than one template has the same match and mode only the
		one with the highest priority will be output;

		6. All xsl:templates with a name. Where more than one template has the same name only the one with the highest
	 	priority will be output (the priority is then stripped for notional XSLT2 compliance).

		This does the heavy lifting. writeTemplates goes through the xsl:templates, groups them, then passes each group
		to writeLastTemplate.

		NOTE the use of xsl:for-each-group here. One really ought not be using this or xsl:for-each as they are extremely
		inefficient. Avoid this in actual client code but this is just part of a build so we don't care too	much and it
		is the easiest way to get the desired result.
	-->
	<xsl:template name="writeTemplates">
		<xsl:variable name="matchNotMode" select=".//xsl:template[@match and not(@mode)]" />
		<xsl:for-each-group select="$matchNotMode" group-by="@match">
			<xsl:call-template name="writeLastTemplate">
				<xsl:with-param name="group" select="current-group()" />
			</xsl:call-template>
		</xsl:for-each-group>

		<xsl:variable name="matchAndMode" select=".//xsl:template[@match and @mode]" />
		<xsl:for-each-group select="$matchAndMode" group-by="@match">
			<xsl:for-each-group select="current-group()" group-by="@mode">
				<xsl:call-template name="writeLastTemplate">
					<xsl:with-param name="group" select="current-group()" />
				</xsl:call-template>
			</xsl:for-each-group>
		</xsl:for-each-group>

		<xsl:variable name="named" select=".//xsl:template[@name]" />
		<xsl:for-each-group select="$named" group-by="@name">
			<xsl:call-template name="writeLastTemplate">
				<xsl:with-param name="group" select="current-group()" />
			</xsl:call-template>
		</xsl:for-each-group>
	</xsl:template>


	<!--
		This helper takes a group as a param then sorts it by priority and writes the highest priority template in the
		group to the output stream.

		param group:  The result of calling current-group() on the grouped nodeLists generated in writeTemplates.
	-->
	<xsl:template name="writeLastTemplate">
		<xsl:param name="group" />
		<xsl:for-each select="$group">
			<xsl:sort select="@priority" />
			<xsl:if test="position()=last()">
				<xsl:element name="xsl:template">
					<xsl:apply-templates select="@*|node()" />
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
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
			<xsl:comment>
				<xsl:value-of select="system-property('xsl:vendor')" />
			</xsl:comment>
			<xsl:apply-templates select=".//xsl:param[parent::xsl:stylesheet]" />
			<xsl:apply-templates select=".//xsl:key[parent::xsl:stylesheet]" />
			<xsl:apply-templates select=".//xsl:variable[parent::xsl:stylesheet]" />
			<xsl:call-template name="writeTemplates" />
		</x:stylesheet>
	</xsl:template>

	<!-- Remove all XSLT documentation elements.
		<xsl:template match="doc:*" /> -->
</xsl:stylesheet>
