<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<xsl:import href="wc.common.checkableSelect.n.checkableSelectOptionLabel.xsl"/>

	<!--
		This template transforms the content of an option to a list item and the
		element relevent for the option.

		Uses checkableSelectOptionLabel

		NOTE:

		This template assumes that the options in a WCheckBoxGroup or WRadioButtonGroup
		are NEVER in an optgroup. This assumption may be flawed.

		param name: The name to be applied to each option. This is based on the parent element's
		id so we generate it once in the parent template and pass it in.

		param type: "radio" or "checkbox"
		The HTML input element type attribute's value. This is the one parameter which
		makes a difference between a radioButtonSelect and a checkBoxSelect

		param readOnly
		Indicates whether the whole checkable group is read only.

		param accessKey
		The accessKey (if any) to apply to the first option in a group. We apply the
		checkable group access key to the first option in the group rather than to the
		legend of the surrounding fieldset as it is common for the legend to be
		rendered off screen.
	-->
	<xsl:template name="checkableSelectOption">
		<xsl:param name="optionName"/>
		<xsl:param name="optionType"/>
		<xsl:param name="readOnly"/>
		<xsl:param name="cgAccessKey"/>
		<xsl:variable name="uid">
			<xsl:value-of select="concat(../@id,generate-id())"/>
		</xsl:variable>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="$readOnly=1">
					<xsl:text>li</xsl:text>
				</xsl:when>
				<xsl:when test="../@layout='flat'">
					<xsl:text>span</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>div</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:choose>
				<xsl:when test="$readOnly=1">
					<xsl:call-template name="checkableSelectOptionLabel"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="input">
						<xsl:attribute name="type">
							<xsl:value-of select="$optionType"/>
						</xsl:attribute>
						<xsl:attribute name="id">
							<xsl:value-of select="$uid"/>
						</xsl:attribute>
						<xsl:attribute name="name">
							<xsl:value-of select="$optionName"/>
						</xsl:attribute>
						<xsl:attribute name="value">
							<xsl:value-of select="@value"/>
						</xsl:attribute>
						<xsl:if test="../@submitOnChange">
							<xsl:attribute name="class">
								<xsl:text>wc_soc</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isNull and $optionType='radio'">
							<xsl:attribute name="${wc.common.attribute.optionIsNull}">
								<xsl:text>1</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="$cgAccessKey!=''">
							<xsl:attribute name="accesskey">
								<xsl:value-of select="$cgAccessKey"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@selected">
							<xsl:attribute name="checked">checked</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
							<xsl:with-param name="field" select="parent::*"/>
						</xsl:call-template>
						<xsl:if test="parent::ui:radiobuttonselect">
							<xsl:call-template name="requiredElement">
								<xsl:with-param name="field" select="parent::ui:radiobuttonselect"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:element>
					<label for="{$uid}">
						<xsl:call-template name="checkableSelectOptionLabel"/>
					</label>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
