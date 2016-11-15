<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Make an abbr element.
	-->
	<xsl:template match="ui:abbr">
		<abbr>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</abbr>
	</xsl:template>
</xsl:stylesheet>
