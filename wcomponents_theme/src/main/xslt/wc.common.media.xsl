<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.common.media.n.mediaUnsupportedContent.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		Transforms for ui:audio from WAudio and ui:video from WVideo and their children.
		
		The media elements are output as a HTML SPAN element containing a HTML5 AUDIO or
		VIDEO element. The native player's capabilities depend upon the user agent
		employed. Where no support is available or the media is not able to be played
		then a link will be created to each source and track.
	-->
	<xsl:template match="ui:audio|ui:video">
		<xsl:variable name="elementType">
			<xsl:choose>
				<xsl:when test="self::ui:audio">
					<xsl:text>${wc.dom.html5.element.audio}</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>${wc.dom.html5.element.video}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<span id="{@id}">
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="normalize-space(@toolTip)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:if test="$isDebug=1">
				<xsl:variable name="warningMessage">
					<xsl:if test="@controls='none' and @autoplay">
						<xsl:text>Autoplay is not universally supported and there is no other way to make this media play. A media element without controls may present an accessibility issue. </xsl:text>
					</xsl:if>
					<xsl:if test="@loop and not(@controls='none')">
						<xsl:text>Loop may cause accessibility, usability or performance issues and should be avoided. </xsl:text>
					</xsl:if>
				</xsl:variable>
				<xsl:variable name="errorMessage">
					<xsl:if test="@controls='none' and not(@autoplay)">
						<xsl:text>This media element has no way to make it play. </xsl:text>
					</xsl:if>
					<xsl:if test="@loop and @controls='none'">
						<xsl:text>Specifying loop with no controls causes a failure of an accessibility guideline and should be avoided. </xsl:text>
					</xsl:if>
				</xsl:variable>

				<xsl:call-template name="debugAttributes"/>
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
					<xsl:with-param name="testForNoInteractive">
						<xsl:choose>
							<xsl:when test="@controls='none'">
								<xsl:number value="0"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="otherDebugText">
						<xsl:if test="@autoplay and not(@controls='none')">
							<xsl:text>Autoplay is not universally supported; this media may not be able to be played.</xsl:text>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:if test="$errorMessage!=''">
					<xsl:call-template name="makeDebugAttrib-debug">
						<xsl:with-param name="name" select="'data-wc-debugerr'"/>
						<xsl:with-param name="text" select="$errorMessage"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="$warningMessage!=''">
					<xsl:call-template name="makeDebugAttrib-debug">
						<xsl:with-param name="name" select="'data-wc-debugwarn'"/>
						<xsl:with-param name="text" select="$warningMessage"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:if>
			<xsl:element name="{$elementType}">
				<xsl:if test="@autoplay and not(@controls='none')"><!-- this is to avoid problems caused by not being able to switch off the media -->
					<xsl:attribute name="autoplay">
						<xsl:value-of select="@autoplay"/>
					</xsl:attribute>
				</xsl:if>
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
				<xsl:if test="@loop and not(@controls='none')"><!-- this is to avoid problems caused by not being able to switch off the media loop -->
					<xsl:attribute name="loop">
						<xsl:value-of select="@loop"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@muted and not(@controls='none')"><!-- if no controls then cannot unmute -->
					<xsl:attribute name="muted">
						<xsl:value-of select="@muted"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@alt">
					<xsl:attribute name="${wc.ui.media.attrib.alt}">
						<xsl:value-of select="@alt"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="not(@controls='play' or @controls='none')">
						<xsl:attribute name="controls">
							<xsl:text>controls</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="${wc.ui.media.attrib.lameControls}">
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
				<xsl:call-template name="mediaUnsupportedContent"/>
			</xsl:element>
		</span>
	</xsl:template>
</xsl:stylesheet>
