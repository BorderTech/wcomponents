<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>

	<xsl:template match="ui:checkboxselect[@readOnly]">
		<span>
			<xsl:call-template name="commonAttributes"/>
			<xsl:call-template name="roComponentName"/>
			<xsl:choose>
				<xsl:when test="@layoutColumnCount and number(@layoutColumnCount) gt 1">
					<span data-wc-colcount="{@layoutColumnCount}">
						<xsl:apply-templates select="ui:option" mode="checkbleGroup"/>
					</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="ui:option" mode="checkbleGroup"/>
				</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>

	<xsl:template match="ui:radiobuttonselect[@readOnly]">
		<span>
			<xsl:call-template name="commonAttributes"/>
			<xsl:call-template name="roComponentName"/>
			<xsl:apply-templates select="ui:option" mode="checkbleGroup"/>
		</span>
	</xsl:template>

	<xsl:template match="ui:checkboxselect|ui:radiobuttonselect">
		<fieldset>
			<xsl:call-template name="commonWrapperAttributes"/>
			<xsl:if test="ui:option">
				<xsl:if test="self::ui:checkboxselect">
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
				</xsl:if>
				<xsl:variable name="inputType">
					<xsl:choose>
						<xsl:when test="self::ui:checkboxselect">
							<xsl:text>checkbox</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>radio</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<div>
					<xsl:if test="@layoutColumnCount and number(@layoutColumnCount) gt 1">
						<xsl:attribute name="data-wc-colcount">
							<xsl:value-of select="@layoutColumnCount"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates select="ui:option" mode="checkbleGroup">
						<xsl:with-param name="type" select="$inputType"/>
					</xsl:apply-templates>
				</div>
			</xsl:if>
			<xsl:call-template name="hField"/>
			<xsl:apply-templates select="ui:fieldindicator"/>
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
		<xsl:choose>
			<xsl:when test="../@readOnly">
				<xsl:variable name="class">
					<xsl:text>wc-option</xsl:text>
					<xsl:if test="count(../ui:option) lt 2">
						<xsl:text> wc-inline</xsl:text>
					</xsl:if>
				</xsl:variable>
				<span class="{$class}">
					<xsl:value-of select="$labelcontent"/>
				</span>
			</xsl:when>
			<xsl:otherwise>
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
						<xsl:if test="@selected">
							<xsl:attribute name="checked">checked</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="field" select=".."/>
						</xsl:call-template>
						<xsl:if test="../@submitOnChange">
							<xsl:attribute name="class">
								<xsl:text>wc_soc</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="parent::ui:radiobuttonselect">
							<xsl:call-template name="requiredElement">
								<xsl:with-param name="field" select=".."/>
							</xsl:call-template>
							<xsl:if test="@isNull">
								<xsl:attribute name="data-wc-null">
									<xsl:text>1</xsl:text>
								</xsl:attribute>
							</xsl:if>
						</xsl:if>
					</xsl:element>
					<span>
						<xsl:value-of select="$labelcontent"/>
					</span>
				</label>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
