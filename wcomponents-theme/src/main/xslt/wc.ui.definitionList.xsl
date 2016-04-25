<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WDefinitionList. This is a pretty straightforwards implementation
		of a HTML definition list.

		The actual layout of the DT and DD descendants depends on the value of the type
		attribute. The default block layout is changed to inline-block if the type is
		flat or column.
	-->
	<xsl:template match="ui:definitionlist">
		<dl>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="live" select="'off'"/>
				<xsl:with-param name="isWrapper" select="1"/>
			</xsl:call-template>
			<xsl:call-template name="makeCommonClass">
				<xsl:with-param name="additional">
					<xsl:value-of select="@type"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:apply-templates select="ui:term"/>
		</dl>
	</xsl:template>
</xsl:stylesheet>
