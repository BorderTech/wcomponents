<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		Output an out of viewport span for accessible column headings
		param text The text content of the off screen span.
	-->
	<xsl:template name="offscreenSpan">
		<xsl:param name="text"/>
		<xsl:param name="class"/>
		<xsl:if test="$text!=''">
			<xsl:element name="span">
				<xsl:attribute name="class">
					<xsl:text>wc_off</xsl:text>
					<xsl:if test="$class!=''">
						<xsl:value-of select="concat(' ', $class)"/>
					</xsl:if>
				</xsl:attribute>
				<xsl:value-of select="concat(' ',$text)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>