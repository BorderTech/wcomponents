<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.multiTextField.n.multiTextFieldInput.xsl"/>
	<xsl:import href="wc.common.multiFormComponent.n.multiFieldIcon.xsl"/>
	<!--
		Generate the content of the multiTextField. Usually ui:values but if there are
		no values we still need to render a field for inputting a value. When the
		multiTextField has values this is a simple apply-templates and the values look
		after themelves. When there are no values we have to construct a single empty
		value field along with all the associated containers and label.
		
		param readOnly: the read only status of the parent multiTextField.
		param myLabel: the label for the parent multiTextField
	-->
	<xsl:template name="multiTextFieldContentRenderer">
		<xsl:param name="myLabel"/>
		<xsl:choose>
			<xsl:when test="ui:value">
				<xsl:apply-templates>
					<xsl:with-param name="myLabel" select="$myLabel"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<li>
					<xsl:call-template name="multiTextFieldInput"/>
					<xsl:call-template name="multiFieldIcon">
						<xsl:with-param name="isSingular" select="1"/>
						<xsl:with-param name="myLabel" select="$myLabel"/>
					</xsl:call-template>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
