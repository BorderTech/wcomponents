<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Tracking info element for ui:application and ui:ajaxResponse. This element contains the page view tracking info
		and some optional configuration attributes which are not used here.
		
		See analytics.config.xsl
	-->
	<xsl:template match="ui:analytic" mode="analytics">
		<xsl:text>
				,"cat":"</xsl:text><xsl:value-of select="@cat"/><xsl:text>",
				"search":"</xsl:text><xsl:value-of select="@search"/><xsl:text>",
				"results":"</xsl:text><xsl:value-of select="@results"/><xsl:text>",
				"params":[</xsl:text><xsl:apply-templates select="ui:param" mode="track"/><xsl:text>]</xsl:text>
	</xsl:template>
</xsl:stylesheet>