<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.icon.xsl"/>

	<xsl:template match="ui:diagnostic">
		<span>
			<xsl:attribute name="id">
				<xsl:value-of select="../@id"/>
				<xsl:choose>
					<xsl:when test="@type = 'warn'">_warn</xsl:when>
					<xsl:otherwise>_err</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="icon">
				<xsl:with-param name="class">
					<xsl:choose>
						<xsl:when test="@type = 'warn'">fa-exclamation-triangle</xsl:when>
						<xsl:otherwise>fa-times-circle</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates />
		</span>
	</xsl:template>
	
	<xsl:template match="ui:diagnostic/ui:message">
		<span>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:apply-templates />
		</span>
	</xsl:template>
</xsl:stylesheet>
