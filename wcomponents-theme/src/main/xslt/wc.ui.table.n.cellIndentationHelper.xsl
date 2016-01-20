<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Applies indentation to the first cell (th or td) in each row. Called from
		transforms of ui:td and ui:th. For info re params see transform of
		ui:table in wc.ui.table.xsl.
	-->
	<xsl:template name="cellIndentationHelper">
		<xsl:param name="indent" select="0"/>
		<xsl:if test="$indent &gt; 0">
			<span class="wc-row-indent" aria-hidden="true">&#x0a;</span>
			<xsl:call-template name="cellIndentationHelper">
				<xsl:with-param name="indent" select="$indent - 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
