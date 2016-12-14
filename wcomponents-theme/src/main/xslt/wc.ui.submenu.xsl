<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0">
	<xsl:import href="wc.common.attributes.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuTabIndexHelper.xsl"/>
	<!-- Transform for WSubMenu. -->
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
					<!--allow AJAX sub menus to be open when they arrive -->
					<xsl:number value="1"/>
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
		<div id="{$id}" role="presentation"><!-- the presentation role is redundant but stops AXS from whining. -->
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:call-template name="makeCommonClass"/>
			<xsl:if test="@selectMode">
				<xsl:attribute name="data-wc-selectmode">
					<xsl:value-of select="@selectMode"/>
				</xsl:attribute>
			</xsl:if>
			<!--
				Determination of disabled state

				A WSubMenu can be disabled itself, it can be disabled by being a descendant of a disabled WSubMenu or it can be disabled by being the
				descendant of a disabled 	WMenu.

				A simple ancestor lookup is insufficient because a WSubMenu will not be disabled if it is a descendant of any disabled component (such
				as a disabled table row).

				Having to test for disabled before calling the disabled helper is cumbersome but unfortunately necessary.

				NOTE: this is outside of the $myAncestor test because we need to reuse it. We still have to re-check the disabled state after ajax.
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
						<!-- 
							Only set an accesskey if we are in the top level of a menu. If we have no context menu we are obviously not in the top 
							level
						-->
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

	<!-- This is the transform of the content of a submenu. -->
	<xsl:template match="ui:content" mode="submenu">
		<xsl:param name="open" select="0"/>
		<xsl:param name="type"/>
		<xsl:variable name="mode" select="../@mode"/>
		<xsl:variable name="isAjaxMode">
			<xsl:choose>
				<xsl:when test="$mode eq 'dynamic' or $mode eq 'eager' or ($mode eq 'lazy' and number($open) ne 1)">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="submenuId">
			<xsl:value-of select="../@id"/>
		</xsl:variable>
		<div id="{@id}" arial-labelledby="{concat($submenuId, '_o')}" role="menu">
			<xsl:attribute name="class">
				<xsl:text>wc_submenucontent</xsl:text>
				<xsl:if test="number($isAjaxMode) eq 1">
					<xsl:text> wc_magic</xsl:text>
					<xsl:if test="$mode eq 'dynamic'">
						<xsl:text> wc_dynamic</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="number($isAjaxMode) eq 1">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="$submenuId"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-expanded">
				<xsl:choose>
					<xsl:when test="number($open) eq 1">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="not(*)">
				<!-- make the sub menu busy.
					Why?
					We have to keep the menu role on the content wrapper to make the menu function but role menu must have at least one descendant
					role menuitem[(?:radio)|(?:checkbox)]? _or_ be aria-busy.
				-->
				<xsl:attribute name="aria-busy">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<!-- This is a manual close button which is not shown unless the menu is on a touch device. -->
			<button id="{generate-id()}" class="wc-menuitem wc_closesubmenu wc-nobutton wc-icon wc-invite" role="menuitem" type="button">
				<xsl:apply-templates select="../ui:decoratedlabel">
					<xsl:with-param name="useId" select="0"/>
				</xsl:apply-templates>
			</button>
			<xsl:apply-templates select="*"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
