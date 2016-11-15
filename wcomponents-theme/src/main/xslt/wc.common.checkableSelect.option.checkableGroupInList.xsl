<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.checkableSelect.n.checkableSelectOption.xsl"/>
	<!--
		Transforms each option which is in a column
		
		param firstItemAccessKey
		The accessKey (if any) to apply to the first option in a group. We apply the
		checkable group access key to the first option in the group rather than to the
		legend of the surrounding fieldset as it is common for the legend to be
		rendered off screen.
		
		param name
		The name to be applied to each option. This is based on the parent element's
		id so we generate it once in the parent template and pass it in.
		
		param type "radio" or "checkbox"
		The HTML input element type attribute's value. This is the one parameter which
		makes a difference between a radioButtonSelect and a checkBoxSelect
		
		param readOnly
		Indicates whether the whole checkable group is read only.
	-->
	<xsl:template match="ui:option" mode="checkableGroupInList">
		<xsl:param name="firstItemAccessKey" select="''"/>
		<xsl:param name="inputName"/>
		<xsl:param name="type"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:if test="number($readOnly) eq 0 or @selected">
			<xsl:variable name="localAccessKey">
				<xsl:if test="position() eq 1 and $firstItemAccessKey ne ''">
					<xsl:value-of select="$firstItemAccessKey"/>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="checkableSelectOption">
				<xsl:with-param name="optionName" select="$inputName"/>
				<xsl:with-param name="optionType" select="$type"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
				<xsl:with-param name="cgAccessKey" select="$localAccessKey"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
