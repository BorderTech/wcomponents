<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Simple template for any component which requires a registration id list.
	-->
	<xsl:template match="*" mode="registerIds">
		<xsl:text>"</xsl:text>
		<xsl:choose>
			<xsl:when test="self::ui:tab">
				<xsl:value-of select="ui:tabcontent/@id"/>
			</xsl:when>
			<xsl:when test="self::ui:collapsible or self::ui:submenu">
				<xsl:value-of select="ui:content/@id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@id"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>"</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
