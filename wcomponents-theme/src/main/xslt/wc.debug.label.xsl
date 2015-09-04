<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<!--
		Debug info for labels: almost as hard as labels!
	-->
	<xsl:template name="label-debug">
		<xsl:param name="elementType"/>
		<xsl:param name="forElement"/>
		<xsl:param name="labelableElement"/>
		<xsl:call-template name="debugAttributes"/>
		
		<xsl:variable name="for" select="@for"/>
		<!--ERROR:
			It is an error if the label:
			* is output as a label element and contains:
				* more than one labellable descendant; or
				* exactly one labellable descendant but the label's for attribute is not the id of that descendant; or
				* another ui:label or html:label; or
			* contains non-phrasing content.
		-->
		<xsl:variable name="labelableDescendant">
			<xsl:apply-templates select="./ui:button|./ui:printButton|./ui:checkBox[not(@readOnly)]|./ui:dateField[not(@readOnly)]|./ui:dropdown[not(@readOnly)]|./ui:fileUpload[@async='false' and not(@readOnly)]|./ui:listBox[not(@readOnly)]|./ui:radioButton[not(@readOnly)]|./ui:selectToggle[@renderAs='control']|./ui:textArea[not(@readOnly)]|./ui:textField[not(@readOnly)]|./ui:emailField[not(@readOnly)]|./ui:phoneNumberField[not(@readOnly)]|./ui:numberField[not(@readOnly)]|./ui:passwordField[not(@readOnly)]|./ui:audio[@controls='play']|./ui:video[@controls='play']" mode="listById"/>
		</xsl:variable>
		
		<xsl:variable name="otherError">
			<xsl:if test="$elementType='label'">
				<xsl:choose>
					<xsl:when test="contains($labelableDescendant,',')">
						<xsl:text>contains more than one interactive element.[</xsl:text>
						<xsl:value-of select="$labelableDescendant"/>
						<xsl:text>]</xsl:text>
					</xsl:when>
					<xsl:when test="$for and $labelableDescendant!='' and $labelableDescendant!= $for">
						<xsl:text>contains an interactive element which is not the 'for' element.[</xsl:text>
						<xsl:value-of select="$labelableDescendant"/>
						<xsl:text>]</xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:if test=".//ui:label or .//html:label">
					<xsl:text> A WLabel cannot contain another </xsl:text>
					<xsl:if test=".//ui:label">
						<xsl:text>WLabel</xsl:text>
						<xsl:if test=".//html:label">
							<xsl:text> or </xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test=".//html:label">
						<xsl:text>HTML label element</xsl:text>
					</xsl:if>
					<xsl:if test=".//ui:label">
						<xsl:text> but contains [</xsl:text>
						<xsl:apply-templates select=".//ui:label" mode="listById"/>
						<xsl:text>]</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		
		<!--WARN if the WLabel is not for:
			* something that is labellable; or
			* something which is output as a fieldset; or
			* something not in the current screen (ie no $forElement).
			Since this implies that developers are associating WLabels willy-nilly.
		-->
		<xsl:variable name="localNamesForFieldsets">
			<xsl:text> checkBoxSelect fieldSet fileUpload multiDropdown multiTextField multiSelectPair radioButtonSelect shuffler dateField</xsl:text>
		</xsl:variable>
		
		<xsl:variable name="warnText">
			<xsl:choose>
				<xsl:when test="$forElement">
					<xsl:if test="not($labelableElement or local-name($forElement)='selectToggle' or contains($localNamesForFieldsets,local-name($forElement)))">
						<xsl:text>The WLabel is not 'for' something which should be labelled it is for [</xsl:text>
						<xsl:apply-templates select="$forElement" mode="listById"/>
						<xsl:text>].</xsl:text>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$for">
					<xsl:text>This WLabel is 'for' a component which is not in this screen.\n</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$warnText!=''">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'data-wc-debugwarn'"/>
				<xsl:with-param name="text" select="$warnText"/>
			</xsl:call-template>
		</xsl:if>
		
		<!-- INFO:
			If the WLabel does not have a for attribute then why is it a WLabel?
			This is subtlely different from being "for" something which is not present. 
			If "for" is set then it implies a decision to setVisible(false) on the 
			labelled component (or not add it to the WComponent tree); whereas if "for" 
			is not set then the WLabel was probably never associated with a component 
			which could be an oversight or simply mis-use of the WLabel component.
		-->
		<xsl:variable name="infoText">
			<xsl:if test="not($for) and $labelableDescendant=''">
				<xsl:text>This WLabel is not for anything. Why is it here?</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="$infoText!=''">
			<xsl:call-template name="makeDebugAttrib-debug">
				<xsl:with-param name="name" select="'ata-wc-debuginfo'"/>
				<xsl:with-param name="text" select="$infoText"/>
			</xsl:call-template>
		</xsl:if>
		
		<!--DEBUG:
			a html label element is interactive content. It must not be nested:
			* in any element which cannot accept interactive content; or
			* in another label.
		-->
		<xsl:call-template name="nesting-debug">
			<xsl:with-param name="testNonPhrase" select="1"/>
			<xsl:with-param name="otherErrorText" select="$otherError"/>
		</xsl:call-template>
		
		<xsl:if test="$elementType='label'">
			<xsl:variable name="otherDebugText">
				<xsl:if test="ancestor::html:label or ancestor::ui:label">
					<xsl:text> A WLabel must not be nested in another </xsl:text>
					<xsl:if test="ancestor::ui:label">
						<xsl:text>WLabel</xsl:text>
						<xsl:if test="ancestor::html:label">
							<xsl:text> or </xsl:text>
						</xsl:if>
					</xsl:if>
					<xsl:if test="ancestor::html:label">
						<xsl:text>HTML label element</xsl:text>
					</xsl:if>
					<xsl:if test="ancestor::ui:label">
						<xsl:text> but found [</xsl:text>
						<xsl:apply-templates select="ancestor::ui:label[1]" mode="listById"/>
						<xsl:text>]</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="thisIsNotAllowedHere-debug">
				<xsl:with-param name="testForNoInteractive" select="1"/>
				<xsl:with-param name="otherDebugText" select="$otherDebugText"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
