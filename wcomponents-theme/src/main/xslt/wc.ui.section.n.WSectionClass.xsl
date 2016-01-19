<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.n.className.xsl"/>
	
	<xsl:template name="WSectionClass">
		<xsl:call-template name="commonClassHelper"/>
		<xsl:if test="@mode='lazy' and @hidden">
			<xsl:text> wc_magic</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
