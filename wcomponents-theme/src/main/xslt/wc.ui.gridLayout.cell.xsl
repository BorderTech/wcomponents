<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.gridLayout.cell.n.gridCell.xsl"/>
	<!--
		This template creates a rows of cells and then applies templates to the
		cell's following-siblings up to the number of columns in the row.

		param cols: the number of columns in the row. This is used to apply
		templates on following-siblings up to 1 less than cols (this cell is the
		first col)
		param colWidth: the width of each cell in the grid.
		param hgap: the horizontal space between cells in a row (if any) in
		pixels. Default 0.
		param vgap: the vertical space between rows (if any) in pixels. Default 0.
	-->
	<xsl:template match="ui:cell" mode="gl">
		<xsl:param name="cols" select="1"/>
		<xsl:param name="colWidth" select="'100%'"/>
		<xsl:param name="hgap" select="0"/>
		<xsl:param name="vgap" select="0"/>

		<xsl:choose>
			<xsl:when test="$cols=1">
				<xsl:call-template name="gridCell">
					<xsl:with-param name="vgap" select="$vgap"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<div class="wc-row">
					<!--
						We apply vgap (if any) to all rows except the first.
					-->
					<xsl:if test="$vgap != 0 and position() &gt; 1">
						<xsl:if test="$vgap != 0">
							<xsl:attribute name="style">
								<xsl:value-of select="concat('padding-top:',$vgap,';')"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:if>
					<xsl:call-template name="gridCell">
						<xsl:with-param name="width" select="$colWidth"/>
						<xsl:with-param name="hgap" select="$hgap"/>
					</xsl:call-template>
					<xsl:if test="$cols &gt; 1">
						<xsl:apply-templates select="following-sibling::ui:cell[position() &lt; $cols]" mode="inRow">
							<xsl:with-param name="width" select="$colWidth"/>
							<xsl:with-param name="hgap" select="$hgap"/>
						</xsl:apply-templates>
					</xsl:if>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
