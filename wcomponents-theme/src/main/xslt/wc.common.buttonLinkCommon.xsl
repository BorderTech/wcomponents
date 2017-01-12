<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>

	<xsl:template name="buttonLinkCommonAttributes">
		<xsl:param name="elementType" select="'button'"/>
		<xsl:param name="class" />
		<xsl:call-template name="commonAttributes">
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

	<!--
		Helper template to draw an img element in a WButton or WLink.
		
		If the component has an imageUrl attribute but no imagePosition then the img element will need an alt attribute. This is set to the text
		content. If this is not set then we will not be able to determine an appropriate alt attribute programatically and will be potentially
		inaccessible. NOTE: it is not appropriate to use the control's toolTip property as the alt text of an image as this serves a different
		purpose.
		
		When the control contains a text node and has @imagePosition set then the alt attribute will be an empty string as the text node will explain
		the control and the image is deemed to be decorative.
	-->
	<xsl:template name="drawButtonImage">
		<xsl:param name="imageAltText" select="''"/>
		<xsl:variable name="text">
			<xsl:value-of select="text()"/>
		</xsl:variable>
		<xsl:element name="img"><!-- shorttag, do not simplify -->
			<xsl:attribute name="src">
				<xsl:value-of select="@imageUrl"/>
			</xsl:attribute>
			<xsl:attribute name="alt">
				<xsl:choose>
					<xsl:when test="$imageAltText ne ''">
						<xsl:value-of select="$imageAltText"/>
					</xsl:when>
					<xsl:when test="$text ne ''">
						<xsl:choose>
							<!-- When a button or link contains a text node and has imagePosition then the alt attribute will be an empty string as
								the text node will explain the button and the image is deemed to be decorative -->
							<xsl:when test="@imagePosition">
								<xsl:value-of select="''"/>
							</xsl:when>
							<!-- when the button has text content which is not output then the alt attribute will be that content -->
							<xsl:otherwise>
								<xsl:value-of select="$text"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
