<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Each option in a multiDropdown called from within the list of selected 
		options.
		
		param option: The currently selected option which was the source of the 
			apply-templates call which calls this template. This is the selected
			option for this select element.
		
		param isSingular: When there are no selected options in the multiDropdown
			this is 1 and no options are selected.
	-->
	<xsl:template match="ui:option" mode="mfcInList">
		<xsl:param name="selectedOption"/>
		<xsl:param name="isSingular" select="0"/>
		<xsl:element name="option">
			<xsl:attribute name="value">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
			<xsl:if test=". eq $selectedOption and number($isSingular) eq 0">
				<xsl:attribute name="selected">
					<xsl:text>selected</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@isNull">
				<xsl:attribute name="data-wc-null">
					<xsl:text>1</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
