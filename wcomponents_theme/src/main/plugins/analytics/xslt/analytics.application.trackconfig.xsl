<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/dibp/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		This XSLT does the module config for analytics.
		See wc/ui/root.n.makeRequireConfig.xsl.
	-->
	<xsl:template match="ui:application" mode="analyticsconfig">
		<xsl:text>
requirejs.config({
	paths: {"${analytics.core.path.name}":"${analytics.core.path.path}","${analytics.core.shim.file}":"${analytics.core.shim.path}"},
	shim: {
			"${analytics.core.shim.file}":{
				exports:"${analytics.core.shim.global}"
			}
		}</xsl:text>
		<xsl:apply-templates select="ui:analytic" mode="analyticsconfig"/>
		<xsl:text>});</xsl:text>
	</xsl:template>
</xsl:stylesheet>