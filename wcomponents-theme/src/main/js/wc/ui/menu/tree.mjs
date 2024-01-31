import abstractMenu from "wc/ui/menu/core.mjs";
import keyWalker from "wc/dom/keyWalker.mjs";
import shed from "wc/dom/shed.mjs";
import treeItem from "wc/ui/menu/treeItem.mjs";
import initialise from "wc/dom/initialise.mjs";
import formUpdateManager from "wc/dom/formUpdateManager.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import ajaxRegion from "wc/ui/ajaxRegion.mjs";
import timers from "wc/timers.mjs";
import icon from "wc/ui/icon.mjs";

let instance;

const mapReturnKey = mapKeyToActionFactory(["Enter", "NumpadEnter"]);
const mapSpaceKey = mapKeyToActionFactory([" ", "Space"]);
const mapLeftKey = mapKeyToActionFactory(["ArrowLeft"]);
const mapRightKey = mapKeyToActionFactory(["ArrowRight"]);
const mapUpKey = mapKeyToActionFactory(["ArrowUp"]);
const mapDownKey = mapKeyToActionFactory(["ArrowDown"]);
const mapHomeKey = mapKeyToActionFactory(["Home"]);
const mapPageDownKey = mapKeyToActionFactory(["PageDown"]);
const mapEndKey = mapKeyToActionFactory(["End"]);
const mapMultiplyKey = mapKeyToActionFactory(["*", "NumpadMultiply"]);

/**
 *
 * @param keys An array of KeyBoardEven code and or key properties.
 * @return {function}
 */
function mapKeyToActionFactory(keys) {
	/**
	 * Helps set up key map, so it can respond to KeyBoardEvent code or key properties.
	 *
	 * @param map The key to action map to update.
	 * @param action The action to map to the keys.
	 */
	return (map, action) => {
		keys.forEach(nextKey => map[nextKey] = action);
	};
}

/**
 * QuerySelector, considering immediate children only.
 * @param {Element} parent
 * @param {string} selector
 * @return {HTMLElement}
 */
function querySelectorImmediate(parent, selector) {
	if (!parent && selector) {
		return null;
	}
	return /** @type {HTMLElement} */(Array.from(parent.children).find(child => child.matches(selector)));
}


/**
 * Menu controller extension for WTree. WTree uses the menu controller because it has the same key-walking, brancho
 * opening, selection and activation mechanisms.
 *
 * @see http://www.w3.org/TR/wai-aria-practices/#TreeView
 *
 * Extends menu functionality to provide a specific implementation of a tree.
 * @constructor
 * @alias module:wc/ui/menu/tree~Tree
 * @extends module:wc/ui/menu/core~AbstractMenu
 * @private
 * @this instance
 */
function Tree() {
	const vopenerSelector = ".wc_leaf_vopener";
	const leafSelector = "[role='treeitem']";
	const imageHolderSelector = ".wc_leaf_img";
	let ajaxTimer;

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
		return root.classList.contains("wc_htree");
	};

	/**
	 * Test an element to determine if it is itself or a descendant of the vertical tree opening button.
	 * @function module:wc/ui/menu/tree.isInVOpen
	 * @public
	 * @param {Element} element the element to test
	 * @returns {Boolean} true if element is a vertical tree branch opener or a descendant thereof.
	 */
	this.isInVOpen = function(element) {
		return !!element.closest(vopenerSelector);
	};

	/**
	 * The descriptors for this menu type.
	 *
	 * @var
	 * @override
	 */
	this._wd = {};

	/**
	 * The descriptions of the ROOT node of a tree menu.
	 * @var
	 * @type {string}
	 * @public
	 * @override
	 */
	this.ROOT = "[role='tree']";

	/**
	 * Roles for the parts of the tree.
	 *
	 * @var
	 * @type {Object}
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
	 * @returns {HTMLElement} A tree root node.
	 */
	function getRootHelper(element) {

		if (!element) {
			throw new ReferenceError("Argument 'element' is required.");
		}

		const _root = instance.isRoot(element) ? element : instance.getRoot(element);
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
	 * @override
	 * @param {Element} element A node of the tree to test. This is mandatory in this override.
	 * @returns {Boolean} true if only one branch may be open at a time.
	 */
	this._oneOpen = function(element) {
		const _root = getRootHelper(element);
		return this.isHTree(_root);
	};

	/**
	 * Trees do not require a branch item to be selected when a branch is opened.
	 *
	 * @var
	 * @type Boolean
	 * @override
	 */
	this._enterOnOpen = false;

	/**
	 * When keyboard navigating a tree we go into open submenus before going to the next option at the current
	 * level.
	 *
	 * @function module:wc/ui/menu/tree._treeWalkDepthFirst
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
	 * @see http://www.w3.org/TR/wai-aria-practices/#TreeView
	 * @var
	 * @type {Boolean}
	 * @override
	 * @default true
	 */
	this._selectOnNavigate = true;

	/**
	 * Selection in trees is complicated as it depends on the presence of absence of chordal key strokes during
	 * selection.
	 *
	 * @see http://www.w3.org/TR/wai-aria-practices/#TreeView
	 * @function module:wc/ui/menu/tree._select
	 * @override
	 * @param {Element} item The menu item being selected
	 * @param {boolean} [silent] If true do not publish the selection.
	 * @param {boolean} [SHIFT] Indicates the SHIFT key was pressed when the selection was made which causes the
	 *     selection of all items between the last selection and current selection to be selected.
	 * @param {boolean} [CTRL] Indicates the CTRL (or META) key was pressed when the selection was made so as to
	 *     create a (potentially) non-contiguous multiple selection (where allowed).
	 */
	this._select = function(item, silent, SHIFT, CTRL) {
		const root = this.getRoot(item);
		if (root?.getAttribute("aria-multiselectable")) {
			if (silent) {
				shed.select(item, silent);
			} else {
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
	 * @override
	 * @param {Element} _item The item which has focus.
	 */
	this._remapKeys = function(_item) {
		let isOpener,
			item = _item,
			root = this.getRoot(item);

		if (!root) {
			return;
		}


		mapReturnKey(this._keyMap, null);
		mapSpaceKey(this._keyMap, null);
		mapLeftKey(this._keyMap, keyWalker.MOVE_TO.PARENT);
		mapRightKey(this._keyMap, null);

		if (this._isBranch(item) || (isOpener = this._isOpener(item))) {
			if (isOpener) {
				item = this._getBranch(item);
			}

			if (item) {
				const isHTree = this.isHTree(root);
				if (!this.isHTree(root)) {
					mapReturnKey(this._keyMap, this._FUNC_MAP.ACTION);
					mapSpaceKey(this._keyMap, this._FUNC_MAP.ACTION);
				}
				const expandable = this._getBranchExpandableElement(item);

				if (expandable && shed.isExpanded(expandable)) {
					if (isHTree) {
						mapRightKey(this._keyMap, keyWalker.MOVE_TO.CHILD);
						mapLeftKey(this._keyMap, keyWalker.MOVE_TO.PARENT);
					} else {
						mapLeftKey(this._keyMap, this._FUNC_MAP.CLOSE);
					}
				} else {
					mapRightKey(this._keyMap, this._FUNC_MAP.ACTION);
					mapLeftKey(this._keyMap, keyWalker.MOVE_TO.PARENT);
				}
			}
		}
	};

	/**
	 * Sets up the initial keymap for tree menus.
	 *
	 * @see http://www.w3.org/TR/wai-aria-practices/#TreeView
	 * @function module:wc/ui/menu/tree._setupKeymap
	 * @override
	 */
	this._setupKeymap = function() {
		const keyMap = {};
		mapUpKey(keyMap, keyWalker.MOVE_TO.PREVIOUS);
		mapDownKey(keyMap, keyWalker.MOVE_TO.NEXT);
		mapLeftKey(keyMap, keyWalker.MOVE_TO.PARENT);
		mapHomeKey(keyMap, keyWalker.MOVE_TO.TOP);
		mapPageDownKey(keyMap, keyWalker.MOVE_TO.END);
		mapEndKey(keyMap, keyWalker.MOVE_TO.END);
		mapMultiplyKey(keyMap, "_openAllBranches");
		this._keyMap = keyMap;
	};

	/**
	 * Set up the Widgets which describe a tree.
	 * @function module:wc/ui/menu/tree._setUpWidgets
	 * @override
	 */
	this._setUpWidgets = function() {
		this._wd.submenu = "[role='group']";
		this._wd.branch = `${leafSelector}[aria-expanded]`;
		this._wd.opener = `${this._wd.branch} > [aria-controls]`;
		this._wd.leaf = [leafSelector];
	};

	/**
	 * Opens all branches in a menu. Note: this has to be public because super._keyActivator() needs to know it
	 * exists.
	 *
	 * @function module:wc/ui/menu/tree._openAllBranches
	 * @param {Element} from the start point for opening all branches
	 */
	this._openAllBranches = function(from) {
		const root = this.getRoot(from);
		if (!root || this.isHTree(root)) {
			return;
		}
		let allBranchOpeners = root.querySelectorAll(this._wd.opener.toString());
		if (allBranchOpeners.length) {
			/* NOTE: Array.prototype.reverse.call does not work in IE8 so I have to convert the nodeList to a real array then reverse it */
			allBranchOpeners = Array.from(allBranchOpeners);
			allBranchOpeners.reverse();
			allBranchOpeners.forEach(this[this._FUNC_MAP.OPEN], this);
		}
	};

	/**
	 * Get the menu element which is able to be "aria-expanded". This is the WSubMenu's content in most menus but is the WSubMenu itself in
	 * trees.
	 *
	 * @function module:wc/ui/menu/tree._getBranchExpandableElement
	 * @override
	 * @param {Element} item The start point for the search. This will normally be a 'branch'.
	 * @returns {HTMLElement} The "expandable" element. This is usually the branch content but is the branch in trees.
	 */
	this._getBranchExpandableElement = function (item) {
		if (!item) {
			throw new TypeError("Item must be defined.");
		}

		if (this._isBranch(item)) {
			return /** @type {HTMLElement} */(item);
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
	 * @param {Element} target the element clicked.
	 */
	function htreeClickHelper(target) {
		const item = instance.getItem(target);
		if (item && instance._isBranch(item) && shed.isExpanded(item)) {
			let parentBranch = instance.getSubMenu(item);
			if (parentBranch) {
				parentBranch = instance._getBranch(parentBranch);
			}
			if (parentBranch) {
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
	 * @param {MouseEvent & { target: HTMLElement }} $event The click event.
	 */
	this.clickEvent = function($event) {
		const target = $event.target;
		// target === window is an IE thing

		if ($event.defaultPrevented || target === document.body) {
			return;
		}
		const root = this.getRoot(target);
		if (root) {
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
		}
	};

	/**
	 * Write the state of WTree.
	 *
	 * @function module:wc/ui/menu/tree.writeMenuState
	 * @override
	 * @param {Element} next the WTree root element
	 * @param {Element} toContainer the state container
	 */
	this.writeMenuState = function(next, toContainer) {

		if (!next) {
			return; // called from the wrong menu type maybe?
		}

		const root = this.getRoot(next);
		if (!root) {
			return;
		}

		const rootId = root.id;

		// expanded branches
		Array.from(next.querySelectorAll(this._wd.branch.toString())).filter(function(nextBranch) {
			const expandable = this._getBranchExpandableElement(nextBranch);
			if (!expandable) {
				return false;
			}
			return !shed.isDisabled(nextBranch) && shed.isExpanded(expandable);
		}, this).forEach((nextBranch) => {
			const name = this._isBranch(nextBranch) ? nextBranch.id : "";
			if (name) {  // tree
				formUpdateManager.writeStateField(toContainer, `${rootId}.open`, name);
			}
		});

		// selected tree items (would prefer to be devolved to tree item but that just ain't possible ...)
		const selectedItems = /** @type {HTMLElement[]} */(getFilteredGroup(next, {
			ignoreInnerGroups: true
		}));
		selectedItems.forEach(nextSelectedItem => formUpdateManager.writeStateField(toContainer, rootId, nextSelectedItem.id));
		formUpdateManager.writeStateField(toContainer, `${rootId}-h`, "x");
	};

	/**
	 * Listen to AJAX updates that are about to affect a tree.
	 * @param {Element} element A candidate tree root
	 * @param {DocumentFragment} content The content that is about to be inserted into the tree
	 * @param action a wc/ui/ajax/processResponse action
	 */
	this.preAjaxSubscriber = function(element, content, action) {
		if (this.isRoot(element) && content && action === "in") {
			const kids = content.childNodes;
			for (let i = 0; i < kids.length; i++) {
				let newBranch = this._getBranch(kids[i]);
				if (newBranch) {
					let currentBranch = document.getElementById(newBranch.id);
					if (currentBranch && shed.isSelected(currentBranch)) {
						/*
						 * This is really handling an HTree situation:
						 * HTree branch opening can drive both selection and expansion
						 * Selection is not updated when we fetch branch content via AJAX so the server thiks the branch is not selected
						 * the new branch, fetched via AJAX, will therefore be deselcted (which is probably wrong for HTree).
						 */
						shed.select(newBranch, true);
					}
				}
			}
		}
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
		const level = instance.getSubMenu(element) || ((root && instance.isRoot(root)) ? root : instance.getRoot(element));

		return /** @type {HTMLElement[]} */(getFilteredGroup(level, {
			itemWd: leafSelector
		})).length === (shed.isSelected(element) ? 1 : 0);
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
		const mode = root.getAttribute("data-wc-mode");

		if (mode && mode !== "client") {
			const elId = element.id;
			const obj = {
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
	 * @override
	 * @param {Element} element The element being acted upon.
	 * @param {String} action The action being taken.
	 */
	this._shedSubscriber = function(element, action) {
		const root = element ? this.getRoot(element) : null;

		if (!root) {
			return;
		}

		if (action === shed.actions.SELECT || action === shed.actions.DESELECT) {
			if (ajaxRegion.getTrigger(root, true)) {
				if (ajaxTimer) {
					timers.clearTimeout(ajaxTimer);
					ajaxTimer = null;
				}
				ajaxTimer = timers.setTimeout(ajaxRegion.requestLoad, 0, root);
			}
			if (this.isHTree(root) && action === shed.actions.SELECT) {
				if (this._isBranch(element) && !shed.isExpanded(element)) {
					this[this._FUNC_MAP.OPEN](element);
				} else {
					this.closeAllPaths(root, element);
				}
			}
			return;
		}

		this.constructor.prototype._shedSubscriber.call(this, element, action);

		if (action === shed.actions.EXPAND) {
			ajaxExpand(element, root);

			let iconContainer = !this.isHTree(root) ? querySelectorImmediate(element, vopenerSelector) : null;
			if (iconContainer) {
				icon.change(iconContainer, "fa-caret-down", "fa-caret-right");
			}
			iconContainer = this._getBranchOpener(element);
			iconContainer = iconContainer ? querySelectorImmediate(iconContainer, imageHolderSelector) : null;
			if (iconContainer) {
				icon.change(iconContainer, "fa-folder-open-o", "fa-folder-o");
			}
		}
	};

	/**
	 * Override the default "animator" to prevent a branch from opening if any other element is selected at its level. Only applies to htree.
	 *
	 * @function module:wc/ui/menu/tree._animateBranch
	 * @param {Element} item The branch being opened/closed.
	 * @param {Boolean} open If true branch is being opened, otherwise its being closed.
	 * @returns {Boolean} true if the branch is able to animate.
	 */
	this._animateBranch = function(item, open) {
		const root = this.getRoot(item);

		if (!(item && root)) {
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
	 * @override
	 * @param {Node} textNode The node being tested.
	 * @returns {Number}
	 */
	this._textMatchFilter = function(textNode) {
		const parent = textNode.parentElement;

		if (!parent.classList.contains("wc_leaf_name")) {
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
	 * @override
	 * @param {Element} element the branch beng collapsed.
	 * @param {Element} [root] the tree's root
	 */
	this._shedCollapseHelper = function (element, root) {
		const _root = root || this.getRoot(element);

		if (!_root) {
			return;
		}

		if (element && this._isBranch(element)) {
			let iconContainer = !this.isHTree(root) ? querySelectorImmediate(element, vopenerSelector) : null;
			if (iconContainer) {
				icon.change(iconContainer, "fa-caret-right", "fa-caret-down");
			}
			iconContainer = this._getBranchOpener(element);
			iconContainer = iconContainer ? querySelectorImmediate(iconContainer, imageHolderSelector) : null;
			if (iconContainer) {
				icon.change(iconContainer, "fa-folder-o", "fa-folder-open-o");
			}

			const groupContainer = this.getSubMenu(element, true);
			let group = groupContainer ? /** @type {HTMLElement[]} */(getFilteredGroup(groupContainer, { itemWd: this._wd.leaf[0] })) : [];
			if (group?.length) {
				group.forEach((next) => shed.deselect(next));
				if (!this.isHTree(_root)) {
					shed.select(element);
				}
			}
		}
	};
}
Tree.prototype = abstractMenu;

instance = new Tree();
instance.constructor = Tree;
export default initialise.register(instance);
