<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Helper template to draw an img element in a WButton or WLink.
		
		If the component has an imageUrl attribute but no imagePosition then the img element will need an alt attribute.
		This is set to the text content. If this is	not set then we will not be able to determine an appropriate alt 
		attribute programatically and will be potentially inaccessible. NOTE: it is not appropriate to use the control's
		toolTip property as the alt text of an image as this serves a different purpose.
		
		When the control contains a text node and has @imagePosition set then the alt attribute will be an empty string 
		as the text node will explain the control and the image is deemed to be decorative.
		
		For more information on appropriate values of the alt attribute see 
		http://www.w3.org/TR/html5/embedded-content-0.html#alt
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
					<xsl:when test="$imageAltText!=''">
						<xsl:value-of select="$imageAltText"/>
					</xsl:when>
					<xsl:when test="$text!=''">
						<xsl:choose>
							<!-- When a button or link contains a text node and has imagePosition 
								then the alt attribute will be an empty string as the text node will
								explain the button and the image is deemed to be decorative -->
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
