<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.buttonLinkCommon.xsl" />
	<!-- 
		WPrintButton
	-->
	<xsl:template match="ui:printbutton">
		<button name="{@id}" value="x" type="button">
			<xsl:call-template name="buttonLinkCommonAttributes">
				<xsl:with-param name="class">
					<xsl:if test="@type">
						<xsl:text>wc-linkbutton</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="buttonLinkCommonContent" />
		</button>
	</xsl:template>
</xsl:stylesheet>
