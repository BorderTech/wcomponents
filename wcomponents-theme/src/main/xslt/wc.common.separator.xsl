<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.common.separator.n.separatorOrientation.xsl"/>
	<!--
		Common template used to build WMenuItemGroup (wc.ui.menuGroup.xsl) and
		separators in menus (wc.ui.separator.xsl).
	-->
	<xsl:template name="separator">
		<xsl:element name="hr"><!-- remember IE! -->
			<xsl:attribute name="role">
				<xsl:text>separator</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:value-of select="local-name(.)"/>
				<xsl:if test="@class">
					<xsl:value-of select="concat(' ', @class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:call-template name="separatorOrientation"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
