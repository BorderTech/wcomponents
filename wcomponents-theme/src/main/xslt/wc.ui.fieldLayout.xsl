
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- WFieldLayout -->
	<xsl:template match="ui:fieldlayout">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
			<xsl:if test="@labelWidth">
				<xsl:value-of select="concat(' wc_fld_lblwth_',@labelWidth)"/>
			</xsl:if>
			<xsl:if test="@ordered">
				<xsl:text> wc_ordered</xsl:text>
			</xsl:if>
			<xsl:if test="@layout">
				<xsl:value-of select="concat(' wc-layout-', @layout)"/>
			</xsl:if>
		</xsl:variable>
		<!-- yes, I know the role is superfluous -->
		<div role="presentation" id="{@id}" class="{normalize-space(concat('wc-fieldlayout ', $additional))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@ordered and number(@ordered) ne 1">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('counter-reset: wcfld ', number(@ordered) - 1, ';')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:field"/>
		</div>
	</xsl:template>

	<!--
		Transform for WField. It is used to represent a label:control pair. WField is a child of a WFieldLayout.
	-->
	<xsl:template match="ui:field">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@inputWidth">
				<xsl:value-of select="concat(' wc_inputwidth wc_fld_inpw_', @inputWidth)"/>
			</xsl:if>
		</xsl:variable>
		<div id="{@id}" class="{normalize-space(concat('wc-field ', $additional))}">
			<!--
				If we are part of an ajaxResponse and we don't have a parent ui:fieldlayout we need to add a transient attribute to act as a flag
				for the ajax subscriber.
			-->
			<xsl:if test="not(parent::ui:fieldlayout)">
				<xsl:attribute name="data-wc-nop">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden"><xsl:attribute name="hidden"><xsl:text>hidden</xsl:text></xsl:attribute></xsl:if>
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
		<div class="wc-input">
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
