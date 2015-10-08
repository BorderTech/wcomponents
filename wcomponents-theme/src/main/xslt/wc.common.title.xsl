<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.hide.xsl"/>
	<!--
		Common helper template to create "title" attributes.
		param title: string to use instead of component's toolTip attribute.
		param contentAfter: additional content to apply after the title content.
	-->
	<xsl:template name="title">
		<xsl:param name="title"/>
		<xsl:param name="contentAfter"/>

		<xsl:variable name="text">
			<xsl:choose>
				<xsl:when test="$title!=''">
					<xsl:value-of select="$title"/>
				</xsl:when>
				<xsl:when test="@toolTip">
					<xsl:value-of select="@toolTip"/>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="$contentAfter !=''">
				<xsl:value-of select="normalize-space(concat(' ', $contentAfter))"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="content" select="normalize-space($text)"/>

		<xsl:if test="$content !=''">
			<xsl:attribute name="title">
				<xsl:value-of select="$content"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
