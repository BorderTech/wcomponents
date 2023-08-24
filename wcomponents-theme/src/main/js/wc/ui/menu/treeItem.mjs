import ariaAnalog from "wc/dom/ariaAnalog";
import initialise from "wc/dom/initialise";
import isAcceptableTarget from "wc/dom/isAcceptableTarget";
import shed from "wc/dom/shed";
import getFilteredGroup from "wc/dom/getFilteredGroup";

let instance;

/**
 * Provides ARIA based functionality for items in a tree (role='treeitem")
 *
 * **NOTE:**
 *
 * According to the WAI-ARIA implementation guide, tree items should select on navigate. Tree items in WComponents are a
 * type of WMenuItem and menu items are supposed to do stuff. For this reason we have decided to not try to make them
 * select on navigate. This will need to change.
 *
 * @see http://www.w3.org/TR/wai-aria-practices/#treeitem
 *
 * @extends module:wc/dom/ariaAnalog
 * @constructor
 */
function TreeItem() {
	let opener, tree;

	require(["wc/ui/menu/tree"], function (eert) {
		tree = eert;
	});

	/**
	 * Helper for clickEvent to determine if the element being clicked is able to process the treeitem
	 * activation. This is required because we have a complex bi-modal case in vertical trees. In
	 * this type of tree an item can be activated (de/selected) if the click is on the opener button but
	 * not if it is on the part of the opener reserved for toggling the open state.
	 *
	 * In the old (WMenu) form of tree we do not make this distinction. This _should_ change.
	 *
	 * @function
	 * @private
	 * @param {HTMLElement} element The element which is being tested.
	 * @param {HTMLElement} target The click event target.
	 * @returns {Boolean} true if the element may be activated.
	 */
	function isAcceptable (element, target) {
		let result = isAcceptableTarget(element, target);

		if (tree && tree.getRoot(target)) {
			if (tree.isInVOpen(target) || tree.isSubMenu(target) || tree.isRoot(target)) {
				return false;
			}

			if (!result) {
				opener = opener || tree._wd.opener;

				if (opener) {
					return !!target.closest(opener.toString());
				}
			}
		}
		return result;
	}

	/**
	 * Helper function to determine if an element to be activated is the first element being activated at a
	 * particular level of the tree. This is needed when a treeitem is in a htree. This is because when we
	 * select the first treeitem in a branch we must deselect elements in all other branches as only one
	 * branch may have selection.
	 *
	 * It is insufficient to rely on only one branch being open at a time because this will not take into
	 * account the case where a tree has selected item(s) in an ancestor branch.
	 *
	 * @function
	 * @private
	 * @param {HTMLElement} element The element to be activated.
	 * @param {HTMLElement} container The root node of the tree. We already calculated this so we reuse it.
	 * @returns {Boolean} true if element is the first treeitem being activated in its level of the tree.
	 */
	function isFirstAtLevel(element, container) {
		const subMenu = tree.getSubMenu(element) || container;

		if (!subMenu) {
			return true;
		}

		const selectedSiblings = getFilteredGroup(subMenu, { itemWd: instance.ITEM }).length;

		return (shed.isSelected(element) ? selectedSiblings === 1 : selectedSiblings === 0);
	}

	/**
	 * The definition of a tree item.
	 * @var
	 * @public
	 * @type {string}
	 * @override
	 */
	this.ITEM = "[role='treeitem']";

	/**
	 * The definition of a selection group container for tree items. This is required to prevent grouping by
	 * role "group". See http://www.w3.org/TR/wai-aria-practices/#TreeView regarding selection.
	 * @var
	 * @public
	 * @type {string}
	 * @override
	 */
	this.CONTAINER = "[role='tree']";

	/**
	 * The selection mode is mixed: tree items may be single or multiple and is defined at the tree level.
	 * @var
	 * @protected
	 * @type int
	 * @override
	 */
	this.exclusiveSelect = this.SELECT_MODE.MIXED;

	/**
	 * Menus have complex two-dimensinal keyboard navigation, therefore the tree items themselves do not
	 * implement navigation.
	 * @var
	 * @type {Boolean}
	 * @public
	 * @override
	 */
	this.groupNavigation = false;

	/**
	 * We need to keep a record of the last activated item in a tree to allow for chordal selection
	 * being either CTRL or SHIFT accompanying a click or navigation event. The Object is keyed on the tree id.
	 * @var
	 * @type Object
	 * @protected
	 * @override
	 */
	this.lastActivated = {};

	/**
	 * If a tree has single selection then the selected state of the treeitems can be toggled by clicking whilst
	 * holding the CTRL key (unlike, for example, a radio button or single-selectable option element).
	 * @override
	 */
	this.ctrlAllowsDeselect = true;

	/**
	 * @override
	 */
	this.allowSelectSelected = true;

	/**
	 * Only select treeItem if the tree allows selection.
	 * A tree allows selection if it has a "aria-multiselectable"
	 * attribute (either true or false).
	 * @function
	 * @public
	 * @override
	 * @param {HTMLElement} element The element to activate
	 * @param {Boolean} [SHIFT] True if the SHIFT key was pressed at the time of activation.
	 * @param {Boolean} [CTRL] True if the CTRLor META key was pressed at the time of activation.
	 */
	this.activate = function(element, SHIFT, CTRL) {
		const container = element.closest(this.CONTAINER.toString());
		const multiSelect = container?.getAttribute("aria-multiselectable")
		if (multiSelect) {
			let selectMode;
			try {
				let forceSingle;
				if (!(SHIFT || CTRL)) {
					forceSingle = true;
				} else if (multiSelect === "true" && tree.isHTree(container)) {
					forceSingle = isFirstAtLevel(element);
				}
				if (forceSingle) {
					selectMode = this.exclusiveSelect;
					this.exclusiveSelect = this.SELECT_MODE.SINGLE;
				}
				this.constructor.prototype.activate.call(this, element, SHIFT, CTRL);
			} finally {
				if (selectMode) {
					this.exclusiveSelect = selectMode;
					selectMode = null;
				}
			}
		}
	};

	/**
	 * Over-ride of the click event handler. This is required because of the complexities of determining
	 * whether we can accept the click to activate the treeitem.
	 *
	 * @function
	 * @public
	 * @param {MouseEvent} $event The click event.
	 */
	this.clickEvent = ($event) => {
		const { defaultPrevented, target, shiftKey, ctrlKey, metaKey} = $event;
		if (defaultPrevented) {
			return;
		}
		const element = this.getActivableFromTarget(target);
		if (element) {
			if (isAcceptable(element, target)) {
				this.activate(element, shiftKey, (ctrlKey || metaKey));
			}
		}
	};
}
TreeItem.prototype = ariaAnalog;
instance = new TreeItem();
instance.constructor = TreeItem;
initialise.register(instance);

export default instance;

