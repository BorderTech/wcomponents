<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<!--
		ui:borderlayout is a layout mode of WPanel which consists of one or more containers displayed in a particular 
		pattern. This is a rough CSS based emulation of AWT BorderLayout. 
		
		This template arranges the child elements in the correct order. If there is one or more of ui:west, ui:center 
		and ui:east then a wrapper is provided for them before they are applied.
	-->
	<xsl:template match="ui:borderlayout">
		<div>
			<xsl:attribute name="class">
				<xsl:text>wc-borderlayout</xsl:text>
				<xsl:call-template name="getHVGapClass">
					<xsl:with-param name="isVGap" select="1"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates select="ui:north"/>
			<xsl:variable name="colCount" select="count(ui:west|ui:center|ui:east)"/>
			<xsl:if test="$colCount &gt;0">
				<div>
					<xsl:attribute name="class">
						<xsl:text>wc_bl_mid</xsl:text>
						<xsl:call-template name="getHVGapClass"/>
					</xsl:attribute>
					<xsl:apply-templates select="ui:west"/>
					<xsl:apply-templates select="ui:center"/>
					<xsl:apply-templates select="ui:east"/>
					
				</div>
			</xsl:if>
			<xsl:apply-templates select="ui:south"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
