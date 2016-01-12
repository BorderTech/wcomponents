<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		A row expander control needs to know what it controls. This could be a set of rows, including other	subRows. 
		Since the rows are siblings they must be controlled individually. This template outputs ids of all rows which 
		are controlled by a rowExpansion control. This is also used by WAI-ARIA to indicate the DOM nodes controlled by 
		the expander. The list is space separated.
	-->
	<xsl:template match="ui:tr" mode="subRowControlIdentifier">
		<xsl:param name="tableId"/>
		<xsl:value-of select="concat($tableId,'-',@rowIndex)"/>
		<xsl:if test="position()!=last()">
			<xsl:value-of select="' '"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
