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
 */
define(["wc/ui/menu/core",
		"wc/dom/keyWalker",
		"wc/dom/shed",
		"wc/dom/Widget",
		"wc/array/toArray",
		"wc/ui/menu/treeItem",
		"wc/dom/initialise",
		"wc/has"],
	/** @param abstractMenu wc/ui/menu/core @param keyWalker wc/dom/keyWalker @param shed wc/dom/shed @param Widget wc/dom/Widget @param toArray wc/array/toArray @param treeItem wc/ui/menu/treeItem @param initialise wc/dom/initialise @param has wc/has @ignore */
	function(abstractMenu, keyWalker, shed, Widget, toArray, treeItem, initialise, has) {
		"use strict";

		/**
		 * Extends menu functionality to provide a specific implementation of a tree.
		 * @constructor
		 * @alias module:wc/ui/menu/tree~Tree
		 * @extends module:wc/ui/menu/core~AbstractMenu
		 * @private */
		function Tree() {
			var SUBMENU_CONTENT,
				DUMMY_ITEM;

			if (has("ie") === 8) {
				// IE8 fails to repaint tree branch closes in a timely manner when closing if the repainter is not included explicitly.
				require(["wc/fix/inlineBlock_ie8"]);
			}

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
				SUBMENU: "group",
				LEAF: {noSelection: "treeitem"}
			};

			/**
			* Trees allow multiple branches to be open at any time. This may need to be amended to support horizontal
			* trees.
			*
			* @var
			* @type {Boolean}
			* @protected
			* @override
			* @default false
			*/
			this._oneOpen = false;

			/**
			 * When keyboard navigating a tree we go into open submenus before going to the next option at the current
			 * level.
			 *
			 * @var
			 * @type {Boolean}
			 * @protected
			 * @override
			 * @default true
			 */
			this._treeWalkDepthFirst = true;

			/**
			 * Tree branches stay open and do not have hover effects or collision detection.
			 *
			 * @var
			 * @type {boolean}
			 * @protected
			 * @override
			 * @default false
			 */
			this._transient = false;

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
				var root = this._getRoot(item);
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
			 * @param {DocumentFragment} container The documentFragment returned from the ajax subsystem.
			 */
			this._fixSubmenuContentInAjaxResponse = function(container) {
				var EXP_ATTRIB = "aria-expanded";
				// generic (role-less) submenu content container is needed because the role is still incorrect
				SUBMENU_CONTENT = SUBMENU_CONTENT || new Widget("", "wc_submenucontent");

				Array.prototype.forEach.call(SUBMENU_CONTENT.findDescendants(container), function (nextSubmenuContent) {
					var branch, opener, isOpen;

					// fix up the submenu content wrapper element attributes
					DUMMY_ITEM = DUMMY_ITEM || new Widget("", "wc_menuitem_dummy");

					nextSubmenuContent.setAttribute("role", this._role.SUBMENU);
					isOpen = nextSubmenuContent.getAttribute(EXP_ATTRIB) || "false"; // we need this for the branch ...
					nextSubmenuContent.removeAttribute(EXP_ATTRIB);
					// now remove the dummy items which are needed for menus but not for trees
					Array.prototype.forEach.call(DUMMY_ITEM.findDescendants(nextSubmenuContent), function(nextDummy) {
						nextDummy.parentNode.removeChild(nextDummy);
					});
					// now fix up the branch container
					if ((branch = this._getBranch(nextSubmenuContent))) {
						branch.removeAttribute("data-wc-selectmode");
						if ((opener = this._getBranchOpener(branch))) {
							opener.removeAttribute("aria-haspopup");
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
					VK_LEFT = "DOM_VK_LEFT",
					VK_RIGHT = "DOM_VK_RIGHT";

				if (this._isLeaf(item)) {
					this._keyMap[VK_LEFT] = keyWalker.MOVE_TO.PARENT;
					this._keyMap[VK_RIGHT] = null;
				}
				else if (this._isBranch(item) || (isOpener = this._isOpener(item))) {
					if (isOpener) {
						item = this._getBranch(item);
					}
					if (item) {
						if (shed.isExpanded(this._getBranchExpandableElement(item))) {
							this._keyMap[VK_LEFT] = this._FUNC_MAP.CLOSE;
							this._keyMap[VK_RIGHT] = null;
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
				if ((root = this._getRoot(from)) && (allBranchOpeners = this.getFixedWidgets().BRANCH_TRIGGER.findDescendants(root)) && allBranchOpeners.length) {
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
				component.setAttribute("role", this._role.LEAF.noSelection);
				component.removeAttribute("data-wc-selectable");
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

				if ((this._wd.submenu.isOneOfMe(item) || this._isOpener(item)) && (myBranch = this._getBranch(item))) {
					return myBranch;
				}

				throw new TypeError("Item must be a branch, submenu or branch opener element.");
			};
		}

		var /** @alias module:wc/ui/menu/tree */ instance;
		Tree.prototype = abstractMenu;
		instance = new Tree();
		instance.constructor = Tree;
		initialise.register(instance);
		return instance;
	});
