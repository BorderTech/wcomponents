<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		Transform of ui:subTr. This is a sub row element which is an optional child of
		a ui:tr element. It should not be present if the table does not have
		rowExpansion. In HTML these are siblings of their parent ui:tr which makes row
		manipulation interesting.
		
		Warning
		Client mode row expansion and client mode pagination are currently incompatible.
		
		param myTable: The first table ancestor of the current row. This is determined
		at the most efficient point (usually ui:tbody using its parent node) and then
		passed through all subsequent transforms to save constant ancestor::ui:table[1]
		lookups.
		
		param parentIsClosed default 0, just passed thourgh to descendants.
		
		param maxIndent: see notes in transform for ui:table in wc.ui.table.xsl.
	-->
	<xsl:template match="ui:subTr">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="maxIndent" select="0"/>
		<xsl:variable name="tableId" select="$myTable/@id"/>
		<!--
		 We have to output content if:
		 
			* she subTr is open;
			* the expansion mode is client;
			* there is a ui:content child element; or
			* there are ui:tr child elements.
		 
		 Otherwise we have to create a null content placeholder with the appropriate
		 wires to make the expansion AJAX enabled or able to force a submit on open.
		-->
		<xsl:choose>
			<xsl:when test="*">
				<xsl:apply-templates select="*">
					<xsl:with-param name="myTable" select="$myTable"/>
					<xsl:with-param name="parentIsClosed" select="$parentIsClosed"/>
					<xsl:with-param name="maxIndent" select="$maxIndent"/>
					<xsl:with-param name="topRowIsStriped" select="$topRowIsStriped"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="tr">
					<xsl:attribute name="id">
						<xsl:value-of select="concat($tableId,'${wc.ui.table.id.subTr.suffix}',../@rowIndex)"/>
					</xsl:attribute>
					<xsl:attribute name="aria-level">
						<xsl:value-of select="count(ancestor::ui:subTr[ancestor::ui:table[1]/@id=$tableId]) + 2"/>
					</xsl:attribute>
					<xsl:call-template name="hiddenElement"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
