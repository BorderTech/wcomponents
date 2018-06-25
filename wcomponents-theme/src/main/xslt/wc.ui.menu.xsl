
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0"
	xmlns:html="http://www.w3.org/1999/xhtml" version="2.0"
	exclude-result-prefixes="xsl ui html">
	<!-- Transform for WMenu. Menus may not be nested. -->
	<xsl:template match="ui:menu">
		<xsl:variable name="id" select="@id"/>
		<xsl:variable name="type" select="@type"/>
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
		<xsl:variable name="additional">
			<xsl:value-of select="@class"/>
			<xsl:apply-templates select="ui:margin" mode="asclass"/>
			<xsl:if test="number($isBarFlyout) eq 1">
				<xsl:text> wc_menu_bar</xsl:text>
			</xsl:if>
		</xsl:variable>
		<div id="{@id}" class="{normalize-space(concat('wc-menu wc-menu-type-', @type, ' ', $additional))}">
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
			<xsl:if test="@disabled">
				<xsl:attribute name="aria-disabled">
					<xsl:text>true</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="ui:separator">
		<xsl:element name="hr">
			<xsl:attribute name="role">
				<xsl:text>separator</xsl:text>
			</xsl:attribute>
			<xsl:if test="@class">
				<xsl:attribute name="class">
					<xsl:value-of select="@class"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<!--
		Transform for WMenuItemGroup. It is strongly recommended that you do not use this component but use WSubMenu or WSeparator instead.

		The rationale for this transform is from the WAI-ARIA authoing practices for a Menu Widget: http://www.w3.org/TR/wai-aria-practices/#menu:

		"Grouping of menuitems in a menu or menubar is performed by introducing an element with the role of separator. A separator delineates groups
		of menu items within a menu or menubar. A separator is not placed in the navigation order and it is in no way interactive. Authors provide an
		aria-orientation consistent with the separator's orientation in the menu or menubar. Since the separator is not navigable it does not support
		the aria-expanded state. It is recommended that when grouping menu items of type menuitemradio that they all be grouped together preceding or
		following a separator."
	-->
	<xsl:template match="ui:menugroup">
		<xsl:if test="not(preceding-sibling::ui:menugroup or preceding-sibling::ui:separator)">
			<hr role="separator"/>
		</xsl:if>
		<xsl:apply-templates select="*[not(self::ui:decoratedlabel)]"/><!-- The WDecoratedLabel is purposely ignored -->
		<xsl:if test="following-sibling::* and not(following-sibling::ui:separator)">
			<hr role="separator"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name='submenuIcon'>
		<xsl:variable name="class">
			<xsl:choose>
				<xsl:when test="@open = 'false'">fa-caret-right</xsl:when> <!-- only tree menus have the @open attribute -->
				<xsl:when test="@open">fa-caret-down</xsl:when>
				<xsl:when test="@nested or @type='column'">fa-caret-right</xsl:when>
				<xsl:otherwise>fa-caret-down</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<i aria-hidden="true" class="fa {$class}"></i>
	</xsl:template>

	<!-- Transform for WSubMenu. -->
	<xsl:template match="ui:submenu">
		<div class="wc-submenu wc-submenu-type-{@type}" id="{@id}" role="presentation">
			<!-- the presentation role is redundant but stops AXS from whining. -->
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@disabled">
				<xsl:attribute name="aria-disabled">true</xsl:attribute>
			</xsl:if>
			<!-- This is the submenu opener/label element. -->
			<xsl:variable name="isTree">
				<xsl:choose>
					<xsl:when test="@type = 'tree'">
						<xsl:number value="1"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:number value="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<button aria-controls="{@id}" class="wc-nobutton wc-invite wc-submenu-o" id="{concat(@id, '_o')}" name="{@id}" type="button">
				<xsl:attribute name="aria-pressed">
					<xsl:choose>
						<xsl:when test="@open = 'true'">true</xsl:when>
						<xsl:otherwise>false</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="number($isTree) eq 1">
					<xsl:attribute name="aria-haspopup">true</xsl:attribute>
				</xsl:if>
				<xsl:if test="@toolTip">
					<xsl:attribute name="title">
						<xsl:value-of select="@toolTip"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="@accessKey">
					<xsl:attribute name="accesskey">
						<xsl:value-of select="@accessKey"/>
					</xsl:attribute>
					<xsl:attribute name="aria-describedby">
						<xsl:value-of select="concat(@id, '_wctt')"/>
					</xsl:attribute>
					<span hidden="hidden" id="{concat(@id,'_wctt')}" role="tooltip">
						<xsl:value-of select="@accessKey"/>
					</span>
				</xsl:if>
				<xsl:if test="number($isTree) eq 1">
					<xsl:call-template name="submenuIcon"/>
				</xsl:if>
				<xsl:apply-templates select="ui:decoratedlabel"/>
				<xsl:if test="number($isTree) eq 0">
					<xsl:call-template name="submenuIcon"/>
				</xsl:if>
			</button>
			<xsl:apply-templates mode="submenu" select="ui:content"/>
		</div>
	</xsl:template>

	<!-- This is the transform of the content of a submenu. -->
	<xsl:template match="ui:content" mode="submenu">
		<xsl:variable name="mode" select="../@mode"/>
		<xsl:variable name="isAjaxMode">
			<xsl:choose>
				<xsl:when test="$mode eq 'dynamic' or $mode eq 'eager' or ($mode eq 'lazy' and not(../@open = 'true'))">
					<xsl:number value="1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="additional">
			<xsl:if test="number($isAjaxMode) eq 1">
				<xsl:text> wc_magic</xsl:text>
				<xsl:if test="$mode eq 'dynamic'">
					<xsl:text> wc_dynamic</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<div id="{@id}" arial-labelledby="{concat(../@id, '_o')}" role="menu" class="{normalize-space(concat('wc-content wc_submenucontent', $additional))}">
			<xsl:if test="number($isAjaxMode) eq 1">
				<xsl:attribute name="data-wc-ajaxalias">
					<xsl:value-of select="../@id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="aria-expanded">
				<xsl:choose>
					<xsl:when test="../@open = 'true'">
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
			<xsl:apply-templates select="*"/>
		</div>
	</xsl:template>

	<!--
		WMenuItem forms part of a single compound widget with the WMenu at its root.

		The transform for WMenuItem. In general this is pretty straightforwards. The menuItem is rendered as a single control.
	-->
	<xsl:template match="ui:menuitem">
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
		<xsl:variable name="role">
			<xsl:choose>
				<xsl:when test="@role">
					<xsl:value-of select="@role"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>menuitem</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- leave tabindex on this button, it is used as a short-hand to find fousable controls in the core menu JavaScript. -->
		<xsl:variable name="additional">
			<xsl:if test="number($actionType) gt 0">
				<xsl:if test="@cancel">
					<xsl:text> wc_btn_cancel</xsl:text>
				</xsl:if>
				<xsl:if test="@unsavedChanges">
					<xsl:text> wc_unsaved</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<button role="{$role}" tabindex="0" id="{@id}" class="{normalize-space(concat('wc-menuitem wc-invite wc-nobutton', $additional))}">
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="number($actionType) eq 2">
						<xsl:text>submit</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>button</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="@disabled">
				<xsl:attribute name="disabled">
					<xsl:text>disabled</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@hidden">
				<xsl:attribute name="hidden">
					<xsl:text>hidden</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@toolTip"><xsl:attribute name="title"><xsl:value-of select="@toolTip"/></xsl:attribute></xsl:if>
			<xsl:choose>
				<xsl:when test="number($actionType) eq 1">
					<xsl:attribute name="data-wc-url">
						<xsl:value-of select="@url"/>
					</xsl:attribute>
					<xsl:if test="@targetWindow">
						<xsl:attribute name="data-wc-window">
							<xsl:value-of select="@targetWindow"/>
						</xsl:attribute>
						<xsl:attribute name="aria-haspopup">
							<xsl:text>true</xsl:text>
						</xsl:attribute>
					</xsl:if>
				</xsl:when>
				<xsl:when test="number($actionType) eq 2">
					<xsl:attribute name="name">
						<xsl:value-of select="@id"/>
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
							<xsl:attribute name="data-wc-validate">
								<xsl:value-of select="@validates"/>
							</xsl:attribute>
						</xsl:when>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="@msg">
				<xsl:attribute name="data-wc-btnmsg">
					<xsl:value-of select="@msg"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$role ne 'menuitem'">
				<xsl:attribute name="aria-checked">
					<xsl:choose>
						<xsl:when test="@selected">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>false</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@accessKey">
				<xsl:attribute name="accesskey">
					<xsl:value-of select="@accessKey"/>
				</xsl:attribute>
				<xsl:attribute name="aria-describedby">
					<xsl:value-of select="concat(@id,'_wctt')"/>
				</xsl:attribute>
				<span id="{concat(@id,'_wctt')}" role="tooltip" hidden="hidden">
					<xsl:value-of select="@accessKey"/>
				</span>
			</xsl:if>
			<xsl:apply-templates select="ui:decoratedlabel"/>
		</button>
	</xsl:template>

	<!--
		This template is used to determine if a WMenuItem should be selectable based on its selectable property, or ancestry. The params are passed in
		because they are expensive to calculate.
	-->
	<xsl:template name="menuRoleIsSelectable">
		<xsl:param name="type"/>
		<xsl:param name="myAncestorMenu"/>
		<xsl:param name="myAncestorSubmenu"/>
		<xsl:choose>
			<xsl:when test="@selectable eq 'false'">
				<xsl:number value="0"/>
			</xsl:when>
			<xsl:when test="@selectable">
				<xsl:number value="1"/>
			</xsl:when>
			<!--
				If we do not have a context menu at all then let the ajax subscriber javascript worry about selection mode based on the transient
				attribute set from @selectable
			-->
			<xsl:when test="not($myAncestorMenu or $myAncestorSubmenu)">
				<xsl:number value="0"/>
			</xsl:when>
			<!-- from here down we know we have an ancestor menu -->
			<xsl:when test="$myAncestorSubmenu/@selectMode">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:when test="$myAncestorSubmenu">
				<xsl:number value="0"/>
			</xsl:when>
			<xsl:when test="$myAncestorMenu/@selectMode">
				<xsl:number value="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:number value="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
