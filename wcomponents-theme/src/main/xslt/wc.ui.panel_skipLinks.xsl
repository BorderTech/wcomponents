<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Make the skipLink links to panels which have accessKey and title attributes set.
	-->
	<xsl:template match="ui:panel" mode="skiplinks">
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:text>#</xsl:text>
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:value-of select="concat($$${wc.ui.skiplink.i18n.prefix}, @title)"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
