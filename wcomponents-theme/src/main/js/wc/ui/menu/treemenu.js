define(["wc/ui/menu/core", "wc/dom/keyWalker", "wc/dom/shed", "wc/dom/Widget", "wc/dom/initialise", "wc/ui/icon", "wc/ui/menu/menuItem"],
	function(abstractMenu, keyWalker, shed, Widget, initialise, icon) {
		"use strict";

		/**
		 * Extends menu functionality to provide a tree-like menu.
		 * @constructor
		 * @alias module:wc/ui/menu/treemenu~TreeMenu
		 * @extends module:wc/ui/menu/core~AbstractMenu
		 * @private
		 */
		function TreeMenu() {
			/**
			 * The descriptors for this menu type.
			 * @type {module:wc/dom/Widget}
			 * @protected
			 * @override
			 */
			this._wd = {};

			/**
			 * The {description of the ROOT node of a column menu.
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ROOT = new Widget("", "wc-menu-type-tree");

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
			 * Trees do not enter on open.
			 *
			 * @var
			 * @type {Boolean}
			 * @protected
			 * @override
			 */
			this._enterOnOpen = false;

			/**
			 * Trees are not transient.
			 *
			 * @var
			 * @type boolean
			 * @public
			 */
			this.isTransient = false;

			/**
			 * Tree menu allows multiple submenus to be open.
			 *
			 * @function
			 * @protected
			 * @override
			 * @returns {Boolean} true if only one branch may be open at a time.
			 */
			this._oneOpen = function() {
				return false;
			};

			/**
			 * Keyboard walking of the tree.
			 *
			 * @function
			 * @protected
			 * @override
			 * @returns {Boolean} true.
			 */
			this._treeWalkDepthFirst = function() {
				return true;
			};

			/**
			 * Reset the key map based on the type and/or state of the menu item passed in.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} item The menu item which has focus.
			 */
			this._remapKeys = function(item) {
				var element = item,
					root = this.getRoot(item);

				if (!root) {
					return;
				}
				if (this._isBranchOrOpener(element)) {
					element = this._getBranchExpandableElement(element);
					if (!shed.isExpanded(element)) {
						this._keyMap["DOM_VK_RIGHT"] = this._FUNC_MAP.ACTION;
					} else {
						this._keyMap["DOM_VK_RIGHT"] = keyWalker.MOVE_TO.CHILD;
					}
				}
			};

			/**
			 * Sets up the initial keymap for tree-ish menus as per http://www.w3.org/TR/wai-aria-practices/#menu.
			 *
			 * @function
			 * @protected
			 * @override
			 */
			this._setupKeymap = function() {
				this._keyMap = {
					"DOM_VK_UP": keyWalker.MOVE_TO.PREVIOUS,
					"DOM_VK_DOWN": keyWalker.MOVE_TO.NEXT,
					"DOM_VK_LEFT": this._FUNC_MAP.CLOSE_MY_BRANCH,
					"DOM_VK_ESCAPE": this._FUNC_MAP.CLOSE_MY_BRANCH
				};
			};


			this._shedSubscriber = function(element, action) {
				var opener;
				if (!(element && this.getRoot(element))) {
					return;
				}

				if (action === shed.actions.EXPAND || action === shed.actions.COLLAPSE) {
					if ((opener = this._getBranch(element)) && (opener = this._getBranchOpener(opener))) {
						if (action === shed.actions.EXPAND) {
							icon.change(opener, "fa-caret-down", "fa-caret-right");
						} else if (action === shed.actions.COLLAPSE) {
							icon.change(opener, "fa-caret-right", "fa-caret-down");
						}
					}
				}

				this.constructor.prototype._shedSubscriber.call(this, element, action);
			};
		}

		/**
		 * Menu controller extension for WMenu of type TREE. This represents a vertical menu with optional sliding submenus
		 * which may be indented. See WTree which produces a WAI-ARIA tree widget which is a selection tool.
		 *
		 * @see http://www.w3.org/TR/wai-aria-practices/#menu
		 * @module
		 * @extends module:wc/ui/menu/core
		 * @requires module:wc/ui/menu/core
		 * @requires module:wc/dom/keyWalker
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/ui/icon
		 */
		var instance;
		TreeMenu.prototype = abstractMenu;
		instance = new TreeMenu();
		instance.constructor = TreeMenu;
		initialise.register(instance);
		return instance;
	});
