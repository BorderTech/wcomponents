<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!-- The error output in the message box. This must include a link to the component in an error state. -->
	<xsl:template match="ui:error">
		<li class="error">
			<a href="{concat('#',@for)}">
				<xsl:apply-templates/>
			</a>
		</li>
	</xsl:template>
	
	<!-- The inline error output for each ui:error for a component in a error state. -->
	<xsl:template match="ui:error" mode="inline">
		<li class="error">
			<xsl:apply-templates/>
		</li>
	</xsl:template>
</xsl:stylesheet>
