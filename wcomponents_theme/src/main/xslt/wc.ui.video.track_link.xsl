<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
<!--
 Output an A element linking to a track file.
-->
	<xsl:template match="ui:track" mode="link">
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="@src"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>track</xsl:text>
			</xsl:attribute>
			<xsl:if test="@lang">
				<xsl:attribute name="lang">
					<xsl:value-of select="@lang"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="${wc.common.attrib.attach}">
				<xsl:text>${wc.common.attrib.attach}</xsl:text>
			</xsl:attribute>
			<xsl:if test="@desc">
				<xsl:value-of select="@desc"/>
			</xsl:if>
			<xsl:if test="@kind">
				<xsl:text> (</xsl:text>
				<xsl:value-of select="@kind"/>
				<xsl:text> )</xsl:text>
			</xsl:if>
		</xsl:element>
		<xsl:if test="position()!=last()">
			<xsl:value-of select="' '"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
