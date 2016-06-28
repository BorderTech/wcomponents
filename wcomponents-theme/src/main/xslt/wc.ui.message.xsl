<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Transform for ui:message.
		
		ui:message is a child of either a WMessageBox, WFieldWarningIndicator or a WFieldErrorIndicator.
		See:
			wc.ui.messageBox.xsl
			wc.ui.fieldIndicator.xsl
	-->
	<xsl:template match="ui:message">
		<li>
			<xsl:apply-templates/>
		</li>
	</xsl:template>
</xsl:stylesheet>
