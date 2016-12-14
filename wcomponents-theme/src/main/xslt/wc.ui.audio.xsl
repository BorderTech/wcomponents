<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!-- Transform for WAudio. -->
	<xsl:template match="ui:audio">
		<span id="{@id}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="title"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>
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
				<xsl:apply-templates select="ui:src"/>
				<xsl:apply-templates select="ui:src" mode="link"/>
			</audio>
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
</xsl:stylesheet>
