<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Helper to determine tab element.
		
		Why do we have this? Because over the history of WComponents there have been
		times when odd designers have wanted tabs to be things other than buttons
		but only for some types of tabset. See the demo xsl for type 'application'.
	-->
	<xsl:template name="tabElement">
		<xsl:text>button</xsl:text>
	</xsl:template>
</xsl:stylesheet>
