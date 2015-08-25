<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.debugInfo.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		WFieldLayout is intended for all layout of fields.

		Child elements
		* ui:field
	-->
	<xsl:template match="ui:fieldLayout">
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="@ordered">
					<xsl:text>ol</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>ul</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:attribute name="class">
				<xsl:value-of select="concat(local-name(),' ', @layout)"/>
			</xsl:attribute>
			<xsl:if test="@ordered and @ordered &gt; 1">
				<xsl:attribute name="start">
					<xsl:value-of select="@ordered"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="debugAttributes"/>
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="@labelWidth">
				<xsl:attribute name="data-wc-labelwidth">
					<xsl:value-of select="@labelWidth"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:apply-templates select="ui:field"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
