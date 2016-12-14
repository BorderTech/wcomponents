<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- Transforms for WVideo. -->
	<xsl:template match="ui:video">
		<span id="{@id}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="title"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>
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
				<xsl:if test="not(@controls) or @controls ne 'none'">
					<xsl:if test="@autoplay"><!-- this is to avoid problems caused by not being able to switch off the media -->
						<xsl:attribute name="autoplay">
							<xsl:value-of select="@autoplay"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@loop"><!-- this is to avoid problems caused by not being able to switch off the media loop -->
						<xsl:attribute name="loop">
							<xsl:value-of select="@loop"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@muted"><!-- if no controls then cannot unmute -->
						<xsl:attribute name="muted">
							<xsl:value-of select="@muted"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:if test="@alt">
					<xsl:attribute name="data-wc-alt">
						<xsl:value-of select="@alt"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="not(@controls eq 'play' or @controls eq 'none')">
						<xsl:attribute name="controls">
							<xsl:text>controls</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="data-wc-mediacontrols">
							<xsl:value-of select="@controls"/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
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

				<xsl:apply-templates select="ui:src" mode="link"/>
				<xsl:if test="ui:src and ui:track">
					<xsl:element name="br"/>
				</xsl:if>
				<xsl:apply-templates select="ui:track" mode="link"/>
			</video>
			<xsl:if test="@controls eq 'play'">
				<button type="button" class="wc_btn_icon wc_av_play wc-invite" aria-pressed="false" aria-controls="{$mediaId}">
					<xsl:if test="not(@autoplay)">
						<!-- do not allow the button to be disabled if autoplay is on - the user MUST be able to stop/pause playback. -->
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
					</xsl:if>
					<span class="wc-off">
						<xsl:text>{{t 'media_play'}}</xsl:text>
					</span>
				</button>
			</xsl:if>
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
		<a href="{@src}" class="wc-track wc-icon">
			<xsl:if test="@lang">
				<xsl:attribute name="lang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="data-wc-attach">
				<xsl:text>data-wc-attach</xsl:text>
			</xsl:attribute>
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
