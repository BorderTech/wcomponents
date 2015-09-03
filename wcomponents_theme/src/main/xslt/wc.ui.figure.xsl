<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.debug.common.bestPracticeHelpers.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		Transform for WFigure. Direct map to Figure element. THe WDecoratedLabel child maps to Figcaption element.
	-->
	<xsl:template match="ui:figure">
		<xsl:variable name="mode" select="@mode"/>
		<xsl:element name="${wc.dom.html5.element.figure}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:if test="$mode='lazy' and @hidden">
				<xsl:attribute name="class">
					<xsl:text> wc_magic</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="ui:decoratedLabel">
				<xsl:attribute name="aria-labelledby">
					<xsl:value-of select="ui:decoratedLabel/@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="debugAttributes"/>
				<xsl:call-template name="nesting-debug">
					<xsl:with-param name="testNonPhrase" select="1"/>
					<xsl:with-param name="el" select="ui:decoratedLabel/ui:labelBody"/>
				</xsl:call-template>
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="*[not(self::ui:margin)] or not($mode='eager')">
				<xsl:if test="ui:content">
					<div class="content">
						<xsl:apply-templates select="ui:content"/>
					</div>
				</xsl:if>
				<xsl:element name="${wc.dom.html5.element.figcaption}">
					<xsl:apply-templates select="ui:decoratedLabel" mode="figure"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
