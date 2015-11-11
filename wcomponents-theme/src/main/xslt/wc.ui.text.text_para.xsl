<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.text.n.WStyledTextContent.xsl"/>
	<!--
		Manipulates text nodes based on ui:text space and type attributes.
		
		param space: The space attribute of the parent ui:text element.
		param type: The type attribute (if any) of the parent ui:text element.
		  Defaults to 'plain' if the type attribute is not set.
	-->
	<xsl:template match="text()" mode="para">
		<xsl:param name="type" select="'plain'"/>
		<xsl:param name="class"/>
		<xsl:element name="p">
			<xsl:attribute name="class">
				<xsl:value-of select="$class"/>
			</xsl:attribute>
			<xsl:call-template name="WStyledTextContent">
				<xsl:with-param name="type" select="$type"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
