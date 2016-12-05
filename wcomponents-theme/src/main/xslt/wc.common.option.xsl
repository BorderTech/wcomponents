<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Option is a child of many components which derive from AbstractList:
			wc.ui.dropdown.xsl
			wc.ui.multiFormComponent.xsl
			wc.ui.multiSelectPair.xsl
			wc.common.optgroup.xsl
			wc.ui.shuffler.xsl

		Null template for unmoded ui:option elements. This should never be invoked but
		is here for completeness.
	-->
	<xsl:template match="ui:option"/>
</xsl:stylesheet>
