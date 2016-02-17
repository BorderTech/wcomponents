<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<xsl:import href="wc.common.missingLabel.xsl"/>
	<!--
		Checkable input controls

		Transform for WCheckBox and WRadioButton to a HTML input element of type
		checkbox or radio (if editable) or a non-interactive representation if read-only.

		When @readOnly is true the component will output a non-interactive graphical
		representation of the control. This will be marked up to provide appropriate
		text content.

		Checkable inputs currently support @submitOnChange. This is an issue with
		WCAG 3.2.2 (http://www.w3.org/TR/WCAG20/#consistent-behavior-unpredictable-change).
		Therefore where submitOnChange is true we must inform users that changing
		selection will cause the form to submit. This must be done for all users. It is
		strongly recommended that @submitOnChange never be used with one of	these components.
	-->
	<xsl:template match="ui:checkbox|ui:radiobutton">
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="self::ui:checkbox or not(@groupName)">
					<xsl:text>checkbox</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>radio</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="id">
			<xsl:value-of select="@id"/>
		</xsl:variable>
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="@groupName and self::ui:radiobutton">
					<xsl:value-of select="@groupName"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$id"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="myLabel" select="key('labelKey',$id)"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="class">
						<xsl:if test="@selected">
							<xsl:text>wc_ro_sel</xsl:text>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="toolTip">
						<xsl:choose>
							<xsl:when test="@selected">
								<xsl:value-of select="$$${wc.ui.checkableInput.i18n.selected}"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$$${wc.ui.checkableInput.i18n.unselected}"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="label" select="$myLabel[1]"/>
				</xsl:call-template>
				<xsl:apply-templates select="$myLabel[1]" mode="checkable">
					<xsl:with-param name="labelableElement" select="."/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<xsl:element name="input">
					<xsl:attribute name="type">
						<xsl:value-of select="$type"/>
					</xsl:attribute>
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="name" select="$name"/>
						<xsl:with-param name="myLabel" select="$myLabel[1]"/>
					</xsl:call-template>
					<!-- Fortunately commonControlAttributes will only output a value attribute if
						the XML element has a value attribute; so we can add the ui:checkbox value
						here without changing the called template. -->
					<xsl:if test="self::ui:checkbox">
						<xsl:attribute name="value">
							<xsl:copy-of select="$t"/>
						</xsl:attribute>
						<xsl:attribute name="${wc.ui.checkBox.attribute.standAlone}">
							<xsl:text>x</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@groupName and self::ui:checkbox">
						<xsl:attribute name="data-wc-cbgroup">
							<xsl:value-of select="@groupName"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@selected">
						<xsl:attribute name="checked">
							<xsl:text>checked</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:element>
				<xsl:choose>
					<xsl:when test="$myLabel">
						<xsl:apply-templates select="$myLabel[1]" mode="checkable">
							<xsl:with-param name="labelableElement" select="."/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="checkLabel">
							<xsl:with-param name="force" select="1"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="self::ui:radiobutton">
					<xsl:call-template name="hField">
						<xsl:with-param name="name" select="$name"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
