<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!-- The error output in the message box. This must include a link to the component in an error state. -->
	<xsl:template match="ui:error">
		<div class="wc-error">
			<a href="{concat('#',@for)}">
				<xsl:apply-templates/>
			</a>
		</div>
	</xsl:template>
	
	<!-- The inline error output for each ui:error for a component in a error state. -->
	<xsl:template match="ui:error" mode="inline">
		<span class="wc-error">
			<xsl:apply-templates/>
		</span>
	</xsl:template>
</xsl:stylesheet>
