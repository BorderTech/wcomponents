define(["wc/ui/menu/core",
		"wc/dom/keyWalker",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/array/toArray",
		"wc/ui/menu/treeItem",
		"wc/dom/initialise",
		"wc/has",
		"wc/dom/classList",
		"wc/dom/formUpdateManager",
		"wc/dom/getFilteredGroup",
		"wc/ui/ajaxRegion",
		"wc/timers"],
	function(abstractMenu, keyWalker, shed, Widget, toArray, treeItem, initialise, has, classList, formUpdateManager, getFilteredGroup, ajaxRegion, timers) {
		"use strict";

		/**
		 * Extends menu functionality to provide a specific implementation of a tree.
		 * @constructor
		 * @alias module:wc/ui/menu/tree~Tree
		 * @extends module:wc/ui/menu/core~AbstractMenu
		 * @private
		 */
		function Tree() {
			var VOPENER,
				LEAF_WD,
				ajaxTimer;

			if (has("ie") === 8) {
				// IE8 fails to repaint closes in a timely manner if the repainter is not included explicitly.
				require(["wc/fix/inlineBlock_ie8"]);
			}

			/**
			 * Test a tree to determine if it is a HTree.
			 * @function module:wc/ui/menu/tree.isHTree
			 * @public
			 * @param {Element} root the tree's root node.
			 * @returns {Boolean} true if the tree is a htree.
			 */
			this.isHTree = function(root) {
				if (!root) {
					return false;
				}
				return classList.contains(root, "wc_htree");
			};

			/**
			 * Test an element to determine if it is itself or a descendant of the vertical tree opening button.
			 * @function module:wc/ui/menu/tree.isInVOpen
			 * @public
			 * @param {Element} element the element to test
			 * @returns {Boolean} true if element is a vertical tree branch opener or a descendant thereof.
			 */
			this.isInVOpen = function(element) {
				VOPENER = VOPENER || new Widget ("", "wc_leaf_vopener");
				return VOPENER.findAncestor(element);
			};

			/**
			 * The descriptors for this menu type.
			 *
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @protected
			 * @override
			 */
			this._wd = {};

			/**
			 * The descriptions of the ROOT node of a tree menu.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ROOT = new Widget("", "", {role: "tree"});

			/**
			 * Roles for the parts of the tree.
			 *
			 * @var
			 * @type {Object}
			 * @protected
			 * @override
			 */
			this._role = {
				MENU: "tree",
				LEAF: {noSelection: "treeitem"}
			};

			/**
			 * A helper to do strict-ish type checking on getting a tree's root element.
			 * @function
			 * @private
			 * @param {Element} element The element from which to start searching for the tree root.
			 * @returns {?Element} A tree root node.
			 */
			function getRootHelper(element) {
				var _root;

				if (!element) {
					throw new ReferenceError("Argument 'element' is required.");
				}

				_root = instance.isRoot(element) ? element : instance.getRoot(element);
				if (!_root) {
					throw new TypeError("Argument is not in a tree node.");
				}
				return _root;
			}

			/**
			 * Indicates if  a particular tree supports multiple open branches. Vertical trees allow multiple branches
			 * to be open at any time. Horizontal trees do not.
			 *
			 * @function module:wc/ui/menu/tree._oneOpen
			 * @protected
			 * @override
			 * @param {Element} element A node of the tree to test. This is mandatory in this override.
			 * @returns {Boolean} true if only one branch may be open at a time.
			 */
			this._oneOpen = function(element) {
				var _root = getRootHelper(element);
				return this.isHTree(_root);
			};

			/**
			 * Trees do not require a branch item to be seleted when a branch is opened.
			 *
			 * @var
			 * @type Boolean
			 * @protected
			 * @override
			 */
			this._enterOnOpen = false;

			/**
			 * When keyboard navigating a tree we go into open submenus before going to the next option at the current
			 * level.
			 *
			 * @function module:wc/ui/menu/tree._treeWalkDepthFirst
			 * @protected
			 * @override
			 * @param {Element} root A node of the tree to test. This is mandatory in this override.
			 * @returns {Boolean} true if treeWalker should traverse depth first.
			 */
			this._treeWalkDepthFirst = function(root) {
				return !this.isHTree(root); // horizontal trees are never depth-first.
			};

			/**
			 * Does the tree open when a branch item is selected?
			 * @function module:wc/ui/menu/tree._openOnSelect
			 * @protected
			 * @override
			 * @param {Element} root the root element of a tree.
			 * @returns {Boolean} true if the tree opens a branch when it is selected.
			 */
			this._openOnSelect = function(root) {
				return this.isHTree(root);
			};

			/**
			 * Trees do not cycle siblings.
			 *
			 * @var
			 * @type {Boolean}
			 * @protected
			 * @override
			 */
			this._cycleSiblings = false;

			/**
			 * Trees are not transient.
			 *
			 * @var
			 * @type boolean
			 * @override
			 * @public
			 */
			this.isTransient = false;

			/**
			 * Trees automatically select selectable treeitems on navigation.
			 *
			 * @see {@link http://www.w3.org/TR/wai-aria-practices/#TreeView}
			 * @var
			 * @type {Boolean}
			 * @protected
			 * @override
			 * @default true
			 */
			this._selectOnNavigate = true;

			/**
			 * Selection in trees is complicated as it depends on the presence of absence of chordal key strokes during
			 * selection.
			 *
			 * @see {@link http://www.w3.org/TR/wai-aria-practices/#TreeView}
			 * @function module:wc/ui/menu/tree._select
			 * @protected
			 * @override
			 * @param {Element} item The menu item being selected
			 * @param {boolean} [silent] If true do not publish the selection.
			 * @param {boolean} [SHIFT] Indicates the SHIFT key was pressed when the selection was made which causes the
			 *     selection of all items between the last selection and current selection to be selected.
			 * @param {boolean} [CTRL] Indicates the CTRL (or META) key was pressed when the selection was made so as to
			 *     create a (potentially) non-contiguous multiple selection (where allowed).
			 */
			this._select = function(item, silent, SHIFT, CTRL) {
				var root = this.getRoot(item);
				if (root && root.getAttribute("aria-multiselectable")) {
					if (silent) {
						shed.select(item, silent);
					}
					else {
						treeItem.activate(item, SHIFT, CTRL);
					}
				}
			};

			/**
			 * Resets this._keyMap based on the type and/or state of the menu item passed in. In the top level the left
			 * and right go to siblings and down goes to child in sub menus up and down go to siblings, right to child
			 * and left to parent.
			 *
			 * @function module:wc/ui/menu/tree._remapKeys
			 * @protected
			 * @override
			 * @param {Element} _item The item which has focus.
			 */
			this._remapKeys = function(_item) {
				var isOpener,
					item = _item,
					root = this.getRoot(item),
					VK_LEFT = "DOM_VK_LEFT",
					VK_RIGHT = "DOM_VK_RIGHT",
					VK_RETURN = "DOM_VK_RETURN",
					VK_SPACE = "DOM_VK_SPACE",
					isHTree,
					expandable;

				if (!root) {
					return;
				}
				isHTree = this.isHTree(root);

				this._keyMap[VK_RETURN] = null;
				this._keyMap[VK_SPACE] = null;
				this._keyMap[VK_LEFT] = keyWalker.MOVE_TO.PARENT;
				this._keyMap[VK_RIGHT] = null;

				if (this._isBranch(item) || (isOpener = this._isOpener(item))) {
					if (isOpener) {
						item = this._getBranch(item);
					}

					if (item) {
						if (!this.isHTree(root)) {
							this._keyMap[VK_RETURN] = this._FUNC_MAP.ACTION;
							this._keyMap[VK_SPACE] = this._FUNC_MAP.ACTION;
						}
						expandable = this._getBranchExpandableElement(item);

						if (expandable && shed.isExpanded(expandable)) {
							if (isHTree) {
								this._keyMap[VK_RIGHT] = keyWalker.MOVE_TO.CHILD;
								this._keyMap[VK_LEFT] = keyWalker.MOVE_TO.PARENT;
							}
							else {
								this._keyMap[VK_LEFT] = this._FUNC_MAP.CLOSE;
							}
						}
						else {
							this._keyMap[VK_RIGHT] = this._FUNC_MAP.ACTION;
							this._keyMap[VK_LEFT] = keyWalker.MOVE_TO.PARENT;
						}
					}
				}
			};

			/**
			 * Sets up the initial keymap for tree menus.
			 *
			 * @see {@link http://www.w3.org/TR/wai-aria-practices/#TreeView}
			 * @function module:wc/ui/menu/tree._setupKeymap
			 * @protected
			 * @override
			 */
			this._setupKeymap = function() {
				this._keyMap = {
					DOM_VK_UP: keyWalker.MOVE_TO.PREVIOUS,
					DOM_VK_DOWN: keyWalker.MOVE_TO.NEXT,
					DOM_VK_LEFT: keyWalker.MOVE_TO.PARENT,
					DOM_VK_HOME: keyWalker.MOVE_TO.TOP,
					DOM_VK_PAGE_DOWN: keyWalker.MOVE_TO.END,
					DOM_VK_END: keyWalker.MOVE_TO.END,
					DOM_VK_MULTIPLY: "_openAllBranches"
				};
			};

			/**
			 * Set up the Widgets which describe a tree.
			 * @function module:wc/ui/menu/tree._setUpWidgets
			 * @protected
			 * @override
			 */
			this._setUpWidgets = function() {
				var opener = new Widget("button", "", { "aria-controls": null });

				LEAF_WD = LEAF_WD || new Widget("", "", { "role": "treeitem" });
				this._wd.submenu = new Widget("", "", { "role": "group" });
				this._wd.branch = LEAF_WD.extend("", { "aria-expanded": null });
				opener.descendFrom(this._wd.branch, true);
				this._wd.opener = opener;
				this._wd.leaf = [LEAF_WD];
			};

			/**
			 * Opens all branches in a menu. Note: this has to be public because super._keyActivator() needs to know it
			 * exists.
			 *
			 * @function module:wc/ui/menu/tree._openAllBranches
			 * @protected
			 * @param {Element} from the start point for opening all branches
			 */
			this._openAllBranches = function(from) {
				var root = this.getRoot(from),
					allBranchOpeners;
				if (!root || this.isHTree(root)) {
					return;
				}

				if ((allBranchOpeners = this._wd.opener.findDescendants(root)) && allBranchOpeners.length) {
					/* NOTE: Array.prototype.reverse.call does not work in IE8 so I have to convert the nodeList to a real array then reverse it */
					allBranchOpeners = toArray(allBranchOpeners);
					allBranchOpeners.reverse();
					allBranchOpeners.forEach(this[this._FUNC_MAP.OPEN], this);
				}
			};

			/**
			 * No op.
			 * @function module:wc/ui/menu/tree._setMenuItemRole
			 * @protected
			 * @override
			 */
			this._setMenuItemRole = null;

			/**
			 * No op.
			 *
			 * @function module:wc/ui/menu/tree._selectAfterAjax
			 * @protected
			 * @override
			 */
			this._selectAfterAjax = null;

			/**
			 * Get the menu element which is able to be "aria-expanded". This is the WSubMenu's content in most menus but is the WSubMenu itself in
			 * trees.
			 *
			 * @function module:wc/ui/menu/tree._getBranchExpandableElement
			 * @override
			 * @param {Element} item The start point for the search. This will normally be a 'branch'.
			 * @returns {?Element} The "expandable" element. This is usually the branch content but is the branch in trees.
			 */
			this._getBranchExpandableElement = function (item) {
				if (!item) {
					throw new TypeError("Item must be defined.");
				}

				if (this._isBranch(item)) {
					return item;
				}

				if (this.isSubMenu(item) || this._isOpener(item)) {
					return this._getBranch(item);
				}

				throw new TypeError("Item must be a branch, submenu or branch opener element.");
			};

			/**
			 * Click with meta on an open branch in a htree. If the branch has an ancestor branch then select that ancestor, otherwise just delected
			 * the branch.
			 * @function
			 * @private
			 * @param {Element} target theelement clicked.
			 */
			function htreeClickHelper(target) {
				var item = instance.getItem(target), parentBranch;
				if (item && instance._isBranch(item) && shed.isExpanded(item)) {
					if ((parentBranch = instance.getSubMenu(item)) && (parentBranch = instance._getBranch(parentBranch))) {
						instance._select(parentBranch, false, false, true);
						return;
					}
					shed.deselect(item);
					shed.collapse(item);
				}
			}

			/**
			 * Click handler override. Do not allow click to toggle tree branch unless it is a htree or on the 'vertical' opener.
			 *
			 * @function module:wc/ui/menu/tree.clickEvent
			 * @public
			 * @override
			 * @param {Event} $event The wrapped click event.
			 */
			this.clickEvent = function($event) {
				var target = $event.target,
					root;
				// target === window is an IE thing
				if ($event.defaultPrevented || target === document.body || target === window || !(root = this.getRoot(target))) {
					return;
				}

				if (this.isHTree(root)) { // htree completely driven by select.
					if ($event.ctrlKey || $event.metaKey) {
						htreeClickHelper(target);
					}
					return;
				}

				if (!this.isInVOpen(target)) {
					return; // do nothing, do not prevent default, do not pass go.
				}
				this.constructor.prototype.clickEvent.call(this, $event);
			};

			/**
			 * Write the state of WTree.
			 *
			 * @function module:wc/ui/menu/tree.writeMenuState
			 * @protected
			 * @override
			 * @param {Element} next the WTree root element
			 * @param {Element} toContainer the state container
			 */
			this.writeMenuState = function(next, toContainer) {
				var root, rootId;

				if (!next) {
					return; // called from the wrong menu type maybe?
				}

				root = this.getRoot(next);

				if (!root) {
					return;
				}

				rootId = root.id;

				// expanded branches
				(toArray(this._wd.branch.findDescendants(next))).filter(function(nextBranch) {
					var expandable = this._getBranchExpandableElement(nextBranch);
					if (!expandable) {
						return false;
					}
					return !shed.isDisabled(nextBranch) && shed.isExpanded(expandable);
				}, this).forEach(function(nextBranch) {
					var name;

					if (this._isBranch(nextBranch)) { // tree
						name = nextBranch.id;
					}

					if (name) {
						formUpdateManager.writeStateField(toContainer, rootId + ".open", name);
					}
				}, this);

				// selected tree items (would prefer to be devolved to tree item but that just ain't possible ...)
				Array.prototype.forEach.call(getFilteredGroup(next, {
					filter: (getFilteredGroup.FILTERS.selected | getFilteredGroup.FILTERS.enabled),
					ignoreInnerGroups: true
				}), function(nextSelectedItem) {
					formUpdateManager.writeStateField(toContainer, rootId, nextSelectedItem.id);
				}, this);
				formUpdateManager.writeStateField(toContainer, rootId + "-h", "x");
			};

			/**
			 * Determines if a given element is the last selected item at its level of the tree.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element being tested.
			 * @param {Element} root The root of the current tree.
			 * @returns {Boolean} true if the element is the only selected item at its level.
			 */
			function isLastSelectedItemAtLevel(element, root) {
				var level = instance.getSubMenu(element) || ((root && instance.isRoot(root)) ? root : instance.getRoot(element));

				return getFilteredGroup(level, {
					itemWd: LEAF_WD
				}).length === (shed.isSelected(element) ? 1 : 0);
			}

			/**
			 * Helper for _shedSubscriber which undertakes an ajax loadwhen a branch is opened if required.
			 *
			 * @function
			 * @private
			 * @param {Element} element The branch being opened.
			 * @param {Element} root The root of the currect tree.
			 */
			function ajaxExpand(element, root) {
				var mode = root.getAttribute("data-wc-mode"),
					obj,
					elId = element.id;

				if (mode && mode !== "client") {
					obj = {
						id: elId,
						alias: root.id,
						loads: [elId + "-content"],
						oneShot: (mode === "lazy" ? 1 : -1),
						getData: "wc_tiid=" + elId,
						serialiseForm: true,
						method: "get",
						formRegion: root.id
					};

					ajaxRegion.requestLoad(element, obj, true);
				}
			}

			/**
			 * Override {@link:module:wc/dom/shed} subscriber to add special cases for trees.
			 *
			 * @function module:wc/ui/menu/tree._shedSubscriber
			 * @protected
			 * @override
			 * @param {Element} element The element being acted upon.
			 * @param {String} action The action being taken.
			 */
			this._shedSubscriber = function(element, action) {
				var root;

				if (!(element && (root = this.getRoot(element)))) {
					return;
				}

				if (action === shed.actions.SELECT || action === shed.actions.DESELECT) {
					if (root.getAttribute("data-wc-mode")) {
						if (ajaxTimer) {
							timers.clearTimeout(ajaxTimer);
							ajaxTimer = null;
						}
						ajaxTimer = timers.setTimeout(ajaxRegion.requestLoad, 0, root);
					}
					if (this.isHTree(root) && action === shed.actions.SELECT) {
						if (this._isBranch(element) && !shed.isExpanded(element)) {
							this[this._FUNC_MAP.OPEN](element);
						}
						else {
							this.closeAllPaths(root, element);
						}
					}
					return;
				}

				if (action === shed.actions.EXPAND) {
					this.constructor.prototype._shedSubscriber.call(this, element, action);
					ajaxExpand(element, root);
					return;
				}

				this.constructor.prototype._shedSubscriber.call(this, element, action);
			};

			/**
			 * Override the default "animator" to prevent a branch from opening if any other element is selected at its level. Only applies to htree.
			 *
			 * @function module:wc/ui/menu/tree._animateBranch
			 * @protected
			 * @param {Object} item The branch being opened/closed.
			 * @param {Object} open If true branch is being opened, otherwise its being closed.
			 * @returns {Boolean} true if the branch is able to animate.
			 */
			this._animateBranch = function(item, open) {
				var root = this.getRoot(item);

				if (!(item && (root = this.getRoot(item)))) {
					return false;
				}

				if (open && this.isHTree(root)) {
					if (isLastSelectedItemAtLevel(item, root)) {
						shed.expand(item);
						this._remapKeys(item);
					}
					return true;
				}

				shed[open ? "expand" : "collapse"](item);
				this._remapKeys(item);
				return true;
			};

			/**
			 * A TreeWalker filter to get a text node match during key-initiated tree walking.
			 * @function module:wc/ui/menu/tree._textMatchFilter
			 * @protected
			 * @override
			 * @param {Node} textNode The node being tested.
			 * @returns {Number}
			 */
			this._textMatchFilter = function(textNode) {
				var parent = textNode.parentNode;

				if (!classList.contains(parent, "wc_leaf_name")) {
					return  NodeFilter.FILTER_SKIP;
				}

				if (textNode.nodeValue) {
					return NodeFilter.FILTER_ACCEPT;
				}

				return NodeFilter.FILTER_SKIP;
			};

			/**
			 * Helper for shed collapse subscriber. This function is concerned with deselecting items in collapsing branches and possibly selecting
			 * the collapsing branches nearest available ancestor (depending on tree type).
			 *
			 * @function module:wc/ui/menu/tree._shedCollapseHelper
			 * @protected
			 * @override
			 * @param {Element} element the branch beng collapsed.
			 * @param {Element} [root] the tree's root
			 */
			this._shedCollapseHelper = function (element, root) {
				var group,
					groupContainer,
					_root = root || this.getRoot(element);

				if (!_root) {
					return;
				}

				if (element && this._isBranch(element)) {
					groupContainer = this.getSubMenu(element, true);
					if (groupContainer && (group = getFilteredGroup(groupContainer, {itemWd: this._wd.leaf[0]})) && group.length) {
						group.forEach(function(next) {
							shed.deselect(next);
						});
						if (!this.isHTree(_root)) {
							shed.select(element);
						}
					}
				}
			};

			/**
			 * Reset selections after Ajax.
			 *
			 * @function module:wc/ui/menu/tree._ajaxSubscriber
			 * @protected
			 *
			 * @param {Element} element the ajax target.
			 * @param {DocumentFragment} documentFragment the content of the response
			 */
			this._ajaxSubscriber = function (element, documentFragment/* , action */) {
				if (element && this.getRoot(element) === this.getFirstMenuAncestor(element)) {
					Array.prototype.forEach.call(this._wd.branch.findDescendants(documentFragment), function(next) {
						var id, _el;
						if ((id = next.id) && (_el = document.getElementById(id))) {
							if (shed.isSelected(_el)) {
								shed.select(next, true);
							}
						}
					}, this);
				}
			};
		}

		/**
		 * Menu controller extension for WTree. WTree uses the menu controller because it has the same key-walking, brancho
		 * opening, selection and activation mechanisms.
		 *
		 * @see <a href="http://www.w3.org/TR/wai-aria-practices/#TreeView">TreeView</a>
		 *
		 * @module
		 * @extends module:wc/ui/menu/core
		 *
		 * @requires module:wc/ui/menu/core
		 * @requires module:wc/dom/keyWalker
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/array/toArray
		 * @requires module:wc/ui/menu/treeItem
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/has
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/formUpdateManager
		 * @requires module:wc/dom/getFilteredGroup
		 * @requires module:wc/ui/ajaxRegion
		 * @requires module:wc/timers
		 */
		var instance;
		Tree.prototype = abstractMenu;
		instance = new Tree();
		instance.constructor = Tree;
		initialise.register(instance);
		return instance;
	});
