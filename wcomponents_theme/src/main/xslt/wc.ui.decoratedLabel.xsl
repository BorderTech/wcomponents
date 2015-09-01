<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.debugInfo.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		WDecoratedLabel allows a labelling element to contain up to three independently
		stylable areas. The output element of the label and its children is dependent
		upon the content model of the containing element and defaults to span.
	
		Child elements
		* ui:labelHead (0..1)
		* ui:labelBody (required, exactly 1)
		* ui:labelTail (0..1)

		Output the WDecoratedLabel
		param output: A HTML element name. Default 'span'
	-->
	<xsl:template match="ui:decoratedLabel">
		<xsl:param name="output" select="'span'"/>
		<xsl:element name="{$output}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>decoratedLabel</xsl:text>
				<xsl:if test="@type">
					<xsl:value-of select="concat(' ',@type)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget">
				<xsl:with-param name="live" select="'off'"/>
			</xsl:call-template>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="debugAttributes"/>
				<!--
					for these components with a WDecoratedLabel labelling
					component the debug info is written into the parent
					component.

					TODO: include ui:submenu when we drop the submenu specific
					transform.
				-->
				<xsl:choose>
					<xsl:when test="$output='span'">
						<xsl:call-template name="nesting-debug">
							<xsl:with-param name="testNonPhrase" select="1"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="thisIsNotAllowedHere-debug">
							<xsl:with-param name="testForPhraseOnly" select="1"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:if>
			<xsl:apply-templates select="*">
				<xsl:with-param name="output" select="$output"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
