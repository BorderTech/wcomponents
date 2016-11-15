<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributeSets.xsl"/>
	<xsl:import href="wc.ui.menu.n.hasStickyOpen.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuRoleIsSelectable.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuTabIndexHelper.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WMenu. Makes bar, tree and column menus.

		Child Elements
		* ui:submenu
		* ui:menuitem

		Menus may not be nested.
	-->
	<xsl:template match="ui:menu">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type" select="@type"/>
		<xsl:variable name="isError" select="key('errorKey',$id)"/>

		<xsl:variable name="isBarFlyout">
			<xsl:choose>
				<xsl:when test="$type eq 'bar' or $type eq 'flyout'">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<div>
			<xsl:call-template name="commonAttributes">
				<xsl:with-param name="class">
					<xsl:if test="number($isBarFlyout) eq 1">wc_menu_bar</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<!--
				attribute role
				ARIA specifies three menu roles: tree, menu and menubar. The difference is in the
				orientation of the menu and how this orientation is reflected in the key
				navigation announced to assistive technologies.
			-->
			<xsl:attribute name="role">
				<xsl:choose>
					<xsl:when test="number($isBarFlyout) eq 1">
						<xsl:text>menubar</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>menu</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			
			<xsl:if test="@selectMode">
				<xsl:attribute name="data-wc-selectmode">
					<xsl:value-of select="@selectMode"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="$isError">
				<xsl:call-template name="invalid"/>
			</xsl:if>

			<xsl:apply-templates select="*[not(self::ui:margin)]"/>

			<xsl:call-template name="inlineError">
				<xsl:with-param name="errors" select="$isError"/>
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--
		DO NOT ALLOW NESTED MENUS!
	-->
	<xsl:template match="ui:menu[ancestor::ui:menu]"/>

</xsl:stylesheet>
