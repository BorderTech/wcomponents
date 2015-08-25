<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:template name="getHVGap">
		<xsl:param name="gap" select="@hgap"/>
		<xsl:param name="divisor" select="1"/>
		<xsl:choose>
			<xsl:when test="$gap">
				<xsl:variable name="px" select="format-number($gap,'0')"/>
				<xsl:value-of select="$px div $divisor"/>
				<xsl:text>px</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>