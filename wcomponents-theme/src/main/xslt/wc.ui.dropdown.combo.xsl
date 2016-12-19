<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl" />
	<!--
		ui:dropdown (@type="combo")
		Transform for WDropdown.COMBO which is a combo-box.
	-->
	<xsl:template match="ui:dropdown[@type='combo' and not(@readOnly)]">
		<xsl:variable name="id" select="@id" />
		<xsl:variable name="listId" select="concat($id, '_l')"/>
		<xsl:variable name="inputId">
			<xsl:value-of select="concat($id,'_input')"/>
		</xsl:variable>
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
				<xsl:attribute name="type">
					<xsl:text>text</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="$inputId"/>
				</xsl:attribute>
				<xsl:attribute name="role">
					<xsl:text>textbox</xsl:text>
				</xsl:attribute>
				<!-- every input that implements combo should have autocomplete turned off -->
				<xsl:attribute name="autocomplete">
					<xsl:text>off</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:choose>
						<xsl:when test="@data">
							<xsl:apply-templates select="ui:option[1]" mode="comboValue" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select=".//ui:option[@selected][1]" mode="comboValue" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:call-template name="requiredElement">
					<xsl:with-param name="useNative" select="1"/>
				</xsl:call-template>
				<xsl:call-template name="ariaLabel"/>
			</xsl:element>
			<button value="{$inputId}" tabindex="-1" id="{concat($id, '_list')}" type="button" aria-hidden="true" class="wc_suggest wc_btn_icon wc-invite">
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="isControl" select="1"/>
				</xsl:call-template>
			</button>
			<span id="{$listId}" role="listbox" aria-controls="{$id}">
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
		The option is the value of the text input of the HTML5 combo.
	-->
	<xsl:template match="ui:option" mode="comboValue">
		<xsl:value-of select="."/>
	</xsl:template>
</xsl:stylesheet>
