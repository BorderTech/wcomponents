<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Tabs should not be grouped in the UI. There is no notion of a tabGroup in any
		real tabset, nor is there a facility for subgroupings of elements with a role
		of tab in WAI-ARIA.
	-->	
	<xsl:template match="ui:tabgroup"/>
</xsl:stylesheet>
