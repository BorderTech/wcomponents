<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Builds the imageEditor description JSON object.
	-->
	<xsl:template match="html:wc-imageedit" mode="JS">
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="@class">
			<xsl:text>,"className":</xsl:text>
			<xsl:value-of select="@class"/>
		</xsl:if>
		<xsl:if test="@width">
			<xsl:text>,"width":</xsl:text>
			<xsl:value-of select="@width"/>
		</xsl:if>
		<xsl:if test="@height">
			<xsl:text>,"height":</xsl:text>
			<xsl:value-of select="@height"/>
		</xsl:if>
		<xsl:if test="@camera">
			<xsl:text>,"camera":</xsl:text>
			<xsl:value-of select="@camera"/>
		</xsl:if>
		<xsl:if test="@overlay">
			<xsl:text>,"overlay":"</xsl:text>
			<xsl:value-of select="@overlay"/>
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
