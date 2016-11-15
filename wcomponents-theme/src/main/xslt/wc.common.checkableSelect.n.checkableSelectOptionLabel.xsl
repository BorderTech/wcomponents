<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Writes the content of the label for each option in the checkable group.
	-->
	<xsl:template name="checkableSelectOptionLabel">
		<xsl:choose>
			<xsl:when test="normalize-space(.)">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:when test="@value">
				<xsl:value-of select="@value"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>{{t 'requiredLabel'}}</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
