<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Gets the indentation before we add the cell for the row collapse 
		control/placeholder for the current table if hierarchic, otherwise 0.
		Called from transforms for ui:subTr/ui:content and named template
		indentCells (see wc.ui.table.n.indentCells.xsl).
	
		param myTable Nearest ancestor table.
		param maxIndent (see wc.ui.table.xsl).
	-->
	<xsl:template name="getPreCollapserIndent">
		<xsl:param name="myTable"/>
		<xsl:param name="maxIndent" select="0"/>
		<xsl:choose>
			<xsl:when test="$maxIndent=0">
				<xsl:value-of select="0"/>
			</xsl:when>
			<xsl:when test="$myTable/@type='hierarchic'">
				<xsl:variable name="tableId" select="$myTable/@id"/>
				<xsl:value-of select="count(ancestor::ui:subTr[ancestor::ui:table[1]/@id=$tableId])"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
