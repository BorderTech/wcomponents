<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.ui.menu.n.hasStickyOpen.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuRoleIsSelectable.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuTabIndexHelper.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<xsl:import href="wc.common.title.xsl"/>
	<!--
		Transform for WSubMenu. The submenu opener element which is part of the
		submenu's parent element as well as a controller for and instrinsic part of the
		submenu itself. This leads to three separate artefacts:

		* the menu branch consisting of the submenu wrapper, the opener and the
		content wrapper. This is a menu item/tree item for the immediate ancestor
		WSubMenu/WMenu;
		* the branch opener element; and
		* the submenu content wrapper which is a menu or group for the the immediate
		ancestor WSubMenu/WMenu;
	-->
	<xsl:template match="ui:submenu">
		<xsl:variable name="myAncestorMenu" select="ancestor::ui:menu[1]"/>
		<xsl:variable name="myAncestorSubmenu" select="ancestor::ui:submenu[not(ancestor::ui:menu) or ancestor::ui:menu[1] eq $myAncestorMenu][1]"/>
		<xsl:variable name="id" select="@id"/>
		<!-- this is a test for ui:submenu in an ajax response without its context menu -->
		<xsl:variable name="noContextMenu">
			<xsl:choose>
				<xsl:when test="not($myAncestorMenu)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="type" select="$myAncestorMenu/@type"/>
		<xsl:variable name="stickyOpen">
			<xsl:choose>
				<xsl:when test="$myAncestorMenu">
					<xsl:call-template name="hasStickyOpen">
						<xsl:with-param name="type" select="$type"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="1"/>
					<!--allow AJAX sub menus to be open when they arrive -->
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="open">
			<xsl:choose>
				<xsl:when test="@open and number($stickyOpen) eq 1">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<div id="{$id}" role="presentation">
			<!--
				We try not to tie functionality or display to classes when we have suitable ARIA
				attributes but the need to differentiate functionality based on whether an
				element with role="menuitem/treeitem" into a branch or simple item needs something
				more than a simple role. This is absolutely required in the CSS where we cannot
				determine implementation based on child nodes and having a class here makes for
				far less verbose CSS with far fewer overrides.

				This <<may>> change so you should try not to rely on this class for too much and
				certainly avoid it for automated testing.
			-->
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:if test="@selectMode">
				<xsl:attribute name="data-wc-selectmode">
					<xsl:value-of select="@selectMode"/>
				</xsl:attribute>
			</xsl:if>
			<!--
				Determination of disabled state

				A WSubMenu can be disabled itself, it can be disabled by being a descendant of a
				disabled WSubMenu or it can be disabled by being the descendant of a disabled
				WMenu.

				A simple ancestor lookup is insufficient because a WSubMenu will not be disabled
				if it is a descendant of any disabled component (such as a disabled table row).

				Having to test for disabled before calling the disabled helper is cumbersome but
				unfortunately necessary.

				NOTE: this is outside of the $myAncestor test because we need to reuse it. We
				still have to re-check the disabled state after ajax.
			-->
			<xsl:variable name="this" select="."/>
			<xsl:variable name="disabledAncestor" select="ancestor-or-self::*[@disabled and
									(self::ui:submenu[. eq $this] or
									($myAncestorMenu and 
										(self::ui:menu[. eq $myAncestorMenu] or 
										self::ui:submenu[ancestor::ui:menu[1] eq $myAncestorMenu])) or
									(number($noContextMenu) eq 1 and self::ui:submenu))]"/>
			<xsl:if test="$disabledAncestor">
				<xsl:call-template name="disabledElement">
					<xsl:with-param name="field" select="$disabledAncestor"/>
				</xsl:call-template>
			</xsl:if>
			<!-- This is the submenu opener/label element. -->
			<button type="button" id="{concat($id, '_o')}" name="{$id}" class="wc-nobutton wc-invite wc-submenu-o" aria-controls="{$id}" aria-haspopup="true">
				<xsl:attribute name="aria-pressed">
					<xsl:choose>
						<xsl:when test="number($open) eq 1">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:call-template name="title"/>
				<!-- see above for how we determine disabled state: it is ugly -->
				<xsl:if test="$disabledAncestor">
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="field" select="$disabledAncestor"/>
						<xsl:with-param name="isControl" select="1"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$myAncestorMenu">
						<xsl:variable name="tabindex">
							<xsl:call-template name="menuTabIndexHelper">
								<xsl:with-param name="menu" select="$myAncestorMenu"/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:if test="$tabindex ne ''">
							<xsl:attribute name="tabindex">
								<xsl:value-of select="$tabindex"/>
							</xsl:attribute>
						</xsl:if>
						<!-- only set an accesskey if we are in the top level of a menu.
							If we have no context menu we are obviously not in the top level -->
						<xsl:if test="@accessKey and not($myAncestorSubmenu)">
							<xsl:call-template name="accessKey"/>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="tabindex">
							<xsl:text>-1</xsl:text>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="ui:decoratedlabel"/>
			</button>
			<xsl:apply-templates select="ui:content" mode="submenu">
				<xsl:with-param name="open" select="$open"/>
				<xsl:with-param name="type" select="$type"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>
</xsl:stylesheet>
