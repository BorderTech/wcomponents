<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.buttonLink.drawButtonImage.xsl"/>
	<!--
		These templates output common aspects of wc.ui.button.xsl and
		wc.ui.link.xsl. Most of the attributes of wc.ui.button.xsl and
		wc.ui.link.xsl are common to both. In addition the content is identical
		in format. This template produces the common attributs and applies the
		content, including positioning the image element if imageUrl is set.
	-->
	<xsl:template name="buttonLinkCommon">
		<xsl:param name="imageAltText" select="''"/>
		<xsl:attribute name="id">
			<xsl:value-of select="@id"/>
		</xsl:attribute>

		<xsl:if test="@toolTip">
			<xsl:attribute name="title">
				<xsl:value-of select="normalize-space(@toolTip)"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:variable name="isControl">
			<xsl:choose>
				<xsl:when test="self::ui:link and (@type='button' or ui:windowAttributes[count(@*) &gt; 1])">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:when test="self::ui:link">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@disabled">
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="isControl" select="$isControl"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="parent::ui:action">
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="field" select="ancestor::ui:table[1]"/>
					<xsl:with-param name="isControl" select="1"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<xsl:call-template name="hideElementIfHiddenSet"/>
		<xsl:call-template name="ajaxController"/>
		<xsl:call-template name="ajaxTarget">
			<xsl:with-param name="live" select="'off'"/>
		</xsl:call-template>
		<xsl:call-template name="accessKey"/>
		<xsl:choose>
			<xsl:when test="@imageUrl">
				<xsl:choose>
					<xsl:when test="@imagePosition='n' or @imagePosition='w'">
						<xsl:call-template name="drawButtonImage">
							<xsl:with-param name="imageAltText" select="$imageAltText"/>
						</xsl:call-template>
						<xsl:apply-templates/>
					</xsl:when>
					<xsl:when test="@imagePosition">
						<xsl:apply-templates/>
						<xsl:call-template name="drawButtonImage">
							<xsl:with-param name="imageAltText" select="$imageAltText"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="drawButtonImage">
							<xsl:with-param name="imageAltText" select="$imageAltText"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
