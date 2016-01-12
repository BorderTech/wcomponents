<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
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
	<xsl:template match="ui:checkBoxSelect|ui:radioButtonSelect">
		<xsl:variable name="inputType">
			<xsl:choose>
				<xsl:when test="self::ui:checkBoxSelect">
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
				<xsl:when test="$readOnly=1 and ($inputType='radio' or count(ui:option[@selected]) &lt;=1)">
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
			<xsl:when test="$hasSingleSelectionRO=1">
				<xsl:call-template name="readOnlyControl">
					<xsl:with-param name="applies" select="ui:option[@selected]"/>
					<xsl:with-param name="useReadOnlyMode" select="1"/>
					<xsl:with-param name="label" select="$myLabel"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="isError" select="key('errorKey',$id)"/>
				<xsl:variable name="title">
					<xsl:choose>
						<xsl:when test="@toolTip ">
							<xsl:value-of select="normalize-space(@toolTip)"/>
						</xsl:when>
						<xsl:when test="@required and self::ui:checkBoxSelect">
							<xsl:value-of select="$$${wc.ui.checkBoxSelect.defaultRequiredTooltip}"/>
						</xsl:when>
						<xsl:when test="@required">
							<xsl:value-of select="$$${wc.ui.radioButtonSelect.defaultRequiredTooltip}"/>
						</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="element">
					<xsl:choose>
						<xsl:when test="$readOnly=1">div</xsl:when>
						<xsl:otherwise>fieldset</xsl:otherwise>
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
						</xsl:with-param>
					</xsl:call-template>

					<xsl:if test="$title!=''">
						<xsl:attribute name="title">
							<xsl:value-of select="$title"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$readOnly=0">
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
						<xsl:call-template name="makeLegend">
							<xsl:with-param name="myLabel" select="$myLabel"/>
						</xsl:call-template>
					</xsl:if>

					<xsl:variable name="firstItemAccessKey">
						<xsl:if test="$myLabel">
							<xsl:value-of select="$myLabel/@accessKey"/>
						</xsl:if>
					</xsl:variable>
					<xsl:variable name="layout" select="@layout"/>
					<xsl:variable name="cols">
						<xsl:choose>
							<xsl:when test="$layout='column' and @layoutColumnCount and @layoutColumnCount &gt; 1">
								<xsl:number value="@layoutColumnCount"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:number value="1"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<!--
						variable rows

						The number of options to show in each column. If all options are in a single
						column we use 0 as a shorthand value so that we do not need to continually
						calculate the total number of options

						When read only we are only interested in selected options. Options which are
						not selected are (currently) not output to the UI.
					-->
					<xsl:variable name="rows">
						<xsl:choose>
							<xsl:when test="$cols=1">
								<xsl:number value="0"/>
							</xsl:when>
							<xsl:when test="$readOnly=1">
								<xsl:value-of select="ceiling(count(ui:option[@selected]) div $cols)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="ceiling(count(ui:option) div $cols)"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<!--
						Applying templates:
						Where we have multiple columns we must only apply options which are in
						particular positions from this template and use those options to apply the
						rest of the options in that column.
					-->
					<xsl:choose>
						<xsl:when test="$readOnly=1 and $rows=0">
							<xsl:element name="ul">
								<xsl:attribute name="class">
									<xsl:value-of select="$layout"/>
									<xsl:text> wc_list_nb</xsl:text>
								</xsl:attribute>
								<xsl:apply-templates select="ui:option[@selected]" mode="checkableGroupInList">
									<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="$readOnly"/>
								</xsl:apply-templates>
							</xsl:element>
						</xsl:when>
						<xsl:when test="$readOnly=1 and $rows=1">
							<xsl:apply-templates select="ui:option[@selected]" mode="checkableGroup">
								<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
								<xsl:with-param name="inputName" select="$id"/>
								<xsl:with-param name="type" select="$inputType"/>
								<xsl:with-param name="readOnly" select="$readOnly"/>
								<xsl:with-param name="rows" select="0"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:when test="$readOnly=1">
							<xsl:apply-templates select="ui:option[@selected][position() mod $rows = 1]" mode="checkableGroup">
								<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
								<xsl:with-param name="inputName" select="$id"/>
								<xsl:with-param name="type" select="$inputType"/>
								<xsl:with-param name="readOnly" select="$readOnly"/>
								<xsl:with-param name="rows" select="$rows"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:when test="$rows=0 and ui:option">
							<xsl:element name="div">
								<xsl:attribute name="class">
									<xsl:value-of select="$layout"/>
								</xsl:attribute>
								<xsl:apply-templates select="ui:option" mode="checkableGroupInList">
									<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
									<xsl:with-param name="inputName" select="$id"/>
									<xsl:with-param name="type" select="$inputType"/>
									<xsl:with-param name="readOnly" select="$readOnly"/>
								</xsl:apply-templates>
							</xsl:element>
						</xsl:when>
						<xsl:when test="$rows=1 and  ui:option">
							<xsl:apply-templates select="ui:option" mode="checkableGroup">
								<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
								<xsl:with-param name="inputName" select="$id"/>
								<xsl:with-param name="type" select="$inputType"/>
								<xsl:with-param name="readOnly" select="$readOnly"/>
								<xsl:with-param name="rows" select="0"/>
							</xsl:apply-templates>
						</xsl:when>
						<xsl:when test="ui:option">
							<xsl:apply-templates select="ui:option[position() mod $rows = 1]" mode="checkableGroup">
								<xsl:with-param name="firstItemAccessKey" select="$firstItemAccessKey"/>
								<xsl:with-param name="inputName" select="$id"/>
								<xsl:with-param name="type" select="$inputType"/>
								<xsl:with-param name="readOnly" select="$readOnly"/>
								<xsl:with-param name="rows" select="$rows"/>
							</xsl:apply-templates>
						</xsl:when>
					</xsl:choose>
					<xsl:if test="$readOnly=0">
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
