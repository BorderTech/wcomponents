<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Builds the dialog description JSON object.
	-->
	<xsl:template match="ui:dialog" mode="JS">
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>","form":"</xsl:text>
		<xsl:choose>
			<xsl:when test="ancestor::ui:application">
				<xsl:value-of select="ancestor::ui:application/@id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="ancestor::*[@id][1]/@id"/>
			</xsl:otherwise>
		</xsl:choose>
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
		<xsl:if test="@resizable">
			<xsl:text>,"resizable":</xsl:text>
			<xsl:value-of select="@resizable"/>
		</xsl:if>
		<xsl:if test="@modal">
			<xsl:text>,"modal":</xsl:text>
			<xsl:value-of select="@modal"/>
		</xsl:if>
		<xsl:if test="@title">
			<xsl:text>,"title":"</xsl:text>
			<xsl:value-of select="@title" disable-output-escaping="yes"/>
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:if test="(ui:content and //ui:dialog[ui:content][1]=.) or (@open and not(//ui:dialog/ui:content) and //ui:dialog[@open][1]=.)">
			<xsl:text>,"open":true</xsl:text>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
