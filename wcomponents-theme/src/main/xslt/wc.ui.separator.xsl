<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.separator.xsl"/>
	<!--
		Transforms for WSeparator. The output of these transforms is dependant upon
		where the separator lies in the XML tree.
	
		It is very common for an implementation to change separator output and appearance.
		Changing the standard HTML HR element to a transparent container is a common
		change as it then spaced out the menu items/tabs without the extra visual
		clutter. If your implementation updates the XSLT for ui:separator you should
		ensure that the top level HTML element you output has its role attribute
		set to "separator" and all styling is based on this role.
		
		If the separator renders vertically then it <<must>> have the attribute
		aria-orientation set to "vertical".
	-->
	<xsl:template match="ui:separator">
		<xsl:call-template name="separator"/>
	</xsl:template>
</xsl:stylesheet>
