<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
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
					<xsl:when test = "$colCount = 1">
						<xsl:value-of select="concat($classPrefix, '100')"/>
					</xsl:when>
					<xsl:when test="(self::ui:west or self::ui:east) and ../ui:center">
						<xsl:value-of select="concat($classPrefix, '25')"/>
					</xsl:when>
					<xsl:when test="(self::ui:east and (../ui:west)) or (self::ui:west and (../ui:east)) or ($colCount = 3 and self::ui:center)">
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
