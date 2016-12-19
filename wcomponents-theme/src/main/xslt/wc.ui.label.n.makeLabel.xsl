<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.offscreenSpan.xsl"/>
	<!-- Helpers to make the actual labels. -->

	<!-- Basic helper template to make a label for a labelable element. -->
	<xsl:template name="makeLabel">
		<xsl:param name="labelableElement"/>
		<xsl:variable name="readOnly">
			<xsl:choose>
				<xsl:when test="$labelableElement/@readOnly">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="elementType">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:text>span</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>label</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="wrappedNames" select="('datefield', 'emailfield', 'phonenumberfield', 'shuffler', 'textfield')"/>
		
		<xsl:element name="{$elementType}">
			<xsl:call-template name="labelCommonAttributes">
				<xsl:with-param name="element" select="$labelableElement"/>
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="$elementType eq 'label'">
					<xsl:if test="@for and @for ne ''"><!-- this is an explicit 'for' and not for implied by nesting -->
						<xsl:attribute name="for">
							<xsl:value-of select="@for"/>
							<xsl:if test="not(empty(index-of($wrappedNames, local-name($labelableElement)))) or 
								(local-name($labelableElement) eq 'dropdown' and $labelableElement/@type) ">
								<xsl:text>_input</xsl:text>
							</xsl:if>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="data-wc-rofor">
						<xsl:value-of select="@for"/>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="labelClassHelper">
				<xsl:with-param name="element" select="$labelableElement"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
			</xsl:call-template>
			<xsl:if test="$elementType eq 'label'">
				<xsl:call-template name="accessKey"/>
			</xsl:if>
			<xsl:apply-templates/>
			<xsl:if test="$elementType eq 'label' and $labelableElement/@required">
				<xsl:call-template name="offscreenSpan">
					<xsl:with-param name="text">
						<xsl:text>{{t 'requiredPlaceholder'}}</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="labelHintHelper">
				<xsl:with-param name="element" select="$labelableElement"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>

	<!-- Helper template to make a pseudo-label for a component which does not transform to a labellable element. -->
	<xsl:template name="makeFauxLabel">
		<!-- 
			param forElement: the element the label is 'for' this is pre-calculated before calling this template so can never be null.
		-->
		<xsl:param name="forElement"/>
		<xsl:variable name="readOnly">
			<xsl:choose>
				<xsl:when test="$forElement/@readOnly">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<span aria-hidden="true"><!-- aria-hidden to prevent double reading of label content as there will be an offscreen legend. -->
			<xsl:call-template name="labelCommonAttributes">
				<xsl:with-param name="element" select="$forElement"/>
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:attribute name="data-wc-rofor">
						<xsl:value-of select="@for"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<!--
						tabindex -1 is required for IE11 (and possibly earlier) to recognise aria-describedby or aria-labelledby. We leave it here 
						untested because it has negligible side effects.
					-->
					<xsl:attribute name="tabindex">
						<xsl:text>-1</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="data-wc-for">
						<xsl:value-of select="@for"/>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="labelClassHelper">
				<xsl:with-param name="element" select="$forElement"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
			</xsl:call-template>
			<xsl:apply-templates/>
			<xsl:call-template name="labelHintHelper">
				<xsl:with-param name="element" select="$forElement"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
			</xsl:call-template>
		</span>
	</xsl:template>

	<!--
		This helper will make a HTML artifact for a ui:label where that XML
		element:
			* does not have a for attribute OR
			  the for attribute is ''
			  AND
			  the ui:label does not have a single labellable element descendant
			
			OR
			
			* the for attribute is the id of a ui:application (since this indicates that the actual component with which the label is associated is
			  not in the render tree).
		In all of these cases the ui:label does not actually label anything.
	-->
	<xsl:template name="makeLabelForNothing">
		<span>
			<xsl:call-template name="labelCommonAttributes">
				<xsl:with-param name="element" select="false()"/>
			</xsl:call-template>
			<xsl:call-template name="labelClassHelper"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:apply-templates/>
			<xsl:call-template name="WLabelHint"/>
		</span>
	</xsl:template>
	
	<!--
		Helper for common attributes for each common type of transform of ui:label.
		
		param element: the XML element the ui:label is 'for' (not necessarily a labellable element and not necessarily set so must be tested).
	-->
	<xsl:template name="labelCommonAttributes">
		<xsl:param name="element"/>
		<xsl:attribute name="id">
			<xsl:value-of select="@id"/>
		</xsl:attribute>
		<xsl:call-template name="title"/>
		<xsl:if test="$element and $element/@hidden">
			<xsl:call-template name="hiddenElement"/>
		</xsl:if>
	</xsl:template>
	
	<!--
		ui:label making helper.to provide the class attribute. 
		
		param element: the XML element the ui:label is 'for' (not necessarily set so must be tested).
		
		param readOnly: The read only state of the 'for' element. This has already been calculated for other purposes so we just pass it in rather 
		than recalculating. If set it will be passed in as xsl:number 1.
	-->
	<xsl:template name="labelClassHelper">
		<xsl:param name="element"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:call-template name="makeCommonClass">
			<xsl:with-param name="additional">
				<xsl:if test="@hidden">
					<!--
						If a WLabel has its @hidden attribute set "true" it will not be hidden but moved out of viewport. A label will be be hidden if
						the component it is labelling is hidden. If both the WLabel and its labelled component are hidden then the label will be out
						of viewport and hidden such that if the component is shown (using subordinate) then the label will remain out of viewport but
						will become available to users of supporting AT.
					-->
					<xsl:text>wc-off</xsl:text>
				</xsl:if>
				<xsl:if test="number($readOnly) ne 1 and $element">
					<xsl:if test="$element/@required">
						<xsl:text> wc_req</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!--
		Helper to make label hints which include a potential submitOnChange warning. 
		
		param element: the XML element the label is 'for'. This should never be null but is best tested just in case.
		param readOnly: the previously calculated readOnly state of the element the label is 'for'.
	-->
	<xsl:template name="labelHintHelper">
		<xsl:param name="element"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:call-template name="WLabelHint"/>
	</xsl:template>

	<!--
		"Hint" handling in ui:label/WLabel. This is unnecessarily complicated because of the way we allow input controls to have a submitOnChange
		mechanism. 

		In an ideal world the submitOnChange flag would simply not exist. It is a hangover from design practices of the past so we have to work with
		it. We add a VISIBLE warning to the label if the control has submitOnChange and is not an ajax trigger.

		param submitNotAjaxTrigger: this needs to be pre-calculated. If set it will be xsl:number 1.
	-->
	<xsl:template name="WLabelHint">
		<xsl:if test="@hint">
			<span class="wc-label-hint">
				<xsl:value-of select="@hint"/>
			</span>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
