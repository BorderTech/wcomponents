<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<xsl:import href="wc.common.makeLegend.xsl"/>
	<!--
		Checkable selection controls

		Transforms for WRadioButtonSelect and WCheckBoxSelect to generate radio buttons or checkboxes output in lists.

		NOTE: optgroups
		Due to an unfortunate assumption in the JAVA API it appears that WCheckBoxSelect and WRadioButtonSelect support 
		optgroup child elements.  However, these make no sense in the context of a group of radio buttons or checkboxen 
		so we are not going to support them in the default theme. If you use optgroup in this context prepare for this 
		XSLT to fail. This API issue is an error which will be rectified in a future release.


		This template creates the outer containing HTML element for the checkableGroup
		and where necessary the header element. It then sets up the structures for
		applying the options dependent upon the read only status and number of columns.
	-->
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
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>

		<xsl:choose>
			<xsl:when test="number($hasSingleSelectionRO) eq 1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select="ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<xsl:variable name="element">
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1">div</xsl:when>
						<xsl:otherwise>fieldset</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:variable name="layout" select="@layout"/>
				<xsl:variable name="rows">
					<xsl:choose>
						<xsl:when test="@layout eq 'flat'">
							<xsl:number value="1"/>
						</xsl:when>
						<xsl:when test="not(@layoutColumnCount)">
							<xsl:number value="0"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="ceiling(count(ui:option) div number(@layoutColumnCount))"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="{$element}">
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="isError" select="$isError"/>
						<xsl:with-param name="isControl" select="1 - $readOnly"/>
						<xsl:with-param name="class">
							<xsl:text>wc_chkgrp</xsl:text>
							<xsl:if test="not(@frameless)">
								<xsl:text> wc_chkgrp_bdr</xsl:text>
							</xsl:if>
							<xsl:if test="$layout eq 'flat' or number($rows) eq 1">
								<xsl:text> wc-hgap-med</xsl:text>
							</xsl:if>
							<xsl:if test="$layout ne 'flat' and number($rows) eq 1">
								<xsl:text> wc-layout-flat</xsl:text>
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
					<!--
						Applying templates:
						Where we have multiple columns we must only apply options which are in
						particular positions from this template and use those options to apply the
						rest of the options in that column.
					-->
					
					<xsl:variable name="rowClass">
						<xsl:text>wc-row wc-hgap-med wc-respond</xsl:text>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="number($readOnly) eq 1 and number($rows) le 1">
							<ul>
								<xsl:attribute name="class">
									<xsl:text>wc_list_nb</xsl:text>
									<xsl:choose>
										<xsl:when test="$layout eq 'flat' or number($rows) eq 1">
											<xsl:text> wc-hgap-med</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text> wc-vgap-sm</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
								<xsl:apply-templates select="ui:option[@selected]" mode="checkableGroupInList">
									<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="$readOnly"/>
								</xsl:apply-templates>
							</ul>
						</xsl:when>
						<xsl:when test="number($readOnly) eq 1">
							<div class="{$rowClass}">
								<xsl:apply-templates select="ui:option[@selected][position() mod number($rows) eq 1]" mode="checkableGroup">
									<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="$readOnly"/>
									<xsl:with-param name="rows" select="$rows"/>
								</xsl:apply-templates>
							</div>
						</xsl:when>
						<xsl:when test="number($rows) eq 0 and ui:option">
							<div class="wc-vgap-sm">
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
						<xsl:call-template name="inlineError">
							<xsl:with-param name="errors" select="$isError"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
