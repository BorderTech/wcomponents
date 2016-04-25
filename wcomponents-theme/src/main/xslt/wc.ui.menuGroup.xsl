<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:include href="wc.common.separator.xsl"/>
	<!--
		Transform for WMenuItemGroup. It is strongly recommended that you do not
		use this component but use {{{./wc.ui.submenu.html}WSubMenu}} or {{{./wc.ui.separator.html}WSeparator}}
		instead.
		
		The rationale for this transform is from the 
		{{{http://www.w3.org/TR/wai-aria-practices/#menu}WAI-ARIA authoing practices for a Menu Widget}}:
		
		"Grouping of menuitems in a menu or menubar is performed by introducing an
		element with the role of separator. A separator delineates groups of menu 
		items within a menu or menubar. A separator is not placed in the navigation 
		order and it is in no way interactive. Authors provide an aria-orientation 
		consistent with the separator's orientation in the menu or menubar. Since 
		the separator is not navigable it does not support the aria-expanded state. 
		It is recommended that when grouping menu items of type menuitemradio that 
		they all be grouped together preceding or following a separator."
	-->
	<xsl:template match="ui:menugroup">
		<xsl:call-template name="separator"/>
		<xsl:apply-templates select="*[not(self::ui:decoratedlabel)]"/>
		<xsl:call-template name="separator"/>
	</xsl:template>
</xsl:stylesheet>
