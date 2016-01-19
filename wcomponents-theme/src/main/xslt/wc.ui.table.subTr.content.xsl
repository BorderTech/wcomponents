<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.n.className.xsl" />
	<xsl:import href="wc.ui.table.n.xsl"/>
	<xsl:import href="wc.ui.table.subTr.content.n.WTableSubTrContentClass.xsl"/>
	<!--
		Transform for ui:content child of a ui:subTr.
	-->
	<xsl:template match="ui:subTr/ui:content">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="indent" select="0"/>
		
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<!--NOTE: aria-level the minimum is going to be level 2 -->
		<tr id="{concat($tableId,'${wc.ui.table.id.subTr.content.suffix}',../../@rowIndex)}" role="row" aria-level="{count(ancestor::ui:subTr[ancestor::ui:table[1]/@id=$tableId]) + 1}">
			<xsl:if test="$parentIsClosed=1 or ancestor::ui:subTr[not(@open) or @open='false']">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:if test="$topRowIsStriped=1">
					<xsl:text> wc_table_stripe</xsl:text>
				</xsl:if>
			</xsl:attribute>
			
			<!-- 
				subTr content is never individually selectable but must have the placeholder if the table has row
				selection.
			-->
			<xsl:if test="$myTable/ui:rowSelection">
				<td role="gridcell">
					<xsl:text>&#x2002;</xsl:text>
				</td>
			</xsl:if>
			
			<!-- 
				subTr content is not itself expandable but must have the placeholder to position it correctly relative
				to its parent row.
			-->
			<td class="wc_table_rowexp_container" role="gridcell">
				<xsl:text>&#x2002;</xsl:text>
			</td>

			<td role="gridcell">
				<xsl:if test="@spanAllCols=$t">
					<xsl:attribute name="colspan">
						<xsl:value-of select="count(../../*) -1"/><!-- -1 because we do not count the ui:subTr -->
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$indent &gt; 0">
					<xsl:call-template name="cellIndentationHelper">
						<xsl:with-param name="indent" select="$indent"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
