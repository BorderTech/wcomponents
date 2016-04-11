<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Templates for marking up errors and invalid components. This template must
		never be excluded.

		Inline output of error messages if an error exists for the component.

		param errors:
		A node list of ui:error elements. This is always calculated in the caller
		template so that the component can be marked as aria-invalid. We pass it in as
		a param rather than recalculating it here as it is an expensive root-down lookup.

		param id: default @id
		The id of the component. This is only used when the inlineError message is called
		from a component other than the one in the error state and at the moment that
		is only from wc.ui.label.xsl when the labelled component is a WCheckBox
		or WRadioButton (wc.ui.checkableInput.xsl).

		When a form control is in an error state the indication of this state and a
		method to alleviate the error must be provided in text. This is a WCAG 2.0 level
		A accessibility requirement. The control itself is also marked as being in an
		invalid state using the aria-invalid attribute. Since this attribute is provided
		for conforming AT it may be used to style components in a way so as to provide
		a visual indication of the componet being in an error state. This indication
		is in addition to the text output of the error.

		The primary indicator of a component being in an error state is the presence
		of the aria-invalid attribute set to the string "true". The secondary indicator
		of a component being in an error state is the presence of a plain text indicator
		of the nature of the error. A tertiary indicator of a component being in an error
		state may include restyling the component.
	-->
	<xsl:template name="inlineError">
		<xsl:param name="errors"/>
		<xsl:param name="id" select="@id"/>
		<xsl:if test="$errors">
			<xsl:element name="ul">
				<xsl:attribute name="class">
					<xsl:text>error</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="concat($id,'_err')"/>
				</xsl:attribute>
				<xsl:apply-templates select="$errors" mode="inline"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
