<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		This helper template is used to determine the panel wrapper element to make
		implementation specific transforms of WPanel easier and less verbose. It is,
		however, really unlikely you would need to over-ride this template.
		
		param overrideType 
		
		The name of a HTML element to use instead of the default element. This will
		only override the default element not the element produced by Type.HEADER,
		Type.FOOTER, Type.CHROME, Type.ACTION or child element ui:listlayout as these 
		have mandatory element types based on HTML5 structure or intended WAI-ARIA
		landmark roles. 
		
		XSLT DEVELOPERS READ THIS
		
		If you set overrideType then it MUST be set to the name of a HTML 
		element which is part of the HTML5 specification unless you rewrite the entire
		client framework to support another output. 
		
		If you set it to an element which is part of HTML5 but not part of HTML4 you 
		should check the list of elements which are added to Internet Explorer in 
		wc.dom.html5.ie8.js and wc.ui.root.xsl and add the element if it is not in the 
		array in those files (you should copy the file to your implementation directory 
		unless you are a core WComponents developer) and add it to the tag list in your 
		implementation's copy of wc.dom.tag.js. You may also want to add it to the 
		elements in your implementation's copy of wc.dom.html5.css and even as a 
		property in your implementation's copy of wc.dom.html5.properties. This last 
		addition will help keep all of the others in sync since each instance of the 
		element should refer to the ANT property.
	-->
	<xsl:template name="WPanelContainerElement">
		<xsl:param name="overrideType"/>
		<xsl:variable name="type" select="@type"/>
		<xsl:choose>
			<xsl:when test="$type='chrome' or $type='action'">
				<xsl:text>${wc.dom.html5.element.section}</xsl:text>
			</xsl:when>
			<xsl:when test="contains($type,'header')">
				<xsl:text>${wc.dom.html5.element.header}</xsl:text>
			</xsl:when>
			<xsl:when test="contains($type,'footer')">
				<xsl:text>${wc.dom.html5.element.footer}</xsl:text>
			</xsl:when>
			<xsl:when test="$overrideType!=''">
				<xsl:value-of select="$overrideType"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>div</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
