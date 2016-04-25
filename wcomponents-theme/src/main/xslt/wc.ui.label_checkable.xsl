<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.label.n.makeLabel.xsl"/>
	<!--
		A Label for a WRadioButton or WCheckBox. This template mode is called
		from the transforms of ui:checkbox and ui:radiobutton to ensure that the
		label immediately follows the checkbox/radiobutton as per WCAG requirements.
		See wc.common.checkableInput.xsl.
	-->
	
	<xsl:template match="ui:label" mode="checkable">
		<xsl:param name="labelableElement"/>
		<xsl:call-template name="makeLabel">
			<xsl:with-param name="labelableElement" select="$labelableElement"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
