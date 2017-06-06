<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl" />
	<!-- 
		WPrintButton
	-->
	<xsl:template match="ui:printbutton">
		<button name="{@id}" type="button">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isControl" select="1"/>
				<xsl:with-param name="class">
					<xsl:if test="@type">
						<xsl:text>wc-linkbutton</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="title"/>
			<xsl:call-template name="accessKey"/>
			<xsl:choose>
				<xsl:when test="@imageUrl">
					<xsl:variable name="alt">
						<xsl:choose>
							<xsl:when test="@imagePosition">
								<xsl:value-of select="''"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="text()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<span>
						<xsl:attribute name="class">
							<xsl:choose>
								<xsl:when test="@imagePosition">
									<xsl:value-of select="concat('wc_btn_img wc_btn_img', @imagePosition)"/><!-- no gap after 2nd `_img` -->
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>wc_nti</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:if test="@imagePosition">
							<span>
								<xsl:apply-templates />
							</span>
						</xsl:if>
						<img src="{@imageUrl}" alt="{$alt}" />
					</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</button>
	</xsl:template>
</xsl:stylesheet>
