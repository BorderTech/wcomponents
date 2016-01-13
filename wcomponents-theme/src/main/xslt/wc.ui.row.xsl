<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.getHVGap.xsl"/>
	<!--
		WRow is used to make rows (yep, really) and it contains ui:column.
	-->
	<xsl:template match="ui:row">
		<div id="{@id}">
			<xsl:attribute name="class">
				<xsl:text>row</xsl:text>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:call-template name="ajaxTarget"/>
			<xsl:apply-templates select="ui:margin"/>
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
