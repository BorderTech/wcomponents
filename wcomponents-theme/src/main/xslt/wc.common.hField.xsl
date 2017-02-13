<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!-- See https://github.com/BorderTech/wcomponents/issues/261 -->
	<xsl:template name="hField">
		<xsl:param name="name" select="@id"/>
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:text>hidden</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="concat($name,'-h')"/>
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:text>x</xsl:text>
			</xsl:attribute>
			<xsl:if test="@disabled">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
