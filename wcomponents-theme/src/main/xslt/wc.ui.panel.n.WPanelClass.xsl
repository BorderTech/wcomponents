<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl" />
	<xsl:import href="wc.common.n.className.xsl" />

	<xsl:template name="WPanelClass">
		<xsl:call-template name="commonClassHelper" />
		<xsl:if test="@type">
			<xsl:value-of select="concat(' ',@type)" />
		</xsl:if>
		<xsl:choose>
			<xsl:when test="(@mode='lazy' and @hidden)"><xsl:text> wc_magic</xsl:text></xsl:when>
			<xsl:when test="@mode='dynamic'"><xsl:text> wc_magic wc_dynamic</xsl:text></xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
