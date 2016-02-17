<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		Transform of ui:subtr. This is a sub row element which is an optional child of
		a ui:tr element. It should not be present if the table does not have
		rowExpansion. In HTML these are siblings of their parent ui:tr which makes row
		manipulation interesting.
		
		Warning
		Client mode row expansion and client mode pagination are currently incompatible.
	-->
	<xsl:template match="ui:subtr">
		<xsl:param name="myTable"/>
		<xsl:param name="parentIsClosed" select="0"/>
		<xsl:param name="topRowIsStriped" select="0"/>
		<xsl:param name="indent" select="0"/>
		<xsl:param name="hasRole" select="0"/>
		
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
					<xsl:with-param name="indent" select="$indent"/>
					<xsl:with-param name="topRowIsStriped" select="$topRowIsStriped"/>
					<xsl:with-param name="hasRole" select="$hasRole" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="tableId" select="$myTable/@id"/>
				<tr id="{concat($tableId,'${wc.ui.table.id.subTr.suffix}',../@rowIndex)}" hidden="hidden"></tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
