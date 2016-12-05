<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.fauxOption.xsl"/>
<!--
	Output the options in a datalist element for a native HTML5 Combo.
	NOTE: we do not output a name attribute as in WComponents for WDropdown.COMBO
	the name and value are always identical.
-->
	<xsl:template match="ui:option" mode="comboDataList">
		<xsl:call-template name="fauxOption">
			<xsl:with-param name="value">
				<xsl:value-of select="."/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
