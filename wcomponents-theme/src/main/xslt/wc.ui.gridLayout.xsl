<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<!--
		Creates a pseudo-grid where each column is the same width and each row is the
		height of the tallest cell in the row. This is a very rough emulation of an AWT
		gridLayout.

		ui:gridlayout is one of the possible child elements of ui:panel.

		A grid will occupy the whole available width. The width of each cell is
		determined by the number of cols requested (or, if cols is not specified the
		number of cells divided by the number of rows). If HGAP is specified then the
		actual content box will be smaller than the width by the HGAP.

		In the default theme a grid is laid out using table display. The cells have
		margins to implement hgap and vgap (if set).

		Child elements

		* ui:cell (minOccurs 0, maxOccurs unbounded)
		Each component placed into a gridLayout is output in a ui:cell. Empty cells
		are ouput into the UI to maintain grid positioning of content.


		This template determines the order in which cell child elements templates are
		applied based on calculations of rows and columns in the grid.

		The recursion rules of XSLT, and its lack of incrementers etc, mean that when
		applying templates which have to have wrappers around certain elements we have
		to split the call and make the siblings into temporary pseudo-parents. Not as
		hard as it sounds!
	-->
	<xsl:template match="ui:gridlayout">
		<xsl:if test="ui:cell">
			<div class="wc-gridlayout">
				<xsl:variable name="cols" select="@cols"/>
				<xsl:variable name="rows" select="@rows"/>
				<!--
					The raw number of columns may not give an accurate reflection of the intended
					state of the grid as cols has an inclusiveMin of 0. For this reason we use cols
					if it is greater than 0, if not we look at rows and if it is greater than 0 we
					calculate the number of columns by ceiling(count(ui:cell) div $rows). Otherwise
					we assume 1 column.
				-->
				<xsl:variable name="useCols">
					<xsl:choose>
						<xsl:when test="$cols &gt; 0">
							<xsl:number value="$cols"/>
						</xsl:when>
						<xsl:when test="$rows &gt; 0">
							<xsl:value-of select="ceiling(count(ui:cell) div $rows)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:number value="1"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:variable name="colWidth">
					<xsl:choose>
						<xsl:when test="$useCols=1">
							<xsl:text>100%</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="format-number(1 div $useCols,'##0.###%')"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!--
					The hgap is applied half to each side of a column. This means that the first and
					last columns will be a little wider than the central columns but not so much
					difference as if we had just used a left or right margin/padding in which case
					one column would have been much larger than the others (a whole hgap wider).

					We therefore divide the hgap by two before passing it through to the cells.
				-->
				<xsl:variable name="hgap">
					<xsl:choose>
						<xsl:when test="$useCols=1">
							<xsl:number value="0"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="getHVGap">
								<xsl:with-param name="divisor" select="2"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!--
					The vgap is applied between rows, therefore we apply the whole vgap since we do
					not have the same width issues as are caused by hgap.
				-->
				<xsl:variable name="vgap">
					<xsl:call-template name="getHVGap">
						<xsl:with-param name="gap" select="@vgap"/>
					</xsl:call-template>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="$useCols=1">
						<xsl:apply-templates select="ui:cell" mode="gl">
							<xsl:with-param name="vgap" select="$vgap"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="ui:cell[position() mod $useCols = 1]" mode="gl">
							<xsl:with-param name="cols" select="$useCols"/>
							<xsl:with-param name="colWidth" select="$colWidth"/>
							<xsl:with-param name="hgap" select="$hgap"/>
							<xsl:with-param name="vgap" select="$vgap"/>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
