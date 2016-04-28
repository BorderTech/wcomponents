<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		WRow is used to make rows (yes, really) and it contains ui:column (guess what that makes?).
	-->
	<xsl:template match="ui:row">
		<xsl:variable name="hgap">
			<xsl:call-template name="getHVGapClass"/>
		</xsl:variable>
		<div id="{@id}">
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional" select="$hgap"/>
			</xsl:call-template>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:apply-templates select="ui:column"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
