<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		ui:label making helper. 
		This helper provides a class attribute. 
		
		param element: the XML element the ui:label is 'for' (not necessarily
		a labellable element and not necessarily set so must be tested).
		
		param readOnly: The read only state of the 'for' element. This has
		already been calculated for other purposes for we just pass it in rather
		than recalculating. If set it will be passed in as xsl:number 1.
		
		param elementType: the HTML element name of the element. Again this has 
		either been calculated (in makeLabel) or is a span. So we default to 
		span and only pass this in from makeLabel.
	-->
	<xsl:template name="labelClassHelper">
		<xsl:param name="element"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:param name="elementType" select="'span'"/>
		<xsl:attribute name="class">
			<xsl:text>label</xsl:text>
			<xsl:if test="@hidden">
				<xsl:text> wc_off</xsl:text>
			</xsl:if>
			<xsl:if test="$readOnly!=1 and $element and $element/@required">
				<xsl:text> wc_req</xsl:text>
			</xsl:if>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
