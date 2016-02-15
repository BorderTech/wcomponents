<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.label.n.makeLabel.xsl"/>
	<xsl:import href="wc.ui.label.n.makeFauxLabel.xsl"/>
	<xsl:import href="wc.ui.label.n.makeLabelForNothing.xsl"/>
	<!--
		Creating a label is not as simple as it may appear. A HTML Label element is specific in its purpose. It may only
		be used to label a labelable element.

		WLabel may be "for" something which is not a labelable element, such as a multiSelectPair, or even not 'for'
		anything. Therefore, in order to determine what (if anything) to output we need to find out what we are
		labelling.

		ui:label is also a child of WField and as such has special output parameters depending upon the contents of the
		field's input child. This makes labels even more complicated.

		In some cases the ui:label should be output as part of another transform and not putput at all in-situ. The
		current components which pull the ui:label are ui:radiobutton, ui:checkbox and
		ui:selecttoggle[@renderAs='control'].

		param style: passed in ultimately from the transform for ui:field. See wc.ui.field.xsl.
	-->
	<xsl:template match="ui:label">
		<xsl:param name="style"/>
		<xsl:variable name="for" select="@for"/>

		<xsl:choose>
			<!-- most labels will be for something (one would hope) -->
			<xsl:when test="$for and $for!=''">
				<xsl:variable name="labelableElement" select="key('labelableElementKey',$for)[1]"/>
				<xsl:choose>
					<xsl:when test="$labelableElement">
						<!-- this test is for components which MUST NOT allow the ui:label to be rendered in-situ -->
						<xsl:if test="not(local-name($labelableElement)='checkbox' or local-name($labelableElement)='radiobutton' or local-name($labelableElement)='selecttoggle')">
							<xsl:call-template name="makeLabel">
								<xsl:with-param name="labelableElement" select="$labelableElement"/>
								<xsl:with-param name="style" select="$style"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<!--
							If a WLabel is "for" the ui:application it is indicative of a Java error in which the real
							labelled component is not in the WComponent render tree.
						-->
						<xsl:variable name="forElement" select="//*[@id=$for and not(self::ui:application)]"/>
						<xsl:choose>
							<xsl:when test="$forElement">
								<xsl:call-template name="makeFauxLabel">
									<xsl:with-param name="forElement" select="$forElement"/>
									<xsl:with-param name="style" select="$style"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="makeLabelForNothing">
									<xsl:with-param name="style" select="$style"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="not($for)">
				<!--
					If we are not for anything we could be 'for' a nested component
					NOTE: this is VERY slow in the Microsoft XSLProcessor (which is much faster with keys) but should be
					a very little used calculation.
					TODO: do some profiling to compare this to a key for typical use cases.
				-->
				<xsl:variable name="labelableDescendant" select=".//ui:button|.//ui:checkbox|.//ui:datefield|.//ui:dropdown|.//ui:emailfield|.//ui:fileupload[@async='false']|.//ui:listbox|.//ui:numberfield|.//ui:passwordfield|.//ui:phonenumberfield|.//ui:printbutton|.//ui:progressbar|.//ui:radiobutton|.//ui:textarea|.//ui:textfield"/>
				<xsl:choose>
					<xsl:when test="count($labelableDescendant)=1">
						<xsl:call-template name="makeLabel">
							<xsl:with-param name="labelableElement" select="$labelableDescendant[1]"/>
							<xsl:with-param name="style" select="$style"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="makeLabelForNothing">
							<xsl:with-param name="style" select="$style"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<!--
					This is VERY dodgy and should not be possible in WComponents. If we get here (ie @for='') then
					someone has overridden the WLabel renderer or broken the determination of the id of the component
					the WLabel is for. In either case this is indicative of a WComponent Java error. We should never
					get here and it could be deleted in which case anything falling through will not be transformed.
				-->
				<xsl:call-template name="makeLabelForNothing">
					<xsl:with-param name="style" select="$style"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
