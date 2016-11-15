<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<!--
		Creates a pseudo-grid where each column is the same width and each row is the height of the tallest cell in the
		row. This is a very rough emulation of an AWT GridLayout. ui:gridlayout is one of the possible child elements of
		ui:panel.
		
		
		Child elements

		* ui:cell (minOccurs 0, maxOccurs unbounded) Each component placed into a gridLayout is output in a ui:cell. 
		Empty cells are ouput into the UI to maintain grid positioning of content.

		This template determines the order in which cell child elements templates are applied based on calculations of 
		rows and columns in the grid. The recursion rules of XSLT 1, and its lack of incrementers etc, mean that when
		applying templates which have to have wrappers around certain elements we have to split the call and make the 
		siblings into temporary pseudo-parents. Not as hard as it sounds.
	-->
	<xsl:template match="ui:gridlayout">
		<xsl:if test="ui:cell">
			<xsl:variable name="cols" select="@cols"/>
			<xsl:variable name="rows" select="@rows"/>
			<!--
				The raw number of columns may not give an accurate reflection of the intended state of the grid as @cols
				has an inclusiveMin of 0. For this reason we use @cols if it is greater than 0, if not we look at @rows
				and if it is greater than 0 we calculate the number of columns by ceiling(count(ui:cell) div @rows).
				Otherwise we assume 1 column (which I grant is a bad assumption; maybe ceiling the square-root of the 
				number of cells would be better?). Practically the Java API requires at least one of @cols or @rows to
				be non-zero.
			-->
			<xsl:variable name="useCols">
				<xsl:choose>
					<xsl:when test="number($cols) gt 0">
						<xsl:number value="number($cols)"/>
					</xsl:when>
					<xsl:when test="number($rows) gt 0">
						<xsl:value-of select="ceiling(count(ui:cell) div number($rows))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="1"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<div>
				<xsl:attribute name="class">
					<xsl:text>wc-gridlayout</xsl:text><!-- prefixed local name -->
					<xsl:if test="number($useCols) le 12">
						<xsl:value-of select="concat(' wc-gridlayout-col-', $useCols)"/>
					</xsl:if>
					<xsl:call-template name="getHVGapClass">
						<xsl:with-param name="isVGap" select="1"/>
					</xsl:call-template>
				</xsl:attribute>

				<xsl:choose>
					<xsl:when test="number($useCols) eq 1">
						<xsl:apply-templates select="ui:cell" mode="gl">
							<xsl:with-param name="hgap" select="@hgap"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="ui:cell[(position() mod number($useCols)) eq 1]" mode="gl">
							<xsl:with-param name="cols" select="number($useCols)"/>
							<xsl:with-param name="colWidth">
								<xsl:if test="number($useCols) gt 12">
									<xsl:value-of select="format-number(1 div number($useCols),'##0.###%')"/>
								</xsl:if>
							</xsl:with-param>
							<xsl:with-param name="hgap" select="@hgap"/>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>

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
			<xsl:when test="number($cols) eq 1">
				<xsl:call-template name="gridCell"/>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:attribute name="class">
						<xsl:text>wc_gl_row</xsl:text>
						<xsl:call-template name="getHVGapClass">
							<xsl:with-param name="gap" select="$hgap"/>
						</xsl:call-template>
					</xsl:attribute>
					<xsl:call-template name="gridCell">
						<xsl:with-param name="width" select="$colWidth"/>
					</xsl:call-template>
					<xsl:if test="number($cols) gt 1">
						<xsl:apply-templates select="following-sibling::ui:cell[position() lt number($cols)]" mode="inRow">
							<xsl:with-param name="width" select="$colWidth"/>
						</xsl:apply-templates>
					</xsl:if>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

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

	<!--
		Helper template to create each cell in a gridLayout.
		
		param width: The width of the cells in percent or '' if cols no more than 12.
	-->
	<xsl:template name="gridCell">
		<xsl:param name="width" select="''"/>
		<div class="wc-cell">
			<xsl:if test="$width ne ''">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:',$width,';')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
