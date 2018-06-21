
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- Transform for WSection. -->
	<xsl:template match="ui:section">
		<xsl:variable name="mode" select="@mode"/>
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
			<xsl:if test="@mode eq 'lazy' and @hidden">
				<xsl:text> wc_magic</xsl:text>
			</xsl:if>
		</xsl:variable>
		<section id="{@id}" class="{normalize-space(concat('wc-section ', $additional))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="*[not(self::ui:margin)] or not($mode eq 'eager')">
				<xsl:apply-templates select="ui:decoratedlabel" mode="section"/>
				<xsl:apply-templates select="ui:panel">
					<xsl:with-param name="type" select="''"/>
				</xsl:apply-templates>
			</xsl:if>
		</section>
	</xsl:template>

	<xsl:template match="ui:decoratedlabel" mode="section">

		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' wc-decoratedlabel-type-', @type)"/>
			</xsl:if>
		</xsl:variable>
		<header id="{@id}" class="{normalize-space(concat('wc-decoratedlabel ', $additional))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:labelhead">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="ui:labelbody">
				<xsl:with-param name="output" select="'h1'"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="ui:labeltail">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</header>
	</xsl:template>
</xsl:stylesheet>
