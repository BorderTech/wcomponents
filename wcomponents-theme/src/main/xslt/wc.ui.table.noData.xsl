<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Transform for the noData child of a tbody. This is a String so just needs to be 
		wrapped up properly.
		
		param addCols see notes in transform for ui:table in wc.ui.table.xsl.
	-->
	<xsl:template match="ui:noData">
		<xsl:param name="addCols" select="0"/>
		<xsl:variable name="numCols">
			<xsl:choose>
				<xsl:when test="../../ui:thead/ui:th">
					<xsl:value-of select="count(../../ui:thead/ui:th)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="tr">
			<xsl:element name="td">
				<xsl:attribute name="colspan">
					<xsl:value-of select="$addCols + $numCols"/>
				</xsl:attribute>
				<xsl:value-of select="."/>
				<xsl:if test="not(node())">
					<xsl:value-of select="$$${wc.ui.table.string.noData}"/>
				</xsl:if>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
