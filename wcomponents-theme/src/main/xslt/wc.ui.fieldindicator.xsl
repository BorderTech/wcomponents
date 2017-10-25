<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>
	<!--
		Transform for ui:fieldindicator which is output of WFieldWarningIndicator.
	-->
	<xsl:template match="ui:fieldindicator">
		<span>
			<xsl:if test="@id">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="icon">
				<xsl:with-param name="class">
					<xsl:choose>
						<xsl:when test="@type = 'warn'">fa-exclamation-triangle</xsl:when>
						<xsl:otherwise>fa-times-circle</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="ui:message" />
		</span>
	</xsl:template>
</xsl:stylesheet>
