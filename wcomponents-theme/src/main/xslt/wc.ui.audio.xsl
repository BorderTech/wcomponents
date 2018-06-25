
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- Transform for WAudio. -->
	<xsl:template match="ui:audio">
		<span class="{normalize-space(concat('wc-audio ', @class))}" id="{@id}">
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:variable name="mediaId" select="concat(@id, '_media')"/>
			<audio id="{$mediaId}">
				<xsl:attribute name="preload">
					<xsl:choose>
						<xsl:when test="@preload">
							<xsl:value-of select="@preload"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>auto</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="@mediagroup">
					<xsl:attribute name="mediagroup">
						<xsl:value-of select="@mediagroup"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="controls">
					<xsl:text>controls</xsl:text>
				</xsl:attribute>
				<xsl:if test="@autoplay">
					<xsl:attribute name="autoplay">
						<xsl:value-of select="@autoplay"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@loop">
					<xsl:attribute name="loop">
						<xsl:value-of select="@loop"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@muted">
					<xsl:attribute name="muted">
						<xsl:value-of select="@muted"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="ui:src"/>
				<xsl:apply-templates mode="link" select="ui:src"/>
			</audio>
		</span>
	</xsl:template>
</xsl:stylesheet>
