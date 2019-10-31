
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- Trigger for WAjaxControl. The ui:ajaxtrigger element is used to register components which make AJAX requests in JavaScript. -->
	<xsl:template match="ui:ajaxtrigger"/>

	<!-- This creates the JSON objects required to register the triggers. -->
	<xsl:template match="ui:ajaxtrigger" mode="JS">
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="@triggerId"/>
		<xsl:text>","oneShot":</xsl:text>
		<xsl:choose>
			<xsl:when test="not(@loadOnce)">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>true</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>,"loads":[</xsl:text>
		<xsl:apply-templates select="*"/>
		<xsl:text>]</xsl:text>
		<xsl:if test="@delay">
			<xsl:text>,"delay":</xsl:text>
			<xsl:value-of select="@delay"/>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- This creates the function used to make a delayed call to an ajaxTrigger on page load. -->
	<xsl:template match="ui:ajaxtrigger" mode="JSdelay">
		<xsl:text>{"id":"</xsl:text>
		<xsl:value-of select="@triggerId"/>
		<xsl:text>","delay":</xsl:text>
		<xsl:value-of select="@delay"/>
		<xsl:text>}</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ui:ajaxtargetid">
		<xsl:text>"</xsl:text>
		<xsl:value-of select="@targetId"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="position() ne last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
