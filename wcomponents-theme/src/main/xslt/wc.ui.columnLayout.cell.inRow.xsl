<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.column.xsl"/>
<!--
	This template creates columns within a row (except the first). Each column has
	to look up the alignment and width of its position equivalent ui:column.

param hgap: the horizontal space (if any) between columns.
-->
	<xsl:template match="ui:cell" mode="clInRow">
		<xsl:param name="hgap"/>
		<!--
			variable colPos
			This variable is used to find the ui:column which holds the meta-data pertinent
			to the column being constructed.
			
			The columns built in this template are columns 2...n but are called from a
			sibling using following-siblings and therefore their position() is 1...n-1. 
			Therefore to match the equivalent ui:column we have to use position() + 1.
		-->
		<xsl:variable name="colPos" select="position() + 1"/>
		<!--
			variable myColumn
			This is a handle to the ui:column sibling of the cell which has position relative
 			to the parent element equal to the value of $colPos calculated above.
		-->
		<xsl:variable name="myColumn" select="../ui:column[position() = $colPos]"/>
		<xsl:variable name="width">
			<xsl:choose>
				<xsl:when test="$myColumn/@width">
					<xsl:value-of select="$myColumn/@width"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="column">
			<xsl:with-param name="align" select="$myColumn/@align"/>
			<xsl:with-param name="width" select="$width"/>
			<xsl:with-param name="hgap" select="$hgap"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
