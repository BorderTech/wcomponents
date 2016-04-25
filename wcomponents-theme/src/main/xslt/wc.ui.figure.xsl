<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.aria.live.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WFigure. Direct map to Figure element. The WDecoratedLabel child maps to Figcaption element.
	-->
	<xsl:template match="ui:figure">
		<xsl:variable name="mode" select="@mode"/>
		<xsl:element name="${wc.dom.html5.element.figure}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
				<xsl:with-param name="class">
					<xsl:if test="$mode='lazy' and @hidden">
						<xsl:text> wc_magic</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="ui:margin"/>
			
			<xsl:if test="ui:decoratedlabel">
				<xsl:attribute name="aria-labelledby">
					<xsl:value-of select="ui:decoratedlabel/@id"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="ui:content or ui:decoratedLabel or not($mode='eager')">
				<xsl:apply-templates select="ui:content"/>
				<xsl:apply-templates select="ui:decoratedlabel" mode="figure"/>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
