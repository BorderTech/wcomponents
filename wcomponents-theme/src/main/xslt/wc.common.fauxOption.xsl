<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	
	<xsl:template name="fauxOption">
		<xsl:param name="value" select="@value"/>
		<xsl:element name="li">
			<xsl:attribute name="data-wc-value">
				<xsl:value-of select="$value" />
			</xsl:attribute>
			<xsl:attribute name="role">
				<xsl:text>option</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="tabindex">
				<xsl:text>0</xsl:text>
			</xsl:attribute>
			<xsl:value-of select="$value"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
