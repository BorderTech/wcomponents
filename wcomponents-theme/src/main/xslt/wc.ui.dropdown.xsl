
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- WDropdown -->
	<xsl:template match="ui:dropdown[@readOnly]">
		<span id="{@id}" class="{normalize-space(concat('wc-dropdown wc-ro-input ', @class))}" data-wc-component="dropdown">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="readOnly" select=".//ui:option"/>
		</span>
	</xsl:template>

	<xsl:template match="ui:dropdown">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-dropdown-type-', @type)"/>
			</xsl:if>
		</xsl:variable>
		<span id="{@id}" class="{normalize-space(concat('wc-dropdown wc-input-wrapper ', $additional))}">
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
			<xsl:if test="@data">
				<xsl:attribute name="data-wc-list">
					<xsl:value-of select="@data"/>
				</xsl:attribute>
			</xsl:if>
			<select id="{concat(@id, '_input')}" name="{@id}">
				<xsl:if test="@toolTip">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@required">
					<xsl:attribute name="required">
						<xsl:text>required</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessibleText">
					<xsl:attribute name="aria-label">
						<xsl:value-of select="@accessibleText"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@buttonId">
					<xsl:attribute name="data-wc-submit">
						<xsl:value-of select="@buttonId"/>
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
				<xsl:if test="@submitOnChange or @optionWidth">
					<xsl:attribute name="class">
						<xsl:if test="@submitOnChange">
							<xsl:text>wc_soc</xsl:text>
						</xsl:if>
						<xsl:if test="@optionWidth">
							<xsl:if test="@submitOnChange">
								<xsl:value-of select="' '"/>
							</xsl:if>
							<xsl:text>wc-dd-ow-</xsl:text>
							<xsl:value-of select="@optionWidth"/>
						</xsl:if>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@autocomplete">
					<xsl:attribute name="autocomplete">
						<xsl:value-of select="@autocomplete"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates mode="selectableList" select="ui:option | ui:optgroup"/>
			</select>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</span>
	</xsl:template>

	<!--
		##############################################################################################################
		##############################################################################################################
		ui:dropdown (@type="combo")
		Transform for WDropdown.COMBO which is a combo-box.
		##############################################################################################################
		##############################################################################################################
	-->
	<xsl:template match="ui:dropdown[@type = 'combo' and not(@readOnly)]">
		<span id="{@id}" aria-autocomplete="list" aria-expanded="false" role="combobox" class="{normalize-space(concat('wc-dropdown wc-dropdown-type-combo wc-input-wrapper wc-combo ', @class))}">
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
			<xsl:if test="@required">
				<xsl:attribute name="aria-required">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@data">
				<xsl:attribute name="data-wc-list">
					<xsl:value-of select="@data"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:element name="input">
				<xsl:attribute name="id">
					<xsl:value-of select="concat(@id, '_input')"/>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:text>text</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:choose>
						<xsl:when test="@data">
							<xsl:value-of select="ui:option[1]"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select=".//ui:option[@selected][1]"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="role">
					<xsl:text>textbox</xsl:text>
				</xsl:attribute>
				<xsl:if test="@required">
					<xsl:attribute name="required">
						<xsl:text>required</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessibleText">
					<xsl:attribute name="aria-label">
						<xsl:value-of select="@accessibleText"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@buttonId">
					<xsl:attribute name="data-wc-submit">
						<xsl:value-of select="@buttonId"/>
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
				<xsl:if test="@optionWidth or @submitOnChange">
					<xsl:attribute name="class">
						<xsl:if test="@submitOnChange">
							<xsl:text>wc_soc</xsl:text>
						</xsl:if>
						<xsl:if test="@optionWidth">
							<xsl:if test="@submitOnChange">
								<xsl:value-of select="' '"/>
							</xsl:if>
							<xsl:text>wc-dd-ow-</xsl:text>
							<xsl:value-of select="@optionWidth"/>
						</xsl:if>
					</xsl:attribute>
				</xsl:if>
				<!-- every input that implements combo should have autocomplete turned off -->
				<xsl:attribute name="autocomplete">
					<xsl:text>off</xsl:text>
				</xsl:attribute>
				<xsl:if test="@optionWidth">
					<xsl:attribute name="size">
						<xsl:value-of select="@optionWidth"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
			<button aria-hidden="true" class="wc_suggest wc-invite" id="{concat(@id, '_list')}" tabindex="-1" type="button"
				value="{concat(@id,'_input')}">
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<i aria-hidden="true" class="fa fa-caret-down"/>
			</button>
			<span aria-controls="{@id}" id="{concat(@id, '_l')}" role="listbox">
				<xsl:if test="not(*)">
					<xsl:attribute name="aria-busy">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates mode="comboDataList" select="ui:option | ui:optgroup"/>
			</span>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</span>
	</xsl:template>

	<!--
		Outputs nested ui:option elements. Optgroups are not supported in datalist.
	-->
	<xsl:template match="ui:optgroup" mode="comboDataList">
		<xsl:apply-templates mode="comboDataList"/>
	</xsl:template>

	<!--
		Output the options in a datalist element for a native HTML5 Combo. NOTE: we do not output a name attribute as in WComponents for
		WDropdown.COMBO the name and value are always identical.
	-->
	<xsl:template match="ui:option" mode="comboDataList">
		<xsl:variable name="value" select="."/>
		<span class="wc-invite wc-option" data-wc-value="{$value}" role="option" tabIndex="0">
			<xsl:value-of select="$value"/>
		</span>
	</xsl:template>

</xsl:stylesheet>
