<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		WFieldLayout is intended for all layout of fields.

		Child elements
		* ui:field
	-->
	<xsl:template match="ui:fieldlayout">
		<div role="presentation">
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="isWrapper" select="1"/>
				<xsl:with-param name="class">
					<xsl:value-of select="concat('wc_fld_', @layout)"/>
					<xsl:if test="@labelWidth">
						<xsl:value-of select="concat(' wc_fld_lblwth_',@labelWidth)"/>
					</xsl:if>
					<xsl:if test="@ordered">
						<xsl:text> wc_ordered</xsl:text>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:variable name="style">
				<xsl:if test="@ordered and @ordered != 1">
					<xsl:value-of select="concat('counter-reset: wcfld ', @ordered - 1, ';')"/>
				</xsl:if>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="ui:margin">
					<xsl:apply-templates select="ui:margin">
						<xsl:with-param name="style" select="$style"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="$style != ''">
					<xsl:attribute name="style">
						<xsl:value-of select="$style"/>
					</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:apply-templates select="ui:field"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
