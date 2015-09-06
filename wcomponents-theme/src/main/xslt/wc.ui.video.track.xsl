<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Output a track element inside a video element.
	-->
	<xsl:template match="ui:track">
		<xsl:element name="${wc.dom.html5.element.track}">
			<xsl:attribute name="src">
				<xsl:value-of select="@src"/>
			</xsl:attribute>
			<xsl:if test="@lang">
				<xsl:attribute name="srclang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@desc">
				<xsl:attribute name="label">
					<xsl:value-of select="@desc"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@kind">
				<xsl:attribute name="kind">
					<xsl:value-of select="@kind"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
