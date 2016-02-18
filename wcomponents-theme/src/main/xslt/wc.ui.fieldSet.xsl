<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for ui:fieldset which is the XML output of WFieldSet.

		Child Elements
		* ui:decoratedlabel
		* ui:content

		Base element:

		If the WFieldset has @readOnly="true" it transforms to a HTML div element; otherwise it transforms
		to a HTML fieldset element.

		Heading element:
		A fieldset has a mandatory legend child element. When the fieldset transforms
		to a div we test whether a heading style element is required and if so
		(that is @frame != notext or none) we output a HTML div element.



		This template determines the appropriate HTML element and outputs it. If the
		frame attribute indicates there should not be a border or if the fieldSet is
		'in error' (see {{{Error}Error State}}) then classes are set to style
		these.

		The label is determined and output then the content is applied. Finally if the
		fieldset in in an error state the error message are output before the fieldset
		is closed.
	-->
	<xsl:template match="ui:fieldset">
		<xsl:variable name="frame">
			<xsl:value-of select="@frame"/>
		</xsl:variable>
		<!--
			The fieldset cannot be in an error state since it is merely a container.
			However, if a ui:validationError exists with a ui:error for the fieldset then
			it is assumed that any component of the fieldset may be in an error state and
			an error indicator is place inside the fieldset after all other content. The
			fieldset is also styled as if it was in an error state.
		-->
		<xsl:variable name="isError" select="key('errorKey',@id)"/>
		<xsl:element name="fieldset">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:if test="$frame='noborder' or $frame='none'">
					<xsl:text> noborder</xsl:text>
				</xsl:if>
				<xsl:if test="$frame='notext' or $frame='none'">
					<xsl:text> notext</xsl:text>
				</xsl:if>
				<xsl:if test="@required">
					<xsl:text> wc_req</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:if test="$isError">
				<xsl:call-template name="invalid"/>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>

			<xsl:call-template name="ajaxTarget"/>
			<!--
				The Legend/Label/Heading

				The fieldset should be labelled with a HTML LEGEND element.

				In HTML5 a FIELDSET must have a LEGEND child element. This legend provides
				context to the form controls contained within the fieldset. For more details see
				{{{./fieldsets.html}using fieldsets and WFieldSet}}. The WDecoratedLabel of the
				WFieldSet (set by the constructor using the passed in java.lang.String title
				argument or by using one of the setTitle functions) is the content of the legend.
				It MUST be meaningful and, if setTitle(WComponent title) is used then the
				WComponent used <<must>> meet the following conditions:

				[[1]] The component must not be an interactive form component

				[[2]] The component must not be empty or null, including an empty string or
				WContainer;

				[[3]] The component must output some content which is perceivable as text
				either as a text node or a title attribute on transformed output; and

				[[4]] The perceivable text content must add context to the components
				contained within the fieldset.

				When readOnly the heading element may not be output at all, depending upon the
				value of the frame property.

				The legend will always be output but may be rendered out of viewport by the frame attribute.
			-->
			<xsl:element name="legend">
				<xsl:call-template name="accessKey"/>
				<xsl:apply-templates select="ui:decoratedlabel"/>
				<xsl:if test="normalize-space(ui:decoratedlabel/*)='' and not(ui:decoratedlabel//ui:image)">
					<xsl:value-of select="$$${wc.common.i18n.requiredLabel}"/>
				</xsl:if>
				<xsl:if test="@required">
					<xsl:call-template name="offscreenSpan">
						<xsl:with-param name="text">
							<xsl:value-of select="$$${wc.common.i18n.requiredPlaceholder}"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</xsl:element>

			<xsl:apply-templates select="ui:content"/>
			<xsl:if test="$isError">
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
