define(["wc/dom/attribute",
	"wc/dom/classList",
	"wc/dom/event",
	"wc/dom/focus",
	"wc/dom/formUpdateManager",
	"wc/dom/getFilteredGroup",
	"wc/dom/keyWalker",
	"wc/dom/shed",
	"wc/dom/viewportCollision",
	"wc/dom/Widget",
	"wc/key",
	"wc/ui/ajax/processResponse",
	"wc/timers",
	"wc/i18n/i18n",
	"wc/dom/getBox",
	"wc/array/toArray",
	"wc/ui/viewportUtils"
],
	function(attribute, classList, event, focus, formUpdateManager, getFilteredGroup, keyWalker, shed,
		viewportCollision, Widget, key, processResponse, timers, i18n, getBox, toArray, viewportUtils) {
		"use strict";

		/* NOTE: Many functions in this module are private but accept an instance of a subclass as an argument. These
		 * private functions are ones which either do not need to be overridden in a subclass because they are generic
		 * or, more specifically, which ought not be overridden because they do exactly that which they are supposed.
		 * This use of private functions with a subclass instance argument is merely a trope to work around the lack of
		 * protected and final as in a real language these would be protected final functions but we don't even have a
		 * JSDoc tag for final so we go private for safety. */

		var abstractMenu,
			BOOTSTRAPPED = "wc/ui/menu/bs",
			BOOTSTRAPPED_TRANSIENT = BOOTSTRAPPED + "-transient",
			ROLE_ATTRIB = "role",
			postAjaxTimer,
			focusTimer,
			collisionTimer,
			TRUE = "true",
			fixedWidgets,
			CLASS = {
				COLLIDE_EAST: "wc_coleast",
				COLLIDE_WEST: "wc_colwest",
				COLLIDE_SOUTH: "wc_colsth",
				// no collide north...
				DEFAULT_DIRECTION: null,
				AGAINST_DEFAULT: null,
				CLOSER: "wc_closesubmenu"
			},
			/**
			 * This object is used to map functions to particular event conditions.
			 * @var
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
			activateOnHover,  // used to track the currently open menu to determine whether hover effects are in place
			openMenu = null,  // used in the focusEvent handler to close a menu if it has lost focus;
			BUTTON = "button",
			CLOSE_BUTTON,
			MENUITEM_ROLE = "menuitem",
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
		 * @function
		 * @private
		 * @returns {Object} an object containing properties the values of which are Widgets.
		 */
		function setupFixedWidgets() {
			return {
				TABSTOP: new Widget("", "", { "tabIndex": "0" }),  // used to get the current tabstop in any menu
				GENERIC_ROOT: [new Widget("", "wc-menu"), new Widget("", "", {"role" : "tree"})],
				OFFSCREEN: new Widget("", "wc-off"),
				SUBMENU: new Widget("", "wc_submenucontent")
			};
		}

		/**
		 * Get the object of fixed (non-subclass-specific) {@link module:wc/dom/Widget}s.
		 * @function
		 * @private
		 * @returns {Object} The object containing the fixed widgets.
		 */
		function getFixedWidgets() {
			return fixedWidgets || (fixedWidgets = setupFixedWidgets());
		}

		/**
		 * Get the nearest ancestor menu from a given element.
		 *
		 * @function
		 * @public
		 * @param {Element} element The start point
		 * @returns {Element} The menu root node if any.
		 */
		AbstractMenu.prototype.getFirstMenuAncestor = function (element) {
			return Widget.findAncestor(element, getFixedWidgets().GENERIC_ROOT);
		};

		/**
		 * Sets the tabIndex of the current element and removes it from the previous 'tab-able' element (if different).
		 * @function
		 * @private
		 * @param {Element} element A menu node.
		 * @param {Object} instance The subclass.
		 */
		function setTabstop(element, instance) {
			var oldTabstops,
				root,
				activeElement;
			if ((root = instance.getRoot(element))) {
				if (instance._isBranch(element) && (activeElement = instance._getBranchOpener(element))) {
					element = activeElement;
				}

				oldTabstops = getFixedWidgets().TABSTOP.findDescendants(root);
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
		 * Allows late binding of event listeners to events so that subclasses can override event listeners if they **really** need to. If we didn't
		 * use this mechanism then the superclass events would always be called even if they were overridden.
		 *
		 * @function
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
			} else if (methodName === "focusin") {
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
		 * Indicates if there is a viewport collision on the sides of the viewport.
		 * @function
		 * @private
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
				} else {
					result = isNotDefaultDirection ? (collision.e > 0) : (collision.w < 0);
				}
			}
			return result;
		}

		/**
		 * Mouse over handler. Sets up hover effects when the menu is transoent and not displayed on a mobile device.
		 * This handler is only bound if required when a menu first receives focus and is bound directly to the menu
		 * root. Note, we do not apply hover effects on mobile even though mobile devices may have keyboards and mice
		 * because we restyle transient sub-menus on these devices to improve usability when NOT using a mouse. This
		 * restyle melds better with most mobile OS native menu systems which are full-page per menu level.
		 *
		 * @function
		 * @private
		 * @param {Event} $event the mouseover event wrapped by {@link module:wc/dom/event}.
		 */
		function mouseoverEvent($event) {
			var target = $event.target,
				root,
				item,
				expandable;
			if ($event.defaultPrevented) {
				return;
			}
			if ((root = this.getRoot(target)) && this.isTransient && root === this.getRoot(document.activeElement)/* element root is same as focus root */) {
				item = this.getItem(target);
				if (!item || shed.isDisabled(item)) {
					return;
				}

				this._focusItem(item, root);
				if (activateOnHover === root.id && !viewportUtils.isPhoneLike()) {
					// current menu is active menu
					if (this._isOpener(item)) {
						item = this._getBranch(item);
					}
					if (item && this._isBranch(item) && (expandable = this._getBranchExpandableElement(item)) && !shed.isExpanded(expandable)) {
						this[FUNC_MAP.OPEN](item);
					}
				}
			}
		}

		/**
		 * A TreeWalker filter to get a text node match during key-initiated tree walking.
		 * @function
		 * @protected
		 * @param {Node} textNode The node being tested.
		 * @returns {Number}
		 */
		AbstractMenu.prototype._textMatchFilter = function(textNode) {
			var parent = textNode.parentNode;

			if (shed.isHidden(parent) || shed.hasDisabledAncestor(textNode, parent) || getFixedWidgets().OFFSCREEN.findAncestor(parent)) {
				return NodeFilter.FILTER_REJECT;
			}
			if (textNode.nodeValue) {
				return NodeFilter.FILTER_ACCEPT;
			}
			return NodeFilter.FILTER_SKIP;
		};

		/**
		 * This is a treewalker which is used to test an elements text node descendants. It is used as part of another
		 * treewalker filter. precondition: element has been tested as a potential element match, now we want to know if
		 * its first visible text node starts with a particular letter.
		 *
		 * @function
		 * @private
		 * @param {Element} element The menu node being tested.
		 * @param {String} letter The letter on the key the user pressed.
		 * @returns {Integer} A NodeFilter STATIC variable
		 */
		AbstractMenu.prototype.hasTextNodeMatch = function(element, letter) {
			var tw,
				node,
				textNodeContent,
				result = NodeFilter.FILTER_SKIP;

			LETTER = LETTER || new RegExp(i18n.get("letter"));

			if (letter && LETTER.test(letter)) {
				tw = document.createTreeWalker(element, NodeFilter.SHOW_TEXT, this._textMatchFilter.bind(this), false);
				tw.currentNode = element;
				tw.nextNode();
				node = tw.currentNode;

				if (node && node.nodeType === Node.TEXT_NODE) {
					textNodeContent = node.nodeValue;
					if (textNodeContent.toLocaleUpperCase().indexOf(letter.toLocaleUpperCase()) === 0) {
						result = NodeFilter.FILTER_ACCEPT;
					} else if (textNodeContent) {
						result = NodeFilter.FILTER_REJECT;
					}
				}
			}
			return result;
		};

		/**
		 * Indicates if a particular element is an item in a menu.
		 * @function
		 * @protected
		 * @param {Element} element The element to test.
		 * @returns {Boolean}
		 */
		AbstractMenu.prototype._isItem = function(element) {
			var role,
				o;
			if (this._isBranch(element)) {
				return true;
			}
			role = element.getAttribute(ROLE_ATTRIB);
			if (!role || this._isOpener(element)) {
				return false;
			}
			for (o in this._role.LEAF) {
				if (this._role.LEAF[o] === role) {
					return true;
				}
			}
			return false;
		};

		/**
		 * Get the menu leaf ancestor of a given element.
		 *
		 * @function
		 * @public
		 * @param {Element} element The element we are testing.
		 * @returns {Element} The leaf Element which is or contains element or null if the element is not in a menu
		 *  item.
		 */
		AbstractMenu.prototype.getItem = function(element) {
			if (this.getRoot(element)) {
				if (this._isBranch(element)) {
					return element;
				}
				if (this._isOpener(element)) {
					return this._getBranch(element);
				}
				return Widget.findAncestor(element, this._wd.leaf);
			}
			return null;
		};

		/**
		 * Curry for creating a tree walker filter. This has been split out of _getTreeWalker because we use the same
		 * filter in the keyActivator helper function but it is passed to the instance of
		 * {@link modeule:wc/dom/keyWalker} as part of the config.
		 *
		 * @function
		 * @public
		 * @param {Boolean} ignoreClosed If true we ignore closed branches.
		 * @param {String} [letter] The key literal of a letter key used for jump navigation.
		 * @returns {Function} A TreeWalker filter function.
		 */
		AbstractMenu.prototype.getNavigationTreeWalkerFilter = function(ignoreClosed, letter) {
			var instance = this;

			return function(element) {
				// treeWalker filter function that provides the core Abstract Tree View of the menu
				var branch,
					result = NodeFilter.FILTER_SKIP,
					expandable;

				if (shed.isDisabled(element) || shed.isHidden(element)) {
					result = NodeFilter.FILTER_REJECT;
				} else if (instance._isItem(element)) {
					// branch or leaf
					result = NodeFilter.FILTER_ACCEPT;
				}

				// skip over closed branches
				if (ignoreClosed && result !== NodeFilter.FILTER_REJECT && instance.isSubMenu(element)) {
					if ((branch = instance._getBranch(element))) { // should always be true
						if ((expandable = instance._getBranchExpandableElement(branch)) && !shed.isExpanded(expandable)) {
							result = NodeFilter.FILTER_REJECT;
						}
					} else {
						result = NodeFilter.FILTER_SKIP;
					}
				}
				// finally, if we define a letter we will only have a match if the candidate match also meets the text content match
				if (letter && (result === NodeFilter.FILTER_ACCEPT)) {
					result = instance.hasTextNodeMatch(element, letter);
				}
				return result;
			};
		};

		/**
		 * Gets the keyWalker configuration for a particular menu.
		 * @function
		 * @protected
		 * @param {Element} item An element inside a menu
		 * @param {Element} [root] The menu root if we already have it.
		 * @returns {Object} a keywalker configuration object.
		 */
		AbstractMenu.prototype._getkeyWalkerConfig = function(item, root) {
			var _root = root || this.getRoot(item),
				kwConfig;
			if (_root) {
				kwConfig = {
					root: _root
				};
				kwConfig[keyWalker.OPTIONS.DEPTH_FIRST] = this._treeWalkDepthFirst(_root);
				kwConfig[keyWalker.OPTIONS.CYCLE] = this._cycleSiblings;
			}
			return kwConfig;
		};

		/**
		 * Get the first available menu node with visible text which starts with a particular letter. NOTE: the
		 * WAI-ARIA guidelines for this functionality indicate the NEXT available item so we do not cycle within the
		 * menu even if the menu supports cycling on key nevigation.
		 * @function
		 * @protected
		 * @param {Element} item The menu node on which we started when the user pressed a letter key.
		 * @param {String} letter The letter pressed by the user.
		 * @param {Element} root The current menu root node.
		 * @returns {Element} The next available menu item with visible text which starts with keyName or null if not
		 *    found.
		 */
		AbstractMenu.prototype._getTextTarget = function(item, letter, root) {
			var target = null,
				keyWalkerConfig = this._getkeyWalkerConfig(item, root);
			keyWalkerConfig.filter = this.getNavigationTreeWalkerFilter(true, letter);
			keyWalkerConfig[keyWalker.OPTIONS.CYCLE] = false;  // do not cycle on key match
			target = keyWalker.getTarget(keyWalkerConfig, item, keyWalker.MOVE_TO.NEXT);
			return target;
		};

		/**
		 * Closes an open menu when an element outside of the menu receives focus or is clicked.
		 *
		 * ## Why is his here?
		 *
		 * If a transient menu is inside an element which is able to receive focus (such as a selectable table row) and
		 * the user agent is Chrome (at least since v27, possibly earlier) then a mousedown on a menu item will set
		 * focus to the focusable ancestor which will lead to this code being invoked. This only becomes a problem
		 * because the menu will close before the click event fires and the button will not actually receive the click
		 * (since it is now hidden). So in those cases we wrap the call to closeAllPaths in a timeout to allow the
		 * webkit focus fix to kick in and refocus the original button.
		 *
		 * @function
		 * @private
		 * @param {Element} menu The menu to close.
		 * @param {Element} [element] the element which has caused the menu to close (most commonly by receiving focus). if not set then close all
		 * @param {Object} instance the singleton instance which is the actual menu controller
		 * paths.
		 */
		function closeOpenMenu(menu, element, instance) {
			try {
				if (element === window) {
					instance.closeAllPaths(menu);
				} else if (!element || element.tabIndex >= 0) {
					timers.setTimeout(instance.closeAllPaths.bind(instance), 150, menu, element);
				} else {
					instance.closeAllPaths(menu, element);
				}
			} finally {
				openMenu = null;
				activateOnHover = null;
			}
		}

		/**
		 * Gets an instance of TreeWalker for a particular menu.
		 * @function
		 * @protected
		 * @param {Element} root The root of the tree to be walked.
		 * @param {Boolean} [ignoreClosed] If true we ignore closed branches.
		 * @returns {TreeWalker} A treeWalker for the menu starting at root.
		 */
		AbstractMenu.prototype._getTreeWalker = function(root, ignoreClosed) {
			var filter = this.getNavigationTreeWalkerFilter(ignoreClosed);
			return document.createTreeWalker(root, NodeFilter.SHOW_ELEMENT, filter, false);
		};

		/**
		 * Gets all ancestor menu nodes betweeen two points.
		 * @function
		 * @protected
		 * @param {Element} item A menu node.
		 * @param {Element} from The highest level ancestor in which we are interested (usually the root).
		 * @returns {Element[]} An array of Elements each item in the array being a menu node on the path from the 'from'
		 *    element to the item in question. This will always be at least item itself (if item is root or start).
		 */
		AbstractMenu.prototype._getPathToItem = function(item, from) {
			var tw,
				result = [
					item
				],
				parent;

			tw = this._getTreeWalker(from, false);
			tw.currentNode = item;

			while ((parent = tw.parentNode()) && parent !== from) {
				result[result.length] = parent;
			}
			result[result.length] = from;
			result.reverse();
			return result;
		};

		/**
		 * Closes all paths from the provided node, except the path to the node represented by 'except'.
		 * @function
		 * @public
		 * @param {Element} from DOM node to start closing, usually the menu root.
		 * @param {Element} [except] DOM Node which is exempt from being closed, usually the item/path being opened or
		 *    currently active. If not set then all paths in the menu are closed.
		 */
		AbstractMenu.prototype.closeAllPaths = function(from, except) {
			var exceptPath,
				tw,
				next;
			if (except) {
				exceptPath = this._getPathToItem(except, from);
				if (exceptPath && exceptPath.length) {
					exceptPath.reverse();
					exceptPath.pop();
				}
			}
			tw = this._getTreeWalker(from, true);
			while ((next = tw.nextNode())) {
				if (exceptPath && next === exceptPath[exceptPath.length - 1]) {
					exceptPath.pop();
					continue;
				}
				if (this._isBranch(next)) {
					this[FUNC_MAP.CLOSE](next);
				}
			}
		};

		/**
		 * Get the first item (menu item or submenu or submenu content) in a given menu or branch which is not disabled
		 * or hidden.
		 * @function
		 * @protected
		 * @param {Element} start Any menu node but preferably a submenu wrapper element.
		 * @returns {Element} The first menu item which is not hidden or disabled or in a closed branch.
		 */
		AbstractMenu.prototype._getFirstAvailableItem = function(start) {
			var tw,
				next,
				submenu = start;

			if (!(this.isSubMenu(submenu) || this.getRoot(submenu) === submenu)) {
				submenu = this.getSubMenu(submenu, this._isBranch(submenu));
			}

			if (submenu) {
				tw = this._getTreeWalker(submenu, true);
				tw.currentNode = submenu;
				while ((next = tw.nextNode())) {
					if (this._isItem(next) && !shed.isDisabled(next) && !shed.isHidden(next)) {
						return next;
					}
				}
			}
			return null;
		};

		/**
		 * Disable/enable all menu items in a given branch.
		 * @see {@link module:wc/ui/menu/core~_shedSubscriber}.
		 * @function
		 * @protected
		 * @param {Element} branch The menu branch we need to manipulate.
		 * @param {String} func The name of the {@link wc/dom/shed} function to invoke: either "enable" or "disable".
		 */
		AbstractMenu.prototype._disableInBranch = function(branch, func) {
			var content = this.getSubMenu(branch, true),
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
		};

		/**
		 * Indicates if the parent submenu (if any) of a given submenu is itself colliding with an edge of the viewport.
		 * @function isParentSubmenuColliding
		 * @private
		 * @param {Element} submenu The submenu currently undergoing collision detection
		 * @param {Object} instance The subclass.
		 * @returns {Boolean} true if the parent is also colliding.
		 */
		function isParentSubmenuColliding(submenu, instance) {
			var parent = submenu.parentNode,
				ancestor,
				result = false;

			if (parent && (ancestor = instance.getSubMenu(parent))) {
				result = classList.contains(ancestor, CLASS.DEFAULT_DIRECTION);
			}
			return result;
		}

		/**
		 * Collision detection which supports rtl and ltr opening submenus.
		 * @todo this is now so cumbersome we may be better off just calculating offsets and position the submenu
		 * directly.
		 * @function
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

			CLASS.DEFAULT_DIRECTION = CLASS.DEFAULT_DIRECTION || "wc_col" + i18n.get("menu_popout_direction");
			CLASS.AGAINST_DEFAULT = CLASS.AGAINST_DEFAULT || "wc_col" + i18n.get("menu_popout_reverse");
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
				} else {
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
			if (collisionTimer) {
				timers.clearTimeout(collisionTimer);
				collisionTimer = null;
			}
			collisionTimer = timers.setTimeout(_doCollisionDetection, 0, submenu, instance);
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
			if (element && (root = this.getRoot(element)) && root === this.getFirstMenuAncestor(element)) {
				if (postAjaxTimer) {
					timers.clearTimeout(postAjaxTimer);
					postAjaxTimer = null;
				}
				/* if we have just opened a branch we will need to focus it. We know
				 * we have opened a branch if the submenu content was the ajax target.
				 * as it is not directly targetable by a generic WAjaxControl. We
				 * should do this AFTER making sure we have set all disabled and
				 * selected states as required.*/
				if (this.isSubMenu(element)) {
					if (this.isTransient) {
						doCollisionDetection(element, this);
					}
					if ((subItem = this._getFirstAvailableItem(element))) {
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
							} else {
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
		 *
		 *  * the former default tabstop was the branch opener for the disabled branch and therefore no longer exist as
		 *    we can assume that if the menu does not have a default tabstop that we just disabled it;
		 *  * the former default tabstop the element we just hid;
		 *  * the old tabstop is the element (which means this is running before the SHED disabled helper: **very**
		 *   unlikely); or
		 *  * The old tabstop is inside the branch we just disabled/hid.
		 *
		 * @function
		 * @protected
		 * @param {Element} element The menu item element being hidden or disabled.
		 * @param {Element} root The menu root node.
		 */
		AbstractMenu.prototype._hideDisableHelper = function(element, root) {
			var path,
				newTabStopItem,
				oldTabstop = getFixedWidgets().TABSTOP.findDescendant(root);  // only need the first one from the root

			if (!oldTabstop || oldTabstop === element || (oldTabstop.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINS)) {
				// need to reset the tabIndex to something else
				path = this._getPathToItem(element, root);
				if (path.length <= 2) {
					// path length of 2 means is only root & branch, anything less is an error
					newTabStopItem = this._getFirstAvailableItem(root);
				} else {
					newTabStopItem = path[(path.length - 2)];  // second last item
				}
			}
			// where was focus?
			if (!document.activeElement || document.activeElement === element || document.activeElement === document.body/* ie */ || document.activeElement === document.documentElement/* ie sometimes does this too */ || (document.activeElement.compareDocumentPosition(element) & Node.DOCUMENT_POSITION_CONTAINS)) {
				// oh, it is in the hidden/disabled submenu better reset focus. If we have a new tabstop item, set it to that
				this._focusItem((newTabStopItem || this._getFirstAvailableItem(root)), root);
			} else if (newTabStopItem) {
				setTabstop(newTabStopItem, this);
			}
		};

		/**
		 * Helper for collapsing branches: focuses the branch opener if possble. If the branch opener cannot be focussed
		 * then focus the first tabstop in the menu.
		 *
		 * @function
		 * @protected
		 * @param {Element} item the branch being collapsed
		 * @param {Element} root the root of the menu
		 */
		AbstractMenu.prototype._shedCollapseHelper = function(item, root) {
			var branch = (this._isBranch(item) ? item : (this.isSubMenu(item) ? this._getBranch(item) : null)),
				opener = branch ? this._getBranchOpener(branch) : null;

			if (opener) {
				// if the focus point is inside the branch then refocus to the opener
				if ((opener !== document.activeElement) && (branch.compareDocumentPosition(document.activeElement) & Node.DOCUMENT_POSITION_CONTAINED_BY)) {
					this._focusItem(opener, root);
				} else {
					this._remapKeys(opener);
					// we still have to reset the tabIndex
					setTabstop(opener, this);
				}
			} else {
				focus.focusFirstTabstop(root);
			}
		};

		/**
		 * Helper for shed EXPAND action. The default implementation does collision detection and then attempts to
		 * focus the opened sub-menu.
		 *
		 * @function
		 * @protected
		 * @param {Element} branch The branch being expanded.
		 * @param {type} root The root of the current menu.
		 */
		AbstractMenu.prototype._expand = function(branch, root) {
			var content, subItem;

			if ((content = this.getSubMenu(branch, true))) {
				if (this.isTransient) {
					doCollisionDetection(content, this);
				}
				if ((subItem = this._getFirstAvailableItem(content))) {
					timers.setTimeout(this._focusItem.bind(this), 0, subItem, root);
				}
			}
		};

		/**
		 * Common shed subscriber for SELECT, DESELECT, EXPAND, COLLAPSE, ENABLE, DISABLE and HIDE.
		 * @function
		 * @protected
		 * @see {@link module:wc/dom/shed}
		 * @param {Element} element The SHED target.
		 * @param {String} action The SHED action.
		 */
		AbstractMenu.prototype._shedSubscriber = function(element, action) {
			var root,
				branch;

			if (!(element && (root = this.getRoot(element)))) {
				return;
			}
			if (action === shed.actions.ENABLE || action === shed.actions.DISABLE) {
				this._enableDisable(element, action, root);
				return;
			}
			if (action === shed.actions.HIDE) {
				this._hideDisableHelper(element, root);
				return;
			}
			if (this.isTransient) { // collision detection on branch open
				if (action === shed.actions.EXPAND || action === shed.actions.COLLAPSE) {
					if (this.isSubMenu(element) && (branch = this._getBranch(element))) {
						expandCollapseTransientBranch(branch, action, root, this);
					}
				}
				return;
			}
			if (action === shed.actions.COLLAPSE) {
				this._shedCollapseHelper(element, root);
				return;
			}
			if (action === shed.actions.EXPAND && this._enterOnOpen) {
				if (this._isBranch(element)) {
					this._expand(element, root);
				} else if (this.isSubMenu(element) && (branch = this._getBranch(element))) {
					this._expand(branch, root);
				}
			}
		};

		/*
		 * Helper for _shedSubscriber. Handles opening an d closing of transient menus. Since we split out tree most
		 * menus are transient.
		 * @function
		 * @private
		 * @param {Element} branch The branch being actioned.
		 */
		function expandCollapseTransientBranch(branch, action, root, instance) {
			var // opener,
				content;

			if (action === shed.actions.EXPAND) {
				openMenu = root.id;
				instance._expand(branch, root);
			} else if (action === shed.actions.COLLAPSE && (content = instance.getSubMenu(branch, true))) {
				if (CLASS.DEFAULT_DIRECTION) {
					classList.remove(content, CLASS.DEFAULT_DIRECTION);
					classList.remove(content, CLASS.AGAINST_DEFAULT);
				}
				classList.remove(content, CLASS.COLLIDE_SOUTH);
				content.style.bottom = "";
				content.removeAttribute("style");
				instance._shedCollapseHelper(branch, root);
//				if ((opener = instance._getBranchOpener(branch))) {
//					// if the focus point is inside the branch then refocus to the opener
//					if ((opener !== document.activeElement) && (branch.compareDocumentPosition(document.activeElement) & Node.DOCUMENT_POSITION_CONTAINED_BY)) {
//						instance._focusItem(opener, root);
//					}
//					else {
//						instance._remapKeys(opener);
//						// we still have to reset the tabIndex
//						setTabstop(opener, instance);
//					}
//				}
			}
		}

		/**
		 * Helper for _shedSubscriber. This helper deals with enable/disable actions. The default implementation is for
		 * transient menus and will close a submenu if a branch is disabled.
		 * @function
		 * @protected
		 * @param {Element} element the element we are acting on
		 * @param {String} action the "enable" or "disable" action
		 * @param {Element} root The menu root element. We had to pre-calculate this to get this far so we may as well
		 *  pass it in rather than recalculate it.
		 */
		AbstractMenu.prototype._enableDisable = function(element, action, root) {
			var shedFunc, branch;

			if (this.getRoot(element) !== root) {
				return;
			}
			if (this._isBranch(element)) {
				shedFunc = action === shed.actions.DISABLE ? "disable" : "enable";
				// close the submenu
				if (action === shed.actions.DISABLE && this.isTransient && (branch = this._getBranchExpandableElement(element)) && shed.isExpanded(branch)) {
					shed.collapse(branch); // do not call this[FUNC_MAP.CLOSE] because we don't want all the animate gubbins
				}
				// dis/en-able the opener
				shed[shedFunc](this._getBranchOpener(element));
				// disable or re-enable stuff inside the submenu
				this._disableInBranch(element, shedFunc);
			}
			// branches and items when disabled: may have to change default tabstop
			if (action === shed.actions.DISABLE) {
				this._hideDisableHelper(element, root);
			}
		};

		function writeExpandedState(nextSubmenu, toContainer) {
			var name, branchItem;

			if (this._isBranch(nextSubmenu)) { // tree
				name = nextSubmenu.id;
			} else if (this.isSubMenu(nextSubmenu) && (branchItem = this._getBranch(nextSubmenu))) { // menu
				name = branchItem.id;
			}

			if (name) {
				formUpdateManager.writeStateField(toContainer, name + ".open", TRUE, false, true);
			}
		}

		function writeSelectedState(nextSelectedItem, toContainer) {
			var root = this.getRoot(nextSelectedItem);
			if (root && root === this.getFirstMenuAncestor(nextSelectedItem)) {
				formUpdateManager.writeStateField(toContainer, nextSelectedItem.id + ".selected", "x");
			}
		}

		AbstractMenu.prototype.writeMenuState = function(next, toContainer) {
			if (!next) {
				return; // called from the wrong menu type maybe?
			}
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

			(toArray(this._wd.branch.findDescendants(next))).filter(function(nextBranch) {
				var expandable = this._getBranchExpandableElement(nextBranch);

				if (!expandable) {
					return false;
				}
				return !shed.isDisabled(nextBranch) && shed.isExpanded(expandable);
			}, this).forEach(function(nextSubMenu) {
				writeExpandedState.call(this, nextSubMenu, toContainer);
			}, this);

			Array.prototype.forEach.call(getFilteredGroup(next, {
				ignoreInnerGroups: true
			}), function(NextItem) {
				writeSelectedState.call(this, NextItem, toContainer);
			}, this);
			formUpdateManager.writeStateField(toContainer, next.id + "-h", "x");
		};

		/**
		 * Write the open state of branches in the menu. NOTE: menu item selection is written in the item class.
		 * @function writeState
		 * @private
		 * @param {Element} container the current WComponents form or a subform thereof which is the root for the state evaluation
		 * @param {Element} toContainer the container into which state fields are written
		 */
		AbstractMenu.prototype.writeState = function(container, toContainer) {
			var root;

			try {
				// menus inside the comtainer
				Array.prototype.forEach.call(this.ROOT.findDescendants(container), function(next) {
					this.writeMenuState(next, toContainer);
				}, this);

				// if the container is a menu
				if (this.isRoot(container)) {
					this.writeMenuState(container, toContainer);
				} else if ((root = this.getRoot(container))) { // if the container is a menu item of some kind.
					if (this._isBranch(container) || this._isLeaf(container) || this.isSubMenu(container)) {
						this.writeMenuState(root, toContainer);
					}
				}
			} catch (ex) {
				console.log(ex);
				throw ex;
			}
		};

		/**
		 * Encapsulates core menu functionality but does not implement a functioning menu. Must be extended.
		 * @constructor
		 * @alias module:wc/ui/menu/core~AbstractMenu
		 * @private
		 */
		function AbstractMenu() {
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
		 * @public
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
		 * If set to true the menu will have transient effects: that is, close when it loses focus or activate on hover
		 * and invoke viewport collision. These are all facted of menus which do not have sticky open-ness. Defaults to
		 * true.
		 *
		 * @var
		 * @type boolean
		 * @public
		 */
		AbstractMenu.prototype.isTransient = true;

		/**
		 * Indicates the method for finding "next" and "previous" when tree walking.
		 * If set to true, the tree walker for the menu will look for children before siblings
		 * otherwise it will look for siblings first.
		 *
		 * @function
		 * @protected
		 * @param {Element} element A node in a menu/tree. Not needed by default but mandatory for mixed-mode trees.
		 * @returns {Boolean} true if treeWalker should traverse depth-first. By default always returns false.
		 */
		AbstractMenu.prototype._treeWalkDepthFirst = function(element) {
			if (!element) {
				throw new TypeError("Argument must not be null");
			}
			return false;
		};

		/**
		 * Used when keyboard walking though a menu/submenu. If set to false do not cycle around ends of sibling groups
		 * (going from last to first and vice-versa).
		 *
		 * @var
		 * @type {Boolean}
		 * @protected
		 */
		AbstractMenu.prototype._cycleSiblings = true;

		/**
		 * Indicates that only one unique branch may be open in the tree at any time and all others will be closed
		 * automatically when opening a branch. This is not quite the same as transient as a single opening tree is
		 * possible (think of a horizontal tree like the OS X finder in tree view). It is this bi-modal model for trees
		 * which makes this a function.
		 *
		 * @function
		 * @protected
		 * @param {Element} element An element in a menu and preferably a root node. This allows us to test an
		 *    individual menu/tree if required. Not needed by default but should always be included in calls for those
		 *    occasions where it is needed (e.g. bi-modal trees).
		 * @returns {Boolean} true if only one branch may be open at a time.
		 */
		AbstractMenu.prototype._oneOpen = function(element) {
			if (!element) {
				throw new TypeError("Argument must not be null");
			}
			return true;
		};

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
		 * Does the menu type expect to open when a branch node is selected? This is the case for some trees but not
		 * all.
		 * @function
		 * @protected
		 * @param {Element} element Any element in the menu. Not used in the default implementation but required by TREEs
		 * multiple modes so should always be passed to the function.
		 * @returns {Boolean}
		 */
		AbstractMenu.prototype._openOnSelect = function(element) {
			if (!element) {
				throw new TypeError("Argument must not be null");
			}
			return false;
		};

		/**
		 * Does the menu expect to focus the sub menu when it is opened? By default, yes.
		 * @var
		 * @type boolean
		 * @protected
		 */
		AbstractMenu.prototype._enterOnOpen = true;

		/**
		 * Roles for the parts of the menu which change. Tree menu will over-ride all of these, all other menus will
		 * leave them all or override maybe one (BAR/FLYOUT redefine MENU)
		 * @var
		 * @type {Object}
		 * @protected
		 */
		AbstractMenu.prototype._role = {
			MENU: "menu",
			LEAF: {
				noSelection: MENUITEM_ROLE,
				single: "menuitemradio",
				multi: "menuitemcheckbox"
			}
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
		 * Get the menu root element for the menu in which the passed in element is enclosed. This is fundamental to the
		 * menu abstraction as usually the only way to tell what kind of a menu an element is in is to inspect the root.
		 *
		 * @function
		 * @public
		 * @param {Element} item Any HTML element
		 * @returns {Element} a menu root element if found and if the menu root for the type of menu is the first
		 *    menu root found.
		 */
		AbstractMenu.prototype.getRoot = function(item) {
			var result = this.ROOT.findAncestor(item);
			if (result && result !== this.getFirstMenuAncestor(item)) { // make sure the first generic root is root
				return null;
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
		 * Get the nearest branch element in which the passed in element is enclosed.
		 * @function
		 * @protected
		 * @param {Element} item Any HTML element
		 * @returns {Element} A branch element if found.
		 */
		AbstractMenu.prototype._getBranch = function(item) {
			return this._wd.branch.findAncestor(item);
		};

		/**
		 * Get the menu element which is able to be "aria-expanded". This is the WSubMenu's content in most menus but
		 * is the WSubMenu itself in trees.
		 *
		 * @function
		 * @protected
		 * @param {Element} item The start point for the search. This will normally be a 'branch'.
		 * @returns {Element} The "expandable" element. This is usually the branch content but is the branch in trees.
		 */
		AbstractMenu.prototype._getBranchExpandableElement = function (item) {
			var myBranch;

			if (!item) {
				throw new TypeError("Item must not be undefined.");
			}

			if (this.isSubMenu(item)) {
				return item;
			}

			if (this._isBranch(item)) {
				return this.getSubMenu(item, true);
			}

			if (this._isOpener(item) && (myBranch = this._getBranch(item))) {
				return this.getSubMenu(myBranch, true);
			}

			throw new TypeError("Item must be a branch, submenu or branch opener element.");
		};

		/**
		 * Gets the nearest submenu element relative to a start point in the direction specified.
		 * @function
		 * @public
		 * @param {Element} item Any HTML element.
		 * @param {Boolean} [descending] true to look for a descendant submenu (usually only set when called from a
		 *    branch item)
		 * @param {Boolean} [all] Find all descendants. Not used if descending != true.
		 * @returns {Element} A submenu element if found.
		 */
		AbstractMenu.prototype.getSubMenu = function(item, descending, all) {
			var func;

			if (this.getRoot(item)) {
				if (this.isSubMenu(item) && !(descending && all)) {
					return item;
				}
				func = descending ? ("findDescendant" + (all ? "s" : "")) : "findAncestor";
				return this._wd.submenu[func](item);
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
			var opener;
			if (item) {
				shed[open ? "expand" : "collapse"](item);
				this._remapKeys(item);
				if ((opener = this._getBranch(item)) && (opener = this._getBranchOpener(opener))) {
					shed[open ? "select" : "deselect"](opener);
				}
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
			var root = this.getRoot(element),
				item,
				branchOrContent;
			if (!root) {
				return false;
			}
			item = this.getItem(element);
			if (!item || shed.isDisabled(item)) {
				return false;
			}
			CLOSE_BUTTON = CLOSE_BUTTON || new Widget(BUTTON, CLASS.CLOSER, {"role": "menuitem"});
			if (CLOSE_BUTTON.isOneOfMe(item)) {
				this[FUNC_MAP.CLOSE_MY_BRANCH](item);
				return true;
			}
			if (this._isBranch(item)) {
				// trees: the treeitem gets expanded, menus: the menu gets expanded.
				branchOrContent = this._getBranchExpandableElement(item);
				if (this._animateBranch(branchOrContent, !shed.isExpanded(branchOrContent))) {
					if (this._oneOpen(root)) {
						this.closeAllPaths(root, item);
					}
					this._remapKeys(item);
					return true;
				}
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
				root = this.getRoot(item);
			if (root && this.isTransient && (branch = this[this._FUNC_MAP.CLOSE_MY_BRANCH](item)) && branch.parentNode) {
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

			if ((root = this.getRoot(branch))) { // usual test for "am i in the correct menu module". TODO: Maybe make this a helper...
				// Open branch may be called from an opener button (pretty common actually) so first we need the real branch.
				if ((_branch = this._getBranch(branch)) && (_expandable = this._getBranchExpandableElement(_branch)) && !shed.isExpanded(_expandable)) {
					if (this._oneOpen(root)) {
						this.closeAllPaths(root, branch); // use the original branch
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
			return !!this._wd.opener.findAncestor(element);
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
		 * Is a given element a menu root?
		 * @function
		 * @public
		 * @param {Element} element The element to test.
		 * @returns {Boolean} true if the element is a menu root for the current sub-class.
		 */
		AbstractMenu.prototype.isRoot = function(element) {
			return this.ROOT.isOneOfMe(element);
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
				_item = item,
				expandable;
			// if we simply called closeMyBranch from a 'closed' opener we would end up doing nothing because the
			// opener's parent branch is the branch it is in. So we need to get the branches parent.
			if (this._isBranchOrOpener(_item)) {
				if ((branch = this._getBranch(_item)) && (expandable = this._getBranchExpandableElement(branch)) && !shed.isExpanded(expandable)) {
					_item = branch.parentNode;
				}
			}
			if ((branch = this._getBranch(_item)) && branch !== this.getRoot(_item)) { // do not try to close root!!
				this[FUNC_MAP.CLOSE](branch);
				this._remapKeys(branch);
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
			return this._wd.opener.findDescendant(branch, true);
		};

		/**
		 * Set focus to a menu item after undertaking any necessary menu manipulation. This is the preferred way to set
		 * focus to a menu.
		 *
		 * @function
		 * @protected
		 * @param {Element} _item The menu item to focus.
		 * @param {Element} _root The current menu's root node.
		 * @param {function} [callback] An optional callback function.
		 */
		AbstractMenu.prototype._focusItem = function(_item, _root, callback) {
			var item = _item.id ? document.getElementById(_item.id) : null,
				root = _root.id ? document.getElementById(_root.id) : null,
				extendedCallback;

			if (item && root && (this.getRoot(item) === root) && !shed.isDisabled(item)) {
				extendedCallback = function(withItem) {
					this._remapKeys(withItem);
					if (callback && typeof callback === "function") {
						callback(withItem);
					}
				};

				if (this.isTransient) {
					// Close any open branches except the path to the current item
					this.closeAllPaths(root, item);
				}

				setTabstop(item, this);

				if (focus.canFocus(item)) {
					focus.setFocusRequest(item, extendedCallback.bind(this));
				} else {
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
			return this._wd.branch.isOneOfMe(item);
		};

		/**
		 * Determines if a given HTML element is a leaf node.
		 * @function
		 * @protected
		 * @param {Element} element the HTML element to test
		 * @returns {Boolean} true if the element is a leaf node of a menu
		 */
		AbstractMenu.prototype._isLeaf = function(element) {
			return (this._isItem(element) && !this._isBranch(element));
		};

		/**
		 * Test if an element is a "submenu" node of the current menu/tree type.
		 * @function
		 * @public
		 * @param {Element} element The element to test
		 * @returns {Boolean} true if element is a submenu and not the root.
		 */
		AbstractMenu.prototype.isSubMenu = function(element) {
			if (!element) {
				return null;
			}
			return this._wd.submenu.isOneOfMe(element) && !this.isRoot(element);
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
		 * @returns {Element} Element if a target appropriate to action is found otherwise null.
		 */
		AbstractMenu.prototype._getTargetItem = function(item, action, root, forceCycle) {
			var keyWalkerConfig = this._getkeyWalkerConfig(item, root);
			keyWalkerConfig.filter = this.getNavigationTreeWalkerFilter(true);
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

			LETTER = LETTER || new RegExp(i18n.get("letter"));

			if (action) {
				if (this[action]) {
					return this[action](item);
				}
				target = this._getTargetItem(item, action, root);
			} else if (keyName && (keyName = keyName.replace(/^DOM_VK_/, "")) && keyName.length === 1 && LETTER.test(keyName)) {
				target = this._getTextTarget(item, keyName, root);
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
			root = ((target === window || target === document) ? null : this.getRoot(target));
			genericRoot = ((target === window || target === document) ? null : this.getFirstMenuAncestor(target));
			if (root && (root === genericRoot)) {
				if (!attribute.get(root, BOOTSTRAPPED)) {
					attribute.set(root, BOOTSTRAPPED, true);
					event.add(root, event.TYPE.keydown, eventWrapper.bind(this));
				}
				if (openMenu && (localOpenMenu = document.getElementById(openMenu)) && localOpenMenu !== root) {
					closeOpenMenu(localOpenMenu, target, this);
				}
				if (this.isTransient) {
					if (!attribute.get(root, BOOTSTRAPPED_TRANSIENT)) {
						attribute.set(root, BOOTSTRAPPED_TRANSIENT, true);
						event.add(root, event.TYPE.mouseover, mouseoverEvent.bind(this));
					}
				}
				if ((item = this.getItem(target)) && !shed.isDisabled(item)) {
					this._remapKeys(item);
					if (this._isBranch(item)) {
						item = this._getBranchOpener(item);
					}
					setTabstop(item, this);
				}
			} else if (!genericRoot && openMenu && (localOpenMenu = document.getElementById(openMenu)) && this.isRoot(localOpenMenu)) {  // focus is not in any menu
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
				expandable,
				preventDefault;
			if ($event.defaultPrevented) {
				return;
			}
			try {
				if (target !== window && (root = this.getRoot(target))) {
					if ((item = this.getItem(target)) && !shed.isDisabled(item)) {
						if (openMenu && (localOpenMenu = document.getElementById(openMenu)) && root !== localOpenMenu) {
							// click in inside a different menu, close the previous open menu.
							closeOpenMenu(localOpenMenu, null, this);
						}
						preventDefault = this[FUNC_MAP.ACTION](target);
						if (this.isTransient) {
							if (this._isBranch(item)) {
								expandable = this._getBranchExpandableElement(item);
								activateOnHover = expandable ? (shed.isExpanded(expandable) ? root.id : null) : null;
							} else if (this._isLeaf(item)) {
								closeOpenMenu(root, null, this);
							}
						}
					}
				} else if (openMenu && (localOpenMenu = document.getElementById(openMenu)) && this.isRoot(localOpenMenu)) {
					// click outside any menu we need to close any open transient menu
					closeOpenMenu(localOpenMenu, null, this);
				}
			} catch (ex) {
				$event.preventDefault(); // in case a link or submit was clicked, don't hide/lose the error
				console.error("Uncaught exception in AbstractMenu.onClick: ", ex.message);
			} finally {
				if (preventDefault) {
					$event.preventDefault();
				}
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
				if ((root = this.getRoot($event.target)) && (target = this.getItem($event.target))) {
					result = this._keyActivator(target, $key, root, $event.shiftKey, ($event.ctrlKey || $event.metaKey));

					if (result && ~keysToCancel.indexOf($key)) {
						$event.preventDefault();
					}
				}
			}
		};



		/**
		 * Sets up the subclass specific {@link module:wc/dom/Widget}s used to describe the various parts of the menu.
		 *
		 * @function
		 * @protected
		 */
		AbstractMenu.prototype._setUpWidgets = function() {
			var o,
				leaf = this._role.LEAF;
			// NOTE: the arua-expanded attribute differentiates a sub menu from a column/tree menu's root.
			this._wd.submenu = new Widget("", "", { "role": "menu" , "aria-expanded" : null});
			this._wd.branch = new Widget("", "wc-submenu");
			this._wd.opener = new Widget(BUTTON, "wc-submenu-o");
			this._wd.leaf = [];
			for (o in leaf) {
				if (leaf.hasOwnProperty(o)) {
					this._wd.leaf[this._wd.leaf.length] = new Widget("", "", { "role": leaf[o] });
				}
			}
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
			fixedWidgets = fixedWidgets || setupFixedWidgets();
			this._setUpWidgets();
			this._setupKeymap();

			if (event.canCapture) {
				event.add(window, event.TYPE.focus, eventWrapper.bind(this), null, null, true);
				event.add(window, event.TYPE.click, eventWrapper.bind(this));
			} else {
				// oddly IE8 does not suffer from the body smaller than the viewport issue!
				event.add(element, event.TYPE.focusin, eventWrapper.bind(this));
				event.add(element, event.TYPE.click, eventWrapper.bind(this));
			}
			if (this.preAjaxSubscriber) {
				processResponse.subscribe(this.preAjaxSubscriber.bind(this));
			}
			processResponse.subscribe(postAjaxSubscriber.bind(this), true);
			formUpdateManager.subscribe(this.writeState.bind(this));

			shed.subscribe(shed.actions.SELECT, this._shedSubscriber.bind(this));
			shed.subscribe(shed.actions.DESELECT, this._shedSubscriber.bind(this));
			shed.subscribe(shed.actions.EXPAND, this._shedSubscriber.bind(this));
			shed.subscribe(shed.actions.COLLAPSE, this._shedSubscriber.bind(this));
			shed.subscribe(shed.actions.HIDE, this._shedSubscriber.bind(this));
			shed.subscribe(shed.actions.ENABLE, this._shedSubscriber.bind(this));
			shed.subscribe(shed.actions.DISABLE, this._shedSubscriber.bind(this));
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

		/**
		 * A module representing an abstract menu without any specific implementation. An instance of this class will do nothing.
		 *
		 * @module
		 *
		 * @requires module:wc/dom/attribute
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/focus
		 * @requires module:wc/dom/formUpdateManager
		 * @requires module:wc/dom/getFilteredGroup
		 * @requires module:wc/dom/keyWalker
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/viewportCollision
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/key
		 * @requires module:wc/ui/ajax/processResponse
		 * @requires module:wc/timers
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/dom/getBox
		 * @requires module:wc/array/toArray
		 * @requires module:wc/ui/viewportUtils
		 *
		 * @see {@link module:wc/ui/menu/bar}
		 * @see {@link module:wc/ui/menu/column}
		 * @see {@link module:wc/ui/menu/tree}
		 * @see {@link module:wc/ui/menu/treemenu}
		 */
		return abstractMenu;
	});
