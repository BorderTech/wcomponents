<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" 
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<!--
		Common template used to build WMenuItemGroup (wc.ui.menuGroup.xsl) and separators in menus (wc.ui.separator.xsl).
	-->
	<xsl:template name="separator">
		<xsl:element name="hr"><!-- remember IE! -->
			<xsl:attribute name="role">
				<xsl:text>separator</xsl:text>
			</xsl:attribute>
			<xsl:call-template name="makeCommonClass"/>
			<!--
				If a separator has a direction/orientation other than horizontal it must have the aria-orientation attribute set to "vertical".
				This will only happen in the top level of a bar or flyout menu.
			-->
			<xsl:variable name="sepV" select="'vertical'"/>
			<xsl:variable name="orientation">
				<xsl:if test="parent::ui:menu or parent::ui:menugroup[parent::ui:menu]">
					<xsl:variable name="menuType" select="ancestor::ui:menu[1]/@type"/>
					<xsl:if test="$menuType eq 'bar' or $menuType eq 'flyout'">
						<xsl:value-of select="$sepV"/>
					</xsl:if>
				</xsl:if>
			</xsl:variable>
			<xsl:if test="$orientation eq $sepV">
				<xsl:attribute name="aria-orientation">
					<xsl:value-of select="$sepV"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
