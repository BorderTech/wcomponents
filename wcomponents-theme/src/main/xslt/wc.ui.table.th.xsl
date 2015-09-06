<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.n.firstRowCellIndentationHelper"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
 Transform of a ui:th element within a ui:tr. This is a row header and is a 1:1 map
 with a HTML th element.

 param myTable: The first table ancestor of the current row. This is determined
 at the most efficient point (usually ui:tbody using its parent node) and then
 passed through all subsequent transforms to save constant ancestor::ui:table[1]
 lookups.

 param maxIndent: see notes in transform for ui:table in wc.ui.table.xsl.
-->
	<xsl:template match="ui:th">
		<xsl:param name="myTable"/>
		<xsl:param name="maxIndent" select="0"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:element name="th">
			<xsl:attribute name="id">
				<xsl:value-of select="concat($tableId,'${wc.ui.table.id.tr.th.suffix}',../@rowIndex)"/>
			</xsl:attribute>
			<xsl:if test="$myTable/ui:thead">
				<xsl:variable name="myHeader" select="$myTable/ui:thead/ui:th[1]"/>
				<xsl:if test="$myHeader">
					<xsl:attribute name="headers">
						<xsl:value-of select="concat($tableId,'${wc.ui.table.id.thead.th.suffix}','1')"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:variable name="align">
					<xsl:value-of select="$myHeader/@align"/>
				</xsl:variable>
				<xsl:attribute name="class">
					<xsl:choose>
						<xsl:when test="$align!=''">
							<xsl:value-of select="$align"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>${wc.common.align.std}</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$myTable/ui:rowExpansion and $myTable/@type='hierarchic'">
				<xsl:call-template name="firstRowCellIndentationHelper">
					<xsl:with-param name="myTable" select="$myTable"/>
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedLabel">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>