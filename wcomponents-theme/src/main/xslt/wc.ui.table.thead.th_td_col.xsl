<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:include href="wc.constants.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
<!--
    Creates each col element in the colgroup created in the transform of the
    table.

    param stripe: 1 if the table has column striping.
    param sortCol: The 0 indexed column which is currently sorted (if any).
    param span: This parameter is used to include an allowance for expander
        cells etc and the hierarchic tables indentation cells in the span of the
        first col in the group.
-->
	<xsl:template match="ui:th|ui:td" mode="col">
		<xsl:param name="stripe"/>
		<xsl:param name="sortCol"/>
		<xsl:variable name="class">
			<xsl:if test="$stripe=1 and position() mod 2 = 0">
				<xsl:text>wc_table_stripe</xsl:text>
			</xsl:if>
			<xsl:if test="$sortCol and position() = $sortCol + 1">
				<xsl:text> wc_table_sort_sorted</xsl:text>
			</xsl:if>
		</xsl:variable>
		<col>
			<xsl:if test="$class !=''">
				<xsl:attribute name="class">
					<xsl:value-of select="normalize-space($class)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@width">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:',@width,'%')"/>
				</xsl:attribute>
			</xsl:if>
		</col>
		<xsl:if test="$isDebug=1">
			<xsl:comment>
				<xsl:value-of select="local-name()"/>
			</xsl:comment>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>