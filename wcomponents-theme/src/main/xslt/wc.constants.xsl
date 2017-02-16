<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Generic utility templates.

		These are templates for text nodes, unmatched elements and unmatched attributes. You will often see example templates like this:

		``` xml
		<xsl:template match="*|@*|node()">
			<xsl:copy>
				<xsl:apply-templates select="@*|node()/>
			</xsl:copy
		</xsl:template>
		```

		whereas we split these into separate templates. This because in XSLT 2 a node with no children cannot apply templates. Using a single template
		like that above is also a performance issue, making a copy of a text node is slower than outputting its value.

		There is also one more caveat with element nodes:

		Template for unmatched elements. Make a copy of the element. We make an element using local-name() rather than the more obvious xsl:copy 
		because copy will retain the namespace attributes..
	-->
	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!--
		Unmatched attributes: make a copy of the attribute.
	-->
	<xsl:template match="@*">
		<xsl:copy />
	</xsl:template>

	<!--
		For text nodes we use value-of rather than apply-templates on text nodes as this provides improved performance. This is actually redundant as
		it is the default rule but I have seen too many variations on `match node(), copy, apply templates` as in the above comment to leave this to
		chance!
	-->
	<xsl:template match="text()">
		<xsl:value-of select="."/>
	</xsl:template>
</xsl:stylesheet>
