<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.listSortControls.xsl"/>
	<xsl:import href="wc.common.readOnly.xsl"/>
	<!--
		WShuffler is a component designed to allow a fixed list of options to have their order changed.

		This template outputs the shuffler DIV/UL element. If not readOnly then
		it also outputs a SELECT element and calls listSortControls to produce the shuffler
		buttons.

		It uses the selectableList mode of ui:option and ui:optgroup in the same way as a regular WMultiSelect
	-->
	<xsl:template match="ui:shuffler">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="myLabel" select="key('labelKey',$id)[1]"/>
		<xsl:choose>
			<xsl:when test="@readOnly">
				<ol>
					<xsl:call-template name="commonAttributes">
						<xsl:with-param name="isWrapper" select="1"/>
						<xsl:with-param name="live" select="'off'"/>
						<xsl:with-param name="class">
							<xsl:text>wc_list_nb</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="title"/>
					<xsl:if test="$myLabel">
						<xsl:attribute name="aria-labelledby">
							<xsl:value-of select="$myLabel/@id"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="roComponentName"/>
					<xsl:apply-templates select="ui:option|ui:optgroup" mode="readOnly">
						<xsl:with-param name="showOptions" select="'all'"/>
						<xsl:with-param name="single" select="0"/>
					</xsl:apply-templates>
				</ol>
			</xsl:when>
			<xsl:otherwise>
				<fieldset>
					<xsl:call-template name="commonWrapperAttributes">
						<xsl:with-param name="myLabel" select="$myLabel"/>
					</xsl:call-template>
					<xsl:call-template name="makeLegend">
						<xsl:with-param name="myLabel" select="$myLabel"/>
					</xsl:call-template>
					<xsl:variable name="listId" select="concat($id,'_l')"/>
					<select id="{$listId}" class="wc_shuffler wc-noajax" multiple="multiple" autocomplete="off">
						<xsl:call-template name="disabledElement">
							<xsl:with-param name="isControl" select="1"/>
						</xsl:call-template>
						<xsl:if test="number(@rows) gt 2">
							<xsl:attribute name="size">
								<xsl:value-of select="@rows"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:apply-templates mode="selectableList"/>
					</select>
					<xsl:call-template name="listSortControls">
						<xsl:with-param name="id" select="$listId"/>
					</xsl:call-template>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
