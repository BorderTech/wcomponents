
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template name="margin">
		<xsl:param name="gap" select="''"/>
		<xsl:param name="extension"/>
		<xsl:if test="$gap ne ''">
			<xsl:value-of select="concat(' wc-margin-', $extension, $gap)"/>
		</xsl:if>
	</xsl:template>

	<!--
		null template for margin without a mode. This is for safer use of
		apply-templates.
	-->
	<xsl:template match="ui:margin"/>

	<xsl:template match="ui:margin" mode="asclass">
		<xsl:choose>
			<xsl:when test="@all">
				<xsl:call-template name="margin">
					<xsl:with-param name="gap" select="@all"/>
					<xsl:with-param name="extension" select="'all-'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="@north">
					<xsl:call-template name="margin">
						<xsl:with-param name="gap" select="@north"/>
						<xsl:with-param name="extension" select="'n-'"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="@east">
					<xsl:call-template name="margin">
						<xsl:with-param name="gap" select="@east"/>
						<xsl:with-param name="extension" select="'e-'"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="@south">
					<xsl:call-template name="margin">
						<xsl:with-param name="gap" select="@south"/>
						<xsl:with-param name="extension" select="'s-'"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="@west">
					<xsl:call-template name="margin">
						<xsl:with-param name="gap" select="@west"/>
						<xsl:with-param name="extension" select="'w-'"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
