<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		Common helper template to create "title" attributes.
		param title: string to use instead of component's toolTip attribute.
		param contentAfter: additional content to apply after the title content.
	-->
	<xsl:template name="title">
		<xsl:param name="title" select="''"/>
		<xsl:param name="contentAfter" select="''"/>

		<xsl:variable name="text">
			<xsl:choose>
				<xsl:when test="$title ne ''">
					<xsl:value-of select="$title"/>
				</xsl:when>
				<xsl:when test="@toolTip">
					<xsl:value-of select="@toolTip"/>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="$contentAfter ne ''">
				<xsl:value-of select="concat(' ', normalize-space($contentAfter))"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="content" select="normalize-space($text)"/>

		<xsl:if test="$content ne ''">
			<xsl:attribute name="title">
				<xsl:value-of select="$content"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
