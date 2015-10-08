<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.table.n.getPreCollapserIndent.xsl"/>

	<!--
	Creates a table cell with a colspan to indent the cells of a hierarchic table
	sub row. Called from templates for ui:tr and ui:subTr/ui:content.
	-->
	<xsl:template name="indentCells">
		<xsl:param name="myTable"/><!-- nearest ancestor ui:table -->
		<xsl:param name="maxIndent" select="0"/><!-- see wc.ui.table.xsl -->

		<!--
		 Calculates how many indentation columns were input before we got to the current
		 row. This is used to put the collapser into the right spot in the column
		 hierarchy by spacing a cell before it.
		-->
		<xsl:variable name="indentBy">
			<xsl:call-template name="getPreCollapserIndent">
				<xsl:with-param name="maxIndent" select="$maxIndent"/>
				<xsl:with-param name="myTable" select="$myTable"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="$indentBy &gt; 0">
			<xsl:element name="td">
				<xsl:attribute name="colspan">
					<xsl:value-of select="$indentBy"/>
				</xsl:attribute>
				<xsl:text>&#x2002;</xsl:text>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
