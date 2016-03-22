/**
 * Menu controller extension for WMenu of type TREE. Provides a tree-like menu structure.
 *
 * @see {@link http://www.w3.org/TR/wai-aria-practices/#TreeView}
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
 */
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
	/** @param abstractMenu @param keyWalker @param shed @param Widget @param toArray  @param treeItem @param initialise @param has s @param classList @param formUpdateManager @param getFilteredGroup @param ajaxRegion @param timers @ignore */
	function(abstractMenu, keyWalker, shed, Widget, toArray, treeItem, initialise, has, classList, formUpdateManager, getFilteredGroup, ajaxRegion, timers) {
		"use strict";

		/**
		 * Extends menu functionality to provide a specific implementation of a tree.
		 * @constructor
		 * @alias module:wc/ui/menu/tree~Tree
		 * @extends module:wc/ui/menu/core~AbstractMenu
		 * @private */
		function Tree() {
			var SUBMENU_CONTENT,
				DUMMY_BRANCH,
				VOPENER,
				LEAF_WD,
				ajaxTimer;

			if (has("ie") === 8) {
				// IE8 fails to repaint tree branch closes in a timely manner when closing if the repainter is not included explicitly.
				require(["wc/fix/inlineBlock_ie8"]);
			}

			function isWMenu(root) {
				return classList.contains(root, "wc-menu");
			}

			this.isHTree = function(root) {
				return classList.contains(root, "wc_htree");
			};

			this.isHTreeOrMenu = function(root) {
				return this.isHTree(root) || isWMenu(root);
			};


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
			 *
			 * @param {Element} element The element from which to start searching for the tree root.
			 * @returns {?Element} A tree root node.
			 */
			function getRootHelper(element) {
				var _root;

				if (!element) {
					throw new ReferenceError("Argument 'root' is required.");
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
			 * @function
			 * @protected
			 * @override
			 * @param {Element} element A node of the tree to test. This is mandatory in this override.
			 * @returns {Boolean} true if only one branch may be open at a time.
			 */
			this._oneOpen = function(element) {
				var _root = getRootHelper(element);

				if (isWMenu(_root)) {
					return false; // WMenu trees are always vertical and always multi-openable.
				}

				return this.isHTree(_root);
			};


			/**
			 * Trees do not require a branch item to be seleted when a branch is opened.
			 *
			 * @function
			 * @protected
			 * @override
			 * @returns {Boolean} false for all trees.
			 */
			this.enterOnOpen = function() {
				return false;
			};

			/**
			 * When keyboard navigating a tree we go into open submenus before going to the next option at the current
			 * level.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} root A node of the tree to test. This is mandatory in this override.
			 * @returns {Boolean} true if treeWalker should traverse depth first.
			 */
			this._treeWalkDepthFirst = function(root) {
				var _root = getRootHelper(root);

				if (isWMenu(_root)) {
					return true; // WMenu trees are always vertical and always depth-first.
				}

				return !this.isHTree(root); // horizontal trees are never depth-first.
			};

			this._openOnSelect = function(root) {
				var _root = getRootHelper(root);

				if (isWMenu(_root)) {
					return false; // WMenu trees are always vertical and always multi-openable.
				}

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
			 * @function
			 * @public
			 * @return {boolean} true if the current menu has transient sub-menu artefacts.
			 */
			this.isTransient = function() {
				return false;
			};

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
			 * @function
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
			 * Fix the role and other attributes in a submenu which has been inserted via AJAX. We default all the XSLT
			 * to menu-like properties if we do not have the top level menu context because menus are more common than
			 * trees.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {DocumentFragment} container The documentFragment returned from the ajax subsystem. This is
			 * a submenu.
			 */
			this._fixSubmenuContentInAjaxResponse = function(container) {
				var EXP_ATTRIB = "aria-expanded";
				// generic (role-less) submenu content container is needed because the role is still incorrect
				SUBMENU_CONTENT = SUBMENU_CONTENT || new Widget("", "wc_submenucontent");
				DUMMY_BRANCH = DUMMY_BRANCH || new Widget("", "wc-submenu");

				Array.prototype.forEach.call(SUBMENU_CONTENT.findDescendants(container), function (nextSubmenuContent) {
					var branch, opener, isOpen;

					nextSubmenuContent.setAttribute("role", "group");
					isOpen = nextSubmenuContent.getAttribute(EXP_ATTRIB) || "false"; // we need this for the branch ...
					nextSubmenuContent.removeAttribute(EXP_ATTRIB);

					// now fix up the branch container
					if ((branch = DUMMY_BRANCH.findAncestor(nextSubmenuContent))) {
						branch.removeAttribute("data-wc-selectmode");
						if ((opener = this._getBranchOpener(branch))) {
							opener.removeAttribute("aria-haspopup");
							opener.removeAttribute("role");
						}
						if (!branch.getAttribute(EXP_ATTRIB)) {
							branch.setAttribute(EXP_ATTRIB, isOpen);
						}
					}
				}, this);
			};

			/**
			 * "Animation" of open/close branch. This needs to explicitly remap keys after open.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Object} item The branch being opened/closed.
			 * @param {Object} [open] If true branch is being opened, otherwise its being closed.
			 * @returns {Boolean} true if any non-false-equivalent value for item is passed in.
			 */
			this._animateBranch = function(item, open) {
				var result = this.constructor.prototype._animateBranch.call(this, item, open);
				this._remapKeys(item);
				return result;
			};

			/**
			 * Resets this._keyMap based on the type and/or state of the menu item passed in. In the top level the left
			 * and right go to siblings and down goes to child in sub menus up and down go to siblings, right to child
			 * and left to parent.
			 *
			 * @function
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
					isHTree;// = this.isHTree(root);

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
						if (!this.isHTreeOrMenu(root)) {
							this._keyMap[VK_RETURN] = this._FUNC_MAP.ACTION;
							this._keyMap[VK_SPACE] = this._FUNC_MAP.ACTION;
						}

						if (shed.isExpanded(this._getBranchExpandableElement(item))) {
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
			 * @function
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
			 * @function
			 * @protected
			 * @param {Element} from the start point for opening all branches
			 */
			this._openAllBranches = function(from) {
				var root, allBranchOpeners;
				if ((root = this.getRoot(from)) && (allBranchOpeners = this._wd.opener.findDescendants(root)) && allBranchOpeners.length) {
					/* NOTE: Array.prototype.reverse.call does not work in IE8 so I have
					 * to convert the nodeList to a real array then reverse it*/
					allBranchOpeners = toArray(allBranchOpeners);
					allBranchOpeners.reverse();
					allBranchOpeners.forEach(this[this._FUNC_MAP.OPEN], this);
				}
			};

			/**
			 * Pre-insertion ajax subscriber helper used to set the correct role for tree items.
			 *
			 * @protected
			 * @override
			 * @param {Element} component The component which was brought in with AJAX.
			 */
			this._setMenuItemRole = function(component) {
				var opener;
				component.setAttribute("role", this._role.LEAF.noSelection);
				component.removeAttribute("data-wc-selectable");

				if (this._isBranch(component) && (opener = this._getBranchOpener(component))) {
					opener.removeAttribute("role");
					opener.removeAttribute("aria-haspopup");
				}
			};

			/**
			 * Resets any seleted state after an item is replaced via ajax.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} component The component which was brought in with AJAX.
			 */
			this._selectAfterAjax = function(component) {
				if (this._isLeaf(component)) {
					this.constructor.prototype._selectAfterAjax.call(this, component);
				}
				else {
					component.removeAttribute("data-wc-selected");
				}
			};

			/**
			 * Get the menu element which is able to be "aria-expanded". This is the WSubMenu's content in most menus but
			 * is the WSubMenu itself in trees.
			 *
			 * @function
			 * @override
			 * @param {Element} item The start point for the search. This will normally be a 'branch'.
			 * @returns {?Element} The "expandable" element. This is usually the branch content but is the branch in trees.
			 */
			this._getBranchExpandableElement = function (item) {
				var myBranch;

				if (!item) {
					throw new TypeError("Item must not be falsey.");
				}

				if (this._isBranch(item)) {
					return item;
				}

				if ((this.isSubMenu(item) || this._isOpener(item)) && (myBranch = this._getBranch(item))) {
					return myBranch;
				}

				throw new TypeError("Item must be a branch, submenu or branch opener element.");
			};

			/**
			 * Click handler override. Do not allow click to toggle tree branch unless it is:
			 *
			 *   * a htree; or
			 *   * on the 'vertical' opener.
			 *
			 * We exempt the old WMenu version of TREE from this.
			 *
			 * @override
			 * @param {Event} $event The wrapped click event.
			 */
			this.clickEvent = function($event) {
				var target = $event.target,
					root;
				if ($event.defaultPrevented || target === window) {
					return;
				}

				if ((root = this.getRoot(target))) {
					if (isWMenu(root)) {
						this.constructor.prototype.clickEvent.call(this, $event);
						return;
					}

					if (this.isHTree(root)) { // htree completely driven by select.
						return;
					}

					if (!this.isInVOpen(target)) {
						return; // do nothing, do not prevent default, do not pass go.
					}
				}
				// if we get here things are odd....
				this.constructor.prototype.clickEvent.call(this, $event);
			};

			this.writeMenuState = function(next, toContainer) {
				var root, rootId;

				if (!next) {
					return; // called from the wrong menu type maybe?
				}

				root = this.getRoot(next);

				if (!root) {
					return;
				}
				if (isWMenu(root)) { // WMenu trees expect different POST values from WTree trees. Greate eh?
					this.constructor.prototype.writeMenuState.call(this, next, toContainer);
					return;
				}

				// now for the WTree trees ...
				rootId = root.id;

				// expanded branches
				(toArray(this._wd.branch.findDescendants(next))).filter(function(nextBranch) {
					return !shed.isDisabled(nextBranch) && shed.isExpanded(this._getBranchExpandableElement(nextBranch));
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
			 * Helper for shedSubscriber which undertakes an ajax loadwhen a branch is opened if required.
			 *
			 * @param {Element} element The branch being opened.
			 * @param {Element} root The root of the currect tree.
			 */
			this.ajaxExpand = function(element, root) {
				var mode = root.getAttribute("data-wc-ajaxmode"),
					obj,
					elId = element.id;

				if (mode && mode !== "client") {
					obj = {
						id: elId,
						alias: root.id,
						loads: [elId + "-content"],
						oneShot: (mode === "lazy"),
						getData: "wc_tiid=" + elId,
						serialiseForm: false,
						method: "get",
						formRegion: root.id
					};

					ajaxRegion.requestLoad(element, obj, true);
				}
			};

			/**
			 * Override {@link:module:wc/dom/shed} subscriber to add special cases for trees.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} element The element being acted upon.
			 * @param {String} action The action being taken.
			 */
			this.shedSubscriber = function(element, action) {
				var root;

				if (!element || !(root = this.getRoot(element)) || isWMenu(root)) {
					// we are only concerned with htree here. Vertical trees are fine.
					this.constructor.prototype.shedSubscriber.call(this, element, action);
					return;
				}

				if (action === shed.actions.SELECT || action === shed.actions.DESELECT) {
					if (root.getAttribute("data-wc-ajaxalias")) {
						if (ajaxTimer) {
							timers.clearTimeout(ajaxTimer);
							ajaxTimer = null;
						}
						ajaxTimer = timers.setTimeout(ajaxRegion.requestLoad, 0, root);
					}
					// do not return, we have more to do.
				}

				if (this.isHTree(root) && action === shed.actions.SELECT) {
					if (this._isBranch(element) && this._openOnSelect(root) && !shed.isExpanded(element)) {
						this[this._FUNC_MAP.OPEN](element);
					}
					else {
						this.closeAllPaths(root, element);
					}
					return;
				}

				if (action === shed.actions.EXPAND) {
					this.constructor.prototype.shedSubscriber.call(this, element, action);
					this.ajaxExpand(element, root);
					return;
				}

				this.constructor.prototype.shedSubscriber.call(this, element, action);
			};

			/**
			 * Override the default "animator" to prevent a beanch from opening if any other element is selected at its
			 * level. Only applies to htree.
			 *
			 * @function
			 * @protected
			 * @param {Object} item The branch being opened/closed.
			 * @param {Object} open If true branch is being opened, otherwise its being closed.
			 * @returns {Boolean} true if any non-false-equivalent value for item is passed in.
			 */
			this._animateBranch = function(item, open) {
				var root;

				if (!open) {
					return this.constructor.prototype._animateBranch.call(this, item, open);
				}

				if (item && (root = this.getRoot(item)) && this.isHTree(root)) {
					if (isLastSelectedItemAtLevel(item, root)) {
						shed.expand(item);
						this._remapKeys(item);
					}
					return true;
				}
				return this.constructor.prototype._animateBranch.call(this, item, open);
			};

			/**
			 * A TreeWalker filter to get a text node match during key-initiated tree walking.
			 * @function
			 * @protected
			 * @override
			 * @param {Node} textNode The node being tested.
			 * @returns {Number}
			 */
			this.textMatchFilter = function(textNode) {
				var parent = textNode.parentNode;

				if (isWMenu(this.getRoot(parent))) {
					return this.constructor.prototype.textMatchFilter.call(this, parent);
				}

				if (!classList.contains(parent, "wc_leaf_name")) {
					return  NodeFilter.FILTER_SKIP;
				}

				if (textNode.nodeValue) {
					return NodeFilter.FILTER_ACCEPT;
				}

				return NodeFilter.FILTER_SKIP;
			};

			this.enableDisable = function(element, action, root) {
				var shedFunc;
				if (this._isBranch(element)) {
					shedFunc = action === shed.actions.DISABLE ? "disable" : "enable";
					// dis/en-able the opener
					shed[shedFunc](instance._getBranchOpener(element));
					// disable or re-enable stuff inside the submenu
					this.disableInBranch(element, shedFunc);
				}
				// branches and items when disabled: may have to change default tabstop
				if (action === shed.actions.DISABLE) {
					this.hideDisableHelper(element, root);
				}
			};

			/**
			 * Find all submenus inside a given element.
			 * @function
			 * @public
			 * @param {Element} element The start element. Should probably be a tree or treeitem.
			 * @returns {?NodeList} a NodeList containing all submenus inside element.
			 */
			this.getSubMenus = function(element) {
				return this._wd.submenu.findDescendants(element);
			};
		}

		var /** @alias module:wc/ui/menu/tree */ instance;
		Tree.prototype = abstractMenu;
		instance = new Tree();
		instance.constructor = Tree;
		initialise.register(instance);
		return instance;
	});
