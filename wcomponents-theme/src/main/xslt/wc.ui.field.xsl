<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		Transform for WField. It is used to represent a label:control pair. WField is a child of a WFieldLayout.
	-->
	<xsl:template match="ui:field">
		<xsl:param name="labelWidth" select="../@labelWidth" />
		<xsl:param name="layout" select="../@layout" />
		<xsl:if test="parent::ui:fieldlayout or parent::ui:ajaxtarget">
			<div id="{@id}">
				<xsl:call-template name="makeCommonClass">
					<xsl:with-param name="additional">
						<xsl:if test="@inputWidth">
							<xsl:value-of select="concat('wc_inputwidth wc_fld_inpw_', @inputWidth)"/>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<!--
					If we are part of an ajaxResponse and we don't have a parent ui:fieldlayout we need to add a transient attribute to act as a flag
					for the ajax subscriber.
				-->
				<xsl:if test="not(parent::ui:fieldlayout)">
					<xsl:attribute name="data-wc-nop">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="hideElementIfHiddenSet" />
				<xsl:variable name="isCheckRadio">
					<xsl:call-template name="fieldIsCheckRadio" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="number($isCheckRadio) eq 1">
						<span class="wc_fld_pl">
							<xsl:apply-templates select="ui:label"/>
						</span>
						<xsl:apply-templates select="ui:input" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="not(ui:label) or ui:label/@hidden">
							<span class="wc_fld_pl">
								<xsl:text>&#x00a0;</xsl:text>
							</span>
						</xsl:if>
						<xsl:apply-templates select="*"/>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>
	
	<!--
 		If the child of the ui:input is a WCheckBox or WRadioButton (or role of either) then the label must be placed after the control.
	-->
	<xsl:template name="fieldIsCheckRadio">
		<xsl:variable name="labelFor" select="ui:label/@for"/>
		<xsl:variable name="localEl">
			<xsl:if test="ui:label/@for and ui:input//*[@id=$labelFor]">
				<xsl:value-of select="local-name(ui:input//*[@id=$labelFor][1])"/>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$localEl eq 'radiobutton' or $localEl eq 'checkbox' or $localEl eq 'selecttoggle'">
				<!-- TODO could use an XSLT 2 function here -->
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		The input part of WField is a wrapper for other components.
	-->
	<xsl:template match="ui:input">
		<div>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
