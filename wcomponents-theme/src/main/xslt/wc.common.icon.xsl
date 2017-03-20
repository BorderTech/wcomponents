<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!-- Output an out of viewport span. -->
	<xsl:template name="icon">
		<xsl:param name="class" select="''"/>
		<span aria-hidden="true">
			<xsl:attribute name="class">
				<xsl:text>fa</xsl:text>
				<xsl:if test="normalize-space($class) ne ''">
					<xsl:value-of select="concat(' ', normalize-space($class))"/>
				</xsl:if>
			</xsl:attribute>
		</span>
	</xsl:template>
</xsl:stylesheet>
