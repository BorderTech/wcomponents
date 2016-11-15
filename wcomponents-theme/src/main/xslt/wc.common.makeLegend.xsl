<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<!--
	Helper template to make a fieldset legend. Used by the following components:
		WCheckBoxSelect
		WFileWidget
		WMultiDropdown
		WMultiSelectPair
		WMultiTextField
		WRadioButtonSelect

	Creates a legend element. If the component has a WLabel associated with it then the legend is created by the
	transform for that WLabel using the explicit param. If the component does not have a WLabel then its legend is
	populated using a text equivalent fallback @accessibleText or @toolTip.

	param myLabel: the WLabel "for" the calling component, if any. This will have already been determined before calling
	this template so we do not have to re-interrogate the label key.
-->
	<xsl:template name="makeLegend">
		<xsl:param name="myLabel"/>
		<xsl:choose>
			<xsl:when test="$myLabel">
				<xsl:call-template name="title"/>
				<xsl:apply-templates select="$myLabel" mode="legend">
					<xsl:with-param name="labelableElement" select="."/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="@accessibleText">
				<xsl:call-template name="title"/>
				<xsl:call-template name="makeTextLegend">
					<xsl:with-param name="content">
						<xsl:value-of select="normalize-space(@accessibleText)"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@toolTip">
				<xsl:call-template name="makeTextLegend">
					<xsl:with-param name="content">
						<xsl:value-of select="normalize-space(@toolTip)"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="not(ancestor::ui:ajaxtarget)">
				<legend class="wc_error">
					<xsl:text>{{t 'requiredLabel'}}</xsl:text>
				</legend>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="makeTextLegend">
		<xsl:param name="content"/>
		<legend class="wc-off">
			<xsl:value-of select="$content"/>
			<xsl:if test="@required">
				<xsl:call-template name="offscreenSpan">
					<xsl:with-param name="text">
						<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</legend>
	</xsl:template>
</xsl:stylesheet>
