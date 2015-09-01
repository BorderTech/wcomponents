<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		This helper template sets attribute aria-live on many components
		and must never be excluded.

		This is an WAI-ARIA property which indicates the verbose announcement level of
		updates to the content if it is updated via AJAX or a subordinate control.

		See:
			wc.ui.collapsible.xsl
			wc.ui.table.xsl
			wc.ui.dialog.xsl
			wc.ui.multiFormComponent.xsl
			wc.ui.panel.xsl
			wc.ui.tab.xsl

		param live default "polite"
		The verbosity level to set. See http://www.w3.org/TR/wai-aria/states_and_properties
		aria-live guidelines for potential values.
	-->
	<xsl:template name="ariaLabel">
		<xsl:if test="@accessibleText">
			<xsl:attribute name="aria-label">
				<xsl:value-of select="@accessibleText"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
