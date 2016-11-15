<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.n.className.xsl" />
	<xsl:import href="wc.constants.xsl"/>
	<!--
		The transform for data cells within the table. These are a 1:1 map with a HTML td element.

		Structural: do not override.
	-->
	<xsl:template match="ui:td">
		<xsl:param name="myTable"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<xsl:variable name="tbleColPos" select="position()"/>
		<xsl:variable name="colHeaderElement" select="$myTable/ui:thead/ui:th[position() eq number($tbleColPos)]"/>
		<xsl:variable name="rowHeaderElement" select="../ui:th[1]"/><!-- the one is redundant -->
		<td>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="$colHeaderElement/@align">
						<xsl:value-of select="concat('wc-align-',$colHeaderElement/@align)"/>
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
			<xsl:apply-templates/>
		</td>
	</xsl:template>
</xsl:stylesheet>
