<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Transform for the noData child of a tbody. This is a String so just needs to be
		wrapped up properly.

	-->
	<xsl:template match="ui:nodata">
		<div class="wc-nodata">
			<xsl:value-of select="."/>
		</div>
	</xsl:template>
</xsl:stylesheet>
