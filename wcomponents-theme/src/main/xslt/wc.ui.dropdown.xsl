<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.fauxOption.xsl"/>
	<!-- WDropdown -->
	<xsl:template match="ui:dropdown">
		<xsl:choose>
			<xsl:when test="@readOnly">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select=".//ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<span>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc_input_wrapper</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="@data">
						<xsl:attribute name="data-wc-list">
							<xsl:value-of select="@data"/>
						</xsl:attribute>
					</xsl:if>
					<select>
						<xsl:call-template name="wrappedInputAttributes"/>
						<xsl:if test="@autocomplete">
							<xsl:attribute name="autocomplete">
								<xsl:value-of select="@autocomplete"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates mode="selectableList"/>
					</select>
				</span>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		ui:dropdown (@type="combo")
		Transform for WDropdown.COMBO which is a combo-box.
	-->
	<xsl:template match="ui:dropdown[@type='combo' and not(@readOnly)]">
		<span role="combobox" aria-expanded="false" aria-autocomplete="list">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:text>wc_input_wrapper wc-combo</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="requiredElement">
				<xsl:with-param name="useNative" select="0"/>
			</xsl:call-template>
			<xsl:call-template name="title"/>
			<xsl:if test="@data">
				<xsl:attribute name="data-wc-list">
					<xsl:value-of select="@data" />
				</xsl:attribute>
			</xsl:if>
			<xsl:element name="input">
				<xsl:call-template name="wrappedInputAttributes">
					<xsl:with-param name="type" select="'text'"/>
					<xsl:with-param name="useTitle" select="0"/>
				</xsl:call-template>
				<xsl:attribute name="role">
					<xsl:text>textbox</xsl:text>
				</xsl:attribute>
				<!-- every input that implements combo should have autocomplete turned off -->
				<xsl:attribute name="autocomplete">
					<xsl:text>off</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:choose>
						<xsl:when test="@data">
							<xsl:value-of select="ui:option[1]" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select=".//ui:option[@selected][1]" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="@optionWidth">
					<xsl:attribute name="size">
						<xsl:value-of select="@optionWidth"/>
					</xsl:attribute>
				</xsl:if>
			</xsl:element>
			<button value="{concat(@id,'_input')}" tabindex="-1" id="{concat(@id, '_list')}" type="button" aria-hidden="true" class="wc_suggest wc_btn_icon wc-invite">
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="isControl" select="1"/>
				</xsl:call-template>
			</button>
			<span id="{concat(@id, '_l')}" role="listbox" aria-controls="{@id}">
				<xsl:if test="not(*)">
					<xsl:attribute name="aria-busy">
						<xsl:text>true</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates mode="comboDataList" />
			</span>
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
		<xsl:call-template name="fauxOption">
			<xsl:with-param name="value">
				<xsl:value-of select="."/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
