<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		This template creates the content of a multiDropdown. This consists of a set
		of label - select - button triplets in appropriate layout.
		
		param readOnly: the read only state of the multiDropdown. This is most efficiently calculated
		once and passed into the template for each option rather than being calculated within each option.
		param myLabel: the label for the multiDropdown (if any)
	-->
	<xsl:template name="multiDropDownContentRenderer">
		<xsl:choose>
			<xsl:when test="count(.//ui:option[@selected]) eq 0">
				<xsl:apply-templates select="(ui:option|ui:optgroup/ui:option)[1]" mode="multiDropDown">
					<xsl:with-param name="isSingular" select="1"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select=".//ui:option[@selected]" mode="multiDropDown"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
