<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.collapsibleToggle.xsl"/>
	<!--
		WCollapsibleToggle is output as a list of buttons. The list is styled to appear
		in a single line The buttons are styled to remove their button appearance.
		
		The WCollapsibleToggle is a WAI-ARIA radiogroup. Each button is a radio. When
		neither button is pressed then the controls state is mixed.
		
		The state of each control in a WCollapsibleToggle is indicated by its aria-checked
		state. This is true when the control has been invoked. A callback from each
		controlled WCollapsible can also update this state.
	-->
	<xsl:template match="ui:collapsibletoggle">
		<xsl:call-template name="collapsibleToggle"/>
	</xsl:template>
</xsl:stylesheet>
