<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.debugInfo.xsl"/>
	<!--
		LAME mode tests for things which are set to round-trip for no good reason.
		For example, a WCollapsible with mode SERVER is lame, you should not need
		a complete screen refresh to open a collapsible, you can do it with AJAX!
	-->
	<xsl:template name="lameMode">
		<xsl:param name="otherText"/>
		<xsl:param name="level" select="'ata-wc-debuginfo'"/>
		<xsl:variable name="lameText">
			<xsl:call-template name="getLameText">
				<xsl:with-param name="otherText" select="$otherText"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$lameText!=''">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="$level"/>
				<xsl:with-param name="text" select="$lameText"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="getLameText">
		<xsl:param name="otherText"/>
		<xsl:choose>
			<xsl:when test="@roundTrip">
				<xsl:text>Consider using clientSide instead of roundTrip. </xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Mode.SERVER is considered harmful. </xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$otherText"/>
	</xsl:template>
	
	
	<!--
		Mystery Meat tests for closed things which contain mandatory fields.
		It is OK that this uses the same attribute as LameMode because they
		are mutually exclusive: If something in server mode has content it is
		open and its meat is not mysterious.
	-->
	<xsl:template name="hasMysteryMeat">
		<xsl:param name="contentElement" select="."/>
		<xsl:variable name="mysteryMeat" select="$contentElement//*[@required and not(@disabled or @readOnly)]"/>
		<xsl:if test="$mysteryMeat">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'ata-wc-debuginfo'"/>
				<xsl:with-param name="text">
					<xsl:text>This component has closed or hidden content which contains one or more mandatory fields. It can be very difficult for a user to find this mystery meat. [</xsl:text>
					<xsl:apply-templates select="$mysteryMeat" mode="listById"/>
					<xsl:text>]</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	
	<!--
		Some access keys are not available in some browsers. I don't care which 
		browser the developer is using because that is not necessarily the same as
		the browser the user will be using.
	-->
	<xsl:template name="problematicAccessKeys">
			<xsl:variable name="problemAccessKeys" select="'AEF'"/>
			<xsl:if test="contains($problemAccessKeys, @accessKey) and not((self::ui:menuItem or self::ui:submenu) and not(ancestor::ui:submenu))">
				<xsl:call-template name="makeDebugAttrib-debug">
					<xsl:with-param name="name" select="'data-wc-debugwarn'"/>
					<xsl:with-param name="text">
						<xsl:text>This component's accessKey (</xsl:text>
						<xsl:value-of select="@accessKey"/>
						<xsl:text>) may not work in all common browsers.</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
	</xsl:template>
</xsl:stylesheet>