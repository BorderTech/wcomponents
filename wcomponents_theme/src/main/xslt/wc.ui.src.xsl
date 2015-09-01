<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Output a source element inside an audio or video element.
	-->
	<xsl:template match="ui:src">
		<xsl:element name="${wc.dom.html5.element.source}">
			<xsl:attribute name="src">
				<xsl:value-of select="@uri"/>
			</xsl:attribute>
			<xsl:if test="@type">
				<xsl:attribute name="type">
					<xsl:value-of select="@type"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
