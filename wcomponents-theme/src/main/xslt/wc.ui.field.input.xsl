<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		The input part of WField is a wrapper for other components. FieldIndicators
		should be output after the functional content of the input.

		param isCheckRadio (1/0)
		Indicates whether the WField is a container for a WCheckBox, WRadioButton
		or a WSelectToggle (renderAs not control) as the primary input control,
		if so we need to make a few adjustments to the output order. We have to
		calculate this in ui:field so we may as well pass it in.
	-->
	<xsl:template match="ui:input">
		<xsl:param name="isCheckRadio"/>
		<div>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:choose>
				<xsl:when test="$isCheckRadio!=1">
					<xsl:apply-templates select="node()[not(self::ui:fieldindicator)]"/>
					<xsl:apply-templates select="ui:fieldindicator"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="ui:checkbox|ui:radiobutton|ui:selecttoggle"/>
					<xsl:apply-templates select="node()[not(self::ui:fieldindicator or self::ui:checkbox or self::ui:radiobutton or self::ui:selecttoggle)]"/>
					<xsl:apply-templates select="ui:fieldindicator"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>
</xsl:stylesheet>
