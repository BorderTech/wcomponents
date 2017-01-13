<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.fauxOption.xsl"/>
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		Option is a child of many components which derive from AbstractList:
		Null template for unmoded ui:option elements. This should never be invoked but is here for completeness.
	-->
	<xsl:template match="ui:option"/>

	<!-- Tranforms the options of a list into HTML option elements. -->
	<xsl:template match="ui:option" mode="selectableList">
		<xsl:variable name="value" select="@value"/>
		<option>
			<xsl:attribute name="value">
				<xsl:choose>
					<xsl:when test="$value">
						<xsl:value-of select="$value"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@selected">
				<xsl:attribute name="selected">selected</xsl:attribute>
			</xsl:if>
			<xsl:if test="@isNull">
				<xsl:attribute name="data-wc-null">
					<xsl:text>1</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</option>
	</xsl:template>

	<!--
		Outputs an option emulator. This is a list item which, if it is a child of an optgroup element, is classed to be styled similar to a HTML
		option element nested in an optgroup element in a select element.
	-->
	<xsl:template match="ui:option" mode="readOnly">
		<xsl:param name="single" select="1"/>
		<xsl:param name="className" select="''"/>
		<xsl:choose>
			<xsl:when test="number($single) eq 1">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="class">
					<xsl:if test="parent::ui:optgroup">wc_inoptgroup</xsl:if>
					<xsl:if test="$className ne ''">
						<xsl:value-of select="concat(' ',$className)"/>
					</xsl:if>
				</xsl:variable>
				<li>
					<xsl:if test="normalize-space($class) ne ''">
						<xsl:attribute name="class">
							<xsl:value-of select="normalize-space($class)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	
	<!-- Template to transform the options in a checkable group. -->
	<xsl:template match="ui:option" mode="checkableGroup">
		<xsl:param name="inputName"/>
		<xsl:param name="type"/>
		<xsl:param name="rows" select="0"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:variable name="layout" select="../@layout"/>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:text>ul</xsl:text>
				</xsl:when>
				<xsl:when test="$layout eq 'flat'">
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
			<xsl:if test="$layout eq 'column'">
				<xsl:text> wc-column</xsl:text>
			</xsl:if>
			<xsl:if test="not($layout eq 'flat')">
				<xsl:text> wc-vgap-sm</xsl:text>
			</xsl:if>
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

	<!-- Transforms each option which is in a column -->
	<xsl:template match="ui:option" mode="checkableGroupInList">
		<xsl:param name="inputName"/>
		<xsl:param name="type"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:if test="number($readOnly) eq 0 or @selected">
			<xsl:call-template name="checkableSelectOption">
				<xsl:with-param name="optionName" select="$inputName"/>
				<xsl:with-param name="optionType" select="$type"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- The content of an option to a list item and the element relevent for the option.
	-->
	<xsl:template name="checkableSelectOption">
		<xsl:param name="optionName"/>
		<xsl:param name="optionType" select="''"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:variable name="uid">
			<xsl:value-of select="concat(../@id,generate-id())"/>
		</xsl:variable>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:text>li</xsl:text>
				</xsl:when>
				<xsl:when test="../@layout eq 'flat'">
					<xsl:text>span</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>div</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
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
						<xsl:if test="@isNull and $optionType eq 'radio'">
							<xsl:attribute name="data-wc-null">
								<xsl:text>1</xsl:text>
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

	<!-- Writes the content of the label for each option in the checkable group. -->
	<xsl:template name="checkableSelectOptionLabel">
		<xsl:choose>
			<xsl:when test="normalize-space(.)">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:when test="@value">
				<xsl:value-of select="@value"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
