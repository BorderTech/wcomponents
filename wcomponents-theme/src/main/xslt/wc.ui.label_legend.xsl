<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.ui.label.n.WLabelHint.xsl"/>

	<!--
		This is used to generate a legend for a component which has a fieldset wrapper. The component element is passed
		in as the forElement. This is not called if the component is in a read only state.

		The ui:label is aso transformed in-situ as a faux-label so we MUST NOT output the label ID in this legend. We do
		output accessKey because accesskey is an allowed and functional attribute on a legend element.

		param: labelableELement: the component being labelled. This is always known and does not need to be calculated
		so it is MUCH cheaper to pass it in as a param.
	-->
	<xsl:template match="ui:label" mode="legend">
		<xsl:param name="labelableElement"/>
		<xsl:variable name="submitNotAjaxTrigger">
			<xsl:if test="$labelableElement and $labelableElement/@submitOnChange and count(key('triggerKey',$labelableElement/@id))=0">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="isEmpty">
			<xsl:if test="normalize-space(.)='' and not(.//ui:image)">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="className">
			<xsl:choose>
				<xsl:when test="$isEmpty = 1">
					<xsl:text>wc_error</xsl:text>
				</xsl:when>
				<xsl:otherwise>wc_off</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<legend class="{$className}">
			<xsl:call-template name="accessKey"/>
			<xsl:if test="$isEmpty = 1">
				<xsl:value-of select="$$${wc.common.i18n.requiredLabel}"/>
			</xsl:if>
			<xsl:apply-templates />
			<xsl:call-template name="WLabelHint">
				<xsl:with-param name="submitNotAjaxTrigger" select="$submitNotAjaxTrigger"/>
			</xsl:call-template>
			<xsl:if test="$labelableElement and $labelableElement/@required">
				<xsl:call-template name="offscreenSpan">
					<xsl:with-param name="text">
						<xsl:value-of select="$$${wc.common.i18n.requiredPlaceholder}"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</legend>
	</xsl:template>
</xsl:stylesheet>
