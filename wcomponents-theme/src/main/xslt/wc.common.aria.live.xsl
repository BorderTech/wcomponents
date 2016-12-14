<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!-- This helper template sets attribute aria-live on many components. -->
	<xsl:template name="setARIALive">
		<xsl:param name="live" select="'polite'"/>
		<xsl:attribute name="aria-live">
			<xsl:value-of select="$live"/>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
