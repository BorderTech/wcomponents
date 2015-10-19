<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
	This helper template has been split out from the ui:panel match to allow for
	implementation overrides of the structure and order of components in the 
	content. The most common of these is to differentiate the output of Types
	CHROME and ACTION as these are designated as decorative panels with visible
	headings.
	
	<<Remember:>> do not re-apply the ui:margin child.
-->
	<xsl:template name="WPanelContentPrep">
		<xsl:apply-templates select="*[not(self::ui:margin)]"/>
	</xsl:template>
</xsl:stylesheet>
