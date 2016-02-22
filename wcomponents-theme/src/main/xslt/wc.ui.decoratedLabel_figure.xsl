<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>

	<xsl:template match="ui:decoratedlabel" mode="figure">
		<xsl:element name="${wc.dom.html5.element.figcaption}">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="live" select="'off'"/>
				<xsl:with-param name="isWrapper" select="1"/>
				<xsl:with-param name="class" select="''"/>
			</xsl:call-template>
			<xsl:apply-templates select="ui:labelhead">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="ui:labelbody">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="ui:labeltail">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
