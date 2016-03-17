<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Apply the tab content. If the tab has no content generate a dummy content node
		to hang AJAX/server functionality off.
	-->
	<xsl:template match="ui:tab" mode="content">
		<xsl:param name="tabset"/>

		<xsl:apply-templates select="ui:tabcontent">
			<xsl:with-param name="tabset" select="$tabset"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>
