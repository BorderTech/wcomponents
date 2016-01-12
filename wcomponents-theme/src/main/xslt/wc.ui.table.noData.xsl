<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Transform for the noData child of a tbody. This is a String so just needs to be 
		wrapped up properly.
		
		param addCols see notes in transform for ui:table in wc.ui.table.xsl.
	-->
	<xsl:template match="ui:noData">
		<div class="noData">
			<xsl:value-of select="."/>
			<xsl:if test="not(node())">
				<xsl:value-of select="$$${wc.ui.table.string.noData}"/>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>
