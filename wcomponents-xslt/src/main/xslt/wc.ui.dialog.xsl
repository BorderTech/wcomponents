
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:dialog">
		<xsl:apply-templates select="html:button"/>
	</xsl:template>
	<!--

		Builds the dialog description JSON object.
	-->
	<xsl:template match="ui:dialog" mode="JS">
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="@id"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="@class">
			<xsl:text>,"className":"</xsl:text>
			<xsl:value-of select="@class"/>
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:if test="@width">
			<xsl:text>,"width":</xsl:text>
			<xsl:value-of select="@width"/>
		</xsl:if>
		<xsl:if test="@height">
			<xsl:text>,"height":</xsl:text>
			<xsl:value-of select="@height"/>
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
		<xsl:if test="@triggerid or ./html:button">
			<xsl:text>,"triggerid":"</xsl:text>
			<xsl:choose>
				<xsl:when test="html:button">
					<xsl:value-of select="html:button/@id"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@triggerid"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>"</xsl:text>
		</xsl:if>
		<xsl:if test="@open">
			<xsl:text>,"open":true</xsl:text>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
