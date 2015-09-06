<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.n.firstRowCellIndentationHelper"/>
	<xsl:import href="wc.ui.table.td.n.WTableAdditionalCellClass"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
 The transform for data cells within the table. These are a 1:1 map with a HTML
 td element.

 param myTable: The first table ancestor of the current row. This is determined
 at the most efficient point (usually ui:tbody using its parent node) and then
 passed through all subsequent transforms to save constant ancestor::ui:table[1]
 lookups.

 param maxIndent: see notes in transform for ui:table in wc.ui.table.xsl.
-->
	<xsl:template match="ui:td">
		<xsl:param name="myTable"/>
		<xsl:param name="maxIndent" select="0"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="tbleColPos">
			<xsl:value-of select="position()"/>
		</xsl:variable>
		<xsl:variable name="colHeaderElement" select="$myTable/ui:thead/ui:th[position()=$tbleColPos]"/>
		<xsl:variable name="rowHeaderElement" select="../ui:th[1]"/>
		<!-- the one is redundant -->
		<xsl:variable name="alignedCol">
			<xsl:value-of select="$colHeaderElement/@align"/>
		</xsl:variable>
		<xsl:variable name="class">
			
			<xsl:if test="$isIE = 1 and $myTable/@striping = 'cols' and position() mod 2 = 0">
				<xsl:text> wc_table_stripe</xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$alignedCol!=''">
					<xsl:value-of select="concat(' ',$alignedCol)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> ${wc.common.align.std}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="WTableAdditionalCellClass">
				<xsl:with-param name="myTable" select="$myTable"/>
				<xsl:with-param name="tbleColPos" select="$tbleColPos"/>
				<xsl:with-param name="rowHeaderElement" select="$rowHeaderElement"/>
				<xsl:with-param name="alignedCol" select="$alignedCol"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:element name="td">
			<xsl:if test="normalize-space($class) !=''">
				<xsl:attribute name="class">
					<xsl:value-of select="normalize-space($class)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$colHeaderElement or $rowHeaderElement">
				<xsl:attribute name="headers">
					<xsl:variable name="colHeader">
						<xsl:if test="$colHeaderElement">
							<xsl:value-of select="concat($tableId,'${wc.ui.table.id.thead.th.suffix}',$tbleColPos)"/>
						</xsl:if>
					</xsl:variable>
					<xsl:variable name="rowHeader">
						<xsl:if test="$rowHeaderElement">
							<xsl:value-of select="concat($tableId,'${wc.ui.table.id.tr.th.suffix}',../@rowIndex)"/>
						</xsl:if>
					</xsl:variable>
					<xsl:value-of select="normalize-space(concat($colHeader,' ',$rowHeader))"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="not(preceding-sibling::*) and $myTable/ui:rowExpansion and $myTable/@type='hierarchic'">
				<xsl:call-template name="firstRowCellIndentationHelper">
					<xsl:with-param name="myTable" select="$myTable"/>
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>