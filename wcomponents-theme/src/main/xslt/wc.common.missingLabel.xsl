<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		A labelable component must be associated with one of:
			WLabel
			toolTip
			accessibleText (deprecated in favour of toolTip)

		If the component does not have a label or toolTip and is not part of
		an ajaxResponse then we test for the presence of the accessibleText
		attribute If this attribute is set we use it to create a label for the
		component. If the accessibleText attribute is not set then a placeholder
		warning label will be created if the component.

		param id:
		The component id, defaults to @id

		param force:
		If this is set to 1 then the label test is omitted. This is useful if
		the label has already been computed for other purposes and found to be
		missing as it prevents a second large key lookup.
	-->
	<xsl:template name="checkLabel">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="for" select="@id"/>
		<xsl:param name="force" select="0"/>
		<xsl:if test="not(@toolTip or @accessibleText or ancestor::ui:ajaxTarget or ancestor::ui:label[not(@for)]) and (force=1 or not(key('labelKey',$id))) ">
			<!-- We give these labels an id so that they will be cleaned up by the AJAX duplicate id checker.-->
			<label class="wc_error" id="{concat($for,'dlbl')}" for="{$for}">
				<xsl:value-of select="$$${wc.common.i18n.requiredLabel}"/>
			</label>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
