<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Creates the class attribute for the outermost `div` wrapper element of a WTable.
	-->
	<xsl:template name="wtableClassName">
		<xsl:call-template name="makeCommonClass"/>
	</xsl:template>
</xsl:stylesheet>
