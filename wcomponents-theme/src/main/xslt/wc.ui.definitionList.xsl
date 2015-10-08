<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.constants.xsl"/>
	<!--
		Transform for WDefinitionList. This is a pretty straightforwards implementation
		of a HTML definition list.

		The actual layout of the DT and DD descendants depends on the value of the type
		attribute. The default block layout is changed to inline-block if the type is
		flat or column.
	-->
	<xsl:template match="ui:definitionList">
		<xsl:element name="dl">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:if test="@type">
				<xsl:attribute name="class">
					<xsl:value-of select="@type"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="ui:margin"/>
			<xsl:call-template name="ajaxTarget">
				<xsl:with-param name="live" select="'off'"/>
			</xsl:call-template>
			<xsl:apply-templates select="ui:term"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
