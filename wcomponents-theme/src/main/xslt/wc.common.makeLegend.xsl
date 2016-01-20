<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<!--
	Helper template to make a fieldset legend. Used by the following components:
		WCheckBoxSelect
		WFileWidget
		WMultiDropdown
		WMultiSelectPair
		WMultiTextField
		WRadioButtonSelect

	Creates a legend element. If the component has a WLabel associated with it then
	the legend is created by the transform for that WLabel using the explicit param.

	Otherwise a legend element is created and populated as follows:

		1 if the element has its accessibleText property set then the legend uses
		this text as its content and is moved out of viewport; otherwise

		2 the legend has the default label text as its content and is left on
		screen as this is an error state for WComponents as it contravenes our
		attempts to implement accessibility guidelines. Whilst a fieldset is not a
		labellable element it does require a legend (in HTML5) and the most
		appropriate means to create a legend is by using a WLabel.

	param myLabel: the WLabel "for" the calling component, if any. This will have already
	been determined before calling this template so we do not have to re-interrogate the
	label key.
-->
	<xsl:template name="makeLegend">
		<xsl:param name="myLabel"/>
		<xsl:choose>
			<xsl:when test="$myLabel">
				<xsl:apply-templates select="$myLabel" mode="legend">
					<xsl:with-param name="labelableElement" select="."/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="@toolTip">
				<xsl:call-template name="makeTextLegend">
					<xsl:with-param name="content">
						<xsl:value-of select="normalize-space(@toolTip)"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@accessibleText">
				<xsl:call-template name="makeTextLegend">
					<xsl:with-param name="content">
						<xsl:value-of select="normalize-space(@accessibleText)"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="not(ancestor::ui:ajaxTarget)">
				<xsl:call-template name="makeTextLegend">
					<xsl:with-param name="content">
						<xsl:value-of select="$$${wc.common.i18n.requiredLabel}"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="makeTextLegend">
		<xsl:param name="content"/>
		<legend class="wc_off">
			<xsl:value-of select="$content"/>
			<xsl:if test="@required">
				<xsl:call-template name="offscreenSpan">
					<xsl:with-param name="text">
						<xsl:value-of select="$$${wc.common.i18n.requiredPlaceholder}"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</legend>
	</xsl:template>
</xsl:stylesheet>
