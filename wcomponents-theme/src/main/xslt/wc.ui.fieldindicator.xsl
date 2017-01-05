<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		Transform for ui:fieldindicator which is output of WFieldWarningIndicator.
	-->
	<xsl:template match="ui:fieldindicator">
		<span id="{@id}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
</xsl:stylesheet>
