<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.borderLayout.n.borderLayoutCell.xsl"/>
	<!--
		The transform for north and south elements within a ui:borderlayout. These	cells
		are simple divs. The actual creation of the HTML elements is passed off to the
		helper template borderLayoutCell.
	-->
	<xsl:template match="ui:north|ui:south">
		<xsl:param name="vgap" select="0"/>
		<xsl:call-template name="borderLayoutCell">
			<xsl:with-param name="vgap">
				<xsl:value-of select="$vgap"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
