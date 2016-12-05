<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	
	<xsl:template match="ui:decoratedlabel" mode="section">
		<header>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="live" select="'off'"/>
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:apply-templates select="ui:labelhead">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="ui:labelbody">
				<xsl:with-param name="output" select="'h1'"/>
			</xsl:apply-templates>
			<xsl:variable name="emptyBody">
				<xsl:value-of select="normalize-space(ui:labelbody)"/>
			</xsl:variable>
			<xsl:if test="$emptyBody eq ''">
				<xsl:text>{{t 'requiredLabel'}}</xsl:text>
			</xsl:if>
			<xsl:apply-templates select="ui:labeltail">
				<xsl:with-param name="output" select="'div'"/>
			</xsl:apply-templates>
		</header>
	</xsl:template>
</xsl:stylesheet>
