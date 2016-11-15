<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:template name="mediaUnsupportedContent">
		<xsl:apply-templates select="ui:src" mode="link"/>
		<xsl:if test="ui:src and ui:track">
			<xsl:element name="br"/>
		</xsl:if>
		<xsl:apply-templates select="ui:track" mode="link"/>
	</xsl:template>
</xsl:stylesheet>
