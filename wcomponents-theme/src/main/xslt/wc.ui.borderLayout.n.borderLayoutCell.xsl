<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Helper to create the HTML output of each cell in the layout.

		param class: If not empty this class is applied to the generated HTML element.
		param width: If not empty this width is applied as an inline style attribute to the generated  HTML element.
		param hgap default 0: The space between west, center and east cells.
		param vgap default 0: The space between rows in the layout.
	-->
	<xsl:template name="borderLayoutCell">
		<xsl:param name="hgap" select="0"/>
		<xsl:param name="vgap" select="0"/>
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:if test="self::ui:west or self::ui:east or self::ui:center">
					<!-- IE8 needs more help because it does not know about last child -->
					<xsl:variable name="colCount" select="count(../ui:west|../ui:east|../ui:center)"/>
					<xsl:choose>
						<xsl:when test = "$colCount = 1">
							<xsl:text> wc_borderlayout_middle100</xsl:text>
						</xsl:when>
						<xsl:when test="(self::ui:west or self::ui:east) and ../ui:center">
							<xsl:text> wc_borderlayout_middle25</xsl:text>
						</xsl:when>
						<xsl:when test="(self::ui:east and (../ui:west)) or (self::ui:west and (../ui:east)) or ($colCount = 3 and self::ui:center)">
							<xsl:text> wc_borderlayout_middle50</xsl:text>
						</xsl:when>
						<xsl:when test="self::ui:center">
							<xsl:text> wc_borderlayout_middle75</xsl:text>
						</xsl:when>
					</xsl:choose>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test=" $hgap!=0 or $vgap!=0">
				<xsl:attribute name="style">
					<xsl:if test="$hgap!=0">
						<xsl:choose>
							<xsl:when test="self::ui:east">
								<xsl:value-of select="concat('padding-left:',$hgap,';')" />
							</xsl:when>
							<xsl:when test="self::ui:west">
								<xsl:value-of select="concat('padding-right:',$hgap,';')" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="../ui:west">
									<xsl:value-of select="concat('padding-left:',$hgap,';')" />
								</xsl:if>
								<xsl:if test="../ui:east">
									<xsl:value-of select="concat('padding-right:',$hgap,';')" />
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
					<xsl:if test="$vgap!=0">
						<xsl:value-of select="concat('margin-top:',$vgap,';')" />
					</xsl:if>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
