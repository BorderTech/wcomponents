<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		ui:label making helper. 
		This helper provides a class attribute. 
		
		param element: the XML element the ui:label is 'for' (not necessarily set so must be tested).
		
		param readOnly: The read only state of the 'for' element. This has already been calculated for other purposes 
		so we just pass it in rather than recalculating. If set it will be passed in as xsl:number 1.
	-->
	<xsl:template name="labelClassHelper">
		<xsl:param name="element"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:attribute name="class">
			<xsl:call-template name="commonClassHelper"/>
			<xsl:if test="@hidden">
				<!--
				If a WLabel has its @hidden attribute set "true" it will not be hidden but moved out of viewport. A 
				label will be be hidden if the component it is labelling is hidden. If both the WLabel and its labelled
				component are hidden then the label will be out of viewport and hidden such that if the component is 
				shown (using subordinate) then the label will remain out of viewport but will become available to users
				of supporting AT. This is outlined in more detail in wc.ui.label.xsl. -->
				<xsl:text> wc_off</xsl:text>
			</xsl:if>
			<xsl:if test="$readOnly!=1 and $element and $element/@required">
				<xsl:text> wc_req</xsl:text>
			</xsl:if>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
