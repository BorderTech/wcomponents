<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WFigure. Direct map to Figure element. The WDecoratedLabel child maps to Figcaption element.
	-->
	<xsl:template match="ui:figure">
		<xsl:variable name="mode" select="@mode"/>
		<figure>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
				<xsl:with-param name="class">
					<xsl:if test="$mode eq 'lazy' and @hidden">
						<xsl:text> wc_magic</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			
			<xsl:if test="ui:decoratedlabel">
				<xsl:attribute name="aria-labelledby">
					<xsl:value-of select="ui:decoratedlabel/@id"/>
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
