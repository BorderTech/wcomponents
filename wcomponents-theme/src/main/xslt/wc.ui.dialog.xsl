<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		We do not currently implement native dialogs in any browser due to a failure in
		the implementation of the dialogs in Internet Explorer. This may be addessed in
		future releases and will have implications on these transforms. We hope to be
		able to use native dialogs and remove the need for all of this custom code,
		along with the associated JavaScript and CSS..
	-->
	<xsl:template match="ui:dialog"/>
	
	<xsl:template match="ui:dialog[ui:button]">
		<xsl:apply-templates select="ui:button"/>
	</xsl:template>
</xsl:stylesheet>
