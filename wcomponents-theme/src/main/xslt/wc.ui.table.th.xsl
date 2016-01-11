<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.n.cellIndentationHelper.xsl"/>
	<!--
		Transform of a ui:th element within a ui:tr. This is a row header and is a 1:1 map
		with a HTML th element.
	-->
	<xsl:template match="ui:th">
		<xsl:param name="myTable"/>
		<xsl:param name="indent" select="0"/>
		<xsl:param name="hasRole" select="0"/>
		
		<xsl:variable name="tableId" select="$myTable/@id"/>
		
		<th id="{concat($tableId,'${wc.ui.table.id.tr.th.suffix}',../@rowIndex)}" scope="row">
			<xsl:if test="$hasRole &gt; 0">
				<xsl:attribute name="role">
					<xsl:text>rowheader</xsl:text>
				</xsl:attribute>
			</xsl:if>
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
			<xsl:if test="$indent &gt; 0">
				<xsl:call-template name="cellIndentationHelper">
					<xsl:with-param name="indent" select="$indent"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedLabel">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</th>
	</xsl:template>
</xsl:stylesheet>
