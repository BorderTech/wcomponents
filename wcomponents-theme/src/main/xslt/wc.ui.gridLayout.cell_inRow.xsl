<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.gridLayout.cell.n.gridCell.xsl"/>
	<!--
		This template outputs each cell in a row other than the first.
		
		param width: the width of each cell in the grid.
		param hgap: the horizontal space between cells in a row (if any)s. This 
		is half of the actual hgap of the XML parent element.
	-->
	<xsl:template match="ui:cell" mode="inRow">
		<xsl:param name="width"/>
		<xsl:param name="hgap"/>
		<xsl:call-template name="gridCell">
			<xsl:with-param name="width" select="$width"/>
			<xsl:with-param name="hgap" select="$hgap"/>
			<xsl:with-param name="inRow" select="1"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
