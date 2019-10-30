
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- Transforms for WMultiDropdown -->
	<xsl:template match="ui:multidropdown[@readOnly]">
		<ul id="{@id}" class="{normalize-space(concat('wc-multidropdown wc-vgap-sm ', @class))}" data-wc-component="multidropdown" >
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

	<xsl:template match="ui:multidropdown">
		<fieldset aria-atomic="false" aria-relevant="additions removals" id="{@id}">
			<xsl:variable name="additional">
				<xsl:value-of select="@class"/>
				<xsl:if test="@required">
					<xsl:text> wc_req</xsl:text>
				</xsl:if>
			</xsl:variable>
			<xsl:attribute name="class">
				<xsl:value-of select="normalize-space(concat('wc-multidropdown wc_mfc wc_noborder ', $additional))"/>
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
			<ul class="wc_list_nb wc-vgap-sm">
				<xsl:choose>
					<xsl:when test="count(.//ui:option[@selected]) eq 0">
						<xsl:apply-templates mode="multiDropDown" select="(ui:option | ui:optgroup/ui:option)[1]"> </xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates mode="multiDropDown" select=".//ui:option[@selected]"/>
					</xsl:otherwise>
				</xsl:choose>
			</ul>
			<xsl:apply-templates select="ui:fieldindicator"/>
		</fieldset>
	</xsl:template>

	<!-- Each (selected) option in a multiDropdown. -->
	<xsl:template match="ui:option" mode="multiDropDown">
		<xsl:variable name="mdd" select="ancestor::ui:multidropdown"/>
		<xsl:variable name="id" select="$mdd/@id"/>
		<xsl:variable name="selectId" select="concat($id, generate-id(), '-', position())"/>
		<li>
			<label for="{$selectId}" class="wc-off">
				<xsl:value-of select="@title"/>
			</label>
			<select id="{$selectId}" name="{$id}">
				<xsl:if test="$mdd/@submitOnChange">
					<xsl:attribute name="class">
						<xsl:text>wc_soc</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$mdd/@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$mdd/@data">
					<xsl:attribute name="data-wc-list">
						<xsl:value-of select="$mdd/@data"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates mode="mfcInList" select="$mdd/*">
					<xsl:with-param name="selectedOption" select="."/>
				</xsl:apply-templates>
			</select>
			<xsl:variable name="toolTip">
				<xsl:choose>
					<xsl:when test="position() eq 1">
						<xsl:text>{{#i18n}}mfc_add{{/i18n}}</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>{{#i18n}}mfc_remove{{/i18n}}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<button class="wc_btn_icon wc-invite" title="{$toolTip}" type="button">
				<xsl:attribute name="aria-controls">
					<xsl:choose>
						<xsl:when test="position() eq 1">
							<xsl:value-of select="$id"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$selectId"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="$mdd/@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:variable name="iconclass">
					<xsl:text>fa-</xsl:text>
					<xsl:choose>
						<xsl:when test="position() = 1">plus</xsl:when>
						<xsl:otherwise>minus</xsl:otherwise>
					</xsl:choose>
					<xsl:text>-square</xsl:text>
				</xsl:variable>
				<i aria-hidden="true" class="fa {$iconclass}"/>
			</button>
		</li>
	</xsl:template>

	<!-- Transform for optgroups within a list of options in WMultiDropdown. -->
	<xsl:template match="ui:optgroup" mode="mfcInList">
		<xsl:param name="selectedOption"/>
		<optgroup label="{@label}">
			<xsl:apply-templates mode="mfcInList" select="ui:option">
				<xsl:with-param name="selectedOption" select="$selectedOption"/>
			</xsl:apply-templates>
		</optgroup>
	</xsl:template>

	<!-- Each option in a multiDropdown called from within the list of selected options. -->
	<xsl:template match="ui:option" mode="mfcInList">
		<xsl:param name="selectedOption"/>
		<option value="{@value}">
			<xsl:if test=". eq $selectedOption">
				<xsl:attribute name="selected">
					<xsl:text>selected</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@isNull">
				<xsl:attribute name="data-wc-null">
					<xsl:text>1</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</option>
	</xsl:template>
</xsl:stylesheet>
