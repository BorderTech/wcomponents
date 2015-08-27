<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">	
	<!--
		Outputs nested ui:option elements. Optgroups are not supported in 
		datalist.
	-->	
	<xsl:template match="ui:optgroup" mode="comboDataList">
		<xsl:apply-templates mode="comboDataList"/>
	</xsl:template>
</xsl:stylesheet>
