<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.menu.n.hasStickyOpen.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuRoleIsSelectable.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuTabIndexHelper.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.inlineError.xsl"/>
	<xsl:import href="wc.common.invalid.xsl"/>
	<xsl:import href="wc.common.hField.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		Transform for WMenu. Makes bar, tree and column menus.

		Child Elements
		* ui:submenu
		* ui:menuItem

		Menus may not be nested.
	-->
	<xsl:template match="ui:menu">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type" select="@type"/>
		<xsl:variable name="isError" select="key('errorKey',$id)"/>

		<xsl:variable name="isBarFlyout">
			<xsl:if test="$type='bar' or $type='flyout'">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>

		<xsl:element name="div">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:call-template name="ajaxTarget"/>

			<xsl:apply-templates select="ui:margin"/>
			<!--
				NOTES on class:
				We would like to be able to define all menu appearance and behaviour
				solely using role. That is not, however, possible without a lot of
				code duplication. This gets particularly heinous in the CSS since there
				is no sensible reuse mechanism. So we base some instrinsic stuff on
				the "menu" class and the important stuff on roles.
			-->
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:value-of select="concat(' ', @type)"/>
			</xsl:attribute>

			<!--
				attribute role
				ARIA specifies three menu roles: tree, menu and menubar. The difference is in the
				orientation of the menu and how this orientation is reflected in the key
				navigation announced to assistive technologies.
			-->
			<xsl:attribute name="role">
				<xsl:choose>
					<xsl:when test="@type='tree'">
						<xsl:text>tree</xsl:text>
					</xsl:when>
					<xsl:when test="$isBarFlyout=1">
						<xsl:text>menubar</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>menu</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@selectMode">
				<xsl:choose>
					<xsl:when test="@type='tree'">
						<xsl:attribute name="aria-multiselectable">
							<xsl:choose>
								<xsl:when test="@selectMode='multiple'">
									<xsl:copy-of select="$t"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>false</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="data-wc-selectmode">
							<xsl:value-of select="@selectMode"/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:if test="$isError">
				<xsl:call-template name="invalid"/>
			</xsl:if>
			<xsl:call-template name="hideElementIfHiddenSet"/>

			<xsl:apply-templates select="*"/>
			<xsl:call-template name="inlineError">
				<xsl:with-param name="errors" select="$isError"/>
			</xsl:call-template>
			<xsl:call-template name="hField"/>
		</xsl:element>
	</xsl:template>

	<!--
		DO NOT ALLOW NESTED MENUS!
	-->
	<xsl:template match="ui:menu[ancestor::ui:menu]"/>

</xsl:stylesheet>
