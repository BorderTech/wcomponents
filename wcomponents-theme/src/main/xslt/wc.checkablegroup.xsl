
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<xsl:template match="ui:checkboxselect[@readOnly]">
		<xsl:variable name="layoutClass">
			<xsl:if test="@layout">
				<xsl:value-of select="concat(' wc-layout-', @layout)"/>
			</xsl:if>
		</xsl:variable>
		<span id="{@id}" data-wc-component="checkboxselect" class="{normalize-space(concat('wc-checkboxselect wc-checkableselect ', @class, $layoutClass))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="@layoutColumnCount and number(@layoutColumnCount) gt 1">
					<span data-wc-colcount="{@layoutColumnCount}">
						<xsl:apply-templates mode="checkbleGroupRO" select="ui:option"/>
					</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates mode="checkbleGroupRO" select="ui:option"/>
				</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>

	<xsl:template match="ui:radiobuttonselect[@readOnly]">
		<xsl:variable name="layoutClass">
			<xsl:if test="@layout">
				<xsl:value-of select="concat(' wc-layout-', @layout)"/>
			</xsl:if>
		</xsl:variable>
		<span id="{@id}" data-wc-component="radiobuttonselect" class="{normalize-space(concat('wc-radiobuttonselect wc-checkableselect ', @class, $layoutClass))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="checkbleGroupRO" select="ui:option"/>
		</span>
	</xsl:template>

	<!-- Transforms each option which is in a column in read-only mode -->
	<xsl:template match="ui:option" mode="checkbleGroupRO">
		<xsl:variable name="labelcontent">
			<xsl:choose>
				<xsl:when test="normalize-space(.)">
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@value">
					<xsl:value-of select="@value"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="class">
			<xsl:text>wc-option</xsl:text>
			<xsl:if test="count(../ui:option) lt 2">
				<xsl:text> wc-inline</xsl:text>
			</xsl:if>
		</xsl:variable>
		<span class="{$class}">
			<xsl:value-of select="$labelcontent"/>
		</span>
	</xsl:template>

	<xsl:template match="ui:checkboxselect">
		<xsl:variable name="additional">
			<xsl:if test="@frameless">
				<xsl:text> wc_noborder</xsl:text>
			</xsl:if>
			<xsl:if test="@required">
				<xsl:text> wc_req</xsl:text>
			</xsl:if>
			<xsl:if test="@layout">
				<xsl:value-of select="concat(' wc-layout-', @layout)"/>
			</xsl:if>
		</xsl:variable>
		<fieldset id="{@id}" class="{normalize-space(concat('wc-checkboxselect wc-checkableselect ', $additional))}">
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
			<xsl:if test="ui:option">
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
				<div>
					<xsl:if test="@layoutColumnCount and number(@layoutColumnCount) gt 1">
						<xsl:attribute name="data-wc-colcount">
							<xsl:value-of select="@layoutColumnCount"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates mode="checkbleGroup" select="ui:option">
						<xsl:with-param name="type" select="'checkbox'"/>
					</xsl:apply-templates>
				</div>
			</xsl:if>
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

	<xsl:template match="ui:radiobuttonselect">
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@frameless">
				<xsl:text> wc_noborder</xsl:text>
			</xsl:if>
			<xsl:if test="@required">
				<xsl:text> wc_req</xsl:text>
			</xsl:if>
			<xsl:if test="@layout">
				<xsl:value-of select="concat(' wc-layout-', @layout)"/>
			</xsl:if>
		</xsl:variable>
		<fieldset id="{@id}" class="{normalize-space(concat('wc-radiobuttonselect wc-checkableselect ', $additional))}">
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
			<xsl:if test="ui:option">
				<div>
					<xsl:if test="@layoutColumnCount and number(@layoutColumnCount) gt 1">
						<xsl:attribute name="data-wc-colcount">
							<xsl:value-of select="@layoutColumnCount"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates mode="checkbleGroup" select="ui:option">
						<xsl:with-param name="type" select="'radio'"/>
					</xsl:apply-templates>
				</div>
			</xsl:if>
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

	<!-- Transforms each option which is in a column -->
	<xsl:template match="ui:option" mode="checkbleGroup">
		<xsl:param name="type"/>

		<xsl:variable name="labelcontent">
			<xsl:choose>
				<xsl:when test="normalize-space(.)">
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@value">
					<xsl:value-of select="@value"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<label class="wc-option">
			<xsl:element name="input">
				<xsl:attribute name="type">
					<xsl:value-of select="$type"/>
				</xsl:attribute>
				<xsl:attribute name="name">
					<xsl:value-of select="../@id"/>
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:value-of select="@value"/>
				</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="generate-id()"/>
				</xsl:attribute>
				<xsl:if test="@selected">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				<xsl:if test="../@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="../@submitOnChange">
					<xsl:attribute name="class">
						<xsl:text>wc_soc</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="parent::ui:radiobuttonselect">
					<xsl:if test="../@required">
						<xsl:attribute name="required">
							<xsl:text>required</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@isNull">
						<xsl:attribute name="data-wc-null">
							<xsl:text>1</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
			</xsl:element>
			<span class="wc-labeltext">
				<xsl:value-of select="$labelcontent"/>
			</span>
		</label>
	</xsl:template>

</xsl:stylesheet>
