<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.constants.xsl"/>
	<!--
		A labelable component must be associated with one of:
			WLabel
			toolTip
			accessibleText

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
		<xsl:if test="not(@toolTip or @accessibleText or ancestor::ui:ajaxtarget or ancestor::ui:label[not(@for)]) and (number($force) eq 1 or not(key('labelKey',$id))) ">
			<!-- We give these labels an id so that they will be cleaned up by the AJAX duplicate id checker.-->
			<label class="wc-label wc_error" id="{concat($for,'dlbl')}" for="{$for}">
				<xsl:text>{{t 'requiredLabel'}}</xsl:text>
			</label>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
