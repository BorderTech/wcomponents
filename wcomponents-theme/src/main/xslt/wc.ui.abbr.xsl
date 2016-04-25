<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Make an abbr element. If the component does not have a description make a span because an abbr without a title 
		is worse than useless.

		If the ui:abbr has no content do not output anything.
	-->
	<xsl:template match="ui:abbr">
		<xsl:variable name="element">
			<xsl:choose>
				<xsl:when test="@toolTip">abbr</xsl:when>
				<xsl:otherwise>span</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$element}">
			<xsl:call-template name="makeCommonClass"/>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="@toolTip"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
