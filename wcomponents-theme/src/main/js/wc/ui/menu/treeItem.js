/**
 * Provides ARIA based functionality for items in a tree (role='treeitem")
 *
 * **NOTE:**
 * 
 * According to the WAI-ARIA implementation guide, tree items should select on navigate. Tree items in WComponents are a
 * type of WMenuItem and menu items are supposed to do stuff. For this reason we have decided to not try to make them
 * select on navigate. This will need to change.</p>
 *
 * @see {@link http://www.w3.org/TR/wai-aria-practices/#treeitem}
 * @module
 * @extends module:wc/dom/ariaAnalog
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 */
define(["wc/dom/ariaAnalog",
		"wc/dom/initialise",
		"wc/dom/Widget"],
	/** @param ariaAnalog wc/dom/ariaAnalog @param initialise wc/dom/initialise @param Widget wc/dom/Widget @ignore */
	function(ariaAnalog, initialise, Widget) {
		"use strict";
		/** @alias module:wc/ui/menu/treeItem */
		function TreeItem() {
			/**
			 * The definition of a tree item.
			 * @var
			 * @public
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.ITEM = new Widget("", "", {role: "treeitem"});

			/**
			 * The definition of a selection group container for tree items. This is required to prevent grouping by
			 * role "group". See http://www.w3.org/TR/wai-aria-practices/#TreeView regarding selection.
			 * @var
			 * @public
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.CONTAINER = new Widget("", "", {role: "tree"});

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
			 * @var
			 * @type Boolean
			 * @protected
			 * @override
			 */
			this.ctrlAllowsDeselect = true;

			/**
			 * Only select treeItem if the tree allows selection.
			 * A tree allows selection if it has a "aria-multiselectable"
			 * attribute (either true or false).
			 * @function
			 * @public
			 * @override
			 * @param {Element} element The element to activate
			 * @param {Boolean} [SHIFT] True if the SHIFT key was pressed at the time of activation.
			 * @param {Boolean} [CTRL] True if the CTRLor META key was pressed at the time of activation.
			 */
			this.activate = function(element, SHIFT, CTRL) {
				var container = this.CONTAINER.findAncestor(element), selectMode;
				if (container && container.hasAttribute("aria-multiselectable")) {
					try {
						if (!(SHIFT || CTRL)) {
							selectMode = this.exclusiveSelect;
							this.exclusiveSelect = this.SELECT_MODE.SINGLE;
						}
						this.constructor.prototype.activate.call(this, element, SHIFT, CTRL);
					}
					finally {
						if (selectMode) {
							this.exclusiveSelect = selectMode;
							selectMode = null;
						}
					}
				}
			};
		}

		var instance;

		TreeItem.prototype = ariaAnalog;
		instance = new TreeItem();
		instance.constructor = TreeItem;
		initialise.register(instance);

		return instance;
	});
