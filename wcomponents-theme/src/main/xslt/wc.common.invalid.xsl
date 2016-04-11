<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Mark a component as invalid using the WAI-ARIA state aria-invalid.

		This attribute is used by conforming AT to provide an indication to the user that
		the component is in an invalid state. CSS is used to provide an indication to
		non-AT users. This CSS should always use an aria-invalid="true" attribute selector
		unless the target browser does not support attribute selectors. If your
		implementation uses any other mechanism to indicate an invalid state in CSS you
		must implement a mechanism to ensure the aria-invalid attribute and the alternate
		mechanism are kept in sync.

		It is insufficient to provide only this state on the control. In order to meet
		the requirements of WCAG 2.0 to level A compliance an indication of the error
		must also be output in text and this text must be associated with the component
		in an error state.
	-->
	<xsl:template name="invalid">
		<xsl:param name="id" select="@id"/>
		<xsl:attribute name="aria-invalid">
			<xsl:copy-of select="$t"/>
		</xsl:attribute>
		<xsl:attribute name="aria-describedby">
			<xsl:value-of select="concat($id,'_err')"/>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
