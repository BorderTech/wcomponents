<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.ui.table.subTr.content.n.WTableAdditionalContentClass.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
 Transform for ui:content child of a ui:subTr.

 param myTable: The first table ancestor of the current row. This is determined
 at the most efficient point (usually ui:tbody using its parent node) and then
 passed through all subsequent transforms to save constant ancestor::ui:table[1]
 lookups.

 param parentIsClosed default 0, if 1 then the element is hidden.

 param maxIndent: see notes in transform for ui:table in wc.ui.table.xsl.
-->
	<xsl:template match="ui:subTr/ui:content">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="maxIndent" select="0"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="isHeirarchic">
			<xsl:if test="$myTable/@type='hierarchic'">
				<xsl:value-of select="1"/>
			</xsl:if>
		</xsl:variable>	
		<xsl:element name="tr">
			<xsl:attribute name="id">
				<xsl:value-of select="concat($tableId,'${wc.ui.table.id.subTr.content.suffix}',../../@rowIndex)"/>
			</xsl:attribute>
			<xsl:if test="$parentIsClosed=1 or ancestor::ui:subTr[not(@open) or @open='false']">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>
			<xsl:attribute name="aria-level">
				<xsl:value-of select="count(ancestor::ui:subTr[ancestor::ui:table/@id=$tableId]) + 1"/>
			</xsl:attribute>
			<xsl:variable name="class">
				<xsl:if test="$topRowIsStriped=1">
					<xsl:text>wc_table_stripe </xsl:text>
				</xsl:if>
				<xsl:call-template name="WTableAdditionalContentClass">
					<xsl:with-param name="myTable" select="$myTable"/>
					<xsl:with-param name="parentIsClosed" select="$parentIsClosed"/>
					<xsl:with-param name="topRowIsStriped" select="$topRowIsStriped"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="$class !=''">
				<xsl:attribute name="class">
					<xsl:value-of select="normalize-space($class)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$myTable/ui:rowSelection">
				<xsl:element name="td">
					<xsl:text>&#x2002;</xsl:text>
				</xsl:element>
			</xsl:if>
			<xsl:if test="$isHeirarchic=1">
				<xsl:call-template name="indentCells">
					<xsl:with-param name="myTable" select="$myTable"/>
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:element name="td">
				<xsl:attribute name="class">
					<xsl:text>wc_table_rowexp_container</xsl:text>
				</xsl:attribute>
			</xsl:element>
			<xsl:element name="td">
				<xsl:if test="@spanAllCols=$t">
					<xsl:variable name="basicColSpan" select="count(../../ui:td|../../ui:th)"/>
					<xsl:attribute name="colspan">
						<xsl:choose>
							<xsl:when test="$isHeirarchic!=1 or $maxIndent=0">
								<xsl:value-of select="$basicColSpan"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:variable name="preCollapserIndent">
									<xsl:call-template name="getPreCollapserIndent">
										<xsl:with-param name="maxIndent" select="$maxIndent"/>
										<xsl:with-param name="myTable" select="$myTable"/>
									</xsl:call-template>
								</xsl:variable>
								<xsl:value-of select="$basicColSpan + $maxIndent - $preCollapserIndent - 1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>