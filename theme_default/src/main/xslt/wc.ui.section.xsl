<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.debug.common.bestPracticeHelpers.xsl"/>
	<xsl:import href="wc.ui.section.n.additionalSectionClass.xsl"/>
	<xsl:import href="wc.ui.section.n.applyContent.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		Transform for WSection. It is simply a major content container with an exposed heading.
		
		Child elements
		* ui:margin (optional)
		* ui:decoratedLabel
		* ui:panel
	-->
	<xsl:template match="ui:section">
		<xsl:variable name="mode" select="@mode"/>
		<xsl:element name="${wc.dom.html5.element.section}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>section</xsl:text>
				<xsl:if test="$mode='lazy' and @hidden">
					<xsl:text> wc_magic</xsl:text>
				</xsl:if>
				<xsl:call-template name="additionalSectionClass"/>
			</xsl:attribute>
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
				<xsl:apply-templates select="ui:decoratedLabel" mode="section"/>
				<xsl:element name="div">
					<xsl:attribute name="class">
						<xsl:text>content</xsl:text>
					</xsl:attribute>
					<xsl:apply-templates select="ui:panel"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
