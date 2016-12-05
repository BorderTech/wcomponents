<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Output a source element inside an audio or video element.
	-->
	<xsl:template match="ui:src">
		<source src="{@uri}">
			<xsl:if test="@type">
				<xsl:attribute name="type">
					<xsl:value-of select="@type"/>
				</xsl:attribute>
			</xsl:if>
		</source>
	</xsl:template>
</xsl:stylesheet>
