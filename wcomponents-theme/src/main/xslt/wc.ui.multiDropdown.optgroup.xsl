<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Transform for optgroups within a list of options in the multiDropdown.

		param option: The currently selected option in the template applying this
			template. This is passed through to the options in this optgroup so
			that if the currently selected option is in this optgroup it will be 
			selected in the final transformed select element.
		
		param isSingular: If no options are selected in the parent multiDropdown
			this is 1. It is passed through to the options in this optgroup.
	-->
	<xsl:template match="ui:optgroup" mode="mfcInList">
		<xsl:param name="selectedOption"/>
		<xsl:param name="isSingular" select="0"/>
		<optgroup label="{@label}">
			<xsl:apply-templates select="ui:option" mode="mfcInList">
				<xsl:with-param name="selectedOption" select="$selectedOption"/>
				<xsl:with-param name="isSingular" select="$isSingular"/>
			</xsl:apply-templates>
		</optgroup>
	</xsl:template>
</xsl:stylesheet>
