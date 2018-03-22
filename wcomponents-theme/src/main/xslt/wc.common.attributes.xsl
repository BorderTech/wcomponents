<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">


	<!-- Make the HTML class attribute and populate it. -->
	<xsl:template name="makeCommonClass">
		<xsl:param name="additional"/>
		<xsl:variable name="baseClass" select="concat(' wc-', local-name(.))"/>
		<xsl:variable name="computed">
			<xsl:apply-templates select="ui:margin" mode="class" />
			<xsl:value-of select="concat($baseClass, ' ', @class, ' ', $additional)"/>
			<xsl:if test="@type and not(self::ui:file)">
				<xsl:value-of select="concat($baseClass,'-type-', @type)"/>
			</xsl:if>
			<xsl:if test="@align">
				<xsl:value-of select="concat(' wc-align-', @align)"/>
			</xsl:if>
			<xsl:if test="@layout">
				<xsl:value-of select="concat(' wc-layout-', @layout)"/>
			</xsl:if>
			<xsl:if test="@track">
				<xsl:text> wc_here</xsl:text>
			</xsl:if>
			<xsl:if test="ui:fieldindicator">
				<xsl:text> wc-rel</xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:attribute name="class">
			<xsl:value-of select="normalize-space($computed)"/>
		</xsl:attribute>
	</xsl:template>
	
</xsl:stylesheet>
