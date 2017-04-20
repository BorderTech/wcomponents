<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.multiFormComponent.n.multiFieldIcon.xsl"/>
	<!--
		Each option in a multiDropdown. Each selected option requires a whole select
		element including all of the other options. When no options are selected we
		call this directly from the parent multiDropdown.

		param isSingular: if there are no selected options this is 1 to indicate the multiDropdown
		is applying the template through the first option rather than through each selected option.
		param myLabel: the WLabel for the parent multiDropdown (if any).
	-->
	<xsl:template match="ui:option" mode="multiDropDown">
		<xsl:param name="isSingular" select="0"/>
		<xsl:param name="myLabel"/>
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
			<xsl:call-template name="multiFieldIcon">
				<xsl:with-param name="myLabel" select="$myLabel"/>
			</xsl:call-template>
		</li>
	</xsl:template>
</xsl:stylesheet>
