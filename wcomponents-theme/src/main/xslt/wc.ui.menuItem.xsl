<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:import href="wc.ui.menu.n.hasStickyOpen.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuRoleIsSelectable.xsl"/>
	<xsl:import href="wc.ui.menu.n.menuTabIndexHelper.xsl"/>
	<xsl:import href="wc.common.disabledElement.xsl"/>
	<xsl:import href="wc.common.hide.xsl"/>
	<xsl:import href="wc.common.accessKey.xsl"/>
	<xsl:import href="wc.common.ajax.xsl"/>
	<xsl:import href="wc.common.n.className.xsl"/>
	<!--
		WMenuItem forms part of a single compound widget with the WMenu at its root.

		The transform for WMenuItem. In general this is pretty straightforwards. The
		menuItem is rendered as a single control.
	-->
	<xsl:template match="ui:menuItem">
		<xsl:variable name="myAncestorMenu" select="ancestor::ui:menu[1]"/>
		<xsl:variable name="myAncestorSubmenu" select="ancestor::ui:submenu[ancestor::ui:menu[1]=$myAncestorMenu or not($myAncestorMenu)][1]"/>
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="menuType" select="$myAncestorMenu/@type"/>
		<!-- this is a test for ui:menuItem in an ajax response without its context menu -->
		<xsl:variable name="noContextMenu">
			<xsl:if test="not($myAncestorMenu)">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="noContextSubMenu">
			<xsl:if test="$noContextMenu=1 and not($myAncestorSubmenu)">
				<xsl:number value="1"/>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="actionType">
			<xsl:choose>
				<xsl:when test="@url">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:when test="@submit">
					<xsl:number value="2"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="menuItemElement">
			<xsl:choose>
				<xsl:when test="$actionType=0">
					<xsl:text>div</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>button</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="isButton">
			<xsl:choose>
				<xsl:when test="$actionType=0">
					<xsl:number value="0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:element name="{$menuItemElement}">
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:call-template name="commonClassHelper"/>
				<xsl:if test="$actionType &gt; 0">
					<xsl:text> wc_btn_nada</xsl:text>
					<xsl:if test="@cancel">
						<xsl:text> wc_btn_cancel</xsl:text>
					</xsl:if>
					<xsl:if test="@unsavedChanges">
						<xsl:text> wc_unsaved</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="@toolTip">
				<xsl:attribute name="title">
					<xsl:value-of select="normalize-space(@toolTip)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$actionType=1">
					<xsl:attribute name="type">
						<xsl:text>button</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="${wc.ui.link.attrib.url.standin}">
						<xsl:value-of select="@url"/>
					</xsl:attribute>
					<xsl:if test="@targetWindow">
						<xsl:attribute name="${wc.ui.link.attrib.window}">
							<xsl:value-of select="@targetWindow"/>
						</xsl:attribute>
						<xsl:attribute name="aria-haspopup">
							<xsl:copy-of select="$t"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$actionType=2">
					<xsl:attribute name="type">
						<xsl:text>submit</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="name">
						<xsl:value-of select="$id"/>
					</xsl:attribute>
					<xsl:attribute name="value">
						<xsl:text>x</xsl:text>
					</xsl:attribute>
					<!--
						client validation:
						* cancel does not validate; else
						* if validation target is set then validate in that target; else
						* if the menuItem is an ajaxTrigger do not validate.
					-->
					<xsl:choose>
						<xsl:when test="@cancel">
							<xsl:attribute name="formnovalidate">
								<xsl:text>formnovalidate</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="@validates">
							<xsl:attribute name="${wc.ui.button.attribute.validates}">
								<xsl:value-of select="@validates"/>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="key('triggerKey',@id)">
							<xsl:attribute name="formnovalidate">
								<xsl:text>formnovalidate</xsl:text>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="@msg">
				<xsl:attribute name="${wc.ui.button.attrib.confirmMessage}">
					<xsl:value-of select="@msg"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="ajaxController"/>
			<xsl:call-template name="hideElementIfHiddenSet"/>
			<xsl:choose>
				<xsl:when test="$myAncestorMenu">
					<!--
						role and selection depend on 'selectableness', 'selectableness'
						depends on context. If we do not know our ancestor menu we cannot
						correctly determine if we are selectable
					-->
					<xsl:variable name="isSelectable">
						<xsl:call-template name="menuRoleIsSelectable">
							<xsl:with-param name="type" select="$menuType"/>
							<xsl:with-param name="myAncestorMenu" select="$myAncestorMenu"/>
							<xsl:with-param name="myAncestorSubmenu" select="$myAncestorSubmenu"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:attribute name="role">
						<xsl:choose>
							<xsl:when test="$menuType='tree'">
								<xsl:text>treeitem</xsl:text>
							</xsl:when>
							<xsl:when test="$isSelectable=1 and ($myAncestorSubmenu[not(@selectMode='single')] or (not($myAncestorSubmenu) and $myAncestorMenu[not(@selectMode='single')]))">
								<xsl:text>menuitemcheckbox</xsl:text>
							</xsl:when>
							<xsl:when test="$isSelectable=1">
								<xsl:text>menuitemradio</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>menuitem</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:if test="$isSelectable=1">
						<xsl:variable name="selectionAttribute">
							<xsl:choose>
								<xsl:when test="$menuType='tree'">
									<xsl:text>aria-selected</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>aria-checked</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:attribute name="{$selectionAttribute}">
							<xsl:choose>
								<xsl:when test="@selected">
									<xsl:copy-of select="$t"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>false</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:if>
					<xsl:variable name="tabindex">
						<xsl:call-template name="menuTabIndexHelper">
							<xsl:with-param name="menu" select="$myAncestorMenu"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:if test="$tabindex!=''">
						<xsl:attribute name="tabindex">
							<xsl:value-of select="$tabindex"/>
						</xsl:attribute>
					</xsl:if>

					<!-- this choice is just to negate the need for the complex ancestor lookup if the menuItem is itself disabled -->
					<xsl:choose>
						<xsl:when test="@disabled">
							<xsl:call-template name="disabledElement">
								<xsl:with-param name="isControl" select="$isButton"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<!--
								The menuItem will be disabled if it is a descendant of a submenu which is disabled,
								or if it is a descendant of a menu which is disabled.
							-->
							<xsl:variable name="disabledAncestor" select="ancestor::*[@disabled and
								(($noContextMenu=1 and self::ui:submenu) or
								($myAncestorMenu and (self::ui:menu[.=$myAncestorMenu] or self::ui:submenu[ancestor::ui:menu[1]=$myAncestorMenu])))]"/>
							<xsl:if test="$disabledAncestor">
								<xsl:call-template name="disabledElement">
									<xsl:with-param name="field" select="$disabledAncestor"/>
									<xsl:with-param name="isControl" select="$isButton"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
					<!--
					 If the menuItem is not in a submenu then the accesskey attribute will be set
					 if required and a balloon help tooltip element is created. If there is no context
					 menu then we have to assume we are in a submenu. This may cause access key
					 problems for direct AJAX targetting of WMenuItems which are at the top level of
					 a WMenu.
					-->
					<xsl:if test="@accessKey and not($myAncestorSubmenu)">
						<xsl:call-template name="accessKey"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<!-- no menu context -->
					<xsl:attribute name="role">
						<xsl:text>${wc.ui.menu.dummyRole}</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="tabindex">
						<xsl:text>-1</xsl:text>
					</xsl:attribute>
					<xsl:if test="@accessKey">
						<xsl:attribute name="accesskey">
							<xsl:value-of select="@accessKey"/>
						</xsl:attribute>
					</xsl:if>
					<!-- no context menu, take the disabled state only from this elements @disabled attribute -->
					<xsl:call-template name="disabledElement">
						<xsl:with-param name="isControl" select="$isButton"/>
					</xsl:call-template>
					<!--
					 Attributes used by AJAX subscribers for menuItems without a menu context

					 These are used and consumed in JavaScript before the transformed elements are
					 injected into the DOM.
					-->
					<xsl:if test="@selected">
						<xsl:attribute name="data-wc-selected">
							<xsl:value-of select="@selected"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@selectable">
						<xsl:attribute name="data-wc-selectable">
							<xsl:value-of select="@selectable"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="ui:decoratedLabel">
				<xsl:with-param name="output">
					<xsl:choose>
						<xsl:when test="$isButton=1">
							<xsl:text>span</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>div</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
