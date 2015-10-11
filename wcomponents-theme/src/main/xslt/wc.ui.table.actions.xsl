<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Table Actions
		see wc.ui.table.action.xsl
	-->
	<xsl:template match="ui:actions">
		<xsl:apply-templates select="ui:action"/>
	</xsl:template>
</xsl:stylesheet>
