import abstractMenu from "wc/ui/menu/core.mjs";
import keyWalker from "wc/dom/keyWalker.mjs";
import shed from "wc/dom/shed.mjs";
import initialise from "wc/dom/initialise.mjs";
import icon from "wc/ui/icon.mjs";
import "wc/ui/menu/menuItem.mjs";

/**
 * Menu controller extension for WMenu of type TREE. This represents a vertical menu with optional sliding submenus
 * which may be indented. See WTree which produces a WAI-ARIA tree widget which is a selection tool.
 *
 * @see http://www.w3.org/TR/wai-aria-practices/#menu

 * Extends menu functionality to provide a tree-like menu.
 * @constructor
 * @alias module:wc/ui/menu/treemenu~TreeMenu
 * @extends module:wc/ui/menu/core~AbstractMenu
 * @private
 */
function TreeMenu() {
	/**
	 * The descriptors for this menu type.
	 * @protected
	 * @override
	 */
	this._wd = {};

	/**
	 * The {description of the ROOT node of a column menu.
	 * @type {string}
	 * @public
	 * @override
	 */
	this.ROOT = ".wc-menu-type-tree";

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
		const root = this.getRoot(item);

		if (!root) {
			return;
		}
		if (this._isBranchOrOpener(item)) {
			const element = this._getBranchExpandableElement(item);
			if (!shed.isExpanded(element)) {
				this._keyMap["ArrowRight"] = this._FUNC_MAP.ACTION;
			} else {
				this._keyMap["ArrowRight"] = keyWalker.MOVE_TO.CHILD;
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
			"ArrowUp": keyWalker.MOVE_TO.PREVIOUS,
			"ArrowDown": keyWalker.MOVE_TO.NEXT,
			"ArrowLeft": this._FUNC_MAP.CLOSE_MY_BRANCH,
			"Escape": this._FUNC_MAP.CLOSE_MY_BRANCH
		};
	};


	this._shedSubscriber = function(element, action) {
		const inTreeMenu = element && this.getRoot(element);
		if (!inTreeMenu) {
			return;
		}

		if (action === shed.actions.EXPAND || action === shed.actions.COLLAPSE) {
			const branch = this._getBranch(element);
			const opener = branch ? this._getBranchOpener(branch) : null;
			if (opener) {
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
TreeMenu.prototype = abstractMenu;

let instance = new TreeMenu();
instance.constructor = TreeMenu;
export default initialise.register(instance);
