<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>

	<!--
		Creating a label is not as simple as it may appear. A HTML Label element is specific in its purpose. It may only
		be used to label a labelable element.

		WLabel may be "for" something which is not a labelable element, such as a multiSelectPair, or even not "for"
		anything. Therefore, in order to determine what (if anything) to output we need to find out what we are
		labelling.

		ui:label is also a child of WField and as such has special output parameters depending upon the contents of the
		field's input child. This makes labels even more complicated.

		In some cases the ui:label should be output as part of another transform and not putput at all in-situ. The
		current components which pull the ui:label are ui:radiobutton, ui:checkbox and ui:selecttoggle[@renderAs='control'].
	-->
	<xsl:template match="ui:label">
		<xsl:choose>
			<xsl:when test="@readonly or not(@what) or @what eq 'group'">
				<span>
					<xsl:call-template name="labelCommonAttributes"/>
					<xsl:call-template name="labelClassHelper"/>
					<xsl:if test="@what">
						<xsl:choose>
							<xsl:when test="@readonly">
								<xsl:attribute name="data-wc-rofor">
									<xsl:value-of select="@for"/>
								</xsl:attribute>
							</xsl:when>
							<xsl:when test="@what eq 'group'">
								<xsl:attribute name="data-wc-for">
									<xsl:value-of select="@for"/>
								</xsl:attribute>
								<xsl:attribute name="aria-hidden">
									<xsl:text>true</xsl:text>
								</xsl:attribute>
								<xsl:if test="@accessKey">
									<xsl:attribute name="data-wc-accesskey">
										<xsl:value-of select="@accessKey"/>
									</xsl:attribute>
								</xsl:if>
							</xsl:when>
						</xsl:choose>
					</xsl:if>
					<xsl:apply-templates/>
					<xsl:call-template name="WLabelHint"/>
				</span>
			</xsl:when>
			<xsl:otherwise><!-- @what is 'input' -->
				<label for="{concat(@for,'_input')}">
					<xsl:call-template name="labelCommonAttributes"/>
					<xsl:call-template name="labelClassHelper" />
					<xsl:call-template name="accessKey"/>
					<xsl:apply-templates/>
					<xsl:if test="@required">
						<xsl:call-template name="offscreenSpan">
							<xsl:with-param name="text">
								<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
					<xsl:call-template name="WLabelHint"/>
				</label>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Helper for common attributes for each common type of transform of ui:label.
		
		param element: the XML element the ui:label is 'for' (not necessarily a labellable element and not necessarily set so must be tested).
	-->
	<xsl:template name="labelCommonAttributes">
		<xsl:attribute name="id">
			<xsl:value-of select="@id"/>
		</xsl:attribute>
		<xsl:call-template name="title"/>
		<xsl:if test="@hiddencomponent">
			<xsl:call-template name="hiddenElement"/>
		</xsl:if>
	</xsl:template>
	
	<!--
		Helper.to provide the class attribute. 

		If a WLabel has its @hidden attribute set it will not be hidden but moved out of viewport.
	-->
	<xsl:template name="labelClassHelper">
		<xsl:call-template name="makeCommonClass">
			<xsl:with-param name="additional">
				<xsl:if test="@hidden">
					<xsl:text>wc-off</xsl:text>
				</xsl:if>
				<xsl:if test="@required and not(@readonly)">
					<xsl:text> wc_req</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!--
		"Hint" handling for WLabel.
	-->
	<xsl:template name="WLabelHint">
		<xsl:if test="@hint">
			<span class="wc-label-hint">
				<xsl:value-of select="@hint"/>
			</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
