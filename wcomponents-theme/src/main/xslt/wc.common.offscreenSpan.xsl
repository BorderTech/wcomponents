<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Output an out of viewport span
		param text The text content of the off screen span.
		param [class] Optional extra class(es) to add to the span.
	-->
	<xsl:template name="offscreenSpan">
		<xsl:param name="text"/>
		<xsl:param name="class"/>
		<xsl:if test="$text!=''">
			<span>
				<xsl:attribute name="class">
					<xsl:text>wc_off</xsl:text>
					<xsl:if test="$class!=''">
						<xsl:value-of select="concat(' ', $class)"/>
					</xsl:if>
				</xsl:attribute>
				<xsl:value-of select="concat(' ',$text)"/>
			</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
