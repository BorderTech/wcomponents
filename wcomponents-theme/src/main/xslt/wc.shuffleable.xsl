
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		Build the button controls used to re-order items in a list box as per wc.ui.shuffler.xsl and wc.ui.multiSelectPair.xsl.
	-->
	<xsl:template name="listSortControls">
		<xsl:param name="id" select="@id"/>
		<span class="wc_sortcont">
			<xsl:if test="self::ui:multiselectpair">
				<xsl:text>&#x00a0;</xsl:text>
			</xsl:if>
			<button aria-controls="{$id}" class="wc_sorter wc_btn_icon wc-invite" type="button" value="top">
				<xsl:attribute name="title">
					<xsl:text>{{#i18n}}shuffle_top{{/i18n}}</xsl:text>
				</xsl:attribute>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<i aria-hidden="true" class="fa fa-fw fa-angle-double-up"/>
			</button>
			<button aria-controls="{$id}" class="wc_sorter wc_btn_icon wc-invite" type="button" value="up">
				<xsl:attribute name="title">
					<xsl:text>{{#i18n}}shuffle_up{{/i18n}}</xsl:text>
				</xsl:attribute>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<i aria-hidden="true" class="fa fa-fw fa-angle-up"/>
			</button>
			<button aria-controls="{$id}" class="wc_sorter wc_btn_icon wc-invite" type="button" value="down">
				<xsl:attribute name="title">
					<xsl:text>{{#i18n}}shuffle_down{{/i18n}}</xsl:text>
				</xsl:attribute>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<i aria-hidden="true" class="fa fa-fw fa-angle-down"/>
			</button>
			<button aria-controls="{$id}" class="wc_sorter wc_btn_icon wc-invite" type="button" value="bottom">
				<xsl:attribute name="title">
					<xsl:text>{{#i18n}}shuffle_bottom{{/i18n}}</xsl:text>
				</xsl:attribute>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<i aria-hidden="true" class="fa fa-fw fa-angle-double-down"/>
			</button>
		</span>
	</xsl:template>

	<!-- WShuffler is a component designed to allow a fixed list of options to have their order changed. -->
	<xsl:template match="ui:shuffler[@readOnly]">
		<ol id="{@id}" data-wc-component="shuffler">
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-shuffler wc_list_nb wc-ro-input ', @class))"/>
			</xsl:attribute>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="readOnly" select="ui:option | ui:optgroup">
				<xsl:with-param name="single" select="0"/>
			</xsl:apply-templates>
		</ol>
	</xsl:template>

	<xsl:template match="ui:shuffler">
		<span id="{@id}">
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-shuffler wc-input-wrapper ', @class))"/>
			</xsl:attribute>
			<xsl:if test="@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:variable name="listId" select="concat(@id, '_input')"/>
			<select autocomplete="off" class="wc_shuffler wc-noajax" id="{$listId}" multiple="multiple">
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="number(@rows) gt 2">
					<xsl:attribute name="size">
						<xsl:value-of select="@rows"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:fieldindicator">
					<xsl:if test="ui:fieldindicator[@id]">
						<xsl:attribute name="aria-describedby">
							<xsl:value-of select="ui:fieldindicator/@id" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ui:fieldindicator[@type='error']">
						<xsl:attribute name="aria-invalid">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<xsl:apply-templates mode="selectableList" select="ui:option | ui:optgroup"/>
			</select>
			<xsl:call-template name="listSortControls">
				<xsl:with-param name="id" select="$listId"/>
			</xsl:call-template>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</span>
	</xsl:template>

	<!-- Transform for WMultiSelectPair. -->
	<xsl:template match="ui:multiselectpair[@readOnly]">
		<ul id="{@id}" class="wc-multiselectpair wc-vgap-sm" data-wc-component="multiselectpair">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="readOnly" select="ui:option | ui:optgroup[ui:option]">
				<xsl:with-param name="single" select="0"/>
			</xsl:apply-templates>
		</ul>
	</xsl:template>

	<xsl:template match="ui:multiselectpair">
		<xsl:variable name="size">
			<xsl:choose>
				<xsl:when test="@size">
					<xsl:value-of select="@size"/>
				</xsl:when>
				<xsl:otherwise>7</xsl:otherwise>
				<!-- 7 is usually big enough to be around the same size as the buttons -->
			</xsl:choose>
		</xsl:variable>
		<fieldset id="{@id}">
			<xsl:variable name="additional">
				<xsl:value-of select="@class"/>
				<xsl:if test="@required">
					<xsl:text> wc_req</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-multiselectpair wc_noborder ', $additional))"/>
			</xsl:attribute>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@accessibleText">
				<xsl:attribute name="aria-label">
					<xsl:value-of select="@accessibleText"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="ui:fieldindicator">
				<xsl:if test="ui:fieldindicator[@id]">
					<xsl:attribute name="aria-describedby">
						<xsl:value-of select="ui:fieldindicator/@id" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="ui:fieldindicator[@type='error']">
					<xsl:attribute name="aria-invalid">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:if>
			</xsl:if>
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
			<span>
				<label for="{$availId}">
					<xsl:value-of select="@fromListName"/>
				</label>
				<select autocomplete="off" class="wc_msp_av wc-noajax" id="{$availId}" multiple="multiple" size="{$size}">
					<xsl:if test="@disabled">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates mode="multiselectPair" select="ui:option[not(@selected)] | ui:optgroup[ui:option[not(@selected)]]">
						<xsl:with-param name="applyWhich" select="'unselected'"/>
					</xsl:apply-templates>
				</select>
			</span>
			<!-- BUTTONS -->
			<span class="wc_msp_btncol">
				<xsl:text>&#x00a0;</xsl:text>
				<button aria-controls="{concat(@id, '_a',' ',@id, '_s')}" class="wc_btn_icon wc-invite" type="button" value="add">
					<xsl:attribute name="title">
						<xsl:text>{{#i18n}}msp_add{{/i18n}}</xsl:text>
					</xsl:attribute>
					<xsl:if test="@disabled">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<i aria-hidden="true" class="fa fa-fw fa-angle-right"/>
				</button>
				<button aria-controls="{concat(@id, '_a',' ',@id, '_s')}" class="wc_btn_icon wc-invite" type="button" value="aall">
					<xsl:attribute name="title">
						<xsl:text>{{#i18n}}msp_addAll{{/i18n}}</xsl:text>
					</xsl:attribute>
					<xsl:if test="@disabled">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<i aria-hidden="true" class="fa fa-fw fa-angle-double-right"/>
				</button>
				<button aria-controls="{concat(@id, '_a',' ',@id, '_s')}" class="wc_btn_icon wc-invite" type="button" value="rem">
					<xsl:attribute name="title">
						<xsl:text>{{#i18n}}msp_remove{{/i18n}}</xsl:text>
					</xsl:attribute>
					<xsl:if test="@disabled">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<i aria-hidden="true" class="fa fa-fw fa-angle-left"/>
				</button>
				<button aria-controls="{concat(@id, '_a',' ',@id, '_s')}" class="wc_btn_icon wc-invite" type="button" value="rall">
					<xsl:attribute name="title">
						<xsl:text>{{#i18n}}msp_removeAll{{/i18n}}</xsl:text>
					</xsl:attribute>
					<xsl:if test="@disabled">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<i aria-hidden="true" class="fa fa-fw fa-angle-double-left"/>
				</button>
			</span>
			<xsl:variable name="toId" select="concat(@id, '_s')"/>
			<span>
				<!-- SELECTED LIST -->
				<label for="{$toId}">
					<xsl:value-of select="@toListName"/>
				</label>
				<select autocomplete="off" class="wc_msp_chos wc-noajax" id="{$toId}" multiple="multiple" size="{$size}">
					<xsl:if test="@disabled">
						<xsl:attribute name="disabled">
							<xsl:text>disabled</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates mode="multiselectPair" select="ui:option[@selected] | ui:optgroup[ui:option[@selected]]">
						<xsl:with-param name="applyWhich" select="'selected'"/>
					</xsl:apply-templates>
				</select>
			</span>
			<xsl:if test="@shuffle">
				<xsl:call-template name="listSortControls">
					<xsl:with-param name="id" select="$toId"/>
				</xsl:call-template>
			</xsl:if>
			<select autocomplete="off" class="wc_msp_order wc_nolabel" hidden="hidden" multiple="multiple">
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates mode="multiselectPair"/>
			</select>
			<xsl:apply-templates select="ui:fieldindicator"/>
			<xsl:element name="input">
				<xsl:attribute name="type">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="concat(@id, '-h')"/>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:text>x</xsl:text>
				</xsl:attribute>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
		</fieldset>
	</xsl:template>

	<!--
		The transform for optGroups within a multiSelectPair option list.

		param applyWhich:
			Use: "selected", "unselected" or "all"; default "all"

			This parameter indicates which options in the optGroup should be included in apply-templates. It will depend upon whether we are building
			the unselected list, the selected list or the reference list.
	-->
	<xsl:template match="ui:optgroup" mode="multiselectPair">
		<xsl:param name="applyWhich" select="'all'"/>
		<optgroup label="{@label}">
			<xsl:choose>
				<xsl:when test="$applyWhich eq 'selected'">
					<xsl:apply-templates mode="multiselectPair" select="ui:option[@selected]"/>
				</xsl:when>
				<xsl:when test="$applyWhich eq 'unselected'">
					<xsl:apply-templates mode="multiselectPair" select="ui:option[not(@selected)]"/>
				</xsl:when>
				<xsl:otherwise>
					<!--the order list comes here -->
					<xsl:apply-templates mode="multiselectPair"/>
				</xsl:otherwise>
			</xsl:choose>
		</optgroup>
	</xsl:template>

	<!--
		The transform for each option in the multiSelectPair.
	-->
	<xsl:template match="ui:option" mode="multiselectPair">
		<option value="{@value}">
			<xsl:value-of select="normalize-space(.)"/>
		</option>
	</xsl:template>

</xsl:stylesheet>
