import AriaAnalog from "wc/dom/ariaAnalog.mjs";
import initialise from "wc/dom/initialise.mjs";
import isAcceptableEventTarget from "wc/dom/isAcceptableTarget.mjs";

const openerSelector = "button.wc-submenu-o";

/**
 * A selectable menu item with a checkbox modality.
 * @see http://www.w3.org/TR/wai-aria-practices/#menuitemcheckbox
 */
class MenuItemCheckbox extends AriaAnalog {
	/**
	 * The definition of a menu item with checkbox modality.
	 * @var
	 * @type {string}
	 * @override
	 */
	ITEM = "[role='menuitemcheckbox']";

	/**
	 * The selection mode is multiple.
	 * @var
	 * @type number
	 * @override
	 */
	exclusiveSelect = this.SELECT_MODE.MULTIPLE;

	/**
	 * Menus have complex two-dimensinal keyboard navigation, therefore the menu items themselves do not
	 * implement navigation.
	 * @var
	 * @type {Boolean}
	 * @override
	 */
	groupNavigation = false;

	/**
	 * Click event listener calls {@link module"wc/ui/menu/menuItem"~clickEventHelper}.
	 * @function
	 * @override
	 * @param {MouseEvent & { target: HTMLElement }} $event The click event.
	 */
	clickEvent($event) {
		clickEventHelper($event, this);
	};
}

/**
 * Model of a menu item which is selectable with a radio button modality.
 * @see http://www.w3.org/TR/wai-aria-practices/#menuitemradio
 */
class MenuItemRadio extends AriaAnalog {
	/**
	 * The definition of a menu item with radio modality.
	 * @var
	 * @type {string}
	 * @override
	 */
	ITEM = "[role='menuitemradio']";

	/**
	 * The selection mode is single as per a radio button.
	 * @var
	 * @type number
	 * @override
	 */
	exclusiveSelect = this.SELECT_MODE.SINGLE;

	/**
	 * Menus have complex two-dimensinal keyboard navigation, therefore the menu items themselves do not
	 * implement navigation.
	 * @var
	 * @type {Boolean}
	 * @override
	 */
	groupNavigation = false;

	/**
	 * The selection of a menuitemradio will only affect the selected state of other menuitemradio roled
	 * elements in the same immediate group. It will not, for example, deselect menuitemradio elements in
	 * a different (even descendant) submenu.
	 * @var
	 * @type boolean
	 * @override
	 */
	selectionIsImmediate = true;

	/**
	 * Click event listener calls {@link module"wc/ui/menu/menuItem"~clickEventHelper}.
	 * @function
	 * @override
	 * @param {MouseEvent & { target: HTMLElement }} $event The click event.
	 */
	clickEvent($event) {
		clickEventHelper($event, this);
	}
}

/**
 * Activates clicks on menu items with a checkbox or radio modality. Activates (selects) the menu item.
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event The click event
 * @param {Object} instance An instance of the module's singleton of MenuItemCheckbox or MenuItemRadio.
 */
function clickEventHelper($event, instance) {
	const {
		target,
		ctrlKey,
		shiftKey,
		metaKey,
		defaultPrevented} = $event;
	if (!defaultPrevented) {
		const element = instance.getActivableFromTarget(target);
		if (element) {
			/* a menu item (checkbox|radio) can be toggled if it is itself an acceptable element OR
			 * if the click event is on a branch opener button, which would normally render the menu
			 * item unacceptable as an event target*/
			if (isAcceptableEventTarget(element, target) || target.closest(openerSelector)) {
				instance.activate(element, shiftKey, (ctrlKey || metaKey));
			}
		}
	}
}

const menuItem = {
	menuItemCheckBox: new MenuItemCheckbox(),
	menuItemRadio: new MenuItemRadio()
};

initialise.register(menuItem.menuItemCheckBox);
initialise.register(menuItem.menuItemRadio);

/**
 * This module provides ARIA role based functionality for selectable menu items.
 *
 * @alias module:wc/ui/menu/menuItem~MenuItem
 */
export default menuItem;
