<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">

	<xsl:template name="gapClass">
		<xsl:param name="gap" select="''"/>
		<xsl:param name="isVGap" select="0"/>
		<xsl:if test="$gap != ''">
			<xsl:text> wc-</xsl:text><!-- leading space is important -->
			<xsl:choose>
				<xsl:when test="number($isVGap) eq 1">
					<xsl:text>v</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>h</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>gap-</xsl:text>
			<xsl:value-of select="$gap"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
