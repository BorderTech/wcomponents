<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.gridLayout.cell.n.gridCell.xsl"/>
	<!--
		This template outputs each cell in a row other than the first.
		
		param width: the width of each cell in the grid.
	-->
	<xsl:template match="ui:cell" mode="inRow">
		<xsl:param name="width"/>
		<xsl:call-template name="gridCell">
			<xsl:with-param name="width" select="$width"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
