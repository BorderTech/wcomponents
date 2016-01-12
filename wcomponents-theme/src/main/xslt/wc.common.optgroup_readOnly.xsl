<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:include href="wc.constants.xsl"/>
	<!--
		Transform for emulator for an optgroup in readOnly mode. This is a list item
		which is classed to be styled similar to a HTML optgroup element in a select.
		ui:optgroup is a child of many components which derive from AbstractList:
			wc.ui.dropdown.xsl
			wc.ui.dropdown.xsl
			wc.ui.multiFormComponent.xsl
			wc.ui.multiSelectPair.xsl
			wc.ui.shuffler.xsl

		param showOptions:
		Indicates which options to include in the apply-templates. The default is to
		only show selected options. The string "all" may be passed in to show all
		options. This is used in the transform for WShuffler for example. Default is
		'selected'
	-->
	<xsl:template match="ui:optgroup" mode="readOnly">
		<xsl:param name="showOptions" select="'selected'"/>
		<xsl:param name="className"/>
		<li class="wc_optgroup">
			<xsl:value-of select="@label"/>
		</li>
		<xsl:choose>
			<xsl:when test="$showOptions='all'">
				<xsl:apply-templates select="ui:option" mode="readOnly">
					<xsl:with-param name="className" select="$className"/>
					<xsl:with-param name="single" select="0"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="ui:option[@selected]" mode="readOnly">
					<xsl:with-param name="className" select="$className"/>
					<xsl:with-param name="single" select="0"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
