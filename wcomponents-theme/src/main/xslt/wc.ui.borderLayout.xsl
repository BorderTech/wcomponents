<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		ui:borderlayout is a layout mode of WPanel which consists of one or more
		containers displayed in a particular pattern.

		This is a rough CSS based emulation of AWT BorderLayout with fixed width
		components. We could apply table layout but max-width is not well supported with
		display:table-cell.

		Inter-cell spacing

		The horizontal spacing between ui:west, ui:center and ui:east is determined
		by the hgap attribute.

		The vertical spacing betwen ui:north, the middle row and ui:south is determined
		by the vgap property

		This template arranges the child elements in the correct order. If there is one
		or more of ui:west, ui:center and ui:east then a wrapper is provided for them
		before they are applied.
	-->
<xsl:template match="ui:borderlayout">
		<xsl:variable name="vgap">
			<xsl:call-template name="getHVGap">
				<xsl:with-param name="gap" select="@vgap"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
			</xsl:attribute>
			<xsl:apply-templates select="ui:north"/>
			<xsl:variable name="colCount" select="count(ui:west|ui:center|ui:east)"/>
			<xsl:if test="$colCount &gt;0">
				<xsl:element name="div">
					<xsl:attribute name="class">
						<xsl:text>wc_borderlayout_middle</xsl:text>
					</xsl:attribute>
					<xsl:if test="ui:north and ($vgap != 0)">
						<xsl:attribute name="style">
							<xsl:value-of select="concat('margin-top:',$vgap,';')" />
						</xsl:attribute>
					</xsl:if>
					<xsl:variable name="hgap">
						<xsl:choose>
							<xsl:when test="not(@hgap) or @hgap='0' or $colCount=1">
								<xsl:number value="0"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="getHVGap">
									<xsl:with-param name="divisor" select="2"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:apply-templates select="ui:west">
						<xsl:with-param name="hgap" select="$hgap"/>
					</xsl:apply-templates>
					<xsl:apply-templates select="ui:center">
						<xsl:with-param name="hgap" select="$hgap"/>
					</xsl:apply-templates>
					<xsl:apply-templates select="ui:east">
						<xsl:with-param name="hgap" select="$hgap"/>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="ui:south">
				<xsl:with-param name="vgap" select="$vgap"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
