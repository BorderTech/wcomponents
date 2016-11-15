<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	
	<!-- 
		Repesents the options in a combo: WDropdown type COMBO or WSuggestions.
	-->
	<xsl:template name="fauxOption">
		<xsl:param name="value" select="@value"/>
		<span data-wc-value="{$value}" role="option" class="wc-invite" tabIndex="0">
			<xsl:value-of select="$value"/>
		</span>
	</xsl:template>
</xsl:stylesheet>
