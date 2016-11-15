<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		A WLabel for a WSelectToggle when that selectToggle has renderAs of 'control'.
		
		This mode is called from the transform of ui:se;ectToggle and the label becomes the title of the toggle button. See wc.common.selectToggle.xsl
	-->
	<xsl:template match="ui:label" mode="selectToggle">
		<xsl:variable name="value">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$value ne '' or @hint">
				<xsl:value-of select="normalize-space(concat($value,' ',@hint))"/>
			</xsl:when>
			<xsl:when test="@accessibleText">
				<xsl:value-of select="@accessibleText"/>
			</xsl:when>
			<xsl:when test="@toolTip">
				<xsl:value-of select="@toolTip"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>{{t 'toggle_all_label'}}</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
