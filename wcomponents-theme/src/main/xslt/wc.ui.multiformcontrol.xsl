<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.readOnly.xsl"/>
	<!-- Transforms for WMultiDropdown and WMultiTextField. -->
	<xsl:template match="ui:multidropdown|ui:multitextfield">
		<xsl:variable name="readOnly">
			<xsl:choose>
				<xsl:when test="@readOnly">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"></xsl:number>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="number($readOnly) eq 1 and (self::ui:multidropdown[count(.//ui:option[@selected]) le 1])">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select=".//ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="number($readOnly) eq 1 and (self::ui:multitextfield[count(ui:value) le 1])">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="useReadOnlyMode" select="1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="number($readOnly) eq 1">
				<ul>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="class">
							<xsl:text>wc_list_nb</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="roComponentName"/>
					<xsl:choose>
						<xsl:when test="self::ui:multidropdown">
							<xsl:apply-templates select="ui:option[@selected]|ui:optgroup[ui:option[@selected]]" mode="readOnly">
								<xsl:with-param name="single" select="0"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="ui:value" mode="readOnlyList"/>
						</xsl:otherwise>
					</xsl:choose>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<fieldset aria-relevant="additions removals" aria-atomic="false">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="class" select="'wc_mfc'"/>
					</xsl:call-template>
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
					<ul class="wc_list_nb">
						<xsl:choose>
							<!-- content transform is dependant upon the actual component being transformed-->
							<xsl:when test="self::ui:multidropdown and count(.//ui:option[@selected]) eq 0">
								<xsl:apply-templates select="(ui:option|ui:optgroup/ui:option)[1]" mode="multiDropDown">
									<xsl:with-param name="isSingular" select="1"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="self::ui:multidropdown">
								<xsl:apply-templates select=".//ui:option[@selected]" mode="multiDropDown"/>
							</xsl:when>
							<xsl:when test="ui:value">
								<xsl:apply-templates/>
							</xsl:when>
							<xsl:otherwise>
								<li>
									<xsl:call-template name="multiTextFieldInput"/>
									<xsl:call-template name="multiFieldIcon">
										<xsl:with-param name="isSingular" select="1"/>
									</xsl:call-template>
								</li>
							</xsl:otherwise>
						</xsl:choose>
					</ul>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Transforms for each value in a multiTextField. -->
	<xsl:template match="ui:value">
		<li>
			<xsl:call-template name="multiTextFieldInput"/>
			<xsl:call-template name="multiFieldIcon"/>
		</li>
	</xsl:template>

	<xsl:template match="ui:value" mode="readOnly">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="ui:value" mode="readOnlyList">
		<li>
			<xsl:value-of select="."/>
		</li>
	</xsl:template>

	<!-- Builds the elements for each text field of a multiTextField. -->
	<xsl:template name="multiTextFieldInput">
		<xsl:variable name="field" select="ancestor-or-self::ui:multitextfield"/>
		<xsl:variable name="fieldId">
			<xsl:value-of select="$field/@id"/>
		</xsl:variable>
		<xsl:element name="input">
			<xsl:attribute name="type">
				<xsl:text>text</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="$fieldId"/>
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="concat($fieldId,generate-id())"/>
				<xsl:if test="self::ui:value">
					<xsl:value-of select="concat('-',position())"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:text>{{t 'mfc_option'}}</xsl:text>
			</xsl:attribute>
			<xsl:if test="$field/@size">
				<xsl:attribute name="size">
					<xsl:value-of select="$field/@size"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$field/@maxLength">
				<xsl:attribute name="maxlength">
					<xsl:value-of select="$field/@maxLength"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$field/@pattern">
				<xsl:attribute name="pattern">
					<xsl:value-of select="$field/@pattern"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$field/@minLength">
				<xsl:attribute name="minlength">
					<xsl:value-of select="$field/@minLength"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$field/@placeholder">
				<xsl:attribute name="placeholder">
					<xsl:value-of select="$field/@placeholder"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$field/@autocomplete">
				<xsl:attribute name="autocomplete">
					<xsl:value-of select="$field/@autocomplete"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="disabledElement">
				<xsl:with-param name="isControl" select="1"/>
				<xsl:with-param name="field" select="$field"/>
			</xsl:call-template>
			<xsl:if test="self::ui:value">
				<xsl:attribute name="value">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!-- Each option in a multiDropdown. -->
	<xsl:template match="ui:option" mode="multiDropDown">
		<xsl:param name="isSingular" select="0"/>
		<xsl:variable name="ancestorMDD" select="ancestor::ui:multidropdown"/>
		<xsl:variable name="id" select="$ancestorMDD/@id"/>
		<li>
			<select name="{$id}" id="{concat($id,generate-id(),'-',position())}" title="{{t 'mfc_option'}}">
				<xsl:if test="$ancestorMDD/@submitOnChange">
					<xsl:attribute name="class">
						<xsl:text>wc_soc</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="isControl" select="1"/>
					<xsl:with-param name="field" select="$ancestorMDD"/>
				</xsl:call-template>
				<xsl:if test="$ancestorMDD/@data">
					<xsl:attribute name="data-wc-list">
						<xsl:value-of select="$ancestorMDD/@data"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$ancestorMDD/@autocomplete">
					<xsl:attribute name="autocomplete">
						<xsl:value-of select="$ancestorMDD/@autocomplete"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="$ancestorMDD/*" mode="mfcInList">
					<xsl:with-param name="selectedOption" select="."/>
					<xsl:with-param name="isSingular" select="$isSingular"/>
				</xsl:apply-templates>
			</select>
			<xsl:call-template name="multiFieldIcon"/>
		</li>
	</xsl:template>
	<!-- Each option in a multiDropdown called from within the list of selected options. -->
	<xsl:template match="ui:option" mode="mfcInList">
		<xsl:param name="selectedOption"/>
		<xsl:param name="isSingular" select="0"/>
		<xsl:element name="option">
			<xsl:attribute name="value">
				<xsl:value-of select="@value"/>
			</xsl:attribute>
			<xsl:if test=". eq $selectedOption and number($isSingular) eq 0">
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
		</xsl:element>
	</xsl:template>
	
	<!-- Transform for optgroups within a list of options in WMultiDropdown. -->
	<xsl:template match="ui:optgroup" mode="mfcInList">
		<xsl:param name="selectedOption"/>
		<xsl:param name="isSingular" select="0"/>
		<optgroup label="{@label}">
			<xsl:apply-templates select="ui:option" mode="mfcInList">
				<xsl:with-param name="selectedOption" select="$selectedOption"/>
				<xsl:with-param name="isSingular" select="$isSingular"/>
			</xsl:apply-templates>
		</optgroup>
	</xsl:template>
	<!-- Generate the + and - buttons -->
	<xsl:template name="multiFieldIcon">
		<xsl:param name="isSingular" select="0"/>
		<xsl:variable name="id">
			<xsl:choose>
				<xsl:when test="self::ui:multitextfield">
					<xsl:value-of select="@id"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../@id"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="toolTip">
			<xsl:choose>
				<xsl:when test="number($isSingular) eq 1 or position() eq 1">
					<xsl:text>{{t 'mfc_add'}}</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>{{t 'mfc_remove'}}</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<button type="button" title="{$toolTip}" class="wc_btn_icon wc-invite">
			<xsl:attribute name="aria-controls">
				<xsl:choose>
					<xsl:when test="self::ui:multitextfield or position() eq 1">
						<xsl:value-of select="$id"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($id, generate-id(), '-', position())"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:choose>
				<xsl:when test="self::ui:multitextfield">
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="1"/>
						<xsl:with-param name="field" select="parent::*"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</button>
	</xsl:template>
</xsl:stylesheet>
