<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:template match="ui:css"/>

	<xsl:template match="ui:css" mode="inHead">
		<xsl:text>s.add("</xsl:text>
		<xsl:value-of select="@url"/>
		<xsl:text>");</xsl:text>
	</xsl:template>

	<xsl:template match="html:link[@rel='stylesheet']" mode="inHead">
		<xsl:text>s.add("</xsl:text>
		<xsl:value-of select="@href"/>
		<xsl:if test="@media">
			<xsl:text>","</xsl:text>
			<xsl:value-of select="@media"/>
		</xsl:if>
		<xsl:text>");</xsl:text>
	</xsl:template>

	<xsl:template match="html:link[@rel='stylesheet']">
		<xsl:if test="ancestor::ui:ajaxtarget">
			<script type="text/javascript">
				<xsl:text>require(["wc/loader/style"],function(s){s.add("</xsl:text>
				<xsl:value-of select="@href"/>
				<xsl:text>","</xsl:text>
				<xsl:if test="@media">
					<xsl:value-of select="@media"/>
				</xsl:if>
				<xsl:text>", true);});</xsl:text>
			</script>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
