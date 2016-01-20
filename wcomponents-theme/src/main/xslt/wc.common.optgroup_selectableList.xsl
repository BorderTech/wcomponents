<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Tranforms the optgroups of a list into HTML optgroup elements.
	-->
	<xsl:template match="ui:optgroup" mode="selectableList">
		<optgroup label="{@label}">
			<xsl:apply-templates mode="selectableList"/>
		</optgroup>
	</xsl:template>
</xsl:stylesheet>
