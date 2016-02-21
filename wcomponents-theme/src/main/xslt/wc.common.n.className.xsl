<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:template name="commonClassHelper">
		<xsl:param name="additional"/>
		<xsl:variable name="class">
			<xsl:text>wc-</xsl:text>
			<xsl:value-of select="local-name()"/>
			<xsl:if test="@track">
				<xsl:text> wc_here</xsl:text>
			</xsl:if>
			<xsl:if test="$additional != ''">
				<xsl:value-of select="concat(' ', $additional)"/>
			</xsl:if>
			<xsl:if test="@class">
				<xsl:value-of select="concat(' ', @class)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:value-of select="normalize-space($class)"/>
	</xsl:template>
	
	<xsl:template name="makeCommonClass">
		<xsl:param name="additional"/>
		<xsl:attribute name="class">
			<xsl:call-template name="commonClassHelper">
				<xsl:with-param name="additional" select="$additional"/>
			</xsl:call-template>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
