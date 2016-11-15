<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Output an out of viewport span
		param text The text content of the off screen span.
		param [class] Optional extra class(es) to add to the span.
	-->
	<xsl:template name="offscreenSpan">
		<xsl:param name="text" select="''"/>
		<xsl:param name="class" select="''"/>
		<xsl:if test="normalize-space($text) ne ''">
			<span>
				<xsl:attribute name="class">
					<xsl:text>wc-off</xsl:text>
					<xsl:if test="normalize-space($class) ne ''">
						<xsl:value-of select="concat(' ', normalize-space($class))"/>
					</xsl:if>
				</xsl:attribute>
				<xsl:value-of select="concat(' ',normalize-space($text))"/>
			</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
