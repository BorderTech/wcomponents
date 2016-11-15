<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		This helper template sets attribute aria-label on many components and must never be excluded.
	-->
	<xsl:template name="ariaLabel">
		<xsl:if test="@accessibleText">
			<xsl:attribute name="aria-label">
				<xsl:value-of select="@accessibleText"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
