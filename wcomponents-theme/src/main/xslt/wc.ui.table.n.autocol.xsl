<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Creates a caption element if required. Called from the transform for ui:table.
	-->
	<xsl:template name="autocol">
		<col class="wc_table_colauto"></col>
	</xsl:template>
</xsl:stylesheet>
