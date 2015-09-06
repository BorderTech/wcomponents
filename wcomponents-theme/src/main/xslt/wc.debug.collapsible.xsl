<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.debug.common.bestPracticeHelpers.xsl"/>
<!--
		debug mode transform for ui:collapsible
-->
	<xsl:template name="collapsible-debug">
		<xsl:param name="collapsed"/>
		<xsl:call-template name="debugAttributes"/>
		<!--
			ERROR:
			collapsible's heading component is a HTML SUMMARY element and
			must not contain non-phrase content. The actual collapsible content
			can contain anything.
		-->
		<xsl:call-template name="nesting-debug">
			<xsl:with-param name="testNonPhrase" select="1"/>
			<xsl:with-param name="el" select="ui:decoratedLabel"/>
		</xsl:call-template>
		<!--
			WARN
			Lame mode is lame (especially for a collapsible).
		-->
		<xsl:if test="@mode='server'">
			<xsl:call-template name="lameMode">
				<xsl:with-param name="level" select="'data-wc-debugwarn'"/>
			</xsl:call-template>
		</xsl:if>
		<!--
			INFO:
			Mystery meat test for mandatory fields inside a closed collapible.
		-->
		<xsl:if test="$collapsed=1">
			<xsl:call-template name="hasMysteryMeat">
				<xsl:with-param name="contentElement" select="ui:content"/>
			</xsl:call-template>
		<!--
			DEBUG:
			if added to a container which cannot accept non-phrase content OR
			interactive content.
		-->
		<xsl:call-template name="thisIsNotAllowedHere-debug">
			<xsl:with-param name="testForPhraseOnly" select="1"/>
			<xsl:with-param name="testForNoInteractive" select="1"/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
