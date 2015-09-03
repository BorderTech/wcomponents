<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
	Transform for WHeading. This is a fairly straightforwards 1:1 match with a HTML
	H# element where the WHeading @level sets the #

	Child elements
	* ui:decoratedLabel (minOccurs 0)
	If the heading does not have a WDecoratedLabel then its text content is used
	as the text in the HTML heading element. If the content is mixed only the
	WDecoratedLabel	is output (this is actually not possible in the Java API so
	it not as draconian as it appears).
-->
	<xsl:template match="ui:heading">
		<xsl:element name="{concat('h',@level)}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="debugAttributes"/>
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
				</xsl:call-template>
				<xsl:call-template name="nesting-debug">
					<xsl:with-param name="testNonPhrase" select="1"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="ui:decoratedLabel">
					<xsl:apply-templates select="ui:decoratedLabel"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
