<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.listSortControls.xsl"/>
	<xsl:import href="wc.ui.multiSelectPair.n.multiSelectPairButton.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<!--
		Transform for WMultiSelectPair. This component is a mechanism to select 0 or
		more options from a list. It is presented in a way which puts two lists side by
		side with a means to move selected options from one to the other (and deselect
		by moving back again).

		When read only the component outputs a simple unordered list of selected
		options.

		Otherwise the component outputs a fieldset containing three select elements,
		one of which is hidden and used as a reference of option order; a set of
		buttons to move options between the visible lists; and optionally a set of
		buttons to change the order of selected options.
	-->
	<xsl:template match="ui:multiselectpair">
		<xsl:variable name="id">
			<xsl:value-of select="@id"/>
		</xsl:variable>
		<xsl:variable name="readOnly">
			<xsl:choose>
				<xsl:when test="@readOnly">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="size">
			<xsl:choose>
				<xsl:when test="@size">
					<xsl:value-of select="@size"/>
				</xsl:when>
				<xsl:otherwise>7</xsl:otherwise><!-- 7 is usually big enough to be around the same size as the buttons -->
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="isError" select="key('errorKey',$id)"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">div</xsl:when>
				<xsl:otherwise>fieldset</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:call-template name="commonWrapperAttributes">
				<xsl:with-param name="isError" select="$isError"/>
				<xsl:with-param name="isControl">
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1">
							<xsl:number value="0"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:number value="1"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="myLabel" select="$myLabel"/>
			</xsl:call-template>
			<xsl:if test="number($readOnly) eq 1">
				<xsl:call-template name="roComponentName"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="number($readOnly) ne 1">
					<xsl:if test="@min">
						<xsl:attribute name="data-wc-min">
							<xsl:value-of select="@min"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@max">
						<xsl:attribute name="data-wc-max">
							<xsl:value-of select="@max"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="makeLegend">
						<xsl:with-param name="myLabel" select="$myLabel"/>
					</xsl:call-template>

					<!-- AVAILABLE LIST -->
					<xsl:variable name="availId" select="concat($id, '_a')"/>
					<span>
						<label for="{$availId}">
							<xsl:value-of select="@fromListName"/>
						</label>
						<!--<xsl:element name="br"/>-->
						<select id="{$availId}" multiple="multiple" class="wc_msp_av wc-noajax" size="{$size}" autocomplete="off">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
							<xsl:apply-templates select="ui:option[not(@selected)]|ui:optgroup[ui:option[not(@selected)]]" mode="multiselectPair">
								<xsl:with-param name="applyWhich" select="'unselected'"/>
							</xsl:apply-templates>
						</select>
					</span>
					<!-- BUTTONS -->
					<span class="wc_msp_btncol">
						<xsl:text>&#x00a0;</xsl:text>
						<xsl:call-template name="multiSelectPairButton">
							<xsl:with-param name="value" select="'add'"/>
							<xsl:with-param name="buttonText"><xsl:text>{{t 'msp_add'}}</xsl:text></xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="multiSelectPairButton">
							<xsl:with-param name="value" select="'aall'"/>
							<xsl:with-param name="buttonText"><xsl:text>{{t 'msp_addAll'}}</xsl:text></xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="multiSelectPairButton">
							<xsl:with-param name="value" select="'rem'"/>
							<xsl:with-param name="buttonText"><xsl:text>{{t 'msp_remove'}}</xsl:text></xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="multiSelectPairButton">
							<xsl:with-param name="value" select="'rall'"/>
							<xsl:with-param name="buttonText"><xsl:text>{{t 'msp_removeAll'}}</xsl:text></xsl:with-param>
						</xsl:call-template>
					</span>
					<!-- SELECTED LIST -->
					<xsl:variable name="toId">
						<xsl:value-of select="concat($id, '_s')"/>
					</xsl:variable>
					<span>
						<label for="{$toId}">
							<xsl:value-of select="@toListName"/>
						</label>
						<!--<xsl:element name="br"/>-->
						<select id="{$toId}" multiple="multiple" class="wc_msp_chos wc-noajax" size="{$size}" autocomplete="off">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
							<xsl:apply-templates select="ui:option[@selected]|ui:optgroup[ui:option[@selected]]" mode="multiselectPair">
								<xsl:with-param name="applyWhich" select="'selected'"/>
							</xsl:apply-templates>
						</select>
					</span>
					<xsl:if test="@shuffle">
						<xsl:call-template name="listSortControls">
							<xsl:with-param name="id" select="$toId"/>
						</xsl:call-template>
					</xsl:if>
					<select multiple="multiple" class="wc_msp_order" hidden="hidden" autocomplete="off">
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
						<xsl:apply-templates mode="multiselectPair"/>
					</select>
					<xsl:call-template name="hField"/>
					
					<xsl:call-template name="inlineError">
						<xsl:with-param name="errors" select="$isError"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="count(.//ui:option[@selected]) gt 0">
					<xsl:call-template name="title"/>
					<ul class="wc_list_nb">
						<xsl:apply-templates select="ui:option[@selected]|ui:optgroup[ui:option[@selected]]" mode="multiselectPair">
							<xsl:with-param name="readOnly" select="1"/>
							<xsl:with-param name="applyWhich" select="'selected'"/>
						</xsl:apply-templates>
					</ul>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
