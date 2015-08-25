<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		A row expander control needs to know what it controls. Unlike a collapsible
		this is not a single element but could be a set of rows, including other
		subRows with their own controllers. Since the rows are siblings they must be
		controlled individually. This template outputs ids of all rows which are 
		controlled by a rowExpansion control. This is also used by WAI-ARIA to 
		indicate the DOM nodes controlled by the expander. The list is space
		separated.
	-->
	<xsl:template match="ui:tr|ui:subTr" mode="subRowControlIdentifier">
		<xsl:param name="tableId"/>
		<xsl:value-of select="$tableId"/>
		<xsl:choose>
			<xsl:when test="self::ui:tr">
				<xsl:value-of select="concat('-',@rowIndex)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat('-',../@rowIndex,'${wc.ui.table.rowExpansion.id.suffix}')"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="position()!=last()">
			<xsl:value-of select="' '"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>