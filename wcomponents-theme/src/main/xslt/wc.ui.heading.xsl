<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
		xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WHeading. This is a fairly straightforwards 1:1 match with a HTML
		H# element where the WHeading @level sets the level
	-->
	<xsl:template match="ui:heading">
		<xsl:element name="{concat('h',@level)}">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
