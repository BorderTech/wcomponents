<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.missingLabel.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.required.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		This transform is reused by ui:listbox and ui:dropdown.
		See wc.ui.combo.xsl for ui:dropdown with @type="combo" and not @readOnly="true"
	-->
	<xsl:template match="ui:dropdown|ui:listbox">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)"/>
		<xsl:choose>
			<xsl:when test="not(@readOnly)">
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<xsl:if test="not($myLabel)">
					<xsl:call-template name="checkLabel">
						<xsl:with-param name="force" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<select>
					<xsl:call-template name="commonControlAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="name" select="$id"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="myLabel" select="$myLabel[1]"/>
					</xsl:call-template>
					<xsl:if test="self::ui:listbox and not(@single)">
						<xsl:attribute name="multiple">
							<xsl:text>multiple</xsl:text>
						</xsl:attribute>
						<xsl:if test="@min">
							<xsl:attribute name="${wc.common.attrib.min}">
								<xsl:value-of select="@min"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@max">
							<xsl:attribute name="${wc.common.attrib.max}">
								<xsl:value-of select="@max"/>
							</xsl:attribute>
						</xsl:if>
					</xsl:if>
					<xsl:if test="@data">
						<xsl:attribute name="${wc.ui.selectLoader.attribute.dataListId}">
							<xsl:value-of select="@data"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@rows">
						<xsl:attribute name="size">
							<xsl:value-of select="@rows"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@optionWidth">
						<xsl:attribute name="style">
							<xsl:text>width:</xsl:text>
							<xsl:value-of select="@optionWidth"/>
							<xsl:text>em;</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates mode="selectableList"/>
				</select>
				<xsl:call-template name="inlineError">
					<xsl:with-param name="errors" select="$isError"/>
				</xsl:call-template>
				<xsl:if test="self::ui:listbox">
					<xsl:call-template name="hField"/>
				</xsl:if>
			</xsl:when>
			<xsl:when test="count(.//ui:option[@selected]) = 1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select=".//ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
					<xsl:with-param name="label" select="$myLabel"/>
					<xsl:with-param name="style">
						<xsl:choose>
							<xsl:when test="@type='combo' and @optionWidth">
								<xsl:text>width:</xsl:text>
								<xsl:value-of select="@optionWidth"/>
								<xsl:text>em;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="''"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test=".//ui:option[@selected]">
				<ul id="{$id}">
					<xsl:attribute name="class">
						<xsl:call-template name="commonClassHelper"/>
						<xsl:text> wc_list_nb</xsl:text>
					</xsl:attribute>
					<xsl:if test="$myLabel">
						<xsl:attribute name="aria-labelledby">
							<xsl:value-of select="$myLabel/@id"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="hideElementIfHiddenSet"/>
					<xsl:call-template name="ajaxTarget"/>
					<xsl:apply-templates select="ui:option[@selected]|ui:optgroup[ui:option[@selected]]" mode="readOnly">
						<xsl:with-param name="single" select="0"/>
					</xsl:apply-templates>
				</ul>
			</xsl:when>
			<xsl:otherwise>
				<!--  read only and no selected options -->
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select="'none'"/>
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
