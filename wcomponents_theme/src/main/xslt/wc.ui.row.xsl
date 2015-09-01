<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.debug.common.bestPracticeHelpers.xsl"/>
	<xsl:output method="html" doctype-public="XSLT-compat" encoding="UTF-8" indent="no" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<!--
		WRow is used to make rows (yep, really) and it contains ui:column.
	-->
	<xsl:template match="ui:row">
		<div id="{@id}" class="{local-name(.)} wc_row">
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:if test="$isDebug=1">
				<xsl:call-template name="thisIsNotAllowedHere-debug">
					<xsl:with-param name="testForPhraseOnly" select="1"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:apply-templates select="ui:column">
				<xsl:with-param name="hgap">
					<xsl:call-template name="getHVGap">
						<xsl:with-param name="divisor" select="2"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:apply-templates>
		</div>
	</xsl:template>
</xsl:stylesheet>