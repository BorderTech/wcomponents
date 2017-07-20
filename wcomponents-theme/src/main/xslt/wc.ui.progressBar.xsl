<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- Transform for WProgressBar is a progress element. -->
	<xsl:template match="ui:progressbar">
		<progress>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:call-template name="title"/>
			<xsl:call-template name="ariaLabel"/>
			<xsl:attribute name="value">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
			<xsl:attribute name="max">
				<xsl:value-of select="@max"/>
			</xsl:attribute>
		</progress>
	</xsl:template>
</xsl:stylesheet>
