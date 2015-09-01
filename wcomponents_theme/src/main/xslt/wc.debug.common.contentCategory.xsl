<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.debugInfo.xsl"/>
	<!--
		These templates are helpers for the debug mode diagnostics and are
		designed to help identify possible HTML5 specification transgressions.

		NOTE:
		All wc.debug.*.xsl XSLT is built into a single stylesheet debug.xsl.
		This is included in the main stylesheet but the include is stripped
		when the XSLT is compressed during build. Therefore these templates
		are not output in normal systems and are, therefore, allowed to be a
		bit heavier and slower than normal.
	-->

	<!--
		This template tests whether a component has incorrectly nested
		descendants based on passed in parameters. If an element has incorrectly
		nested descendants it is marked with a visual style and a list of the
		component(s) which is should not contain. Where possible we leverage
		ui:debugInfo to provide contextual information to developers as to where
		the actual component causing the error may be found.
		
		If a nesting error is found this will add an attribute named 
		data-wc-debugerr to the element being tested so if you 
		want to add other ERROR level messages you must calculate them and pass
		them in to this template as otherErrorText.

		A component is deemed to be incorrectly nested if the HTML element(s)
		output meet any of the following:
			do[es] not have content category phrasing content but are nested
			inside any component which transforms to HTML with a content type
			limited to phrasing content (eg a div inside a span); and/or

			outputs one or more elements which are interactive elements and is
			nested inside any component which transforms to HTML with a content
			type limited to non-interactive content (eg an input element inside
			a button element);

		param testNonPhrase: If set to 1 then we test for descendants which
			output HTML with a content category which does not include phrasing
			content. This is used when the calling element's content model
			requires phrasing content. Default 0.

		param testInteractive: If set to 1 then we test for descendants which
			output HTML with a content category which includes interactive
			content. This is used when the calling element's content model
			precludes interactive content. Default 0.

		param otherErrorText: If set then an error is output containing this
			text in addition to any nesting error. If no nesting error exists
			but this parameter is set then an error is output containing only
			this text. Line breaks (<br/>) can be put into the final debugInfo
			output HTML by putting "\n" in this string.
	-->
	<xsl:template name="nesting-debug">
		<xsl:param name="el" select="."/>
		<xsl:param name="testNonPhrase" select="0"/>
		<xsl:param name="testInteractive" select="0"/>
		<xsl:param name="otherErrorText"/>

		<xsl:variable name="phraseContentErrors">
			<xsl:if test="$testNonPhrase=1">
				<xsl:apply-templates select="$el//ui:application|$el//ui:checkBoxSelect|$el//ui:collapsible|$el//ui:collapsibleToggle|$el//ui:dateField|$el//ui:definitionList|$el//ui:fieldLayout|$el//ui:fieldSet|$el//ui:fieldUpload[not(@axFiles='1')]|$el//ui:heading|$el//ui:hr|$el//ui:menu|$el//ui:messageBox|$el//ui:multiDropdown|$el//ui:multiSelectPair|$el//ui:multiTextField|$el//ui:panel|$el//ui:radioButtonSelect|$el//ui:row|$el//ui:selectToggle[not(@renderAs='control')]|$el//ui:separator|$el//ui:shuffler|$el//ui:skipLinks|$el//ui:table|$el//ui:tabset|$el//ui:validationErrors" mode="listById"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="interactiveContentErrors">
			<xsl:if test="$testInteractive=1">
				<xsl:apply-templates select="$el//ui:button|$el//ui:printButton|$el//ui:checkBox[not(@readOnly)]|$el//ui:checkBoxSelect|$el//ui:collapsibleToggle|$el//ui:dateField[not(@readOnly)]|$el//ui:dropdown[not(@readOnly)]|$el//ui:fileUpload[not(@readOnly)]|$el//ui:listBox[not(@readOnly)]|$el//ui:multiDropdown[not(@readOnly)]|$el//ui:multiSelectPair[not(@readOnly)]|$el//ui:multiTextField[not(@readOnly)]|$el//ui:radioButton[not(@readOnly)]|$el//ui:radioButtonSelect[not(@readOnly)]|$el//ui:selectToggle[not(@readOnly)]|$el//ui:shuffler[not(@readOnly)]|$el//ui:table[ui:pagination or ui:rowExpansion or ui:rowSelection[@selectAll]]|$el//ui:textArea[not(@readOnly)]|$el//ui:textField[not(@readOnly)]|$el//ui:emailField[not(@readOnly)]|$el//ui:phoneNumberField[not(@readOnly)]|$el//ui:numberField[not(@readOnly)]|$el//ui:passwordField[not(@readOnly)]|$el//ui:label|$el//ui:audio[not(@controls) or @controls='none']|$el//ui:collapsible|$el//ui:menu|$el//ui:video[not(@controls) or @controls='none']" mode="listById"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="nestingerrors">
			<xsl:if test="$phraseContentErrors!='' or $interactiveContentErrors!=''">
				<xsl:text>This component</xsl:text>
				<xsl:if test="$phraseContentErrors!=''">
					<xsl:text> must not contain non-phrasing content but found: [</xsl:text>
					<xsl:value-of select="$phraseContentErrors"/>
					<xsl:text>]</xsl:text>
					<xsl:if test="$interactiveContentErrors!=''">
						<xsl:text> and </xsl:text>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$interactiveContentErrors!=''">
					<xsl:text> must not contain interactive content but found: [</xsl:text>
					<xsl:value-of select="$interactiveContentErrors"/>
					<xsl:text>]</xsl:text>
				</xsl:if>
				<xsl:if test="$otherErrorText!=''">
					<xsl:text> and </xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:variable>

		<xsl:if test="$otherErrorText!='' or $nestingerrors!=''">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'data-wc-debugerr'"/>
				<xsl:with-param name="text">
					<xsl:value-of select="$nestingerrors"/>
					<xsl:if test="$otherErrorText!='' and $nestingerrors!=''">
						<xsl:text>\n</xsl:text>
					</xsl:if>
					<xsl:value-of select="$otherErrorText"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
		This template tests whether a component is incorrectly nested based on
		passed in parameters.

		This is a companion to nesting-debug the component which own the
		descendants is deemed to own the error, the component placed into the
		wrong context is provided with an info styling which is used to isolate
		the erroneously nested component.
		
		If an issue is found this will add an attribute named 
		data-wc-dubugdebug to the element being tested so if you 
		want to add other DEBUG level messages you must calculate them and pass
		them in to this template as otherDebugText.

		param testForPhraseOnly: if set to 1 then we test for ancesotrs which
			output HTML with a content model which does not include phrasing
			content. This would be set when the calling element's content
			category does not include phrasing content. Default 0.

		param testInteractive: If set to 1 then we test for descendants which
			output HTML with a content type which includes interactive content.
			This would be set when the calling element's content category
			includes interactive content. Default 0.

		param otherDebugText: If set then a debug message is output containing 
			this text in addition to any nesting isues. If noissues exists
			but this parameter is set then the message contains only this text. 
			Line breaks (<br/>) can be put into the final debugInfo output HTML 
			by putting "\n" in this string.
	-->
	<xsl:template name="thisIsNotAllowedHere-debug">
		<xsl:param name="testForPhraseOnly"/>
		<xsl:param name="testForNoInteractive"/>
		<xsl:param name="otherDebugText"/>

		<xsl:variable name="nestingerrors">
			<xsl:choose>
				<xsl:when test="$testForPhraseOnly=1 and $testForNoInteractive=1">
					<xsl:apply-templates select="ancestor::ui:abbr|ancestor::ui:button|ancestor::ui:decoratedLabel[not(parent::ui:heading or parent::ui:th or parent::ui:section)]|ancestor::ui:label|ancestor::ui:link|ancestor::ui:menuItem[@url or @submit]" mode="listById"/>
				</xsl:when>
				<xsl:when test="$testForPhraseOnly=1">
					<xsl:apply-templates select="ancestor::ui:abbr|ancestor::ui:button|ancestor::ui:decoratedLabel[not(parent::ui:heading or parent::ui:th or parent::ui:section)]|ancestor::ui:label|ancestor::ui:menuItem[@submit]" mode="listById"/>
				</xsl:when>
				<xsl:when test="$testForNoInteractive=1">
					<xsl:apply-templates select="ancestor::ui:button|ancestor::ui:link|ancestor::ui:menuItem[@url or @submit]|ancestor::ui:decoratedLabel/parent::ui:submenu|ancestor::ui:decoratedLabel/parent::ui:tab" mode="listById"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:if test="$otherDebugText!='' or $nestingerrors!=''">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'data-wc-dubugdebug'"/>
				<xsl:with-param name="text">
					<xsl:if test="$nestingerrors!=''">
						<xsl:text>This component should not be nested anywhere in any component which can</xsl:text>
						<xsl:choose>
							<xsl:when test="$testForPhraseOnly=1 and $testForNoInteractive=1">
								<xsl:text> only accept phrase content and no interactive content </xsl:text>
							</xsl:when>
							<xsl:when test="$testForPhraseOnly=1">
								<xsl:text> only accept phrase content </xsl:text>
							</xsl:when>
							<xsl:when test="$testForNoInteractive=1">
								<xsl:text>not accept interactive content </xsl:text>
							</xsl:when>
						</xsl:choose>
						<xsl:text> but is nested in: [</xsl:text>
						<xsl:value-of select="$nestingerrors"/>
						<xsl:text>]</xsl:text>
						<xsl:if test="$otherDebugText!=''">
							<xsl:text>\n</xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:value-of select="$otherDebugText"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>