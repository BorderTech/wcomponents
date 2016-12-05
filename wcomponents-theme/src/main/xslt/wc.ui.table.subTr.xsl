<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.n.className.xsl" />
	<!--
		Transform of ui:subtr. This is a sub row element which is an optional child of a ui:tr element. It should not be
		present if the table does not have rowExpansion. In HTML these are siblings of their parent ui:tr which makes
		row manipulation interesting.

		Structural: do not override.
	-->
	<xsl:template match="ui:subtr">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="indent" select="0"/>

		<!--
		 We have to output content if:

			* she subTr is open;
			* the expansion mode is client;
			* there is a ui:content child element; or
			* there are ui:tr child elements.

		 Otherwise we have to create a null content placeholder with the appropriate wires to make the expansion AJAX 
		 enabled or able to force a submit on open.
		-->
		<xsl:choose>
			<xsl:when test="*">
				<xsl:apply-templates select="*">
					<xsl:with-param name="myTable" select="$myTable"/>
					<xsl:with-param name="parentIsClosed" select="$parentIsClosed"/>
					<xsl:with-param name="indent" select="$indent"/>
					<xsl:with-param name="topRowIsStriped" select="$topRowIsStriped"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="tableId" select="$myTable/@id"/>
				<tr id="{concat($tableId,'_sub',../@rowIndex)}" hidden="hidden"></tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Transform for ui:content child of a ui:subtr.
	-->
	<xsl:template match="ui:subtr/ui:content">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="indent" select="0"/>

		<xsl:variable name="tableId" select="$myTable/@id"/>
		<!--NOTE: aria-level the minimum is going to be level 2 -->
		<tr id="{concat($tableId,'_subc',../../@rowIndex)}" role="row" aria-level="{count(ancestor::ui:subtr[ancestor::ui:table[1]/@id eq $tableId]) + 1}">
			<xsl:if test="number($parentIsClosed) eq 1 or ancestor::ui:subtr[not(@open)]">
				<xsl:call-template name="hiddenElement"/>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="number($topRowIsStriped) eq 1">
						<xsl:text>wc_table_stripe</xsl:text>
					</xsl:if>
					<xsl:if test="number($indent) gt 0">
						<xsl:value-of select="concat(' wc_tbl_indent_', $indent)"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>

			<!--
				subTr content is never individually selectable but must have the placeholder if the table has row
				selection.
			-->
			<xsl:if test="$myTable/ui:rowselection">
				<td class="wc_table_sel_wrapper">
					<xsl:text>&#x2002;</xsl:text>
				</td>
			</xsl:if>

			<!--
				subTr content is not itself expandable but must have the placeholder to position it correctly relative
				to its parent row.
			-->
			<td class="wc_table_rowexp_container">
				<xsl:text>&#x2002;</xsl:text>
			</td>

			<td>
				<xsl:if test="@spanAllCols">
					<xsl:attribute name="colspan">
						<xsl:value-of select="count(../../*) -1"/><!-- -1 because we do not count the ui:subtr -->
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
