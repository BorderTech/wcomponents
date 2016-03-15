/**
 * This module provides ARIA role based functionality for selectable menu items.
 *
 * @module
 * @extends module:wc/dom/ariaAnalog
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget"
 * @requires module:wc/dom/isAcceptableTarget
 * @requires module:wc/dom/shed
 */
define(["wc/dom/ariaAnalog",
		"wc/dom/initialise",
		"wc/dom/Widget",
		"wc/dom/isAcceptableTarget",
		"wc/dom/shed"],
	/** @param ariaAnalog wc/dom/ariaAnalog @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param isAcceptableEventTarget wc/dom/isAcceptableTarget @param shed wc/dom/shed @ignore */
	function(ariaAnalog, initialise, Widget, isAcceptableEventTarget, shed) {
		"use strict";

		var opener;

		/**
		 * @constructor
		 * @alias module:wc/ui/menu/menuItem~MenuItem
		 * @private
		 */
		function MenuItem() {
			/**
			 * Activates clicks on menu items with a checkbox or radio modality. Activates (selects) the menu item.
			 * @function
			 * @private
			 * @param {Event} $event The click event
			 * @param {Object} instance An instance of the module's singleton of MenuItemCheckbox or MenuItemRadio.
			 */
			function clickEventHelper($event, instance) {
				var target = $event.target, element;
				if (!$event.defaultPrevented && (element = instance.getActivableFromTarget(target)) && !shed.isDisabled(element)) {
					opener = opener || new Widget("button", "wc-submenu-o");
					/* a menu item (checkbox|radio) can be toggled if it is itself an acceptable element OR
					 * if the click event is on a branch opener button, which would normally render the menu
					 * item unacceptable as an event target*/
					if (isAcceptableEventTarget(element, target) || opener.findAncestor(target)) {
						instance.activate(element, $event.shiftKey, ($event.ctrlKey || $event.metaKey));
					}
				}
			}

			/**
			 * A selectable menu item with a checkbox modality.
			 * @see {@link http://www.w3.org/TR/wai-aria-practices/#menuitemcheckbox}
			 *
			 * @constructor module:wc/ui/menu/menuItem~MenuItem~MenuItemCheckbox
			 * @extends module:wc/dom/ariaAnalog~AriaAnalog
			 * @private
			 */
			function MenuItemCheckbox() {
				/**
				 * The definition of a menu item with checkbox modality.
				 * @var
				 * @public
				 * @type {module:wc/dom/Widget}
				 * @override
				 */
				this.ITEM = new Widget("", "", {role: "menuitemcheckbox"});

				/**
				 * The selection mode is multiple.
				 * @var
				 * @protected
				 * @type int
				 * @override
				 */
				this.exclusiveSelect = this.SELECT_MODE.MULTIPLE;

				/**
				 * Menus have complex two-dimensinal keyboard navigation, therefore the menu items themselves do not
				 * implement navigation.
				 * @var
				 * @type {Boolean}
				 * @public
				 * @override
				 */
				this.groupNavigation = false;

				/**
				 * Click event listener calls {@link module"wc/ui/menu/menuItem"~clickEventHelper}.
				 * @function
				 * @protected
				 * @override
				 * @param {Event} $event The click event.
				 */
				this.clickEvent = function($event) {
					clickEventHelper($event, this);
				};
			}

			/**
			 * Model of a menu item which is selectable with a radio button modality.
			 * @see {@link http://www.w3.org/TR/wai-aria-practices/#menuitemradio}
			 *
			 * @constructor module:wc/ui/menu/menuItem~MenuItem~MenuItemRadio
			 * @extends module:wc/dom/ariaAnalog~AriaAnalog
			 * @private
			 */
			function MenuItemRadio() {
				/**
				 * The definition of a menu item with radio modality.
				 * @var
				 * @public
				 * @type {module:wc/dom/Widget}
				 * @override
				 */
				this.ITEM = new Widget("", "", {role: "menuitemradio"});

				/**
				 * The selection mode is single as per a radio button.
				 * @var
				 * @protected
				 * @type int
				 * @override
				 */
				this.exclusiveSelect = this.SELECT_MODE.SINGLE;

				/**
				 * Menus have complex two-dimensinal keyboard navigation, therefore the menu items themselves do not
				 * implement navigation.
				 * @var
				 * @type {Boolean}
				 * @public
				 * @override
				 */
				this.groupNavigation = false;

				/**
				 * The selection of a menuitemradio will only affect the selected state of other menuitemradio roled
				 * elements in the same immediate group. It will not, for example, deselect menuitemradio elements in
				 * a different (even descendant) submenu.
				 * @var
				 * @protected
				 * @type int
				 * @override
				 */
				this.selectionIsImmediate = true;

				/**
				 * Click event listener calls {@link module"wc/ui/menu/menuItem"~clickEventHelper}.
				 * @function
				 * @protected
				 * @override
				 * @param {Event} $event The click event.
				 */
				this.clickEvent = function($event) {
					clickEventHelper($event, this);
				};
			}

			var _menuItemCheckBox,
				_menuItemRadio;

			MenuItemCheckbox.prototype = ariaAnalog;
			_menuItemCheckBox = new MenuItemCheckbox();
			_menuItemCheckBox.constructor = MenuItemCheckbox;
			initialise.register(_menuItemCheckBox);

			MenuItemRadio.prototype = ariaAnalog;
			_menuItemRadio = new MenuItemRadio();
			_menuItemRadio.constructor = MenuItemRadio;
			initialise.register(_menuItemRadio);

			this.menuItemCheckBox = _menuItemCheckBox;
			this.menuItemRadio = _menuItemRadio;
		}

		return /** @alias module:wc/ui/menu/menuItem */ new MenuItem();
	});
