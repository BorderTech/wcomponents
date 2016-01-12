<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	
	<!-- 
		Repesents the options in a combo: WDropdown type COMBO or WSuggestions.
	-->
	<xsl:template name="fauxOption">
		<xsl:param name="value" select="@value"/>
		<li data-wc-value="{$value}" role="option" tabIndex="0">
			<xsl:value-of select="$value"/>
		</li>
	</xsl:template>
</xsl:stylesheet>
