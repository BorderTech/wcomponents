<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		The data items in a defintion list map directly to HTML DD elements
-->
	<xsl:template match="ui:data">
		<dd>
			<xsl:apply-templates/>
		</dd>
	</xsl:template>
</xsl:stylesheet>
