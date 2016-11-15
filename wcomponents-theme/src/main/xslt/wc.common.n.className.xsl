<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<!-- do not override -->
	
	<xsl:template name="commonClassHelper">
		<xsl:param name="additional" select="''"/>

		<xsl:variable name="baseClass" select="concat('wc-', local-name(.))"/>
		<xsl:value-of select="$baseClass"/>
		<xsl:if test="@type">
			<xsl:value-of select="concat(' ',$baseClass,'-type-', @type)"/>
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
		<xsl:if test="$additional ne ''">
			<xsl:variable name="moreclasses" select="normalize-space($additional)"/>
			<xsl:if test="$moreclasses ne ''">
				<xsl:value-of select="concat(' ', $moreclasses)"/>
			</xsl:if>
		</xsl:if>
		<xsl:if test="@class">
			<xsl:variable name="classes" select="normalize-space(@class)"/>
			<xsl:if test="$classes ne ''">
				<xsl:value-of select="concat(' ', $classes)"/>
			</xsl:if>
		</xsl:if>
		<xsl:apply-templates select="ui:margin" mode="class" />
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
