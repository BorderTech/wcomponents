<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for ui:fieldindicator which is output of WFieldErrorIndicator and
		WFieldWarningIndicator. This is normally output to provide inline messaging in
		a WField.
	
	
		Implementation Issues
	
		Under certain circumstances this may conflict with messages from
		WValidationErrors. In general WValidationErrors is preferred for error messages
		since they are bound to any control and not just to those in a WField. In addition
		WValidationErrors can be transformed to always show an in-context message whereas
		WFieldErrorIndicator requires that the component be explicitly added for any
		input control which is not added as part of WFieldLayout.addField.
		
		At this point it is assumed that a WField[Warning|Error]Indicator will be applied
		to a WField as part of the control group and, as such, is output as part of the
		ui:field's ui:input child element payload. This allows us to position the field
		indicator within the field so that it is in a place appropriate to the form
		component for which it is providing feedback.
	
	-->
	<xsl:template match="ui:fieldindicator">
		<ul id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="@type"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>
	<!--
		Currently there is a consistency problem with the use of WFieldErrorIndicator
		which results in a possible double-up of error messages. Therefore we
		currently do not output ui:fieldindicator of type 'error' and output
		the ui:error children of WValidationErrors instead.

		This is under investigation.
	-->
	<xsl:template match="ui:fieldindicator[@type='error']"/>
</xsl:stylesheet>
