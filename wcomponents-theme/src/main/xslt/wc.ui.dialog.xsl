<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		We do not currently implement native dialogs in any browser due to a failure in
		the implementation of the dialogs in Internet Explorer. This may be addessed in
		future releases and will have implications on these transforms. We hope to be
		able to use native dialogs and remove the need for all of this custom code,
		along with the associated JavaScript and CSS..
	
		Child elements
		* ui:button which is used to launch the dialog.
		* ui:content if the dialog is to launch on page load.
		These are both optional and not mutually exclusive.
	-->
	<xsl:template match="ui:dialog"/>
	
	<xsl:template match="ui:dialog[ui:button and not(ui:content)]">
		<xsl:apply-templates select="ui:button"/>
	</xsl:template>
</xsl:stylesheet>
