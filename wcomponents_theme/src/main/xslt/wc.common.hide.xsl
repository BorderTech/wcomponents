<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Templates used to hide components in the UI. This template must never be excluded.
		Hides an element from all user agents by setting the hidden attribute to "hidden".

		This should not be called for the top level element of most components, instead
		use template "hideElementIfHiddenSet" which tests if a component should be hidden.

		This direct call to hide will hide the target element from all conforming AT
		and conforming CSS aware browsers. In HTML5 aware browsers it will hide the
		element without CSS but this support is patch as of July 2013.

		An element which is hidden is hidden from conforming AT. A component which
		should be available to AT but not part of the visible screen render should be
		moved out of the viewport. There is a theme class goverened by an ANT property
		to do this if necessary.
	-->
	<xsl:template name="hiddenElement">
		<xsl:attribute name="hidden">
			<xsl:text>hidden</xsl:text>
		</xsl:attribute>
	</xsl:template>

	<!--
		Hides an HTML element from all user agents if the XML element's hidden
		attribute is set 'true'. This is set from setHidden(true) in UI WComponents
		<<not>> from setVisible(false).

		Note on the output of  ui:label

		If a WLabel has its @hidden attribute set "true" it will not be hidden but moved
		out of viewport. A label will be be hidden if the component it is labelling is
		hidden. If both the WLabel and its labelled component are hidden then the label
		will be out of viewport and hidden such that if the component is shown (using
		subordinate) then the label will remain out of viewport but will become
		available to users of supporting AT. This is outlined in more detail in
		wc.ui.label.xsl.
	-->
	<xsl:template name="hideElementIfHiddenSet">
		<xsl:if test="@hidden">
			<xsl:call-template name="hiddenElement"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
