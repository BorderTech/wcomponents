<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<!--
		ui:columnlayout is one of the possible layout child elements of WPanel.

		A ColumnLayout is used to create a set of rows and columns. It is related to
		WRow and WColumn.

		The spacing between rows and columns is determined by the properties HGAP and
		VGAP. HGAP and VGAP apply only between cells in the layout. They do not apply
		space between the ColumnLayout and surrounding components.

		The column width is set in percent. This is to allow for flexible positioning
		within any level of container. The hgap and vgap are in pixels. If we apply these
		as margins or padding without modifying the width we would cause the columns to
		occupy more than 100% of the available space and this would cause wrapping.

		We do not have any awareness at this stage as to the space available to us (in
		fact, the only way we could manipulate the width of a column to allow pixel gaps
		would be by recalculating all widths after the page has rendered which would
		be an excessively expensive task and would also cause UI flicker).

		To alleviate this we use box-sizing:border-box and apply the hgaps as padding.
		This reduces the space available to the content. If we applied this gap to the
		left of all but the first column, or to the right of all but the last, we would
		have one column which has a content box hgap pixels larger than the other columns.
		This would be noticeable if all columns were set to the same width. To reduce
		this we apply 0.5x the hgap to each of the left and right of all columns except
		the first and last in each row. We apply 0.5 x hgap to the right of the first and
		0.5 x hgap to the left of the last. This still leaves us with two columns which
		have a larger content box than the others but given that the discrepancy is now
		0.5 x hgap and hgap is generally small (commonly in the 3-12 pixel range) we
		live with this.

		If you can fix this problem please let us know.

		Child elements:
		* ui:column Provides the number of columns in the layout and the alignment of each column.
			NOTE: This is unrelated to the ui:column child of a ui:row.
		* ui:cell: Each component placed into a ColumnLayout is output in a ui:cell.
			These cells become the columns. Empty cells are ouput into the UI.


		This calculates the number of rows required and then walks through the cells
		using a mod. The cells included then apply their following siblings up to the
		number of columns (note: once you have written 'column' this many times it
		starts to look weird).
	-->
	<xsl:template match="ui:columnlayout">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>wc-columnlayout</xsl:text>
			</xsl:attribute>
			<xsl:variable name="width">
				<xsl:choose>
					<xsl:when test="ui:column[1]/@width">
						<xsl:value-of select="ui:column[1]/@width"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="hgap">
				<xsl:call-template name="getHVGap">
					<xsl:with-param name="divisor" select="2"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="vgap">
				<xsl:call-template name="getHVGap">
					<xsl:with-param name="gap" select="@vgap"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="cols" select="count(ui:column)"/>
			<xsl:choose>
				<xsl:when test="$cols=1"><!-- I don't know why people do this, but they do -->
					<xsl:apply-templates select="ui:cell" mode="clRow">
						<xsl:with-param name="align" select="ui:column[1]/@align"/>
						<xsl:with-param name="width" select="$width"/>
						<xsl:with-param name="hgap" select="$hgap"/>
						<xsl:with-param name="vgap" select="$vgap"/>
						<xsl:with-param name="cols" select="$cols"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="ui:cell[position() mod $cols = 1]" mode="clRow">
						<xsl:with-param name="align" select="ui:column[1]/@align"/>
						<xsl:with-param name="width" select="$width"/>
						<xsl:with-param name="hgap" select="$hgap"/>
						<xsl:with-param name="vgap" select="$vgap"/>
						<xsl:with-param name="cols" select="$cols"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
