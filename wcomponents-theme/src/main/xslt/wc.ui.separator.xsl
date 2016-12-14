<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.separator.xsl"/>
	<xsl:template match="ui:separator">
		<xsl:call-template name="separator"/>
	</xsl:template>
</xsl:stylesheet>
