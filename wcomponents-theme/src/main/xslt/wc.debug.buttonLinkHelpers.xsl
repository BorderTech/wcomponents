<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<!--
		Debug info for ui:button etc and ui:link
	-->
	<xsl:template name="buttonLinkCommon-debug">
		<xsl:call-template name="debugAttributes"/>
		<!--
			ERRORS: nesting errors
				do not allow non-phrase content
				do not allow interactive content
		-->
		<xsl:call-template name="nesting-debug">
			<xsl:with-param name="testNonPhrase" select="1"/>
			<xsl:with-param name="testInteractive" select="1"/>
		</xsl:call-template>
		<!--
			WARN: if the button/link has an imageUrl but does not have text() then we will not be able to calculate an appropriate alt attribute for the image
		-->
		<xsl:if test="@imageUrl and not(text() or @toolTip)">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'data-wc-debugwarn'"/>
				<xsl:with-param name="text" select="'Image and no text content: the value of the alt attribute for the image cannot be calculated.'"/>
			</xsl:call-template>
		</xsl:if>
		<!--
			INFO: test for ancestors which do not allow interactive content
		-->
		<xsl:call-template name="thisIsNotAllowedHere-debug">
			<xsl:with-param name="testForNoInteractive" select="1"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>

