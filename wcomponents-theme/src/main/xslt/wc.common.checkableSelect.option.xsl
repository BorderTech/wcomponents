<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.checkableSelect.n.checkableSelectOption.xsl"/>
	<!--
		Template to transform the options in a checkable group. There are several
		components which have ui:option child elements. This is why we use modes
		for all options rather than having a file for ui:option. This particular mode
		is called

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
		
		param rows
		The number of options in each column. This must be greater than 0. This
		parameter is calculated once for each checkable group as it is a simple but
		non-trivial calculation requiring an xsl:choose block
		
		param readOnly: Indicates whether the whole checkable group is read only.

		Applying templates
		
		Since we may have to output options in several distinct lists we apply
		templates again here but select following-sibling options with position() less
		than $rows
	-->
	<xsl:template match="ui:option" mode="checkableGroup">
		<xsl:param name="firstItemAccessKey"/>
		<xsl:param name="inputName"/>
		<xsl:param name="type"/>
		<xsl:param name="rows" select="0"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:variable name="firstAccessKey">
			<xsl:if test="position() eq 1">
				<xsl:value-of select="$firstItemAccessKey"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="layout" select="../@layout"/>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:text>ul</xsl:text>
				</xsl:when>
				<xsl:when test="$layout eq 'flat' or number($rows) eq 0">
					<xsl:text>span</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>div</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="class">
			<xsl:text>wc_checkableselect_option_wrapper</xsl:text>
			<xsl:if test="$elementName eq 'ul'">
				<xsl:text> wc_list_nb</xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$layout eq 'flat' or number($rows) eq 0">
					<xsl:text> wc-hgap-med</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> wc-vgap-sm</xsl:text>
					<xsl:if test="$layout eq 'column'">
						<xsl:text> wc-column</xsl:text>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:if test="$class ne ''">
				<xsl:attribute name="class">
					<xsl:value-of select="normalize-space($class)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="checkableSelectOption">
				<xsl:with-param name="optionName" select="$inputName"/>
				<xsl:with-param name="optionType" select="$type"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
				<xsl:with-param name="cgAccessKey" select="$firstAccessKey"/>
				<xsl:with-param name="element">
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1">
							<xsl:text>li</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$elementName"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>			
			<xsl:if test="number($rows) gt 0">
				<xsl:choose>
					<xsl:when test="number($readOnly) eq 1">
						<xsl:apply-templates select="following-sibling::ui:option[@selected][position() lt number($rows)]" mode="checkableGroupInList">
							<xsl:with-param name="inputName" select="$inputName"/>
							<xsl:with-param name="type" select="$type"/>
							<xsl:with-param name="readOnly" select="1"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="following-sibling::ui:option[position() lt number($rows)]" mode="checkableGroupInList">
							<xsl:with-param name="inputName" select="$inputName"/>
							<xsl:with-param name="type" select="$type"/>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
