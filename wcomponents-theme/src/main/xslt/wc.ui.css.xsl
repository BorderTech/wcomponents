<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">

	<xsl:template match="ui:css"/>

	<xsl:template match="ui:css" mode="inHead">
		<xsl:element name="link">
			<xsl:attribute name="href">
				<xsl:value-of select="@url"/>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:text>text/css</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="rel">
				<xsl:text>stylesheet</xsl:text>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
