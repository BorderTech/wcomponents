<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!--
		Common helper template for generating the attributes for accessKey implementation.

		*** READ THIS ***
		If you are changing the XSLT: If you call this template without this parameter set to an int other than 1 then this MUST (ABSOLUTELY MUST) be
		the last attribute on the element as it adds content to the element.
	-->
	<xsl:template name="accessKey">
		<xsl:param name="useToolTip" select="1"/>
		<xsl:if test="@accessKey">
			<xsl:attribute name="accesskey">
				<xsl:value-of select="@accessKey"/>
			</xsl:attribute>
			<xsl:if test="number($useToolTip) eq 1">
				<xsl:attribute name="aria-describedby">
					<xsl:value-of select="concat(@id,'_wctt')"/>
				</xsl:attribute>
				<span id="{concat(@id,'_wctt')}" role="tooltip" hidden="hidden">
					<xsl:value-of select="@accessKey"/>
				</span>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
