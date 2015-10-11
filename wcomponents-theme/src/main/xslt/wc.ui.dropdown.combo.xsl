<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl" />
	<xsl:import href="wc.constants.xsl" />
	<xsl:import href="wc.common.missingLabel.xsl" />
	<!--
		ui:dropdown (@type="combo")
		Transform for WDropdown.COMBO which is a combo-box. See wc.ui.dropdown.xsl.
		TODO: this should be removed once WCombo has replaced WDropdown type.COMBO.
	-->
	<xsl:template match="ui:dropdown[@type='combo' and not(@readOnly='true')]">
		<xsl:variable name="id" select="@id" />
		<xsl:variable name="isError" select="key('errorKey',$id)" />
		<xsl:variable name="myLabel" select="key('labelKey',$id)"/>
		<xsl:variable name="listId" select="concat($id, '${wc.ui.combo.id.list.suffix}')"/>
		<xsl:if test="not($myLabel)">
			<xsl:call-template name="checkLabel">
				<xsl:with-param name="force" select="1"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:text>text</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="role">
				<xsl:text>combobox</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="aria-autocomplete">
				<xsl:text>both</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="aria-owns">
				<xsl:value-of select="$listId" />
			</xsl:attribute>
			<!-- every input that implements combo should have autocomplete turned off -->
			<xsl:attribute name="autocomplete">
				<xsl:text>off</xsl:text>
			</xsl:attribute>
			<xsl:if test="@optionWidth">
				<xsl:attribute name="size">
					<xsl:value-of select="@optionWidth" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@data">
				<xsl:attribute name="${wc.ui.selectLoader.attribute.dataListId}">
					<xsl:value-of select="@data" />
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="commonControlAttributes">
				<xsl:with-param name="isError" select="$isError" />
				<xsl:with-param name="name" select="$id" />
				<xsl:with-param name="live" select="'off'" />
				<xsl:with-param name="value">
					<xsl:choose>
						<xsl:when test="@data">
							<xsl:apply-templates select="ui:option[1]" mode="comboValue" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select=".//ui:option[@selected][1]" mode="comboValue" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="myLabel" select="$myLabel[1]"/>
			</xsl:call-template>
		</xsl:element>
		<xsl:element name="ul">
			<xsl:attribute name="id">
				<xsl:value-of select="$listId" />
			</xsl:attribute>
			<xsl:attribute name="role">
				<xsl:text>listbox</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="aria-controls">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:call-template name="hiddenElement"/>
			<xsl:apply-templates mode="comboDataList" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
