<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.getSpace.vars.xsl"/><!-- contains the gap limit definitions -->
	
	<xsl:template name="margin">
		<xsl:param name="gap" select="0"/>
		<xsl:param name="extension"/>
		<xsl:variable name="baseclass" select="' wc-margin-'"/>
		<xsl:if test="number($gap) ne 0">
			<xsl:text> wc-margin-</xsl:text>
			<xsl:value-of select="$extension"/>
			<xsl:call-template name="getSizeClassExtension">
				<xsl:with-param name="gap" select="number($gap)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="ui:margin" mode="class">
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
	
	<xsl:template match="ui:margin"/>
</xsl:stylesheet>
