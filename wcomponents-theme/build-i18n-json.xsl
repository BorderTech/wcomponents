<?xml version="1.0" encoding="UTF-8"?>
<!--
	This transform takes an Java Properties XML file and creates a simple JSON object where each property
	name is the key and value is the value.
	
	Note that this is not a true XML > JSON conversion because EVERY value is treated as a STRING no matter what type it may actually be.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0">
	<xsl:output method="text" encoding="UTF-8" />
	
	<xsl:template match="/">
		<xsl:text>{</xsl:text>
		<xsl:apply-templates/>
		<xsl:text>}</xsl:text>
	</xsl:template>

	<xsl:template match="property">
		<xsl:text>"</xsl:text>
		<xsl:call-template name="escapeJson">
			<xsl:with-param name="text">
				<xsl:value-of select="@name"/>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>":</xsl:text>
		<xsl:text>"</xsl:text>
		<xsl:call-template name="escapeJson">
			<xsl:with-param name="text">
				<xsl:value-of select="@value"/>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text>"</xsl:text>
		<xsl:if test="following-sibling::property">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<!--
		Double quotes must be escaped with a backslash and therefore backslashes must also be escaped... first.
	-->
	<xsl:template name="escapeJson">
		<xsl:param name="text"/>
		<xsl:variable name="textNewline">
			<xsl:value-of select="replace($text, '&#xa;', '\\n')"/>
		</xsl:variable>
		<xsl:variable name="textBackslash">
			<xsl:value-of select="replace($textNewline, '\\', '\\\\')"/>
		</xsl:variable>
		<xsl:variable name="textDoublequotes">
			<xsl:value-of select="replace($textBackslash, '&quot;', '\\&quot;')"/>
		</xsl:variable>
		<xsl:value-of select="$textDoublequotes"/>
	</xsl:template>
</xsl:stylesheet>
