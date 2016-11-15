<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.buttonLink.drawButtonImage.xsl"/>
	
	<xsl:template name="buttonLinkCommonAttributes">
		<xsl:param name="elementType" select="'button'"/>
		<xsl:param name="class" />
		
		<xsl:call-template name="commonAttributes">
			<xsl:with-param name="live" select="'off'"/>
			<xsl:with-param name="isControl">
				<xsl:choose>
					<xsl:when test="$elementType eq 'button'">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="class">
				<xsl:value-of select="$class"/>
				<xsl:choose>
					<xsl:when test="@imagePosition">
						<xsl:value-of select="concat(' wc_btn_img', @imagePosition)"/><!-- no gap after _img -->
					</xsl:when>
					<xsl:when test="@imageUrl">
						<xsl:text> wc_nti</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>

		<xsl:call-template name="title"/>
		<xsl:call-template name="ajaxController"/>
		
		<xsl:variable name="linkHasPopup">
			<xsl:choose>
				<xsl:when test="ui:windowAttributes[count(@*) gt 1] or (@type eq 'button' and ui:windowAttributes)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:if test="@popup or parent::ui:dialog or number($linkHasPopup) eq 1">
			<xsl:attribute name="aria-haspopup">
				<xsl:copy-of select="$t"/>
			</xsl:attribute>

			<xsl:if test="number($linkHasPopup) eq 1">
				<xsl:attribute name="data-wc-specs">
					<xsl:apply-templates select="ui:windowAttributes" mode="specs"/>
				</xsl:attribute>
				<xsl:attribute name="data-wc-window">
					<xsl:value-of select="ui:windowAttributes/@name"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<!--
		These templates output common content of ui:button, ui:printButton and ui:link. Thi sincludes the accessKey toolTip
		as this is part attribute, part content. Therefore you **MUST** call this template before any other content but
		after all other attribtues.
		
		See wc.common.button.xsl and wc.ui.link.xsl. 
	-->
	<xsl:template name="buttonLinkCommonContent">
		<xsl:param name="imageAltText" select="''"/>
		<xsl:call-template name="accessKey"/>
		<xsl:choose>
			<xsl:when test="@imageUrl">
				<xsl:choose>
					<xsl:when test="@imagePosition and (@imagePosition eq 'n' or @imagePosition eq 'w')">
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
