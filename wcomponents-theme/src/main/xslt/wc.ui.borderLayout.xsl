<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.gapClass.xsl"/>
	<!-- BorderLayout is a rough CSS emulation of AWT BorderLayout. This Layout is deprecated and will be removed. -->
	<xsl:template match="ui:borderlayout">
		<xsl:variable name="vgap">
			<xsl:if test="@vgap">
				<xsl:call-template name="gapClass">
					<xsl:with-param name="gap" select="@vgap"/>
					<xsl:with-param name="isVGap" select="1"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>
		<div>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="$vgap"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="ui:north"/>
			<xsl:if test="count(ui:west|ui:center|ui:east) gt 0">
				<div>
					<xsl:attribute name="class">
						<xsl:text>wc_bl_mid</xsl:text>
						<xsl:if test="@hgap">
							<xsl:call-template name="gapClass">
								<xsl:with-param name="gap" select="@hgap"/>
							</xsl:call-template>
						</xsl:if>
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
			<xsl:apply-templates />
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
					IE8 needs more help because it does not know about last child or flex layouts.
					We should be able to remove all this stuff (eventually) when flex-grow: 3 differs from flex-grow: 1 on all target browsers
					(wishful thinking?).
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
			<xsl:apply-templates />
		</div>
	</xsl:template>
</xsl:stylesheet>
