<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.debug.common.contentCategory.xsl"/>
	<xsl:import href="wc.ui.menu.n.hasStickyOpen.xsl"/>
	<!--
		Diagnostic information for Menu
	-->
	<xsl:template name="menu-debug">
		<xsl:param name="id"/>
		<xsl:param name="type"/>
		<xsl:call-template name="debugAttributes"/>
		<xsl:call-template name="thisIsNotAllowedHere-debug">
			<xsl:with-param name="testForPhraseOnly" select="1"/>
		</xsl:call-template>
		<xsl:variable name="hasStickyOpen">
			<xsl:call-template name="hasStickyOpen">
				<xsl:with-param name="type" select="$type"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$hasStickyOpen = 0">
			<xsl:variable name="modeServerSubs" select=".//ui:submenu[@mode='server' and ancestor::ui:menu[1]/@id=$id]"/>
			<!-- It is considered an error state for a menu which does not support sticky open to have server mode submenus as these can make the menu inoperable -->
			<xsl:if test="$modeServerSubs">
				<xsl:call-template name="makeDebugAttrib-debug">
					<xsl:with-param name="name" select="'data-wc-debugerr'"/>
					<xsl:with-param name="text">
						<xsl:text>WMenu of type </xsl:text>
						<xsl:value-of select="$type"/>
						<xsl:text> must not contain WSubMenus with Mode.SERVER but found [</xsl:text>
						<xsl:apply-templates select="$modeServerSubs" mode="listById"/>
						<xsl:text>]</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>