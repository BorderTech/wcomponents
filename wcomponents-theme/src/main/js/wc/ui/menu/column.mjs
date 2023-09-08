import abstractMenu from "wc/ui/menu/core";
import keyWalker from "wc/dom/keyWalker";
import shed from "wc/dom/shed";
import initialise from "wc/dom/initialise";
/* Unused dependencies:
 * We will need ["wc/ui/menu/menuItem" if we have any selectable items so we get it just in case rather than doing a
 * convoluted XPath lookup in XSLT.
 */
import "wc/ui/menu/menuItem";

let instance;

/**
 * Menu controller extension for WMenu of type COLUMN. This represents a vertical menu with optional flyout submenus.
 *
 * Extends menu functionality to provide a specific implementation of a vertical menu with optional flyout sub-menus.
 * @constructor
 * @alias module:wc/ui/menu/column~Column
 * @extends module:wc/ui/menu/core~AbstractMenu
 * @private
 */
function Column() {
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
	this.ROOT = ".wc-menu-type-column";

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
	 * Sets up the initial keymap for column menus as per http://www.w3.org/TR/wai-aria-practices/#menu.
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
}
Column.prototype = abstractMenu;

instance = new Column();
instance.constructor = Column;
export default initialise.register(instance);

