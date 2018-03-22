<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		Transform for WFigure. Direct map to Figure element. The WDecoratedLabel child maps to Figcaption element.
	-->
	<xsl:template match="ui:figure">
		<xsl:variable name="mode" select="@mode"/>
		<figure id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:if test="$mode eq 'lazy' and @hidden">
						<xsl:text>wc_magic</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
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
