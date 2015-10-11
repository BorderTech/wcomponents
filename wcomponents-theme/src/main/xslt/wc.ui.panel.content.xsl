<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		ui:content child of a WPanel
	-->
	<xsl:template match="ui:content[parent::ui:panel]">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>content</xsl:text>
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
