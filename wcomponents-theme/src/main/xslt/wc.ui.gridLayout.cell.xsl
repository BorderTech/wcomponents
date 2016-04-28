<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" 
	version="1.0">
	<xsl:import href="wc.ui.gridLayout.cell.n.gridCell.xsl"/>
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<!--
		This template creates a rows of cells and then applies templates to the
		cell's following-siblings up to the number of columns in the row.

		param cols: the number of columns in the row. This is used to apply
		templates on following-siblings up to 1 less than cols (this cell is the
		first col)
		param colWidth: the width of each cell in the grid.
		param hgap the gridlayouts hgap value.
	-->
	<xsl:template match="ui:cell" mode="gl">
		<xsl:param name="cols" select="1"/>
		<xsl:param name="colWidth"/>
		<xsl:param name="hgap"/>

		<xsl:choose>
			<xsl:when test="$cols=1">
				<xsl:call-template name="gridCell"/>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:attribute name="class">
						<xsl:text>wc-row</xsl:text>
						<xsl:call-template name="getHVGapClass">
							<xsl:with-param name="gap" select="$hgap"/>
						</xsl:call-template>
					</xsl:attribute>
					<xsl:call-template name="gridCell">
						<xsl:with-param name="width" select="$colWidth"/>
					</xsl:call-template>
					<xsl:if test="$cols &gt; 1">
						<xsl:apply-templates select="following-sibling::ui:cell[position() &lt; $cols]" mode="inRow">
							<xsl:with-param name="width" select="$colWidth"/>
						</xsl:apply-templates>
					</xsl:if>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
