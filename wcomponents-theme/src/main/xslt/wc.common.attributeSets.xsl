<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.missingLabel.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<xsl:import href="wc.common.aria.label.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>

	<!--
		TODO: these need a good clean up.

		A set of helper templates to add commonly used groups of attributes to
		elements. We cannot use actual XSLT attribute-sets because the
		attributes require calculation and parameters.
	-->

	<!--
		A set of attributes added to form control elements.
		param id: The id of the element, default @id.
		param isError: A node list of ui:errors (if any) associated with the
			element.
		param name: The value of the element's name attribute if it is to be set.
		param value: The value of the element's value attribute if it is to be
			set, default @value.
		param live: The value to set to aria-live if the element is an ajax
			target, default 'polite'.
		param toolTip: a string to add to the element as a title attribute.
			Defaults to the components toolTip attribute.
	-->
	<xsl:template name="commonControlAttributes">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="isError"/>
		<xsl:param name="name"/>
		<xsl:param name="value" select="@value"/>
		<xsl:param name="live" select="'polite'"/>
		<xsl:param name="myLabel" select="key('labelKey',$id)[1]"/>

		<xsl:call-template name="commonAttributes">
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="live" select="$live"/>
			<xsl:with-param name="isControl" select="1"/>
		</xsl:call-template>
		<xsl:if test="not($name='')">
			<xsl:attribute name="name">
				<xsl:value-of select="$name"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="$value">
			<xsl:attribute name="value">
				<xsl:value-of select="$value"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="$isError and $isError !=''">
			<xsl:call-template name="invalid"/>
		</xsl:if>

		<xsl:attribute name="class">
			<xsl:call-template name="commonClassHelper"/>
			<xsl:if test="@submitOnChange and not(@list)">
				<xsl:text> wc_soc</xsl:text>
			</xsl:if>
		</xsl:attribute>

		<xsl:call-template name="requiredElement"/>
		<xsl:call-template name="ajaxController">
			<xsl:with-param name="id">
				<xsl:choose>
					<xsl:when test="self::ui:radioButton">
						<xsl:value-of select="@groupName"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@id"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:if test="@buttonId">
			<xsl:attribute name="${wc.common.attribute.button}">
				<xsl:value-of select="@buttonId"/>
			</xsl:attribute>
		</xsl:if>
		<!--<xsl:if test="ancestor::ui:application/@defaultFocusId = $id">
			<xsl:attribute name="autofocus">
				<xsl:text>autofocus</xsl:text>
			</xsl:attribute>
		</xsl:if>-->
		<xsl:call-template name="title"/>
		<xsl:if test="not($myLabel)">
			<xsl:call-template name="ariaLabel"/>
		</xsl:if>
	</xsl:template>

	<!--
		Attributes applied to the outer 'wrapper' element of complex components.
		param id: The component's id, default @id.
		param isError: A node list of ui:errors (if any) associated with the
			component.
		param live: The value to set to aria-live if the component is an ajax
			target,  default 'polite'.
		param isControl: see "commonAtrributes: below, default 1.
	-->
	<xsl:template name="commonWrapperAttributes">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="isError"/>
		<xsl:param name="live" select="'polite'"/>
		<xsl:param name="isControl" select="1"/>
		<xsl:param name="class"/>
		<xsl:param name="myLabel"/>
		<!--normally fieldset-->
		<xsl:call-template name="commonAttributes">
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="live" select="$live"/>
			<xsl:with-param name="isControl" select="$isControl"/>
			<xsl:with-param name="isWrapper" select="1"/>
		</xsl:call-template>
		<xsl:attribute name="class">
			<xsl:call-template name="commonClassHelper"/>
			<xsl:if test="not(@readOnly)">
				<xsl:text> notext noborder</xsl:text>
				<xsl:if test="@required">
					<xsl:text> wc_req</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="$class!= ''">
				<xsl:value-of select="concat(' ', $class)"/>
			</xsl:if>
		</xsl:attribute>
		<xsl:call-template name="ajaxController">
			<xsl:with-param name="id" select="$id"/>
		</xsl:call-template>
		<xsl:if test="@readOnly and $myLabel">
			<xsl:attribute name="aria-labelledby">
				<xsl:value-of select="$myLabel/@id"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="$isError and $isError !=''">
			<xsl:call-template name="invalid"/>
		</xsl:if>
	</xsl:template>

	<!--
		A set of attributes commonly applied to the transformed output of many components.
		param id:  The component's id, default @id.
		param live: The value to set to aria-live if the component is an ajax
			target, default 'polite'.
		param isControl: Set to integer 1 if the transformed output is a HTML
			form control which supports the disabled attribute, default 0.
	-->
	<xsl:template name="commonAttributes">
		<xsl:param name="id" select="@id"/>
		<xsl:param name="live" select="'polite'"/>
		<xsl:param name="isControl" select="0"/>
		<xsl:param name="readOnly" select="@readOnly"/>
		<xsl:param name="isWrapper" select="0"/>
		<xsl:attribute name="id">
			<xsl:value-of select="$id"/>
		</xsl:attribute>
		<xsl:call-template name="hideElementIfHiddenSet"/>
		<xsl:call-template name="ajaxTarget">
			<xsl:with-param name="live" select="$live"/>
		</xsl:call-template>
		<xsl:if test="not($readOnly=$t or $isWrapper=1)">
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="$isControl"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
