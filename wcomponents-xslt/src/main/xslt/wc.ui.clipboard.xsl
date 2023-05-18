<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
				xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
				exclude-result-prefixes="xsl ui html">

	<xsl:template name="clipboard">
		<xsl:param name="target"/>
		<button type="button"
				class="wc-clipboard"
				aria-controls="{$target}"
				id="{@id}-clipboard"
				title="Copy to clipboard">
			<i aria-hidden="true" class="fa fa-clipboard"></i>
		</button>
	</xsl:template>

</xsl:stylesheet>
