<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Helper template for WColumn and ColumnLayout cells.
	-->
	<xsl:template name="column">
		<xsl:param name="align" select="''"/>
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
					<xsl:choose>
						<xsl:when test="self::ui:column">
							<xsl:if test="not(@align)">
								<xsl:text>wc-align-left</xsl:text>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>wc-column</xsl:text>
							<xsl:choose>
								<xsl:when test="not($align) or $align eq ''">
									<xsl:text> wc-align-left</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(' wc-align-', $align)"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="$width and number($width) ne 0">
						<xsl:value-of select="concat(' wc_col_',$width)"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
</xsl:stylesheet>
