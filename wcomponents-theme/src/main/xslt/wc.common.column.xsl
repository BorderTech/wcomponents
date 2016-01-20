<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Helper template for WColumn and ColumnLayout cells.
	-->
	<xsl:template name="column">
		<xsl:param name="align" select="@align"/>
		<xsl:param name="width" select="@width"/>
		<xsl:param name="hgap" select="0"/>
		<xsl:param name="ignoreLeftGap" select="0" />
	
		<xsl:variable name="style">
			<xsl:if test="$width and $width != 0">
				<xsl:value-of select="concat('width:',$width,'%;')"/>
			</xsl:if>
			<xsl:if test="$hgap != 0">
				<xsl:if test="(self::ui:cell and $ignoreLeftGap = 0) or (self:: ui:column and position() &gt; 1)">
					<xsl:value-of select="concat('padding-left:',$hgap,';')"/>
				</xsl:if>
				<xsl:if test="position() != last()">
					<xsl:value-of select="concat('padding-right:',$hgap,';')"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
	
		<div>
			<xsl:if test="self::ui:column">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:call-template name="ajaxTarget"/>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:choose>
					<xsl:when test="$align">
						<xsl:value-of select="concat(' ',$align)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text> ${wc.common.align.std}</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$style != ''">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
