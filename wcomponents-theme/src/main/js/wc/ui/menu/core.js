/**
 * A module representing an abstract menu without any specific implementation. An instance of this class will do
 * nothing.
 *
 * @module
 *
 * @requires module:wc/has
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/classList
 * @requires module:wc/dom/event
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/formUpdateManager
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/dom/keyWalker
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/textContent
 * @requires module:wc/dom/viewportCollision
 * @requires module:wc/dom/Widget
 * @requires module:wc/key
 * @requires module:wc/ui/ajax/processResponse
 * @requires module:wc/timers
 * @requires module:wc/i18n/i18n
 * @requires module:wc/dom/getBox
 * @requires module:wc/array/toArray
 *
 * @see {@link module:wc/ui/menu/bar}
 * @see {@link module:wc/ui/menu/column}
 * @see {@link module:wc/ui/menu/tree}
 */
define(["wc/has",
		"wc/dom/attribute",
		"wc/dom/classList",
		"wc/dom/event",
		"wc/dom/focus",
		"wc/dom/formUpdateManager",
		"wc/dom/getFilteredGroup",
		"wc/dom/keyWalker",
		"wc/dom/shed",
		"wc/dom/textContent",
		"wc/dom/viewportCollision",
		"wc/dom/Widget",
		"wc/key",
		"wc/ui/ajax/processResponse",
		"wc/timers",
		"wc/i18n/i18n",
		"wc/dom/getBox",
		"wc/array/toArray"
	],
	/** @param has wc/has @param attribute wc/dom/attribute @param classList wc/dom/classList @param event wc/dom/event @param focus wc/dom/focus @param formUpdateManager wc/dom/formUpdateManager @param getFilteredGroup wc/dom/getFilteredGroup @param keyWalker wc/dom/keyWalker @param shed wc/dom/shed @param textContent wc/dom/textContent @param viewportCollision wc/dom/viewportCollision @param Widget wc/dom/Widget @param key wc/key @param processResponse wc/ui/ajax/processResponse @param timers wc/timers @param i18n wc/i18n/i18n @param getBox wc/dom/getBox @param toArray wc/array/toArray @ignore */
	function(has, attribute, classList, event, focus, formUpdateManager, getFilteredGroup, keyWalker, shed, textContent, viewportCollision, Widget, key, processResponse, timers, i18n, getBox, toArray) {
		"use strict";

		/* NOTE: Many functions in this module are private but accept an instance of a subclass as an
		 * argument. These private functions are ones which either do not need to be overridden in a subclass because they are
		 * generic or, more specifically, which ought not be overridden because they do exactly that which they are supposed.
		 * This use of private functions with a subclass instance argument is merely a trope to work around the lack of protected
		 * and final as in a real language these would be protected final functions but we don't even have a JSDoc tag for final
		 * so we go private for safety. */

		var abstractMenu,
			BOOTSTRAPPED = "wc/ui/menu/bs",
			ROLE_ATTRIB = "role",
			AJAX_CONTEXTLESS_ITEM,
			postAjaxTimer,
			focusTimer,
			TRUE = "true",
			fixedWidgets,
			CLASS = {
				COLLIDE_EAST: "wc_coleast",
				COLLIDE_WEST: "wc_colwest",
				COLLIDE_SOUTH: "wc_colsth",
				// no collide north...
				DEFAULT_DIRECTION: i18n.get("${wc.ui.menu.i18n.defaultDirection}"),
				AGAINST_DEFAULT: i18n.get("${wc.ui.menu.i18n.otherDirection}")
			},
			/**
			 * This object is used to map functions to particular event conditions.
			 * @var {Object} FUNC_MAP
			 * @private
			 */
			FUNC_MAP = {
				OPEN: "_openBranch",
				CLOSE_MY_BRANCH: "_closeMyBranch",
				ACTION: "_actionItem",
				CLOSE: "_closeBranch",
				ESCAPE: "_escape"
			},
			LETTER,
			KEY_NAME_RE = /^DOM_VK_/,
			activateOnHover,  // used to track the currently open menu to determine whether hover effects are in place
			openMenu = null,  // used in the focusEvent handler to close a menu if it has lost focus;
			TRANSIENT_SELECTED_ATTRIB = "data-wc-selected",
			BUTTON = "button",
			CLOSE_BUTTON,
			MENU_ROLE = "menu",
			MENU_CLASS = "wc-menu",
			MENUITEM_ROLE = "menuitem",
			DEFAULT_CLOSE_LABEL,
			/**
			 * An array of keys which will cause us to call preventDefault in the keydown event handler if they are
			 * handled. This is here just so we do not have to rebuild this array every time a key is pressed!
			 * @var {int[]} keysToCancel
			 * @private
			 */
			keysToCancel = [
				KeyEvent.DOM_VK_SPACE,
				KeyEvent.DOM_VK_UP,
				KeyEvent.DOM_VK_DOWN,
				KeyEvent.DOM_VK_LEFT,
				KeyEvent.DOM_VK_RIGHT
			];

		/**
		 * Sets up {@link module:wc/dom/Widget} descriptors for parts of a menu which do not vary between subclasses.
		 * @function setupFixedWidgets
		 * @private
		 * @returns {Object} an object containing properties the values of which are Widgets.
		 */
		function setupFixedWidgets() {
			var branchWidget = new Widget("", "wc-submenu"),
				branchTriggerWidget = new Widget(BUTTON);
			branchTriggerWidget.descendFrom(branchWidget, true);
			return {
				TABSTOP: new Widget("", "", { "tabIndex": "0" }),  // used to get the current tabstop in any menu
				GENERIC_ROOT: new Widget("", MENU_CLASS),
				BRANCH_TRIGGER: branchTriggerWidget,
				BRANCH: branchWidget,
				OFFSCREEN: new Widget("", "wc_off")
			};
		}

		/**
		 * Sets up the subclass specific {@link module:wc/dom/Widget}s used to describe the various parts of the menu.
		 * @function setUpWidgets
		 * @private
		 * @param {Object} instance The sub-class currently being initialised.
		 */
		function setUpWidgets(instance) {
			var o,
				leaf = instance._role.LEAF;
			fixedWidgets = fixedWidgets || setupFixedWidgets();
			instance._wd.submenu = new Widget("", "wc_submenucontent", { "role": instance._role.SUBMENU });
			instance._wd.leaf = [];
			for (o in leaf) {
				instance._wd.leaf[instance._wd.leaf.length] = new Widget("", "", { "role": leaf[o] });
			}
		}

		/**
		 * Indicates if the current menu is transient. This is used rather than testing this._transient directly because
		 * it allows us to be sure we have the real _transient property of he menu being acted upon and not the
		 * _transient property of the class which just happens to be doing the test.
		 * @function isMenuTransient
		 * @private
		 * @param {Element} root The root node of the current menu.
		 * @param {Object} instance The subclass we are interested in.
		 * @returns {Boolean} true if the menu is one which is modelled by instance and that model menu is transient.
		 */
		function isMenuTransient(root, instance) {
			var result = false;
			if (instance.ROOT.isOneOfMe(root)) {
				result = instance._transient;
			}
			return result;
		}

		/**
		 * Get the menu leaf ancestor of a given element.
		 * @function getItem
		 * @private
		 * @param {Element} element The element we are testing.
		 * @param {Object} instance The subclass instance.
		 * @returns {?Element} The leaf Element which is or contains element or null if the lelemnt is not in a
		 *    menu item.
		 */
		function getItem(element, instance) {
			if (instance._getRoot(element)) {
				return Widget.findAncestor(element, instance._wd.leaf);
			}
			return null;
		}

		/**
		 * Sets the tabIndex of the current element and removes it from the previous 'tab-able' element (if different).
		 * @function setTabstop
		 * @private
		 * @param {Element} element A menu node.
		 * @param {Object} instance The subclass.
		 */
		function setTabstop(element, instance) {
			var oldTabstops,
				root,
				activeElement;
			if ((root = instance._getRoot(element))) {
				if (instance._isBranch(element) && (activeElement = instance._getBranchOpener(element))) {
					element = activeElement;
				}

				oldTabstops = instance.getFixedWidgets().TABSTOP.findDescendants(root);
				if (oldTabstops.length) {
					Array.prototype.forEach.call(oldTabstops, function(next) {
						if (element !== next) {
							next.tabIndex = "-1";
						}
					});
				}
				element.tabIndex = "0";
			}
		}

		/**
		 * This is a treewalker which is used to test an elements text node descendants. It is used as part of another
		 * treewalker filter. precondition: element has been tested as a potential element match, now we want to know if
		 * its first visible text node starts with a particular letter.
		 *
		 * @function hasTextNodeMatch
		 * @private
		 * @see {@link getNavigationTreeWalkerFilter}
		 * @param {Element} element The menu node being tested.
		 * @param {String} letter The letter on the key the user pressed.
		 * @param {Object} instance The subclass.
		 * @returns {Integer} A NodeFilter STATIC variable
		 */
		function hasTextNodeMatch(element, letter, instance) {
			var tw,
				node,
				textNodeContent,
				result = NodeFilter.FILTER_SKIP;

			function textNodeFilter(textNode) {
				var rval = NodeFilter.FILTER_SKIP,
					parent = textNode.parentNode;

				if (shed.hasHiddenAncestor(textNode, element) || shed.hasDisabledAncestor(textNode, element) || instance.getFixedWidgets().OFFSCREEN.findAncestor(parent)) {
					rval = NodeFilter.FILTER_REJECT;
				}
				else if (textNode.nodeValue) {
					rval = NodeFilter.FILTER_ACCEPT;
				}
				return rval;
			}

			LETTER = LETTER || new RegExp(i18n.get("${wc.common.i18n.letter}"));

			if (letter && LETTER.test(letter)) {
				tw = document.createTreeWalker(element, NodeFilter.SHOW_TEXT, textNodeFilter, false);
				tw.currentNode = element;
				tw.nextNode();
				node = tw.currentNode;

				if (node && node.nodeType === Node.TEXT_NODE) {
					textNodeContent = node.nodeValue;
					if (textNodeContent.toLocaleUpperCase().indexOf(letter.toLocaleUpperCase()) === 0) {
						result = NodeFilter.FILTER_ACCEPT;
					}
					else if (textNodeContent) {
						result = NodeFilter.FILTER_REJECT;
					}
				}
			}
			return result;
		}

		/**
		 * Indicates if a particular element is an item in a menu.
		 * @function isItem
		 * @private
		 * @param {Element} element The element to test.
		 * @param {Object} instance The subclass we are currently testing, required to make sure we only get positives
		 *    for the type of menu we are in.
		 * @returns {Boolean}
		 */
		function isItem(element, instance) {
			var role = element.getAttribute(ROLE_ATTRIB),
				o;
			if (!role) {
				return false;
			}
			for (o in instance._role.LEAF) {
				if (instance._role.LEAF[o] === role) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Curry for creating a tree walker filter. This has been split out of {@link getTreeWalker} because we use the
		 * same filter in the keyActivator helper function but it is passed to the instance of {@link wc/dom/keyWalker}
		 * as part of the config.
		 *
		 * @function getNavigationTreeWalkerFilter
		 * @private
		 * @param {Boolean} ignoreClosed If true we ignore closed branches.
		 * @param {Object} instance An instance of a menu class which extends this class.
		 * @param {String} [letter] The key literal of a letter key used for jump navigation.
		 * @returns {Function} A TreeWalker filter function.
		 */
		function getNavigationTreeWalkerFilter(ignoreClosed, instance, letter) {
			return function(element) {
				// treeWalker filter function that provides the core Abstract Tree View of the menu
				var branch, result = canNavigate(element, instance);

				// skip over closed branches
				if (ignoreClosed && result !== NodeFilter.FILTER_REJECT && instance._wd.submenu.isOneOfMe(element)) {
					if ((branch = instance._getBranch(element))) { // should always be true
						if (!shed.isExpanded(instance._getBranchExpandableElement(branch))) {
							result = NodeFilter.FILTER_REJECT;
						}
					}
					else {
						console.warn("A submenu cannot find its branch ancestor in AbstractMenu.getNavigationTreeWalkerFilter");
						result = NodeFilter.FILTER_SKIP;
					}
				}
				// finally, if we define a letter we will only have a match if the candidate match also meets the text content match
				if (letter && (result === NodeFilter.FILTER_ACCEPT)) {
					result = hasTextNodeMatch(element, letter, instance);
				}
				return result;
			};
		}

		/**
		 * Helper for getNavigationTreeWalkerFilter.
		 * Determines if the current element is navigable, e.g. it is not disabled or hidden etc.
		 * @param element Anelement, as passed to getNavigationTreeWalkerFilter
		 * @param instance A menu instance as passed to getNavigationTreeWalkerFilter
		 * @returns {Number} A NodeFilter value.
		 * @function
		 * @private
		 */
		function canNavigate(element, instance) {
			var result = NodeFilter.FILTER_SKIP;
			if (shed.isDisabled(element) || shed.isHidden(element)) {
				result = NodeFilter.FILTER_REJECT;
			}
			else if (isItem(element, instance)) {
				// branch or leaf
				result = NodeFilter.FILTER_ACCEPT;
			}
			return result;
		}

		/**
		 * Gets the keyWalker configuration for a particular menu.
		 * @function getkeyWalkerConfig
		 * @private
		 * @param {Element} item An element inside a menu
		 * @param {Element} [root] The menu root if we already have it.
		 * @param {Object} instance The subclass instance.
		 * @returns {Object} a keywalker configuration object.
		 */
		function getkeyWalkerConfig(item, root, instance) {
			var _root = root || instance._getRoot(item),
				kwConfig;
			if (_root) {
				kwConfig = {
					root: _root
				};
				kwConfig[keyWalker.OPTIONS.DEPTH_FIRST] = instance._treeWalkDepthFirst;
				kwConfig[keyWalker.OPTIONS.CYCLE] = instance._cycleSiblings;
			}
			return kwConfig;
		}

		/**
		 * Get the first available menu node with visible text which starts with a particular letter. NOTE: the
		 * WAI-ARIA guidelines for this functionality indicate the NEXT available item so we do not cycle within the
		 * menu even if the menu supports cycling on key nevigation.
		 * @function getTextTarget
		 * @private
		 * @param {Element} item The menu node on which we started when the user pressed a letter key.
		 * @param {String} letter The letter pressed by the user.
		 * @param {Element} root The current menu root node.
		 * @param {Object} instance The subclass instance.
		 * @returns {Element} The next available menu item with visible text which starts with keyName or null if not
		 *    found.
		 */
		function getTextTarget(item, letter, root, instance) {
			var target = null,
				keyWalkerConfig = getkeyWalkerConfig(item, root, instance);
			keyWalkerConfig.filter = getNavigationTreeWalkerFilter(true, instance, letter);
			keyWalkerConfig[keyWalker.OPTIONS.CYCLE] = false;  // do not cycle on key match
			target = keyWalker.getTarget(keyWalkerConfig, item, keyWalker.MOVE_TO.NEXT);
			return target;
		}

		/**
		 * <p>Closes an open menu when an element outside of the menu receives focus or is clicked.<p>
		 *
		 * <p><strong>Why is his here?</strong> If a transient menu is inside an element which is able to receive focus
		 * (such as a selectable table row) and the user agent is Chrome (at least since v27, possibly earlier) then a
		 * mousedown on a menu item will set focus to the focusable ancestor which will lead to this code being invoked.
		 * This only becomes a problem because the menu will close before the click event fires and the button will not
		 * actually receive the click (since it is now hidden). So in those cases we wrap the call to
		 * {@link closeAllPaths} in a timeout to allow the webkit focus fix to kick in and refocus the original
		 * button.</p>
		 *
		 * @function closeOpenMenu
		 * @private
		 * @param {Element} menu The menu to close.
		 * @param {Element} element The element which has caused the menu to close (most commonly by receiving focus).
		 * @param {Object} instance The subclass instance.
		 */
		function closeOpenMenu(menu, element, instance) {
			try {
				if (element === window) {
					closeAllPaths(menu, null, instance);
				}
				else if (element.tabIndex >= 0) {
					timers.setTimeout(closeAllPaths, 150, menu, null, instance);
				}
				else {
					closeAllPaths(menu, null, instance);
				}
			}
			finally {
				openMenu = null;
				activateOnHover = null;
			}
		}

		/**
		 * Mouse over handler. Sets up hover effects when the menu is transoent and not displayed on a mobile device.
		 * This handler is only bound if required when a menu first receives focus and is bound directly to the menu
		 * root. Note, we do not apply hover effects on mobile even though mobile devices may have keyboards and mice
		 * because we restyle transient sub-menus on these devices to improve usability when NOT using a mouse. This
		 * restyle melds better with most mobile OS native menu systems which are full-page per menu level.
		 *
		 * @see {@link focusEvent}
		 * @function mouseoverEvent
		 * @private
		 * @param {Event} $event the mouseover event wrapped by {@link module:wc/dom/event}.
		 */
		function mouseoverEvent($event) {
			var target = $event.target,
				root,
				item;
			if ($event.defaultPrevented || this.isMobile) {
				return;
			}
			if ((root = this._getRoot(target)) && isMenuTransient(root, this) && root === this._getRoot(document.activeElement)/* element root is same as focus root */) {
				item = getItem(target, this);
				if (!item || shed.isDisabled(item)) {
					return;
				}
				this._focusItem(item, root);

				if (activateOnHover === root.id) {
					// current menu is active menu
					if (this._isOpener(item)) {
						item = this._getBranch(item);
					}
					if (item && this._isBranch(item) && !shed.isExpanded(this._getBranchExpandableElement(item))) {
						this[FUNC_MAP.OPEN](item);
					}
				}
			}
		}

		/**
		 * Allows late binding of event listeners to events so that subclasses can override event listeners if they
		 * <strong>really</strong> need to. If we didn't use this mechanism then the superclass events would always be
		 * called even if they were overridden.
		 *
		 * @function eventWrapper
		 * @private
		 * @param {Event} $event The event wrapped by {@link module:wc/dom/event}.
		 * @returns {(boolean|undefined)} The return value, if any, of the event handler. Should generally be
		 *    undefined as we do not usually return from an event handler but use preventDefault to prevent further
		 *    action.
		 */
		function eventWrapper($event) {
			var result,  // return undefined by default;
				type = $event.type,
				methodName = type.toLowerCase(),
				handler;
			if (methodName === "focusout") {
				methodName = "blur";
			}
			else if (methodName === "focusin") {
				methodName = "focus";
			}
			handler = this[methodName + "Event"];
			if (handler) {
				// there's a handler for this event so pass the call through
				result = handler.call(this, $event);
			}
			return result;
		}

		/**
		 * Gets an instance of TreeWalker for a particular menu.
		 * @function getTreeWalker
		 * @private
		 * @param {Element} root The root of the tree to be walked.
		 * @param {Boolean} [ignoreClosed] If true we ignore closed branches.
		 * @param {Object} instance The subclass.
		 * @returns {TreeWalker} A treeWalker for the menu starting at root.
		 */
		function getTreeWalker(root, ignoreClosed, instance) {
			var filter = getNavigationTreeWalkerFilter(ignoreClosed, instance);
			return document.createTreeWalker(root, NodeFilter.SHOW_ELEMENT, filter, false);
		}

		/**
		 * Gets all ancestor menu nodes betweeen two points.
		 * @function getPathToItem
		 * @private
		 * @param {Element} item A menu node.
		 * @param {Element} from The highest level ancestor in which we are interested (usually the root).
		 * @param {Object} instance The subclass.
		 * @returns {Element[]} An array of Elements each item in the array being a menu node on the path from the 'from'
		 *    element to the item in question. This will always be at least item itself (if item is root or start).
		 */
		function getPathToItem(item, from, instance) {
			var tw,
				result = [
					item
				],
				parent;

			tw = getTreeWalker(from, false, instance);
			tw.currentNode = item;

			while ((parent = tw.parentNode()) && parent !== from) {
				result[result.length] = parent;
			}
			result[result.length] = from;
			result.reverse();
			return result;
		}

		/**
		 * Closes all paths from the provided node, except the path to the node represented by 'except'.
		 * @function closeAllPaths
		 * @private
		 * @param {Element} from DOM node to start closing, usually the menu root.
		 * @param {Element} [except] DOM Node which is exempt from being closed, usually the item/path being opened or
		 *    currently active. If not set then all paths in the menu are closed.
		 * @param {Object} instance The subclass.
		 */
		function closeAllPaths(from, except, instance) {
			var exceptPath,
				tw,
				next;
			if (except) {
				exceptPath = getPathToItem(except, from, instance);
				if (exceptPath && exceptPath.length) {
					exceptPath.reverse();
					exceptPath.pop();
				}
			}
			tw = getTreeWalker(from, true, instance);
			while ((next = tw.nextNode())) {
				if (exceptPath && next === exceptPath[exceptPath.length - 1]) {
					exceptPath.pop();
					continue;
				}
				if (instance._isBranch(next)) {
					instance[FUNC_MAP.CLOSE](next);
				}
			}
		}

		/**
		 * Get the first item (menu item or submenu or submenu content) in a given menu or branch which is not disabled
		 * or hidden.
		 * @function getFirstAvailableItem
		 * @private
		 * @param {Element} start Any menu node but preferably a submenu wrapper element.
		 * @param {Object} instance The subclass.
		 * @returns {Element} The first menu item which is not hidden or disabled or in a closed branch.
		 */
		function getFirstAvailableItem(start, instance) {
			var tw,
				next,
				submenu = start;
			if (instance._getRoot(submenu) === submenu || (submenu = instance._getSubMenu(submenu))) {
				tw = getTreeWalker(submenu, true, instance);
				tw.currentNode = submenu;
				while ((next = tw.nextNode())) {
					if (isItem(next, instance) && !shed.isDisabled(next) && !shed.isHidden(next)) {
						return next;
					}
				}
			}
			return null;
		}

		/**
		 * Disable/enable all menu items in a given branch.
		 * @see {@link module:wc/ui/menu/core~shedSubscriber}.
		 * @see {@link module:wc/ui/menu/core~fixDisabledAfterAjax}
		 * @function disableInBranch
		 * @private
		 * @param {Element} branch The menu branch we need to manipulate.
		 * @param {String} func The {@link wc/dom/shed} function to invoke: either "enable" or "disable".
		 * @param {Object} instance The subclass used to ensure we are in the right type of menu.
		 */
		function disableInBranch(branch, func, instance) {
			var content = instance._getSubMenu(branch, true),
				kids = content.children || content.childNodes,
				i,
				next;

			for (i = 0; i < kids.length; ++i) {
				next = kids[i];
				if (next.nodeType !== Node.ELEMENT_NODE) {
					continue;
				}
				shed[func](next);  // by calling disable/enable on anything we ensure it will be honoured or passed on as appropriate
			}
		}

		/**
		 * We need to investigate the existing DOM to determine the menu type
		 * and selection mode of the submenu (or menu ancestor) which is then
		 * used to set the role of menu items and the submenus.
		 *
		 * If the role indicates selection of the component is possible we
		 * will also have to add the correct properties and states to reflect
		 * the current selection status.
		 *
		 * @function ajaxSubscriber
		 * @private
		 * @param {Element} element The reference element (element being replaced).
		 * @param {DocumentFragment} documentFragment The document fragment which will be inserted.
		 */
		function ajaxSubscriber(element, documentFragment/* , action */) {
			var root,
				opener;
			/*
			 * helper to update attributes in the content of an ajaxed-in branch/submenuContent
			 */
			function fixBranchContent(nextBranch, useThisContent, inst) {
				var myContent = useThisContent || inst._wd.submenu.findDescendant(nextBranch),
					immediate = true;
				if (!myContent) {
					// I would worry if I had no content since a content holder is always created
					console.warn("Menu ajax subscriber: transform of submenu content failed to create a content container.");
					return;
				}

				if (myContent.nodeType !== Node.ELEMENT_NODE) {
					immediate = false;
				}
				// fix up the menu items in this branch
				Array.prototype.forEach.call(Widget.findDescendants(myContent, AJAX_CONTEXTLESS_ITEM, immediate),
					function(nextLeaf) {
						var okToFix = true,
							_localBranch;
						if (!immediate) {
							// we are ok if we are in the same branch or if we have no branch ancestor
							_localBranch = inst._getBranch(nextLeaf);
							if (_localBranch && _localBranch !== nextBranch) {
								okToFix = false;
							}
						}
						if (okToFix) {
							inst._setMenuItemRole(nextLeaf, nextBranch);
						}
					});
			}
			/* before we do anything else make sure we are in the right kind of menu */
			if (element && (root = this._getRoot(element)) === this.getFixedWidgets().GENERIC_ROOT.findAncestor(element)) {
				AJAX_CONTEXTLESS_ITEM = AJAX_CONTEXTLESS_ITEM || new Widget("", "", {"role": "${wc.ui.menu.dummyRole}"});
				/*
				 * If the ajaxTarget is the content of a WSubMenu then we can be sure
				 * that the ajax is caused by the branch and the action is FILL
				 * because the content is not directly addressable in the JAVA API.
				 */
				if (this._wd.submenu.isOneOfMe(element)) {
					/* submenu content may have the wrong role
					 * NOTE: this only needs to be done in tree menus because
					 * the fallback role is menu. */
					if (this._fixSubmenuContentInAjaxResponse) {
						this._fixSubmenuContentInAjaxResponse(documentFragment);
					}
					/* Now work on branches within the submenu content we are inserting.
					 * Remember, the submenu content is itself a child of a branch so all branches
					 * will have a branch ancestor */
					Array.prototype.forEach.call(this.getFixedWidgets().BRANCH.findDescendants(documentFragment), function(nextBranch) {
						/*
						 * Helper for the ajax subscriber. This is a forEach iterator function
						 * which is used to set up branches in a submenuContent fill response.
						 *
						 * Note on the submenuContext element:
						 * We are dealing explicitly with branches that are inside the submenuContent
						 * of a branch which is being filled by AJAX. This means that
						 * if the branch is an immediate child of the submenu content being
						 * filled it will have an ancestor branch of the parent of that
						 * submenu content. If the branch is nested inside another branch
						 * then it should be fairly obvious that it has a branch ancestor!
						 * So we are always safe to call this._getBranch on the submenuContext of
						 * any branch in this type of ajax response.
						 */
						var submenuContext = (this._wd.submenu.findAncestor(nextBranch) || element);
						this._setMenuItemRole(nextBranch, this._getBranch(submenuContext));
						if (isMenuTransient(root, this) && (opener = this._getBranchOpener(nextBranch))) {
							opener.setAttribute("aria-haspopup", TRUE);
						}
						fixBranchContent(nextBranch, null, this);
					}, this);

					// set menu item attributes on the items directly in the submenuContent
					fixBranchContent(this._getBranch(element), documentFragment, this);
				}
			}
		}

		/**
		 * Indicates if there is a viewport collision on the sides of the viewport.
		 * @function doICollide
		 * @private
		 * @see {@link _doCollisionDetection}
		 * @param {module:wc/dom/viewportCollision} collision The calculated 'collision'.
		 * @param {Boolean} [isNotDefaultDirection] Indicates the collision direction to test. If true we test against the
		 *    side deemed to be the DEFAULT direction of reading.
		 * @returns {Boolean} true if the collsion shows the colliding element hits the relevant viewport edge.
		 */
		function doICollide(collision, isNotDefaultDirection) {
			var result = false;

			if (collision) {
				if (CLASS.DEFAULT_DIRECTION === CLASS.COLLIDE_EAST) {
					result = isNotDefaultDirection ? (collision.w < 0) : (collision.e > 0);
				}
				else {
					result = isNotDefaultDirection ? (collision.e > 0) : (collision.w < 0);
				}
			}
			return result;
		}

		/**
		 * Indicates if the parent submenu (if any) of a given submenu is itself colliding with an edge of the viewport.
		 * @function isParentSubmenuColliding
		 * @private
		 * @see {@link  module:wc/ui/menu/core~_doCollisionDetection}
		 * @param {Element} submenu The submenu currently undergoing collision detection
		 * @param {Object} instance The subclass.
		 * @returns {Boolean} true if the parent is also colliding.
		 */
		function isParentSubmenuColliding(submenu, instance) {
			var parent = submenu.parentNode,
				ancestor,
				result = false;

			if (parent && (ancestor = instance._wd.submenu.findAncestor(parent))) {
				result = classList.contains(ancestor, CLASS.DEFAULT_DIRECTION);
			}
			return result;
		}
		/**
		 * Collision detection which supports rtl and ltr opening submenus.
		 * @todo this is now so cumbersome we may be better off just calculating offsets and position the submenu
		 * directly.
		 * @see {@link  module:wc/ui/menu/core~doCollisionDetection}
		 * @function _doCollisionDetection
		 * @private
		 * @param {Element} submenu The submenu content which may be colliding with the edge of the viewport.
		 * @param {Object} instance An instance of a subclass.
		 */
		function _doCollisionDetection(submenu, instance) {
			var _submenu = document.getElementById(submenu.id),  // required for IE8 because of the timeout, we could be dealing with a different HTML element
				collision,
				box,
				iCollideInDefaultDirection,
				iCollideAgainstDefaultDirection;
			if (_submenu) {
				collision = viewportCollision(_submenu);
				iCollideInDefaultDirection = doICollide(collision);  // we need to know if the submenu would collide east by itself not because its parent submenu collides east
				iCollideAgainstDefaultDirection = doICollide(collision, true);  // same with west collision

				/*
				 * Collision detection is a bit weird so please read this: it does make sense
				 * If the submenu collides east and west before allowing for its ancestors then
				 * we are actually in a simple situation. this really should never happen, but
				 * we do have at least one spec with a fixed width (in ems) submenu (which is
				 * why we have those size ANT properties) and do run on small screens
				 * so it is possible. Also, predetermining if we have both collisions helps later.
				 */
				if (iCollideInDefaultDirection && iCollideAgainstDefaultDirection) {
					classList.add(_submenu, CLASS.DEFAULT_DIRECTION);
					classList.add(_submenu, CLASS.AGAINST_DEFAULT);
					console.warn("There is something seriously wrong with this menu design, it overflows both edges of the screen");
				}
				else {
					/* If my parent menu is colliding in the default direction I am deemed
					 * to be colliding in the default direction until or unless I collide
					 * in the other direction.
					 */
					if (iCollideInDefaultDirection || (!iCollideAgainstDefaultDirection && isParentSubmenuColliding(_submenu, instance))) {
						classList.add(_submenu, CLASS.DEFAULT_DIRECTION);
						// this could make me collide west so I need to recalculate
						collision = viewportCollision(_submenu);
					}
					// if the submenu has been moved because of a default direction collision it may now collide the other way, so we have to test again
					if (doICollide(collision, true)) {
						classList.add(_submenu, CLASS.AGAINST_DEFAULT);
						/*
						 * A submenu may have been moved because its nearest ancestor submenu was deemed to
						 * collide rather than because it collided itself. If this was the case
						 * and moving it caused the other collision, remove the default collision.
						 */
						if (!iCollideInDefaultDirection) {
							classList.remove(_submenu, CLASS.DEFAULT_DIRECTION);
						}
					}
				}
				if (collision.s > 0) {
					classList.add(_submenu, CLASS.COLLIDE_SOUTH);
					// after a south collision test for overflow to the North
					if ((box = getBox(_submenu)) && box.top < 0) {
						_submenu.style.bottom = box.top + "px";
					}
				}
			}
		}

		/**
		 * Wrapper to undertake collision detection of submenus. This wrapper function is needed because some famous but
		 * obsolete browsers are rubbish at working out where an element is on the screen.
		 * @function doCollisionDetection
		 * @private
		 * @param {Element} submenu The submenu content which may be colliding with the edge of the viewport.
		 * @param {Object} instance An instance of a subclass.
		 */
		function doCollisionDetection(submenu, instance) {
			if (instance.isMobile) {
				return;
			}
			if (has("ie") === 8) {
				timers.setTimeout(_doCollisionDetection, 0, submenu, instance);  // IE needs time to repaint to be able to calculate the submenu's bounding box
			}
			else {
				_doCollisionDetection(submenu, instance);
			}
		}

		/**
		 * Menu descendants which are inseeted via AJAX may not have enough info in the XML to correctly determine their
		 * selectable state. This function fixes the selected state after the item is inserted when we have full
		 * context. The actual selection is subclass specific see {@link  module:wc/ui/menu/core~_selectAfterAjax}
		 * @function setSelectionStateAfterAjax
		 * @private
		 * @param {Element} element The element being inserted.
		 * @param {Object} instance The subclass.
		 */
		function setSelectionStateAfterAjax(element, instance) {
			var candidates = [],
				items;
			if (element.getAttribute(TRANSIENT_SELECTED_ATTRIB)) {
				candidates[candidates.length] = element;
			}
			if (!instance._isLeaf(element)) {
				// we may also have selected items inside element
				items = toArray(Widget.findDescendants(element, instance._wd.leaf.concat(instance.getFixedWidgets().BRANCH))).filter(function(next) {
					return next.getAttribute(TRANSIENT_SELECTED_ATTRIB);
				});
				if (items && items.length) {
					candidates = candidates.concat(items);
				}
			}
			candidates.forEach(instance._selectAfterAjax.bind(instance));
		}

		/**
		 * Disable any menu item which is ajaxed into a menu if it has a disabled ancestor in the originating menu. Yes,
		 * This is possible because WMenuItem is a stand-alone ajax target.
		 * @function fixDisabledAfterAjax
		 * @private
		 * @param {Element} element The element inserted using AJAX.
		 * @param {Element} root The target menu's root node.
		 * @param {Object} instance The subclass.
		 */
		function fixDisabledAfterAjax(element, root, instance) {
			var path = getPathToItem(element, root, instance),
				next,
				iHaveBeenDisabled = false;
			// first go from root to element and find if there are any disabled ancestors
			while ((next = path.shift())) {
				if (shed.isDisabled(next)) {
					shed.disable(element);  // we do want to notify on this because it will disable any children
					iHaveBeenDisabled = true;
					break;
				}
			}
			// if we have disabled element then all descendants will be disabled by the shed subscriber and further effort is not required
			if (!iHaveBeenDisabled && !instance._isLeaf(element)) {
				// if I am a leaf I have no descendants
				Array.prototype.forEach.call(instance.getFixedWidgets().BRANCH.findDescendants(element),
					function(nextBranch) {
						// an ajax response does not have a context menu so only individual nodes get disabled
						if (shed.isDisabled(nextBranch)) {
							disableInBranch(nextBranch, "disable", instance);
						}
					});
			}
		}

		/**
		 * After the AJAX malarkey has finished we have to set up some item properties which are not able to be determined
		 * in XSLT due to lac of menu context, focus into the new submenu and do collision detection if the menu is
		 * transient and not mobile. NOTE: bound to this as part of the subscription.
		 * @function postAjaxSubscriber
		 * @private
		 * @param {Element} element The element inserted via AJAX.
		 */
		function postAjaxSubscriber(element) {
			var root,
				subItem;
			if (element && (root = this._getRoot(element)) && root === this.getFixedWidgets().GENERIC_ROOT.findAncestor(element)) {
				if (postAjaxTimer) {
					timers.clearTimeout(postAjaxTimer);
					postAjaxTimer = null;
				}
				fixDisabledAfterAjax(element, root, this);
				setSelectionStateAfterAjax(element, this);
				/* if we have just opened a branch we will need to focus it. We know
				 * we have opened a branch if the submenu content was the ajax target.
				 * as it is not directly targetable by a generic WAjaxControl. We
				 * should do this AFTER making sure we have set all disabled and
				 * selected states as required.*/
				if (this._wd.submenu.isOneOfMe(element)) {
					if (isMenuTransient(root, this) && !this.isMobile) {
						doCollisionDetection(element, this);
					}
					if ((subItem = getFirstAvailableItem(element, this))) {
						if (focusTimer) {
							timers.clearTimeout(focusTimer);
							focusTimer = null;
						}
						timers.setTimeout(this._focusItem.bind(this), 0, subItem, root);
					}
				}
				/* last thing to do: make sure we have not lost focus by replacing the focused element. */
				if (!document.activeElement) {
					// we probably blew away the focus point and need to reset it, but do not do it immediately
					postAjaxTimer = timers.setTimeout(function() {
						if (!document.activeElement) {
							if (focus.canFocus(element)) {
								focus.setFocusRequest(element);
							}
							else {
								focus.focusFirstTabstop(element);
							}
						}
					}, 150);
				}
			}
		}

		/**
		 * Sets tabstops when a menu item is hidden or disabled.
		 * When we disable or hide any item we may have to move the default tabstop. This will be the case if:
		 * <ul><li>the former default tabstop was the branch opener for the disabled branch and therefore no
		 *    longer exists. We can assume that if the menu does not have a default tabstop that we just disabled it.</li>
		 * <li>the former default tabstop the element we just hid</li>
		 * <li> the old tabstop is the element (which means this is running before the SHED disabled helper: VERY unlikely)</li>
		 * <li>The old tabstop is inside the branch we just disabled/hid.</li></ul>
		 * @function hideDisableHelper
		 * @private
		 * @param {Element} element The menu item element being hidden or disabled.
		 * @param {Element} root The menu root node.
		 * @param {Object} instance The subclass menu we are processing.
		 */
		function hideDisableHelper(element, root, instance) {
			var path,
				newTabStopItem,
				oldTabstop = instance.getFixedWidgets().TABSTOP.findDescendant(root);  // only need the first one from the root

			if (!oldTabstop || oldTabstop === element || (oldTabstop.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINS)) {
				// need to reset the tabIndex to something else
				path = getPathToItem(element, root, instance);
				if (path.length <= 2) {
					// path length of 2 means is only root & branch, anything less is an error
					newTabStopItem = getFirstAvailableItem(root, instance);
				}
				else {
					newTabStopItem = path[(path.length - 2)];  // second last item
				}
			}
			// where was focus?
			if (!document.activeElement || document.activeElement === element || document.activeElement === document.body/* ie */ || document.activeElement === document.documentElement/* ie sometimes does this too */ || (document.activeElement.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINS)) {
				// oh, it is in the hidden/disabled submenu better reset focus. If we have a new tabstop item, set it to that
				instance._focusItem((newTabStopItem || getFirstAvailableItem(root, instance)), root);
			}
			else if (newTabStopItem) {
				setTabstop(newTabStopItem, instance);
			}
		}

		/**
		 * Common shed subscriber for SELECT, DESELECT, EXPAND, COLLAPSE, ENABLE, DISABLE and HIDE
		 * @function shedSubscriber
		 * @private
		 * @see {@link module:wc/dom/shed}
		 * @param {Element} element The SHED target.
		 * @param {String} action The SHED action.
		 */
		function shedSubscriber(element, action) {
			var opener,
				root,
				branch,
				isTransient;

			if (element && (root = this._getRoot(element))) {
				isTransient = isMenuTransient(root, this);

				if (action === shed.actions.ENABLE || action === shed.actions.DISABLE) {
					enableDisable(element, action, root, isTransient, this);
				}
				else if (action === shed.actions.HIDE) {
					hideDisableHelper(element, root, this);
				}
				else if (isTransient) {
					// collision detection on branch open
					if (action === shed.actions.EXPAND || action === shed.actions.COLLAPSE) {
						if (this._isBranch(element)) { // tree
							branch = element;
							opener = this._getBranchOpener(branch);
						}
						else if (this._wd.submenu.isOneOfMe(element) && (branch = this._getBranch(element))) {
							opener = this._getBranchOpener(branch);
						}

						expandCollapseTransientBranch(branch, action, root, opener, this);
					}
				}
				else if (action === shed.actions.COLLAPSE) {
					if (this._isBranch(element)) { // tree
						opener = this._getBranchOpener(element);
					}
					else if (this._wd.submenu.isOneOfMe(element) && (branch = this._getBranch(element))) {
						opener = this._getBranchOpener(branch);
					}
					this._focusItem(opener, root);
				}
			}
		}

		/**
		 * Gets the content controlled by a menu.
		 * @param {Element} opener The control that triggers a menu to "open".
		 * @function
		 * @private
		 * @returns {Element} The content controlled by the menu opener if found.
		 */
		function getContent(opener) {
			var content,
				CONTENT_ATTRIB = "aria-controls",
				contentId = opener.getAttribute(CONTENT_ATTRIB);
			if (contentId) {
				content = document.getElementById(contentId);
			}
			return content;
		}


		/*
		 * Helper for shedSubscriber.
		 * @function
		 * @private
		 */
		function expandCollapseTransientBranch(element, action, root, opener, instance) {
			var subItem, content = getContent(opener);
			if (action === shed.actions.EXPAND) {
				openMenu = root.id;
				content = getContent(opener);
				if (content) {
					if (!instance.isMobile) {
						doCollisionDetection(content, instance);
					}
					if ((subItem = getFirstAvailableItem(content, instance))) {
						timers.setTimeout(instance._focusItem.bind(instance), 0, subItem, root);
					}
				}
			}
			else if (action === shed.actions.COLLAPSE && (content = getContent(opener))) {
				classList.remove(content, CLASS.DEFAULT_DIRECTION);
				classList.remove(content, CLASS.AGAINST_DEFAULT);
				classList.remove(content, CLASS.COLLIDE_SOUTH);
				content.style.bottom = "";
				content.removeAttribute("style");

				// if the focus point is inside the branch then refocus to the opener
				if ((opener !== document.activeElement) && (element.compareDocumentPosition(document.activeElement) & Node.DOCUMENT_POSITION_CONTAINED_BY)) {
					instance._focusItem(opener, root);
				}
				else {
					instance._remapKeys(opener, root);
					// we still have to reset the tabIndex
					setTabstop(opener, instance);
				}
			}
		}

		/*
		 * Helper for shedSubscriber.
		 * @function
		 * @private
		 */
		function enableDisable(element, action, root, isTransient, instance) {
			var shedFunc, branch;
			if (instance._isBranch(element)) {
				shedFunc = action === shed.actions.DISABLE ? "disable" : "enable";
				if (isTransient) {
					// close the submenu
					if (action === shed.actions.DISABLE && (branch = instance._getBranchExpandableElement(element)) && shed.isExpanded(branch)) {
						shed.collapse(branch); // do not call this[FUNC_MAP.CLOSE] because we don't want all the animate gubbins
					}
				}
				// dis/en-able the opener
				shed[shedFunc](instance._getBranchOpener(element));
				// disable or re-enable stuff inside the submenu
				disableInBranch(element, shedFunc, instance);
			}
			// branches and items when disabled: may have to change default tabstop
			if (action === shed.actions.DISABLE) {
				hideDisableHelper(element, root, instance);
			}
		}

		/**
		 * Write the open state of branches in the menu. NOTE: menu item selection is written in the item class.
		 * @function writeState
		 * @private
		 * @param {Element} container the current WComponents form or a subform thereof which is the root for the state evaluation
		 * @param {Element} toContainer the container into which state fields are written
		 */
		function writeState(container, toContainer) {
			var _widgets,
				root,
				branchItem;

			function writeExpandedState(nextSubmenu) {
				var name;

				if (this._isBranch(nextSubmenu)) { // tree
					name = nextSubmenu.id;
				}
				else if (this._wd.submenu.isOneOfMe(nextSubmenu) && (branchItem = this._getBranch(nextSubmenu))) { // menu
					name = branchItem.id;
				}

				if (name) {
					formUpdateManager.writeStateField(toContainer, name + "${wc.ui.menu.submenu.nameSuffix}", TRUE);
				}
			}

			function writeSelectedState(nextSelectedItem) {
				var root = this._getRoot(nextSelectedItem);
				if (root && root === this.getFixedWidgets().GENERIC_ROOT.findAncestor(nextSelectedItem)) {
					formUpdateManager.writeStateField(toContainer, nextSelectedItem.id + "${wc.ui.menu.selectable.nameSuffix}", "x");
				}
			}

			function writeMenuState(next, includeMenu) {
				/* Cannot use getFilteredGroup for expandables any more.
				 * Why not?
				 * Well:
				 * 1. roles menu and menubar do not include role menu as a scoped role;
				 * 2. roles menuitem, menuitemradio and menuitemcheckbox do not support aria-expanded
				 * 3. therefore we cannot use scoped roles to get the group of expanded menu items.
				 * Array.prototype.forEach.call(getFilteredGroup(next, {
					filter: (getFilteredGroup.FILTERS.expanded | getFilteredGroup.FILTERS.enabled),
					ignoreInnerGroups: true
				}),
				writeExpandedState, this);*/

				(toArray(this.getFixedWidgets().BRANCH.findDescendants(next))).filter(function(nextBranch) {
					return !shed.isDisabled(nextBranch) && shed.isExpanded(this._getBranchExpandableElement(nextBranch));
				}, this).forEach(writeExpandedState, this);

				Array.prototype.forEach.call(getFilteredGroup(next, {
					filter: (getFilteredGroup.FILTERS.selected | getFilteredGroup.FILTERS.enabled),
					ignoreInnerGroups: true
				}),
				writeSelectedState, this);
				if (includeMenu) {
					formUpdateManager.writeStateField(toContainer, next.id + "-h", "x");
				}
			}

			try {
				Array.prototype.forEach.call(this.ROOT.findDescendants(container), writeMenuState, this);
				if (this.ROOT.isOneOfMe(container)) {
					writeMenuState.call(this, container, true);
				}
				else {
					_widgets = [this._wd.submenu].concat(this._wd.leaf);
					if (Widget.isOneOfMe(container, _widgets) && (root = this._getRoot(container))) {
						writeMenuState.call(this, root, true);
					}
				}
			}
			catch (ex) {
				console.log(ex);
				throw ex;
			}
		}

		/**
		 * Encapsulates core menu functionality but does not implement a functioning menu. Must be extended.
		 * @constructor
		 * @alias module:wc/ui/menu/core~AbstractMenu
		 * @private
		 */
		function AbstractMenu() {
			/**
			 * Indicates if the current platform is deemed to be mobile. This is frozen into the abstractMenu instance
			 * which is returned from this module.
			 * @var
			 * @type {Boolean}
			 * @protected
			 */
			this.isMobile = has("ios") || has("android") || has("iemobile") || has("operamobi") || has("operamini") || has("bb");

			/**
			 * A function map to keep strings in sync used for changing key mappings. This uses the class var FUNC_MAP so
			 * that we can keep some ugliness at bay. If we could make this CONST or FINAL STATIC we would! If your sub
			 * class overrides this._FUNC_MAP you can expect things to break. This is why it is here in the constructor
			 * of an object which will ultimately be frozen!
			 *
			 * @see {@link module:wc/ui/menu/core~FUNC_MAP}
			 * @constant
			 * @type {Object}
			 * @protected
			 */
			this._FUNC_MAP = FUNC_MAP;
		}

		/**
		 * Widget descriptor of the menu root element. This <strong>must</strong> be overridden for anything to work
		 * <del>properly</del><ins>at all</ins>.
		 * @var
		 * @type {module:wc/dom/Widget}
		 * @protected
		 * @abstract
		 */
		AbstractMenu.prototype.ROOT = null;

		/**
		 * The map of key to action. <strong>ABSTRACT</strong>: must be overridden
		 * for each menu type.
		 * @var
		 * @type {Object}
		 * @protected
		 * @abstract
		 * @see {@link  module:wc/ui/menu/core~_keyActivator}
		 */
		AbstractMenu.prototype._keyMap = {};

		/**
		 * Widget descriptors: these really describe each menu type. If you do not have these expect things to break. A
		 * complete _wd will have at least submenu and leaf. If they are not {@link wc/dom/Wdiget} descriptors they may
		 * as well not exist. The default is abstract.
		 * @var
		 * @type {Object}
		 * @protected
		 * @abstract
		 */
		AbstractMenu.prototype._wd = {
			submenu: null,
			leaf: null
		};

		/**
		 * If set to true the menu will have transient effects: that is, close when
		 * it loses focus or activate on hover and invoke viewport collision. These are
		 * all facted of menus which do not have sticky open-ness. In abstract menu this
		 * property should not be addressed directly but should be tested using the private
		 * helper function isMenuTransient. This prevents false results from subclasses.
		 *
		 * @var
		 * @type {Boolean}
		 * @protected
		 */
		AbstractMenu.prototype._transient = true;

		/**
		 * Indicates the method for finding "next" and "previous" when tree walking.
		 * If set to true, the tree walker for the menu will look for children before siblings
		 * otherwise it will look for siblings first.
		 *
		 * @var
		 * @type {Boolean}
		 * @protected
		 */
		AbstractMenu.prototype._treeWalkDepthFirst = false;

		/**
		 * Used when keyboard walking though a menu/submenu. If set to false do not
		 * cycle around ends of sibling groups (going from last to first and vice-versa).
		 *
		 * @var
		 * @type {Boolean}
		 * @protected
		 */
		AbstractMenu.prototype._cycleSiblings = true;

		/**
		 * Indicates that only one unique branch may be open in the tree at any time and all others will be closed
		 * automatically when opening a branch. This is not quite the same as transient as a single opening tree is
		 * possible (think of a horizontal tree like the OS X finder in tree view).
		 * @var
		 * @type {Boolean}
		 * @protected
		 */
		AbstractMenu.prototype._oneOpen = true;

		/**
		 * Indicate whether selectable menu items are selected as soon as the cursor arrives. If false then selection
		 * requires a second interaction. This should only be true for TREES according to current ARIA practices.
		 * This cannot be deferred to the item aria helpers since ariaAnalog knows nothing about the tree structure of
		 * menus so we have to do the navigation here.
		 * @var
		 * @type {Boolean}
		 * @protected
		 */
		AbstractMenu.prototype._selectOnNavigate = false;

		/**
		 * Roles for the parts of the menu which change. Tree menu will over-ride all of these, all other menus will
		 * leave them all or override maybe one (BAR/FLYOUT redefine MENU)
		 * @var
		 * @type {Object}
		 * @protected
		 */
		AbstractMenu.prototype._role = {
			MENU: MENU_ROLE,
			LEAF: {
				noSelection: MENUITEM_ROLE,
				single: "menuitemradio",
				multi: "menuitemcheckbox"
			},
			SUBMENU: MENU_ROLE
		};

		/**
		 * Sets up the initial keyboard interaction maps for the menu in its initial state. This key map should meet the
		 * appropriate WAI-ARIA implementation guide.
		 * @function
		 * @protected
		 * @abstract
		 */
		AbstractMenu.prototype._setupKeymap = function() {};

		/**
		 * Key re-mapping function to meet WAI-ARIA implementation guide.
		 * @function
		 * @protected
		 * @abstract
		 */
		AbstractMenu.prototype._remapKeys = function() {};

		/**
		 * Get the object of fixed (non-subclass-specific) {@link module:wc/dom/Widget}s.
		 * @see {@link module:wc/ui/menu/menuItem} which does not extend this module.
		 * @see {@link setupFixedWidgets}
		 * @function
		 * @public
		 * @returns {Object} The object containing the fixed widgets.
		 */
		AbstractMenu.prototype.getFixedWidgets = function() {
			return fixedWidgets || (fixedWidgets = setupFixedWidgets());
		};

		/**
		 * Get the menu root element for the menu in which the passed in element is enclosed. This is fundamental to the
		 * menu abstraction as usually the only way to tell what kind of a menu an element is in is to inspect the root.
		 * @function
		 * @protected
		 * @param {Element} item Any HTML element
		 * @returns {?Element} a menu root element if found and if the menu root for the type of menu is the first
		 *    menu root found.
		 */
		AbstractMenu.prototype._getRoot = function(item) {
			var result = this.ROOT.findAncestor(item);
			if (result && result !== this.getFixedWidgets().GENERIC_ROOT.findAncestor(item)) {
				// make sure the first generic root is root
				result = null;
			}
			return result;
		};

		/**
		 * Item selection is not straight forward as in some cases it is dependent on chordal keystrokes. Most menu
		 * items can just be selected and let the ariaAnalog shed subscribers take care of the rest.
		 * @function
		 * @protected
		 * @param {Element} element The menu element to select.
		 */
		AbstractMenu.prototype._select = function(element) {
			shed.select(element);
		};

		/**
		 * Used to add close buttons to menus (and other artifact manipulation) when the menu is transient and the
		 * application is viewed on a mobile device.
		 *
		 * @function
		 * @abstract
		 */
		AbstractMenu.prototype.updateMenusForMobile = null;

		/**
		 * Get the nearest branch element in which the passed in element is enclosed.
		 * @function
		 * @protected
		 * @param {Element} item Any HTML element
		 * @returns {?Element} A branch element if found.
		 */
		AbstractMenu.prototype._getBranch = function(item) {
			return this.getFixedWidgets().BRANCH.findAncestor(item);
		};

		/**
		 * Get the menu element which is able to be "aria-expanded". This is the WSubMenu's content in most menus but
		 * is the WSubMenu itself in trees.
		 *
		 * @function
		 * @protected
		 * @param {Element} item The start point for the search. This will normally be a 'branch'.
		 * @returns {?Element} The "expandable" element. This is usually the branch content but is the branch in trees.
		 */
		AbstractMenu.prototype._getBranchExpandableElement = function (item) {
			var myBranch;

			if (!item) {
				throw new TypeError("Item must not be undefined.");
			}

			if (this._wd.submenu.isOneOfMe(item)) {
				return item;
			}

			if (this._isBranch(item)) {
				return this._getSubMenu (item, true);
			}

			if (this._isOpener(item) && (myBranch = this._getBranch(item))) {
				return this._getSubMenu (myBranch, true);
			}

			throw new TypeError("Item must be a branch, submenu or branch opener element.");
		};

		/**
		 * Gets the nearest submenu element relative to a start point in the direction specified.
		 * @function
		 * @protected
		 * @param {Element} item Any HTML element.
		 * @param {Boolean} [descending] true to look for a descendant submenu (usually only set when called from a
		 *    branch item)
		 * @returns {?Element} A submenu element if found.
		 */
		AbstractMenu.prototype._getSubMenu = function(item, descending) {
			var up = !descending;  // mostly look up not down

			if (this._getRoot(item)) {
				if (this._wd.submenu.isOneOfMe(item)) {
					return item;
				}
				return up ? this._wd.submenu.findAncestor(item) : this._wd.submenu.findDescendant(item);
			}
			return null;
		};

		/**
		 * 'Animate' the opening or closing of a branch. This is actually an easily overridden helper for openBranch and
		 * closeBranch which by default does not invoke any animator.
		 *
		 * @function
		 * @protected
		 * @param {Object} item The branch being opened/closed.
		 * @param {Object} open If true branch is being opened, otherwise its being closed.
		 * @returns {Boolean} true if any non-false-equivalent value for item is passed in.
		 */
		AbstractMenu.prototype._animateBranch = function(item, open) {
			var func;
			if (item) {
				func = open ? "expand" : "collapse";
				shed[func](item);
				return true;
			}
			return false;
		};

		/**
		 * This is the helper function for actioning a node in a menu. By default it should only action (open/close)
		 * branches.
		 * @function
		 * @protected
		 * @param {Element} element The menu node being actioned.
		 * @returns {Boolean} true if this element was actioned.
		 */
		AbstractMenu.prototype._actionItem = function(element) {
			var root = this._getRoot(element),
				item, branchOrContent;
			if (!root) {
				return false;
			}
			item = getItem(element, this);
			if (!item || shed.isDisabled(item)) {
				return false;
			}
			if (this.isMobile) {
				CLOSE_BUTTON = CLOSE_BUTTON || new Widget("button", "closesubmenu", {"role": "menuitem"});
				if (CLOSE_BUTTON.isOneOfMe(item)) {
					this[FUNC_MAP.CLOSE_MY_BRANCH](item);
					return true;
				}
			}
			if (this._isBranch(item)) {
				// trees: the treeitem gets expanded, menus: the menu gets expanded.
				branchOrContent = this._getBranchExpandableElement(item);
				this._animateBranch(branchOrContent, !shed.isExpanded(branchOrContent));
				if (this._oneOpen) {
					closeAllPaths(root, item, this);
				}
				return true;
			}
			return false;
		};

		/**
		 * Action for ESCAPE key. Only really applies to menus with _transient true. Closes a branch and if the
		 * resultant focused item is at the top level of the menu it turns off hover effects for that menu.
		 * @function
		 * @protected
		 * @param {Element} item The HTML element which was the target of the key event which lead to this method being
		 *    called.
		 */
		AbstractMenu.prototype._escape = function(item) {
			var branch,
				root = this._getRoot(item);
			if (root && isMenuTransient(root, this) && (branch = this[this._FUNC_MAP.CLOSE_MY_BRANCH](item)) && branch.parentNode) {
				// if we have successfully closed a submenu at the top of the menu then remove the hover flag
				branch = this._getBranch(branch.parentNode);
				if (!branch || (branch === root)) {
					activateOnHover = null;
				}
			}
		};

		/**
		 * Opens a branch.
		 * @function
		 * @protected
		 * @param  {Element} branch The branch, opener or submenu node of the branch to open.
		 * @returns {Boolean} true if the branch opened.
		 */
		AbstractMenu.prototype._openBranch = function(branch) {
			var _branch,
				_expandable,
				root;

			if ((root = this._getRoot(branch))) { // usual test for "am i in the correct menu module". TODO: Maybe make this a helper...
				// Open branch may be called from an opener button (pretty common actually) so first we need the real branch.
				if ((_branch = this._getBranch(branch)) && (_expandable = this._getBranchExpandableElement(_branch)) && !shed.isExpanded(_expandable)) {
					if (this._oneOpen) {
						closeAllPaths(root, branch, this); // use the original branch
					}
					return this._animateBranch(_expandable, true);
				}
			}

			return null;
		};

		/**
		 * Closes a branch: only works if called from a branch opener, submenu or branch.
		 * @function
		 * @protected
		 * @param {Element} branch tThe branch to close (or its 'opener' button or submenu child).
		 * @returns {Boolean} true if the branch closed.
		 */
		AbstractMenu.prototype._closeBranch = function(branch) {
			var _expandable;
			/* close branch may be called from an opener button or a submenu (pretty common actually) */
			if ((_expandable = this._getBranchExpandableElement(branch)) && shed.isExpanded(_expandable)) {
				return this._animateBranch(_expandable, false);
			}
			return false;
		};

		/**
		 * Indicates if an element is a branch opener.
		 * @function
		 * @protected
		 * @param {Element} element An element in a menu
		 * @returns {Boolean} true if the element is a branch opener.
		 */
		AbstractMenu.prototype._isOpener = function(element) {
			return this.getFixedWidgets().BRANCH_TRIGGER.isOneOfMe(element);
		};

		/**
		 * Indicates whether an item is either a branch or branch opener button.
		 * @function
		 * @protected
		 * @param {Element} item The menu node to test.
		 * @returns {Boolean} True if item is a branch or a branch opener button.
		 */
		AbstractMenu.prototype._isBranchOrOpener = function(item) {
			return this._isBranch(item) || this._isOpener(item);
		};

		/**
		 * Close the branch in which an item is nested.
		 * @function
		 * @alias AbstractMenu._closeMyBranch
		 * @protected
		 * @param {Element} item A menu item in a branch.
		 * @returns {Element} or undefined. If the item is in a branch that branch is returned.
		 */
		AbstractMenu.prototype._closeMyBranch = function(item) {
			var branch,
				_item = item;
			// if we simply called closeMyBranch from a 'closed' opener we would end up doing nothing because the
			// opener's parent branch is the branch it is in. So we need to get the branches parent.
			if (this._isBranchOrOpener(_item)) {
				if ((branch = this._getBranch(_item)) && !shed.isExpanded(this._getBranchExpandableElement(branch))) {
					_item = branch.parentNode;
				}
			}
			if ((branch = this._getBranch(_item)) && branch !== this._getRoot(_item)) { // do not try to close root!!
				this[FUNC_MAP.CLOSE](branch);
				return branch;
			}
			return null;
		};

		/**
		 * Gets the opener button of a branch node. This will be the node focussed when
		 * a branch is given focus.
		 * @function
		 * @protected
		 * @param {Element} branch a branch node.
		 * @returns {Element} A button element or null if not found.
		 */
		AbstractMenu.prototype._getBranchOpener = function(branch) {
			return this.getFixedWidgets().BRANCH_TRIGGER.findDescendant(branch, true);
		};

		/**
		 * Set focus to a menu item after undertaking any necessary menu manipulation. This is the preferred way to set
		 * focus to a menu.
		 *
		 * @function
		 * @protected
		 * @param {Element} _item the menu item to focus.
		 * @param {Element} _root the current menu's root node.
		 * @param {function} [callback] an optional callback function.
		 */
		AbstractMenu.prototype._focusItem = function(_item, _root, callback) {
			var item = document.getElementById(_item.id),
				root = document.getElementById(_root.id),
				extendedCallback;

			if (item && root && (this.ROOT.findAncestor(item) === root) && !shed.isDisabled(item)) {
				extendedCallback = function(item) {
					this._remapKeys(item, root);
					if (callback && typeof callback === "function") {
						callback(item);
					}
				};

				if (isMenuTransient(root, this)) {
					// Close any open branches except the path to the current item
					closeAllPaths(this._getRoot(item), item, this);
				}

				setTabstop(item, this);

				if (focus.canFocus(item)) {
					focus.setFocusRequest(item, extendedCallback.bind(this));
				}
				else {
					focus.focusFirstTabstop(item, extendedCallback.bind(this));
				}
			}
		};

		/**
		 * determines if an item is a branch node.
		 * @function
		 * @protected
		 * @param {Element} item The HTML element to test
		 * @returns {Boolean} true if the item is a branch node
		 */
		AbstractMenu.prototype._isBranch = function(item) {
			return this.getFixedWidgets().BRANCH.isOneOfMe(item);
		};

		/**
		 * Determines if a given HTML element is a leaf node.
		 * @function
		 * @protected
		 * @param {Element} element the HTML element to test
		 * @returns {Boolean} true if the element is a leaf node of a menu
		 */
		AbstractMenu.prototype._isLeaf = function(element) {
			return (isItem(element, this) && !this._isBranch(element));
		};

		/**
		 * Use a keyWalker to get a target item/opener/branch. Pulled out of {@link _keyActivator} so that it can be
		 * reused in {@link module:wc/ui/menu/bar~isFirstLastItem}.
		 * @function
		 * @protected
		 * @param {Element} item The current menu item/opener
		 * @param {String} action The keyMap action
		 * @param {Element} [root] The menu root element
		 * @param {Boolean} [forceCycle] Allows sibling cycling to be forced true or false independent of the menu's
		 *    default setting
		 * @returns {?Element} Element if a target appropriate to action is found otherwise null.
		 */
		AbstractMenu.prototype._getTargetItem = function(item, action, root, forceCycle) {
			var keyWalkerConfig = getkeyWalkerConfig(item, root, this);
			keyWalkerConfig.filter = getNavigationTreeWalkerFilter(true, this);
			if (forceCycle || forceCycle === false) {
				keyWalkerConfig[keyWalker.OPTIONS.CYCLE] = forceCycle;
			}
			return keyWalker.getTarget(keyWalkerConfig, item, action);
		};

		/**
		 * <p>Key lister navigation/activation helper: selection is determined by the ariaAnalog.</p>
		 * <p><strong>NOTE:</strong> the direction of travel is determined by the nearest menu/menubar/tree role as per
		 *  WAI-ARIA guidelines.</p>
		 * <p>NOTE: this is actually public but just for testing as it is difficult to test implicitly.</p>
		 *
		 * @see	http://www.w3.org/TR/wai-aria-practices/#menu
		 * @see http://www.w3.org/TR/wai-aria-practices/#TreeView
		 * @see http://www.w3.org/TR/wai-aria-practices/#accordion
		 *
		 * @function
		 * @protected
		 *
		 * @param {Element} item Where we start
		 * @param {KeyEvent.keyCode} $key The key that was pressed
		 * @param {Element} root The menu Root node
		 * @param {Boolean} [SHIFT] was the SHIFT key down during the event?
		 * @param {Boolean} [CTRL] was the CTRL key down during the event?
		 * @returns {Boolean} true if the event has been fully handled and we can prevent default
		 */
		AbstractMenu.prototype._keyActivator = function(item, $key, root, SHIFT, CTRL) {
			var target,
				keyName = key.getLiteral($key),
				action = this._keyMap[keyName];

			LETTER = LETTER || new RegExp(i18n.get("${wc.common.i18n.letter}"));
			if (action) {
				if (this[action]) {
					return this[action](item);
				}
				target = this._getTargetItem(item, action, root);
			}
			else if (keyName && (keyName = keyName.replace(KEY_NAME_RE, "")) && keyName.length === 1 && LETTER.test(keyName)) {
				target = getTextTarget(item, keyName, root, this);
			}
			if (target) {
				this._focusItem(target, root);
				if (this._selectOnNavigate && !CTRL) {
					this._select(target, false, SHIFT);
				}
				return true;
			}
			return false;
		};

		/**
		 * <p>Focus event handler.</p>
		 * <p>When a menu node receives focus we have to:</p>
		 * <ul>
		 * <li>close any other open menu of the same type;</li>
		 * <li>optionally turn on mouse over listeners if this menu type supports transient effects and this is the
		 * first time an element in this menu has been focussed;</li>
		 * <li>reset key bindings if the node is a branch or branch opener;</li>
		 * <li>reset the tabIndex so that the current node is 'tab-able' when we leave the menu.</li></ul>
		 * <p>If an element outside any menu receives focus and there is an open transient menu then that open menu
		 * must be closed but may only be closed by the controller of the same type as the open menu.</p>
		 * @function
		 * @protected
		 * @param {Event} $event The wrapped focus event.
		 */
		AbstractMenu.prototype.focusEvent = function($event) {  // ignore the claim this function is unused - it is bound up in the event wrapper.
			var localOpenMenu,
				root,
				genericRoot,
				item,
				target = $event.target;
			if ($event.defaultPrevented) {
				return;
			}
			root = ((target === window || target === document) ? null : this._getRoot(target));
			genericRoot = ((target === window || target === document) ? null : this.getFixedWidgets().GENERIC_ROOT.findAncestor(target));
			if (root && (root === genericRoot)) {
				if (openMenu && (localOpenMenu = document.getElementById(openMenu)) && localOpenMenu !== root) {
					closeOpenMenu(localOpenMenu, target, this);
				}
				if (isMenuTransient(root, this)) {
					if (!attribute.get(root, BOOTSTRAPPED)) {
						attribute.set(root, BOOTSTRAPPED, true);
						event.add(root, event.TYPE.mouseover, mouseoverEvent.bind(this));
					}
				}
				else if (this._isBranchOrOpener(target)) {
					this._remapKeys(target, root);
				}
				if ((item = getItem(target, this)) && !shed.isDisabled(item)) {
					if (this._isBranch(item)) {
						item = this._getBranchOpener(item);
					}
					setTabstop(item, this);
				}
			}
			else if (!genericRoot && openMenu && (localOpenMenu = document.getElementById(openMenu)) && this.ROOT.isOneOfMe(localOpenMenu)) {  // focus is not in any menu
				closeOpenMenu(localOpenMenu, target, this);
			}
		};

		/**
		 * Click event handler.
		 * @function
		 * @protected
		 * @param {Event} $event the click event wrapped by {@link module:wc/dom/event}.
		 */
		AbstractMenu.prototype.clickEvent = function($event) {
			var target = $event.target,
				root,
				item,
				localOpenMenu,
				me;
			if ($event.defaultPrevented) {
				return;
			}
			try {
				if (target !== window && (root = this._getRoot(target))) {
					if ((item = getItem(target, this)) && !shed.isDisabled(item)) {
						if (openMenu && (localOpenMenu = document.getElementById(openMenu)) && root !== localOpenMenu) {
							closeOpenMenu(localOpenMenu, target, this);
						}
						this[FUNC_MAP.ACTION](target);
						if (isMenuTransient(root, this)) {
							if (this._isBranch(item)) {
								activateOnHover = shed.isExpanded(this._getBranchExpandableElement(item)) ? root.id : null;
							}
							else if (this._isLeaf(item)) {
								me = this;
								timers.setTimeout(closeOpenMenu, 0, root, target, me);
							}
						}
					}
				}
				else if (openMenu && (localOpenMenu = document.getElementById(openMenu)) && this.ROOT.isOneOfMe(localOpenMenu)) {
					// click outside any menu we need to close any open transient menu
					closeOpenMenu(localOpenMenu, target, this);
				}
			}
			catch (ex) {
				$event.preventDefault();  // in case a link or submit was clicked, don't hide/lose the error
				console.error("Uncaught exception in AbstractMenu.onClick: ", ex.message);
			}
		};

		/**
		 * Keydown event handler. If the keydown event is of interest calls a helper function to undertake the correct
		 * action for the key.
		 *
		 * @function
		 * @protected
		 * @param {Event} $event the keydown event wrapped by {@link module:wc/dom/event}.
		 */
		AbstractMenu.prototype.keydownEvent = function($event) {
			var result = false,
				target,
				root,
				$key = $event.keyCode;

			if (!$event.defaultPrevented) {
				if ((root = this._getRoot($event.target)) && (target = getItem($event.target, this))) {
					result = this._keyActivator(target, $key, root, $event.shiftKey, ($event.ctrlKey || $event.metaKey));

					if (result && ~keysToCancel.indexOf($key)) {
						$event.preventDefault();
					}
				}
			}
		};

		/**
		 * Set the role attribute on an item which has been added/replaced in an ajax response.
		 * @see {@link  module:wc/ui/menu/core~ajaxSubscriber}
		 * @function
		 * @protected
		 * @param {Element} component The component which requires the role
		 * @param {Element} contextElement An element which is used for comparison or to determine the correct role:
		 *    <ul><li>when we are replacing an item the context element will be the original</li>
		 *    <li>when we are filling a submenuContent the context will be the content's branch</li>
		 *    <li>when we are updating nested branches inside a response the context is a branch or root</li></ul>
		 */
		AbstractMenu.prototype._setMenuItemRole = function(component, contextElement) {
			var _role = this._role.LEAF,
				result = _role.noSelection,
				selectMode,
				branch = contextElement,
				TRANSIENT_SELECTABLE_ATTRIB = "data-wc-selectable",
				isSelectable = component.getAttribute(TRANSIENT_SELECTABLE_ATTRIB),
				FALSE = "false";

			if (isSelectable !== FALSE && !this._isBranch(component)) {
				if (!(this._isBranch(branch) || this.ROOT.isOneOfMe(branch))) {
					branch = this._getBranch(contextElement) || this._getRoot(contextElement);
				}
				selectMode = getSelectMode(branch, isSelectable, _role);
				if (selectMode) {
					result = selectMode;
					component.setAttribute("aria-checked", FALSE);
				}
			}
			component.setAttribute(ROLE_ATTRIB, result);
			component.removeAttribute(TRANSIENT_SELECTABLE_ATTRIB);
		};

		/**
		 * Helper for _setMenuItemRole.
		 * @param {Element} branch Menu branch element.
		 * @param {string} isSelectable "true" or "false"
		 * @param _role LEAF role
		 * @returns {_role.multi|_role.single}
		 * @function
		 * @private
		 */
		function getSelectMode(branch, isSelectable, _role) {
			var result, selectModeAttrib;
			if (branch) {
				selectModeAttrib = branch.getAttribute("data-wc-selectmode");

				if (selectModeAttrib === "single") {
					result = _role.single;
				}
				else if (selectModeAttrib || isSelectable === TRUE) {
					result = _role.multi;
				}
			}
			else if (isSelectable === TRUE) {
				result = _role.multi;
			}
			return result;
		}

		/**
		 * Sets the selected state of a menu descendant which was inserted via AJAX.
		 * @see {@link  module:wc/ui/menu/core~setSelectionStateAfterAjax}
		 * @function
		 * @protected
		 * @param {Element} component The element being inserted.
		 */
		AbstractMenu.prototype._selectAfterAjax = function(component) {
			if (component.getAttribute(TRANSIENT_SELECTED_ATTRIB) === TRUE) {
				this._select(component);
			}
			else if (shed.isSelected(component)) {
				shed.deselect(component, true);  // quietly deselect because this item should never have been selected and no one knows it was yet!!
			}
			component.removeAttribute(TRANSIENT_SELECTED_ATTRIB);
		};

		/**
		 * Adds the close button to the top of each submenu content in a mobile transient sub-menu.
		 *
		 * @see {@link module:wc/ui/menu/bar#updateMenusForMobile}
		 * @see {@link module:wc/ui/menu/column#updateMenusForMobile}
		 * @function
		 * @protected
		 * @param {Element} nextSubmenu The subMenu content wrapper.
		 */
		AbstractMenu.prototype.fixSubMenuContent = function(nextSubmenu) {
			var closeMenuButton = document.createElement(BUTTON),
				opener = nextSubmenu.previousSibling,
				openerContent,
				CLOSER_CLASS = "closesubmenu";

			CLOSE_BUTTON = CLOSE_BUTTON || new Widget(BUTTON, CLOSER_CLASS, {"role": MENUITEM_ROLE});

			if (nextSubmenu.firstChild) {
				if (CLOSE_BUTTON.isOneOfMe(nextSubmenu.firstChild)) {
					return;  // already done
				}
				nextSubmenu.insertBefore(closeMenuButton, nextSubmenu.firstChild);
			}
			else {
				nextSubmenu.appendChild(closeMenuButton);
			}

			closeMenuButton.setAttribute(ROLE_ATTRIB, MENUITEM_ROLE);
			closeMenuButton.type = BUTTON;
			closeMenuButton.className = CLOSER_CLASS + " wc_btn_nada";

			openerContent = opener ? textContent.get(opener) : "";
			closeMenuButton.innerHTML = openerContent || (DEFAULT_CLOSE_LABEL = DEFAULT_CLOSE_LABEL || i18n.get("${wc.ui.menu.bar.i18n.submenuCloseLabelDefault}"));
		};

		/**
		 * Initialisation of menus. If you override this you are responsible for calling it from the subclass, perhaps
		 * like this: `this.constructor.prototype.initialise.call(this, element);`
		 *
		 * @function
		 * @public
		 * @param {Element} element The DOM element being initialised, usually document.body.
		 */
		AbstractMenu.prototype.initialise = function(element) {
			setUpWidgets(this);
			this._setupKeymap();

			if (event.canCapture) {
				event.add(window, event.TYPE.focus, eventWrapper.bind(this), null, null, true);
				event.add(window, event.TYPE.click, eventWrapper.bind(this));
			}
			else {
				// oddly IE8 does not suffer from the body smaller than the viewport issue!
				event.add(element, event.TYPE.focusin, eventWrapper.bind(this));
				event.add(element, event.TYPE.click, eventWrapper.bind(this));
			}
			event.add(element, event.TYPE.keydown, eventWrapper.bind(this));

			if (this.updateMenusForMobile) {
				this.updateMenusForMobile(element);
				processResponse.subscribe(this.updateMenusForMobile.bind(this), true);
			}
			processResponse.subscribe(ajaxSubscriber.bind(this));
			processResponse.subscribe(postAjaxSubscriber.bind(this), true);
			formUpdateManager.subscribe(writeState.bind(this));

			shed.subscribe(shed.actions.SELECT, shedSubscriber.bind(this));
			shed.subscribe(shed.actions.DESELECT, shedSubscriber.bind(this));
			shed.subscribe(shed.actions.EXPAND, shedSubscriber.bind(this));
			shed.subscribe(shed.actions.COLLAPSE, shedSubscriber.bind(this));
			shed.subscribe(shed.actions.HIDE, shedSubscriber.bind(this));
			shed.subscribe(shed.actions.ENABLE, shedSubscriber.bind(this));
			shed.subscribe(shed.actions.DISABLE, shedSubscriber.bind(this));
		};

		/**
		 * Used to clean up hover tests in transient menu unit tests. Public for testing only.
		 * @function
		 * @public
		 * @ignore
		 */
		AbstractMenu.prototype._clearActivateOnHover = function() {
			activateOnHover = null;
		};

		abstractMenu = new AbstractMenu();
		if (typeof Object.freeze !== "undefined") {
			Object.freeze(abstractMenu);
		}
		return abstractMenu;
	});
