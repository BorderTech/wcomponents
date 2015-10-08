<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Helper template to position tab list in relation to tab panel.
		Return 1 if the list goes after the content.
	-->
	<xsl:template name="tabsAfterContent">
		<xsl:if test="@type ='right'">
			<xsl:number value="1"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
