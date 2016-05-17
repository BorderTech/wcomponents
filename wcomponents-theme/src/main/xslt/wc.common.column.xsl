<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Helper template for WColumn and ColumnLayout cells.
	-->
	<xsl:template name="column">
		<xsl:param name="align" select="@align"/>
		<xsl:param name="width" select="@width"/>

		<div>
			<xsl:if test="self::ui:column">
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<xsl:call-template name="ajaxTarget"/>
			</xsl:if>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="$align"/>
					<xsl:if test="not($align) or $align = ''">
						<xsl:text>left</xsl:text>
					</xsl:if>
					<xsl:if test="not(self::ui:column)">
						<xsl:text> wc-column</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:if test="$width and $width != 0">
				<xsl:attribute name="style">
					<xsl:value-of select="concat('width:',$width,'%;')"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
