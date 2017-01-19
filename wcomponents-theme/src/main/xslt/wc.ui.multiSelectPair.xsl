<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.listSortControls.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!-- Transform for WMultiSelectPair. -->
	<xsl:template match="ui:multiselectpair">
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="isList" select="1"/>
					<xsl:with-param name="class" select="'wc-vgap-sm'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="size">
					<xsl:choose>
						<xsl:when test="@size">
							<xsl:value-of select="@size"/>
						</xsl:when>
						<xsl:otherwise>7</xsl:otherwise><!-- 7 is usually big enough to be around the same size as the buttons -->
					</xsl:choose>
				</xsl:variable>
				<fieldset>
					<xsl:call-template name="commonWrapperAttributes"/>
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
					<!-- AVAILABLE LIST -->
					<xsl:variable name="availId" select="concat(@id, '_a')"/>
					<label for="{$availId}">
						<span class="wc-lbltext">
							<xsl:value-of select="@fromListName"/>
						</span>
						<select id="{$availId}" multiple="multiple" class="wc_msp_av wc-noajax" size="{$size}" autocomplete="off">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
							<xsl:apply-templates select="ui:option[not(@selected)]|ui:optgroup[ui:option[not(@selected)]]" mode="multiselectPair">
								<xsl:with-param name="applyWhich" select="'unselected'"/>
							</xsl:apply-templates>
						</select>
					</label>
					
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
						<xsl:value-of select="concat(@id, '_s')"/>
					</xsl:variable>
					<label for="{$toId}">
						<span class="wc-lbltext">
							<xsl:value-of select="@toListName"/>
						</span>
						<select id="{$toId}" multiple="multiple" class="wc_msp_chos wc-noajax" size="{$size}" autocomplete="off">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="1"/>
							</xsl:call-template>
							<xsl:apply-templates select="ui:option[@selected]|ui:optgroup[ui:option[@selected]]" mode="multiselectPair">
								<xsl:with-param name="applyWhich" select="'selected'"/>
							</xsl:apply-templates>
						</select>
					</label>
					<xsl:if test="@shuffle">
						<xsl:call-template name="listSortControls">
							<xsl:with-param name="id" select="$toId"/>
						</xsl:call-template>
					</xsl:if>
					<select multiple="multiple" class="wc_msp_order wc_nolabel" hidden="hidden" autocomplete="off">
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
						<xsl:apply-templates mode="multiselectPair"/>
					</select>
					<xsl:call-template name="hField"/>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		This template produces the add and remove buttons used in multiSelectPair.
	
		param value: The value attribute is used to determine the function of the button.
		param buttonText: The text placed into the button's title attribute to  provide text equivalence of the button's function.
	-->
	<xsl:template name="multiSelectPairButton">
		<xsl:param name="value"/>
		<xsl:param name="buttonText"/>
		<button type="button" value="{$value}" title="{$buttonText}" class="wc_btn_icon wc-invite" aria-controls="{concat(@id, '_a',' ',@id, '_s')}">
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="1"/>
			</xsl:call-template>
		</button>
	</xsl:template>
	
	<!--
		The transform for optGroups within a multiSelectPair option list.
		
		param applyWhich:
			Use: "selected", "unselected" or "all"; default "all"
			
			This parameter indicates which options in the optGroup should be included in apply-templates. It will depend upon whether we are building 
			the unselected list, the selected list or the reference list.
		
		param readOnly: the read only state of the parent multiSelectPair
	-->
	<xsl:template match="ui:optgroup" mode="multiselectPair">
		<xsl:param name="applyWhich" select="'all'"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:choose>
			<xsl:when test="number($readOnly) ne 1">
				<optgroup label="{@label}">
					<xsl:choose>
						<xsl:when test="$applyWhich eq 'selected'">
							<xsl:apply-templates select="ui:option[@selected]" mode="multiselectPair"/>
						</xsl:when>
						<xsl:when test="$applyWhich eq 'unselected'">
							<xsl:apply-templates select="ui:option[not(@selected)]" mode="multiselectPair"/>
						</xsl:when>
						<xsl:otherwise>
							<!--the order list comes here -->
							<xsl:apply-templates mode="multiselectPair"/>
						</xsl:otherwise>
					</xsl:choose>
				</optgroup>
			</xsl:when>
			<xsl:otherwise>
				<li class="wc-optgroup">
					<xsl:value-of select="@label"/>
				</li>
				<xsl:apply-templates select="ui:option[@selected]" mode="multiselectPair">
					<xsl:with-param name="readOnly" select="$readOnly"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- 
		The transform for each option in the multiSelectPair.
		
		param readOnly: the read only state of the parent multiSelectPair.
	-->
	<xsl:template match="ui:option" mode="multiselectPair">
		<xsl:param name="readOnly" select="0"/>
		<xsl:choose>
			<xsl:when test="number($readOnly) ne 1">
				<option value="{@value}">
					<xsl:value-of select="normalize-space(.)"/>
				</option>
			</xsl:when>
			<xsl:otherwise>
				<li>
					<xsl:if test="parent::ui:optgroup">
						<xsl:attribute name="class">
							<xsl:text>wc_inoptgroup</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="normalize-space(.)">
							<xsl:value-of select="normalize-space(.)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@value"/>
						</xsl:otherwise>
					</xsl:choose>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
