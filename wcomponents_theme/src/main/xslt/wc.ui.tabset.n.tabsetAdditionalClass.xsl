<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Helper template used to output values for the class attribute on the 
		tabset element. Called from transform for ui:tabset. Any String output 
		must commence with a single space.
	-->
	<xsl:template name="tabsetAdditionalClass">
		<xsl:if test="@type='application'">
			<xsl:text> top</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>