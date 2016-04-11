<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.n.className.xsl" />
	<xsl:import href="wc.ui.table.n.cellIndentationHelper.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<!--
		The transform for data cells within the table. These are a 1:1 map with a HTML td element.
	-->
	<xsl:template match="ui:td">
		<xsl:param name="myTable"/>
		<xsl:param name="indent" select="0"/>
		<xsl:param name="hasRole" select="0"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="tbleColPos">
			<xsl:value-of select="position()"/>
		</xsl:variable>
		<xsl:variable name="colHeaderElement" select="$myTable/ui:thead/ui:th[position()=$tbleColPos]"/>
		<xsl:variable name="rowHeaderElement" select="../ui:th[1]"/>
		<!-- the one is redundant -->
		<td>
			<xsl:if test="$hasRole &gt; 0">
				<xsl:attribute name="role">gridcell</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="$colHeaderElement/@align"/>
					<!-- IE 8- needs more help with striping -->
					<xsl:if test="$myTable/@striping = 'cols' and position() mod 2 = 0">
						<xsl:text> wc_table_stripe</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:if test="$colHeaderElement or $rowHeaderElement">
				<xsl:attribute name="headers">
					<xsl:variable name="colHeader">
						<xsl:if test="$colHeaderElement">
							<xsl:value-of select="concat($tableId,'_thh',$tbleColPos)"/>
						</xsl:if>
					</xsl:variable>
					<xsl:variable name="rowHeader">
						<xsl:if test="$rowHeaderElement">
							<xsl:value-of select="concat($tableId,'_trh',../@rowIndex)"/>
						</xsl:if>
					</xsl:variable>
					<xsl:value-of select="normalize-space(concat($colHeader,' ',$rowHeader))"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$indent &gt; 0 and not(preceding-sibling::*)">
				<xsl:call-template name="cellIndentationHelper">
					<xsl:with-param name="indent" select="$indent"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates/>
		</td>
	</xsl:template>
</xsl:stylesheet>
