<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!-- Key used to find a component's label. Pass in the component's id and key will provide a list of labels with matching for attribute. -->
	<xsl:key name="labelKey" match="//ui:label" use="@for"/>
</xsl:stylesheet>
