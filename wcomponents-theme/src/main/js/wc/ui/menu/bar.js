/**
 * Menu controller extension for WMenu of type BAR and type FLYOUT. These are menus which are horizontal at the top
 * level and if they have submenus they are transient fly-out artifacts.
 *
 * @see {@link http://www.w3.org/TR/wai-aria-practices/#menu}
 *
 * @module
 * @extends module:wc/ui/menu/core
 *
 * @requires module:wc/ui/menu/core
 * @requires module:wc/dom/keyWalker
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/uid
 * @requires module:wc/ui/menu/menuItem
 */
define(["wc/ui/menu/core", "wc/dom/keyWalker", "wc/dom/shed", "wc/dom/Widget", "wc/dom/initialise", "wc/dom/uid", "wc/ui/menu/menuItem"],
	/** @param abstractMenu wc/ui/menu/core @param keyWalker wc/dom/keyWalker @param shed wc/dom/shed @param Widget wc/dom/Widget @param initialise wc/dom/initialise @param uid wc/dom/uid @ignore */
	function(abstractMenu, keyWalker, shed, Widget, initialise, uid) {
		"use strict";

		/* Unused dependencies:
		 * We will need "wc/ui/menu/menuItem" if we have any selectable items so we get it just in case rather than
		 * doing a convoluted XPath lookup in XSLT. */

		/**
		 * Extends menu functionality to provide a specific implementation of a menu bar.
		 * @constructor
		 * @alias module:wc/ui/menu/bar~Menubar
		 * @extends module:wc/ui/menu/core~AbstractMenu
		 * @private
		 */
		function Menubar() {
			var BANNER;

			/**
			 * The descriptors for this menu type.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @protected
			 * @override
			 */
			this._wd = {};

			/**
			 * The description of the ROOT node of a BAR or FLYOUT menu.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ROOT = new Widget("", "", {role: "menubar"});

			/**
			 * The role which is applied to the ROOT node of a BAR or FLYOUT menu.
			 * @var
			 * @type {String}
			 * @protected
			 * @override
			 **/
			this._role.MENU = "menubar";

			/**
			 * Determines if the current element/item is the first or last item in a menu/submenu.
			 *
			 * @function
			 * @private
			 * @param {Element} element The menu item/opener to test.
			 * @param {Element} root The root element of the current menu.
			 * @param {Boolean} [isLast] true if we want to know if the element is the last in the menu, otherwise we
			 *     want to know if it is first.
			 * @returns {Boolean} true if first/last item.
			 */
			function isFirstLastItem(element, root, isLast) {
				var result = false,
					target,
					direction = isLast ? keyWalker.MOVE_TO.NEXT : keyWalker.MOVE_TO.PREVIOUS;

				/* get the element which would be focussed if we were to use findFn without
				 * allowing cycling and forcing depthFirstNavigation false. If we don't get anything then
				 * the element passed in is the first/last*/
				if ((target = instance._getTargetItem(element, direction, root, false))) {
					result = element === target;  // if the target is the same as target then element is first &/or last
				}
				else {
					result = true;  // there are no other elements so it MUST be the first & last!
				}
				return result;
			}

			/**
			 * Reset the key map according to the currently focused item. In the top level the  left and right go to
			 * siblings and down goes to children (if any) in sub-menus up and down go to siblings, right to child (if
			 * any) and left to parent.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} item The item which has focus.
			 * @param {Element} root The root element of the current menu.
			 */
			this._remapKeys = function(item, root) {
				var submenu = this._getSubMenu(item),
					VK_UP = "DOM_VK_UP",
					VK_DOWN = "DOM_VK_DOWN",
					VK_RIGHT = "DOM_VK_RIGHT",
					VK_LEFT = "DOM_VK_LEFT";

				if (submenu) {
					/* If a submenu left closes the current branch and right will
					 * trigger the action if the item is a branch or opener but
					 * otherwise do nothing. */
					this._keyMap[VK_LEFT] = this._FUNC_MAP.CLOSE_MY_BRANCH;
					if (this._isBranchOrOpener(item)) {
						this._keyMap[VK_RIGHT] = this._FUNC_MAP.ACTION;
					}
					else {
						this._keyMap[VK_RIGHT] = null;
					}
					/* Up and down is  a bit more convoluted.
					 * We need to know if we are in a sub menu immediately below the root
					 * menu. We can do this by getting the current branch and then
					 * testing if *it* has an ancestor branch, if it does we are nested.
					 */
					if ((this._getSubMenu(submenu.parentNode))) {
						// we are nested submenu in submenu so mapping is simple
						this._keyMap[VK_UP] = keyWalker.MOVE_TO.PREVIOUS;
						this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.NEXT;
					}
					else {
						// we are in a submenu under MENU
						if (this._isBranchOrOpener(item)) {
							item = this._getBranch(item);
						}
						if (item) {
							if (isFirstLastItem(item, root)) {
								// If I am the first submenu item UP should go to the opener
								this._keyMap[VK_UP] = keyWalker.MOVE_TO.PARENT;
							}
							else {
								this._keyMap[VK_UP] = keyWalker.MOVE_TO.PREVIOUS;
							}
							// this is no an else if because an item can be both first and last
							if (isFirstLastItem(item, root, true)) {
								// If I am the last submenu item then DOWN should go to the opener
								this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.PARENT;
							}
							else {
								this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.NEXT;
							}
						}
					}
				}
				else {
					this._keyMap[VK_LEFT] = keyWalker.MOVE_TO.PREVIOUS;
					this._keyMap[VK_RIGHT] = keyWalker.MOVE_TO.NEXT;
					// if I am a branch/opener and the menu is open then I have to cycle through my children
					if (this._isOpener(item)) {
						item = this._getBranch(item);
					}
					if (this._isBranch(item)) {
						if (shed.isExpanded(this._getBranchExpandableElement(item))) {
							this._keyMap[VK_UP] = keyWalker.MOVE_TO.LAST_CHILD;  // "lastChildItem";
							this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.CHILD;
						}
						else {
							this._keyMap[VK_UP] = this._FUNC_MAP.ACTION;
							this._keyMap[VK_DOWN] = this._FUNC_MAP.ACTION;
						}
					}
				}
			};

			/**
			 * Set the initial key map for a bar menu.
			 *
			 * @see http://www.w3.org/TR/wai-aria-practices/#menu
			 * @function
			 * @protected
			 * @override
			 */
			this._setupKeymap = function() {
				this._keyMap = {
					DOM_VK_LEFT: keyWalker.MOVE_TO.PREVIOUS,
					DOM_VK_RIGHT: keyWalker.MOVE_TO.NEXT,
					DOM_VK_HOME: keyWalker.MOVE_TO.FIRST,
					DOM_VK_END: keyWalker.MOVE_TO.LAST,
					DOM_VK_ESCAPE: this._FUNC_MAP.ESCAPE,
					DOM_VK_UP: this._FUNC_MAP.ACTION,
					DOM_VK_DOWN: this._FUNC_MAP.ACTION
				};
			};

			/**
			 * Array iterator function for {@link module:wc/ui/menu/bar~updateMenusForMobile} which processes each
			 * menu found and manipulates it for improved display and usability on mobile devices. Each sub-menu has a
			 * close button added to the top and when the BAR menu is in the HEADER panel (role "banner") we collapse
			 * the entire menu into a sub-menu and add a launcher to where the top-level items used to be.
			 *
			 * @function
			 * @private
			 * @param {Element} nextMenu The menu or sub-menu to be processed.
			 */
			function processMenu(nextMenu) {
				var branchElement,
					button,
					submenuContentElement,
					contentId,
					menuItem,
					MENU_FIXED = "data-wc-menufixed",
					ROLE = "role",
					childCount;
				if (nextMenu.hasAttribute(MENU_FIXED)) {
					return;
				}

				nextMenu.setAttribute(MENU_FIXED, "true");
				BANNER = BANNER || new Widget("header", "", {role: "banner"});
				// only do this if the menu contains more than one child
				childCount = typeof nextMenu.children !== "undefined" ? nextMenu.children.length : (typeof nextMenu.childNodes !== "undefined" ? nextMenu.childNodes.length : 0);

				if (childCount > 1 && BANNER.findAncestor(nextMenu)) {
					branchElement = document.createElement("div");
					branchElement.setAttribute(ROLE, "menuitem");
					branchElement.className = "wc-submenu";
					branchElement.setAttribute("aria-expanded", "false");
					button = document.createElement("button");
					button.type = "button";
					button.setAttribute("aria-haspopup", "true");
					button.title = "open";
					contentId = uid();
					button.setAttribute("aria-controls", contentId);
					button.className = "wc_btn_nada";
					branchElement.appendChild(button);

					submenuContentElement = document.createElement("div");
					submenuContentElement.className = "wc_submenucontent";
					submenuContentElement.id = contentId;
					submenuContentElement.setAttribute(ROLE, "menu");
					branchElement.appendChild(submenuContentElement);

					while ((menuItem = nextMenu.firstChild)) {
						submenuContentElement.appendChild(menuItem);
					}
					nextMenu.appendChild(branchElement);
				}
				else {
					branchElement = nextMenu;
				}

				Array.prototype.forEach.call(instance._wd.submenu.findDescendants(branchElement), instance.fixSubMenuContent);
			}

			/**
			 * When a mobile device is used add a close button to the top of every submenu content as the submenus are
			 * shown near full screen and there is (usually) no ESCAPE key.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} element The element which may be a menu, submenu or something containing a menu.
			 */
			this.updateMenusForMobile = function (element) {
				var candidates = [];
				if (!this.isMobile) {
					return;
				}
				if (this._wd.submenu.isOneOfMe(element)) {
					if (this.ROOT.findAncestor(element)) {
						this.fixSubMenuContent(element);
					}
					return;
				}
				else if (this.ROOT.isOneOfMe(element)) {
					candidates = [element];
				}
				else {
					candidates = this.ROOT.findDescendants(element);
				}

				Array.prototype.forEach.call(candidates, processMenu);
			};
		}

		var /** @alias module:wc/ui/menu/bar */ instance;
		Menubar.prototype = abstractMenu;
		instance = new Menubar();
		instance.constructor = Menubar;
		initialise.register(instance);
		return instance;
	});
