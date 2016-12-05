<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
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
			<xsl:if test="count(ui:west|ui:center|ui:east) gt 0">
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

	<!--
		The transform for north and south elements within a ui:borderlayout.
	-->
	<xsl:template match="ui:north|ui:south">
		<div class="wc-{local-name()}">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--
		The transform for west, center and east elements within a ui:borderlayout.
	-->
	<xsl:template match="ui:east|ui:west|ui:center">
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="concat('wc-',local-name(.))"/>
				<!-- 
					IE8 needs more help because it does not know about last child or flex layouts. We should be able to
					remove all this stuff (eventually) when flex-grow: 3 differs from flex-grow: 1 on all target
					browsers (wishful thinking?).
				-->
				<xsl:variable name="colCount" select="count(../ui:west|../ui:east|../ui:center)"/>
				<xsl:variable name="classPrefix">
					<xsl:text> wc_bl_mid</xsl:text>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="number($colCount) eq 1">
						<xsl:value-of select="concat($classPrefix, '100')"/>
					</xsl:when>
					<xsl:when test="(self::ui:west or self::ui:east) and ../ui:center">
						<xsl:value-of select="concat($classPrefix, '25')"/>
					</xsl:when>
					<xsl:when test="(self::ui:east and (../ui:west)) or (self::ui:west and (../ui:east)) or (number($colCount) eq 3 and self::ui:center)">
						<xsl:value-of select="concat($classPrefix, '50')"/>
					</xsl:when>
					<xsl:when test="self::ui:center">
						<xsl:value-of select="concat($classPrefix, '75')"/>
					</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
