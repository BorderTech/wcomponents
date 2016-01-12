<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Templates used to hide components in the UI. 
		
		This template must never be excluded.
		
		
		Hides an element from all user agents by setting the hidden attribute to "hidden".

		This should not be called for the top level element of most components, instead use template 
		"hideElementIfHiddenSet" which tests if a component should be hidden.

		This direct call to hide will hide the target element from all conforming AT and conforming CSS aware browsers. 
		In HTML5 aware browsers it will hide the element without CSS but this support is patch as of Jan 2016.

		An element which is hidden is hidden from conforming AT. A component which should be available to AT but not 
		part of the visible screen render should be moved out of the viewport.
	-->
	<xsl:template name="hiddenElement">
		<xsl:attribute name="hidden">
			<xsl:text>hidden</xsl:text>
		</xsl:attribute>
	</xsl:template>

	<!--
		Hides an HTML element from all user agents if the XML element's hidden attribute is set 'true'.
	-->
	<xsl:template name="hideElementIfHiddenSet">
		<xsl:if test="@hidden">
			<xsl:call-template name="hiddenElement"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
