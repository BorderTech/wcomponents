<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.label.n.labelCommonAttributes.xsl"/>
	<xsl:import href="wc.ui.label.n.labelClassHelper.xsl"/>
	<xsl:import href="wc.ui.label.n.labelHintHelper.xsl"/>
	<!--
		Hhelper template to make a pseudo-label for a component which does not
		transform to a labellable element.

		See the header comment in wc.ui.label.n.makeLabel.xsl: it provides the
		reasoning for this split.

		param forElement: the element the label is 'for' this is pre-calculated
		before calling this template so can never be null.

		param style: passed in ultimately from the transform for ui:field. See
		wc.ui.field.xsl.
	-->
	<xsl:template name="makeFauxLabel">
		<xsl:param name="forElement"/>
		<xsl:param name="style"/>

		<xsl:variable name="readOnly">
			<xsl:if test="$forElement/@readOnly">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>

		<!--
			tabindex -1 is required for IE11 (and possibly earlier) to recognise aria-describedby or aria-labelledby.
			We leave it here untested because it has negligible side effects.
		-->
		<span tabindex="-1" aria-hidden="true">
			<xsl:call-template name="labelCommonAttributes">
				<xsl:with-param name="element" select="$forElement"/>
				<xsl:with-param name="style" select="$style"/>
			</xsl:call-template>

			<xsl:choose>
				<xsl:when test="$readOnly=1">
					<xsl:attribute name="${wc.ui.label.attribute.readonlyFor}">
						<xsl:value-of select="@for"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="data-wc-for">
						<xsl:value-of select="@for"/>
					</xsl:attribute>
					<xsl:call-template name="hideElementIfHiddenSet"/>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:call-template name="labelClassHelper">
				<xsl:with-param name="element" select="$forElement"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
			</xsl:call-template>

			<xsl:apply-templates/>

			<xsl:call-template name="labelHintHelper">
				<xsl:with-param name="element" select="$forElement"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
			</xsl:call-template>
		</span>
	</xsl:template>
</xsl:stylesheet>
