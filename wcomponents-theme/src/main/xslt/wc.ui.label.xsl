<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.label.n.makeLabel.xsl"/>
	<xsl:import href="wc.ui.label.key.labelableElementKey.xsl"/>
	<!--
		Creating a label is not as simple as it may appear. A HTML Label element is specific in its purpose. It may only
		be used to label a labelable element.

		WLabel may be "for" something which is not a labelable element, such as a multiSelectPair, or even not 'for'
		anything. Therefore, in order to determine what (if anything) to output we need to find out what we are
		labelling.

		ui:label is also a child of WField and as such has special output parameters depending upon the contents of the
		field's input child. This makes labels even more complicated.

		In some cases the ui:label should be output as part of another transform and not putput at all in-situ. The
		current components which pull the ui:label are ui:radiobutton, ui:checkbox and ui:selecttoggle[@renderAs='control'].

		param style: passed in ultimately from the transform for ui:field. See wc.ui.field.xsl.
	-->
	<xsl:template match="ui:label">
		<xsl:variable name="for" select="@for"/>
		<xsl:choose>
			<!-- most labels will be for something (one would hope) -->
			<xsl:when test="$for and $for ne ''">
				<xsl:variable name="labelableElement" select="key('labelableElementKey',$for)[1]"/>
				<xsl:choose>
					<xsl:when test="$labelableElement">
						<!-- this test is for components which MUST NOT allow the ui:label to be rendered in-situ -->
						<xsl:if test="not(local-name($labelableElement) eq 'checkbox' or local-name($labelableElement) eq 'radiobutton' or local-name($labelableElement) eq 'selecttoggle')">
							<xsl:call-template name="makeLabel">
								<xsl:with-param name="labelableElement" select="$labelableElement"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<!--
							If a WLabel is "for" the ui:application it is indicative of a Java error in which the real labelled component is not in
							the WComponent render tree.
						-->
						<xsl:variable name="forElement" select="//*[@id eq $for and not(self::ui:application)]"/>
						<xsl:choose>
							<xsl:when test="$forElement">
								<xsl:call-template name="makeFauxLabel">
									<xsl:with-param name="forElement" select="$forElement"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="makeLabelForNothing"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="not($for)">
				<!--
					If we are not for anything we could be 'for' a nested component
					NOTE: this is VERY slow in the Microsoft XSLProcessor (which is much faster with keys) but should be a very little used calculation.
					TODO: do some profiling to compare this to a key for typical use cases.
				-->
				<xsl:variable name="labelableDescendant" select=".//ui:button|.//ui:checkbox|.//ui:datefield|.//ui:dropdown|.//ui:emailfield|.//ui:fileupload[@async='false']|.//ui:listbox|.//ui:numberfield|.//ui:passwordfield|.//ui:phonenumberfield|.//ui:printbutton|.//ui:progressbar|.//ui:radiobutton|.//ui:textarea|.//ui:textfield"/>
				<xsl:choose>
					<xsl:when test="count($labelableDescendant) eq 1">
						<xsl:call-template name="makeLabel">
							<xsl:with-param name="labelableElement" select="$labelableDescendant[1]"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="makeLabelForNothing"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<!--
					This is VERY dodgy and should not be possible in WComponents. If we get here (ie @for='') then someone has overridden the WLabel
					renderer or broken the determination of the id of the component the WLabel is for. In either case this is indicative of a
					WComponent Java error. We should never get here and it could be deleted in which case anything falling through will not be
					transformed.
				-->
				<xsl:call-template name="makeLabelForNothing"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		A WLabel for a WSelectToggle when that selectToggle has renderAs of 'control'. This mode is called from the transform of ui:selecttoggle and
		the label becomes the title of the toggle button.
	-->
	<xsl:template match="ui:label" mode="selectToggle">
		<xsl:variable name="value">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$value ne '' or @hint">
				<xsl:value-of select="normalize-space(concat($value,' ',@hint))"/>
			</xsl:when>
			<xsl:when test="@accessibleText">
				<xsl:value-of select="@accessibleText"/>
			</xsl:when>
			<xsl:when test="@toolTip">
				<xsl:value-of select="@toolTip"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>{{t 'toggle_all_label'}}</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		A Label for a WRadioButton or WCheckBox. This template mode is called from the transforms of ui:checkbox and ui:radiobutton to ensure that the
		label immediately follows the checkbox/radiobutton as per WCAG requirements.
	-->
	<xsl:template match="ui:label" mode="checkable">
		<xsl:param name="labelableElement"/>
		<xsl:call-template name="makeLabel">
			<xsl:with-param name="labelableElement" select="$labelableElement"/>
		</xsl:call-template>
	</xsl:template>

	<!--
		This is used to generate a legend for a component which has a fieldset wrapper. The component element is passed in as the forElement. This is
		not called if the component is in a read only state.
		
		The ui:label is aso transformed in-situ as a faux-label so we MUST NOT output the label ID in this legend. We do output accessKey because
		accesskey is an allowed and functional attribute on a legend element.
		
		param: labelableELement: the component being labelled. This is always known and does not need to be calculated so it is MUCH cheaper to pass
		it in as a param.
	-->
	<xsl:template match="ui:label" mode="legend">
		<xsl:param name="labelableElement"/>
		<legend>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:text>wc-off</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="accessKey"/>
			<xsl:apply-templates />
			<xsl:call-template name="WLabelHint"/>
			<xsl:if test="$labelableElement and $labelableElement/@required">
				<xsl:call-template name="offscreenSpan">
					<xsl:with-param name="text">
						<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</legend>
	</xsl:template>
</xsl:stylesheet>
