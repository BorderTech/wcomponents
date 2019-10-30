
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!--
		Transform for WFigure. Direct map to Figure element. The WDecoratedLabel child maps to Figcaption element.
	-->
	<xsl:template match="ui:figure">
		<xsl:variable name="mode" select="@mode"/>
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
			<xsl:if test="$mode eq 'lazy' and @hidden">
				<xsl:text> wc_magic</xsl:text>
			</xsl:if>
		</xsl:variable>
		<figure id="{@id}" class="{normalize-space(concat('wc-figure ', $additional))}">
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="ui:content or ui:decoratedLabel or not($mode eq 'eager')">
				<xsl:apply-templates select="ui:content"/>
				<figcaption>
					<xsl:apply-templates select="ui:decoratedlabel"/>
				</figcaption>
			</xsl:if>
		</figure>
	</xsl:template>
</xsl:stylesheet>
