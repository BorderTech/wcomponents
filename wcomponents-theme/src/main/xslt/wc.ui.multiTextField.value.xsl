<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.multiTextField.n.multiTextFieldInput.xsl"/>
	<xsl:import href="wc.common.multiFormComponent.n.multiFieldIcon.xsl"/>
	<!--
		Transform for each value in a multiTextField.
	-->
	<xsl:template match="ui:value">
		<li>
			<xsl:call-template name="multiTextFieldInput"/>
			<xsl:call-template name="multiFieldIcon"/>
		</li>
	</xsl:template>
</xsl:stylesheet>
