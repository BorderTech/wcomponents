<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		The transform for north and south elements within a ui:borderlayout.
	-->
	<xsl:template match="ui:north|ui:south">
		<div class="wc-{local-name()}">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
