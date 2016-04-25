<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<!--
		This template produces the add and remove buttons used in multiSelectPair.
	
		param value: The value attribute is used to determine the function of the
			button.
		param buttonText: The text placed into the button's title attribute to 
			provide text equivalence of the button's function.
	-->
	<xsl:template name="multiSelectPairButton">
		<xsl:param name="value"/>
		<xsl:param name="buttonText"/>
		<button type="button" value="{$value}" title="{$buttonText}" class="wc_btn_icon" aria-controls="{concat(@id, '_a',' ',@id, '_s')}">
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="1"/>
			</xsl:call-template>
		</button>
	</xsl:template>
</xsl:stylesheet>
