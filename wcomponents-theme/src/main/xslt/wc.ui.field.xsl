<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl" />
	<xsl:import href="wc.common.ajax.xsl" />
	<xsl:import href="wc.common.hide.xsl" />
	<xsl:import href="wc.ui.field.n.isCheckRadio.xsl" />
	<xsl:import href="wc.common.n.className.xsl"/>

	<!--
		Transform for WField. It is used to represent a label:control pair. WField is a
		child of a WFieldLayout.

		Child elements
		* ui:label
		* ui:input
		
		We do not output a ui:field or its content if it is incorrectly parented. A
		WField must be a child of a WFieldLayout and a ui:field must, therefore, be
		either a child of a ui:fieldlayout or a child of a ui:ajaxtarget.
		
		
		
		This template outputs a field wrapper DIV. The order in which its child
		elements are applied depends upon the child of the input element. If the
		first child is a WCheckBox or WRadioButton then the label is placed into the
		'input' container after the field. Otherwise it is placed before the 'input'
		container.

		The input wrapper div is output in this parent template rather than in the
		template match for ui:input because we need different input modes all with the
		same wrapper.

		If we do not have a WFieldLayout parent then this field is the target of
		an AJAX response and will not be able to work out its layout or
		labelWidth (and therefore its input width). In this case we add a
		transient attribute which is used JavaScript.
	-->
	<xsl:template match="ui:field">
		<xsl:param name="labelWidth" select="../@labelWidth" />
		<xsl:param name="layout" select="../@layout" />
		<xsl:if test="parent::ui:fieldlayout or parent::ui:ajaxtarget">
			<!--
 				If the child of the ui:input is a WCheckBox or WRadioButton then
 				the label must be placed after the control and any
 				ui:fieldindicator placed after the label.
			-->
			<xsl:variable name="isCheckRadio">
				<xsl:call-template name="fieldIsCheckRadio" />
			</xsl:variable>
			<div id="{@id}">
				<xsl:call-template name="makeCommonClass">
					<xsl:with-param name="additional">
						<xsl:if test="@inputWidth">
							<xsl:value-of select="concat('wc_inputwidth wc_fld_inpw_', @inputWidth)"/>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<!--
					If we are part of an ajaxResponse and we don't have a parent ui:fieldlayout we
					need to add a transient attribute to act as a flag for the ajax subscriber
				-->
				<xsl:if test="not(parent::ui:fieldlayout)">
					<xsl:attribute name="data-wc-nop">
						<xsl:copy-of select="$t" />
					</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="hideElementIfHiddenSet" />
				<xsl:call-template name="ajaxTarget" />
				<xsl:if test="not($layout = 'stacked') and ($isCheckRadio=1 or not(ui:label) or ui:label/@hidden)">
					<span class="wc_fld_pl">
						<xsl:text>&#x00a0;</xsl:text>
					</span>
				</xsl:if>
				<xsl:if test="$isCheckRadio!=1">
					<xsl:apply-templates select="ui:label"/>
				</xsl:if>
				<xsl:apply-templates select="ui:input">
					<xsl:with-param name="isCheckRadio" select="$isCheckRadio" />
				</xsl:apply-templates>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
