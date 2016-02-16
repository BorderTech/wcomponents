<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.column.xsl"/>
	<!--
		Transform for WColumn. We mode this so that any column erroneously placed 
		somewhere other than in a row will simply not be output. 
		
		WRow and WColumn are related to ColumnLayout. ColumnLayout should be used for
		most purposes but does require that in all rows in a single column layout the 
		columns at position n will all have the same width and alignment.
		
		see wc.ui.columnLayout.xsl
	-->
	<xsl:template match="ui:column">
		<xsl:param name="hgap"/>
		<xsl:if test="parent::ui:row or parent::ui:ajaxtarget"><!-- do not allow randomly nested columns -->
			<xsl:call-template name="column">
				<xsl:with-param name="hgap" select="$hgap"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
