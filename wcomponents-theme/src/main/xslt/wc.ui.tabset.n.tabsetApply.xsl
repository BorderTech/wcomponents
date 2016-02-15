<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	
	<!-- 
		Another template to make overriding easier.
	
		This template determines what we are going to apply when we apply templates in a 
		ui:tabset. This is to allow implementations to maintain support for 
		sub-components which have been removed from the core because of a11y or
		usability concerns but which continue to be in the Java API for backwards
		compatibility with old applications.
		
		Called from named template doTabList in wc.ui.tabset.n.doTabList.xsl
	-->
	<xsl:template name="tabsetApply">
		<xsl:param name="firstOpenTab"/>
		<xsl:apply-templates select="ui:tab|ui:tabgroup/ui:tab">
			<xsl:with-param name="tabset" select="."/>
			<xsl:with-param name="firstOpenTab" select="$firstOpenTab"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>
