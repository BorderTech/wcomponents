
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- Transforms for WVideo. -->
	<xsl:template match="ui:video">
		<span class="{normalize-space(concat('wc-video ', @class))}" id="{@id}">
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
			<video id="{$mediaId}">
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
				<xsl:if test="@alt">
					<xsl:attribute name="data-wc-alt">
						<xsl:value-of select="@alt"/>
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
				<xsl:if test="@poster">
					<xsl:attribute name="poster">
						<xsl:value-of select="@poster"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@width">
					<xsl:attribute name="width">
						<xsl:value-of select="@width"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@height">
					<xsl:attribute name="height">
						<xsl:value-of select="@height"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="ui:src"/>
				<xsl:apply-templates select="ui:track"/>
				<xsl:apply-templates mode="link" select="ui:src"/>
				<xsl:if test="ui:src and ui:track">
					<xsl:element name="br"/>
				</xsl:if>
				<xsl:apply-templates mode="link" select="ui:track"/>
			</video>
		</span>
	</xsl:template>

	<!--
		Output a track element inside a video element.
	-->
	<xsl:template match="ui:track">
		<track src="{@src}">
			<xsl:if test="@lang">
				<xsl:attribute name="srclang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@desc">
				<xsl:attribute name="label">
					<xsl:value-of select="@desc"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@kind">
				<xsl:attribute name="kind">
					<xsl:value-of select="@kind"/>
				</xsl:attribute>
			</xsl:if>
		</track>
	</xsl:template>

	<!--
	 Output an A element linking to a track file.
	-->
	<xsl:template match="ui:track" mode="link">
		<a href="{@src}" class="wc-track">
			<xsl:if test="@lang">
				<xsl:attribute name="lang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="data-wc-attach">
				<xsl:text>data-wc-attach</xsl:text>
			</xsl:attribute>
			<i aria-hidden="true" class="fa fa-fw fa-file-text"></i>
			<xsl:if test="@desc">
				<xsl:value-of select="@desc"/>
			</xsl:if>
			<xsl:if test="@kind">
				<xsl:text> (</xsl:text>
				<xsl:value-of select="@kind"/>
				<xsl:text> )</xsl:text>
			</xsl:if>
		</a>
		<xsl:if test="position() ne last()">
			<xsl:value-of select="' '"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
