define(["wc/ui/menu/core",
	"wc/array/toArray",
	"wc/dom/event" ,
	"wc/dom/keyWalker",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/dom/initialise",
	"wc/dom/uid",
	"wc/i18n/i18n",
	"wc/dom/classList",
	"wc/timers",
	"wc/ui/ajax/processResponse",
	"wc/loader/resource",
	"lib/handlebars/handlebars",
	"wc/ui/viewportUtils",
	"wc/ui/menu/menuItem"],
	function(abstractMenu, toArray, event, keyWalker, shed, Widget, initialise, uid, i18n, classList, timers, processResponse, loader, handlebars, viewportUtils) {
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
			var BANNER = BANNER || new Widget("header", "", {role: "banner"}),
				HAMBURGER,
				MENU_FIXED = "wc_menu_fix",
				resizeTimer,
				BURGER_MENU_CLASS = "wc_menu_hbgr",
				submenuTemplate,
				closeButtonTemplate,
				DECORATED_LABEL,
				RESPONSIVE_MENU;

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
				var target,
					direction = isLast ? keyWalker.MOVE_TO.NEXT : keyWalker.MOVE_TO.PREVIOUS;

				/* get the element which would be focussed if we were to use findFn without
				 * allowing cycling and forcing depthFirstNavigation false. If we don't get anything then
				 * the element passed in is the first/last*/
				if ((target = instance._getTargetItem(element, direction, root, false))) {
					return element === target;  // if the target is the same as target then element is first &/or last
				}
				return true;
			}

//
//			function openTopLevelSibling(element, next) {
//				var branch = element,
//					result, target;
//
//				if (instance._isBranch(branch)) {
//					result =  branch;
//					branch = branch.parentNode;
//				}
//
//				while (branch) {
//					if ((branch = instance._getBranch(branch))) {
//						result =  branch;
//						branch = branch.parentNode;
//					}
//				}
//
//				if (result) {
//					target = instance._getTargetItem(result, (next ? keyWalker.MOVE_TO.PREVIOUS : keyWalker.MOVE_TO.NEXT), instance.getRoot(result), true);
//				}
//
//				if(target) {
//					instance[instance._FUNC_MAP.ACTION](target);
//				}
//			}
//
//			this.openPreviousTopLevelSibling = function(element) {
//				openTopLevelSibling(element);
//			};
//
//			this.openNextTopLevelSibling = function (element) {
//				openTopLevelSibling(element, true);
//			};

			/**
			 * Reset the key map according to the currently focused item. In the top level the  left and right go to
			 * siblings and down goes to children (if any) in sub-menus up and down go to siblings, right to child (if
			 * any) and left to parent.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} item The item which has focus.
			 */
			this._remapKeys = function(item) {
				var submenu,
					_item = item,
					VK_UP = "DOM_VK_UP",
					VK_DOWN = "DOM_VK_DOWN",
					VK_RIGHT = "DOM_VK_RIGHT",
					VK_LEFT = "DOM_VK_LEFT",
					branch, grandparent,
					root = this.getRoot(_item);

				if (!root) {
					return;
				}
				if ((submenu = this.getSubMenu(_item))) {
					if ((branch = this._getBranch(submenu)) && (grandparent = this.getSubMenu(branch))) {
						// more than one level deep.
						/* If a submenu left closes the current branch and right will
						 * trigger the action if the item is a branch or opener but
						 * otherwise do nothing. */
						this._keyMap[VK_LEFT] = this._FUNC_MAP.CLOSE_MY_BRANCH;
						this._keyMap[VK_UP] = keyWalker.MOVE_TO.PREVIOUS;
						this._keyMap[VK_DOWN] = keyWalker.MOVE_TO.NEXT;
					}
					else {
						// this._keyMap[VK_LEFT] = "openPreviousTopLevelSibling";
						this._keyMap[VK_LEFT] = null;
					}

					if (this._isBranchOrOpener(_item)) {
						this._keyMap[VK_RIGHT] = this._FUNC_MAP.ACTION;
					}
					else {
						// this._keyMap[VK_RIGHT] = "openNextTopLevelSibling";
						this._keyMap[VK_RIGHT] = null;
					}
					/* Up and down is  a bit more convoluted.
					 * We need to know if we are in a sub menu immediately below the root
					 * menu. We can do this by getting the current branch and then
					 * testing if *it* has an ancestor branch, if it does we are nested.
					 */
					if (!grandparent) {
						// we are in a submenu under MENU
						if (this._isBranchOrOpener(_item)) {
							_item = this._getBranch(_item);
						}
						if (_item) {
							if (isFirstLastItem(_item, root)) {
								// If I am the first submenu item UP should go to the opener
								this._keyMap[VK_UP] = keyWalker.MOVE_TO.PARENT;
							}
							else {
								this._keyMap[VK_UP] = keyWalker.MOVE_TO.PREVIOUS;
							}
							// this is no an else if because an item can be both first and last
							if (isFirstLastItem(_item, root, true)) {
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
					if (this._isOpener(_item)) {
						_item = this._getBranch(_item);
					}
					if (this._isBranch(_item) && (_item = this._getBranchExpandableElement(_item))) {
						if (shed.isExpanded(_item)) {
							this._keyMap[VK_UP] = keyWalker.MOVE_TO.LAST_CHILD; // "lastChildItem";
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

			function removeIconified(nextMenu) {
				var burger,
					submenuContent,
					current;
				if (!classList.contains(nextMenu, MENU_FIXED)) {
					return;
				}

				try {
					HAMBURGER = HAMBURGER || new Widget("div", BURGER_MENU_CLASS);
					if (!(burger = HAMBURGER.findDescendant(nextMenu))) {
						return;
					}

					submenuContent = instance._wd.submenu.findDescendant(burger);
					if (!submenuContent) {
						return;
					}
					while ((current = submenuContent.firstChild)) {
						if (classList.contains(current, "wc_closesubmenu")) {
							submenuContent.removeChild(current);
						}
						else {
							nextMenu.appendChild(current);
						}
					}
					burger.parentNode.removeChild(burger);

				}
				finally {
					classList.remove(nextMenu, MENU_FIXED);
				}
			}

			function attachSubMenuCloseButton(el) {
				var branch,
					opener,
					label,
					props = {},
					closeButton;
				if (el && instance.isSubMenu(el)) {
					closeButtonTemplate = closeButtonTemplate || getTemplate("submenuCloseButton.mustache");
					if ((branch = instance._getBranch(el)) && (opener = instance._getBranchOpener(branch))) {
						DECORATED_LABEL = DECORATED_LABEL || new Widget("", "wc-decoratedlabel");
						label = DECORATED_LABEL.findDescendant(opener);

						if (label) {
							Array.prototype.forEach.call(label.childNodes, function(child) {
								if (child.nodeType !== Node.ELEMENT_NODE) {
									return;
								}
								if (classList.contains(child, "wc-labelhead")) {
									props.labelhead = child.innerHTML;
								}
								else if (classList.contains(child, "wc-labeltail")) {
									props.labeltail = child.innerHTML;
								}
								else {
									props.content = child.innerHTML;
								}
							});
						}
					}
					if (!props.content) {
						props.content = i18n.get("menu_close_label");
					}
					el.insertAdjacentHTML("afterBegin", closeButtonTemplate(props));
					closeButton = el.firstChild;
					if (!closeButton.id) {
						closeButton.id = uid();
					}
				}
			}

			function getTemplate(name) {
				var template = loader.load(name, true);
				return handlebars.compile(template);
			}

			/**
			 * Array iterator function for {@link module:wc/ui/menu/bar~toggleIconMenus} which processes each
			 * menu found and manipulates it for improved display and usability on mobile devices. Each sub-menu has a
			 * close button added to the top and when the BAR menu is in the HEADER panel (role "banner") we collapse
			 * the entire menu into a sub-menu and add a launcher to where the top-level items used to be.
			 *
			 * @function
			 * @private
			 * @param {Element} nextMenu The menu to be processed.
			 */
			function makeIconified(nextMenu) {
				var branchElement,
					props;

				if (classList.contains(nextMenu, MENU_FIXED)) {
					return;
				}

				props = {
					id: uid(),
					class: " " + BURGER_MENU_CLASS,
					opener: {
						class: " wc_hbgr wc-icon",
						tooltip: i18n.get("menu_open_label")
					},
					contentId: uid(),
					open: false,
					closeText: i18n.get("menu_close_label"),
					items: nextMenu.innerHTML
				};
				submenuTemplate = submenuTemplate || getTemplate("submenu.mustache");
				branchElement = submenuTemplate(props);
				nextMenu.innerHTML = branchElement;
				classList.add(nextMenu, MENU_FIXED);
			}

			function resizeEvent(/* $event */) {
				if (resizeTimer) {
					timers.clearTimeout(resizeTimer);
				}
				resizeTimer = timers.setTimeout(toggleIconMenus, 100);
			}

			/**
			 * Determine if the iconification of any menus has to be toggled.
			 *
			 * @function
			 * @private
			 * @param {Element} el The element which may be a menu, submenu or something containing a menu.
			 */
			function toggleIconMenus(el) {
				var candidates, element = el || document.body;
				if (instance.isSubMenu(element)) {
					return;
				}
				RESPONSIVE_MENU = RESPONSIVE_MENU || instance.ROOT.extend(["wc-respond"]);

				if (RESPONSIVE_MENU.isOneOfMe(element)) {
					candidates = [element];
				}
				else {
					candidates = toArray(RESPONSIVE_MENU.findDescendants(element));
				}

				if (!candidates.length) {
					return;
				}

				candidates = candidates.filter(function(next) {
					return next.childNodes.length > 1;
				});

				if (candidates.length) {
					if (viewportUtils.isPhoneLike()) {
						candidates.forEach(makeIconified);
					}
					else {
						candidates.forEach(removeIconified);
					}
				}
			};

			this.initialise = function(element) {
				this.constructor.prototype.initialise.call(this, element);
				toggleIconMenus(element);
				processResponse.subscribe(attachSubMenuCloseButton, true);
				processResponse.subscribe(toggleIconMenus, true);
				event.add(window, event.TYPE.resize, resizeEvent, 1);
			};
		}

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
		 * @requires module:wc/array/toArray
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/keyWalker
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/uid
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/dom/classList
		 * @requires module:wc/timers
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/loader/resource
		 * @requires:module:lib/handlebars/handlebars
		 * @requires module:wc/ui/viewportUtils
		 */
		var instance;
		Menubar.prototype = abstractMenu;
		instance = new Menubar();
		instance.constructor = Menubar;
		initialise.register(instance);
		return instance;
	});
