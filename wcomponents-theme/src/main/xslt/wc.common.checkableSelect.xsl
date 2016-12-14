<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.makeLegend.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<!--  WRadioButtonSelect and WCheckBoxSelect -->
	<xsl:template match="ui:checkboxselect|ui:radiobuttonselect">
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
		<xsl:variable name="readOnly">
			<xsl:choose>
				<xsl:when test="@readOnly">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="hasSingleSelectionRO">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1 and ($inputType eq 'radio' or count(ui:option[@selected]) le 1)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="id">
			<xsl:value-of select="@id"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="number($hasSingleSelectionRO) eq 1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select="ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="element">
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1">div</xsl:when>
						<xsl:otherwise>fieldset</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="layout" select="@layout"/>
				<xsl:variable name="cols">
					<xsl:choose>
						<xsl:when test="$layout eq 'column' and @layoutColumnCount and number(@layoutColumnCount) gt 1">
							<xsl:number value="number(@layoutColumnCount)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:number value="1"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="rows">
					<xsl:choose>
						<xsl:when test="number($cols) eq 1">
							<xsl:number value="0"/>
						</xsl:when>
						<xsl:when test="number($readOnly) eq 1">
							<xsl:value-of select="ceiling(count(ui:option[@selected]) div $cols)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="ceiling(count(ui:option) div $cols)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
				<xsl:element name="{$element}">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="isControl" select="1 - $readOnly"/>
						<xsl:with-param name="class">
							<xsl:text>wc_chkgrp</xsl:text>
							<xsl:if test="not(@frameless)">
								<xsl:text> wc_chkgrp_bdr</xsl:text>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:choose>
						<xsl:when test="number($readOnly) ne 1">
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
							<xsl:call-template name="makeLegend">
								<xsl:with-param name="myLabel" select="$myLabel"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="title"/>
							<xsl:call-template name="roComponentName"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:variable name="firstItemAccessKey">
						<xsl:if test="$myLabel">
							<xsl:value-of select="$myLabel/@accessKey"/>
						</xsl:if>
					</xsl:variable>
					<xsl:variable name="rowClass">
						<xsl:text>wc-row wc-hgap-med wc-respond</xsl:text>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1 and number($rows) eq 0">
							<ul>
								<xsl:attribute name="class">
									<xsl:text>wc_list_nb</xsl:text>
									<xsl:choose>
										<xsl:when test="$layout eq 'flat'">
											<xsl:text> wc-hgap-med</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text> wc-vgap-sm</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:apply-templates select="ui:option[@selected]" mode="checkableGroupInList">
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="1"/>
								</xsl:apply-templates>
							</ul>
						</xsl:when>
						<xsl:when test="number($readOnly) eq 1 and number($rows) eq 1">
							<xsl:apply-templates select="ui:option[@selected]" mode="checkableGroup">
								<xsl:with-param name="inputName" select="$id"/>
								<xsl:with-param name="type" select="$inputType"/>
								<xsl:with-param name="readOnly" select="1"/>
								<xsl:with-param name="rows" select="0"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:when test="number($readOnly) eq 1">
							<div class="{$rowClass}">
								<xsl:apply-templates select="ui:option[@selected][position() mod number($rows) eq 1]" mode="checkableGroup">
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="1"/>
									<xsl:with-param name="rows" select="$rows"/>
								</xsl:apply-templates>
							</div>
						</xsl:when>
						<xsl:when test="number($rows) eq 0 and ui:option">
							<div>
								<xsl:attribute name="class">
									<xsl:choose>
										<xsl:when test="$layout eq 'flat'">
											<xsl:text>wc-hgap-med</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>wc-vgap-sm</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:apply-templates select="ui:option" mode="checkableGroupInList">
									<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="$readOnly"/>
								</xsl:apply-templates>
							</div>
						</xsl:when>
						<xsl:when test="number($rows) eq 1 and  ui:option">
							<xsl:apply-templates select="ui:option" mode="checkableGroup">
								<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
								<xsl:with-param name="inputName" select="$id"/>
								<xsl:with-param name="type" select="$inputType"/>
								<xsl:with-param name="readOnly" select="$readOnly"/>
								<xsl:with-param name="rows" select="0"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:when test="ui:option">
							<div class="{$rowClass}">
								<xsl:apply-templates select="ui:option[position() mod number($rows) eq 1]" mode="checkableGroup">
									<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="$readOnly"/>
									<xsl:with-param name="rows" select="$rows"/>
								</xsl:apply-templates>
							</div>
						</xsl:when>
					</xsl:choose>
					<xsl:if test="number($readOnly) ne 1">
						<xsl:call-template name="hField"/>
					</xsl:if>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- Template to transform the options in a checkable group. -->
	<xsl:template match="ui:option" mode="checkableGroup">
		<xsl:param name="firstItemAccessKey"/>
		<xsl:param name="inputName"/>
		<xsl:param name="type"/>
		<xsl:param name="rows" select="0"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:variable name="firstAccessKey">
			<xsl:if test="position() eq 1">
				<xsl:value-of select="$firstItemAccessKey"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="layout" select="../@layout"/>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:text>ul</xsl:text>
				</xsl:when>
				<xsl:when test="$layout eq 'flat'">
					<xsl:text>span</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>div</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="class">
			<xsl:text>wc_checkableselect_option_wrapper</xsl:text>
			<xsl:if test="$elementName eq 'ul'">
				<xsl:text> wc_list_nb</xsl:text>
			</xsl:if>
			<xsl:if test="$layout eq 'column'">
				<xsl:text> wc-column</xsl:text>
			</xsl:if>
			<xsl:if test="not($layout eq 'flat')">
				<xsl:text> wc-vgap-sm</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:if test="$class ne ''">
				<xsl:attribute name="class">
					<xsl:value-of select="normalize-space($class)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="checkableSelectOption">
				<xsl:with-param name="optionName" select="$inputName"/>
				<xsl:with-param name="optionType" select="$type"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
				<xsl:with-param name="cgAccessKey" select="$firstAccessKey"/>
			</xsl:call-template>			
			<xsl:if test="number($rows) gt 0">
				<xsl:choose>
					<xsl:when test="number($readOnly) eq 1">
						<xsl:apply-templates select="following-sibling::ui:option[@selected][position() lt number($rows)]" mode="checkableGroupInList">
							<xsl:with-param name="inputName" select="$inputName"/>
							<xsl:with-param name="type" select="$type"/>
							<xsl:with-param name="readOnly" select="1"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="following-sibling::ui:option[position() lt number($rows)]" mode="checkableGroupInList">
							<xsl:with-param name="inputName" select="$inputName"/>
							<xsl:with-param name="type" select="$type"/>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<!-- Transforms each option which is in a column -->
	<xsl:template match="ui:option" mode="checkableGroupInList">
		<xsl:param name="firstItemAccessKey" select="''"/>
		<xsl:param name="inputName"/>
		<xsl:param name="type"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:if test="number($readOnly) eq 0 or @selected">
			<xsl:variable name="localAccessKey">
				<xsl:if test="position() eq 1 and $firstItemAccessKey ne ''">
					<xsl:value-of select="$firstItemAccessKey"/>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="checkableSelectOption">
				<xsl:with-param name="optionName" select="$inputName"/>
				<xsl:with-param name="optionType" select="$type"/>
				<xsl:with-param name="readOnly" select="$readOnly"/>
				<xsl:with-param name="cgAccessKey" select="$localAccessKey"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- The content of an option to a list item and the element relevent for the option.
	-->
	<xsl:template name="checkableSelectOption">
		<xsl:param name="optionName"/>
		<xsl:param name="optionType" select="''"/>
		<xsl:param name="readOnly" select="0"/>
		<xsl:param name="cgAccessKey" select="''"/>
		<xsl:variable name="uid">
			<xsl:value-of select="concat(../@id,generate-id())"/>
		</xsl:variable>
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:text>li</xsl:text>
				</xsl:when>
				<xsl:when test="../@layout eq 'flat'">
					<xsl:text>span</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>div</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$elementName}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:choose>
				<xsl:when test="number($readOnly) eq 1">
					<xsl:call-template name="checkableSelectOptionLabel"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="input">
						<xsl:attribute name="type">
							<xsl:value-of select="$optionType"/>
						</xsl:attribute>
						<xsl:attribute name="id">
							<xsl:value-of select="$uid"/>
						</xsl:attribute>
						<xsl:attribute name="name">
							<xsl:value-of select="$optionName"/>
						</xsl:attribute>
						<xsl:attribute name="value">
							<xsl:value-of select="@value"/>
						</xsl:attribute>
						<xsl:if test="../@submitOnChange">
							<xsl:attribute name="class">
								<xsl:text>wc_soc</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@isNull and $optionType eq 'radio'">
							<xsl:attribute name="data-wc-null">
								<xsl:text>1</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="$cgAccessKey ne ''">
							<xsl:attribute name="accesskey">
								<xsl:value-of select="$cgAccessKey"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="@selected">
							<xsl:attribute name="checked">checked</xsl:attribute>
						</xsl:if>
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
							<xsl:with-param name="field" select="parent::*"/>
						</xsl:call-template>
						<xsl:if test="parent::ui:radiobuttonselect">
							<xsl:call-template name="requiredElement">
								<xsl:with-param name="field" select="parent::ui:radiobuttonselect"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:element>
					<label for="{$uid}">
						<xsl:call-template name="checkableSelectOptionLabel"/>
					</label>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<!-- Writes the content of the label for each option in the checkable group. -->
	<xsl:template name="checkableSelectOptionLabel">
		<xsl:choose>
			<xsl:when test="normalize-space(.)">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:when test="@value">
				<xsl:value-of select="@value"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
