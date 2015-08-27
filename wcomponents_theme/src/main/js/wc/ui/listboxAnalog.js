/**
 * Provides ARIA based listbox functionality (lists of selectable options - cf a select element).
 *
 * Note that ARIA practices are not very precise but the information is there between the lines. The implications are
 * that arrow keys drive selection in listboxes, otherwise there would be no point for Ctrl+Arrow.
 *
 * * Tab: When a list is tabbed to, select the first item if nothing else is already selected. A second tab will take
 *   the user out of the widget to the next tab stop on the page.
 * * Up/down arrows navigate up and down the list.
 * * Shift+Up Arrow and Shift+Down Arrow move and extend the selection if the list allows multiple selection.
 * * Typing letter or several letters to navigate (same letter goes to each item starting with that, different letters
 *   go to first item starting with that entire string).
 * * Shift+F10: If the current item has an associated context menu, then this key combination will launch that menu.
 *
 * Selection:
 *
 * * Checkbox - Space toggles checkboxes, if the list items are checkable
 * * Selectable List Items:
 *    * Space acts as a toggle to select and deselect the current item. If previous items have been selected, it also
 *      deselects them and selects the current item;
 *    * Shift+Space selects contiguous items from the last selected item to the current item;
 *    * Ctrl/Meta+Arrow moves without selecting;
 *    * Ctrl/Meta+Space selects non-contiguous items and adds the current selected item to all previously selected items;
 *    * Ctrl/Meta+A - It is recommended a checkbox, link or other method be used to select all. The Ctrl/Meta+A key
 *      could be used to provide the shortcut key.
 *
 *
 * @see {@link http://www.w3.org/TR/wai-aria-practices/#Listbox}
 * @module
 * @extends module:wc/dom/ariaAnalog
 * @requires module:wc/dom/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 */
define(["wc/dom/ariaAnalog", "wc/dom/initialise", "wc/dom/Widget"],
	/** @param ariaAnalog wc/dom/ariaAnalog @param initialise wc/dom/initialise @param Widget wc/dom/Widget @ignore */
	function(ariaAnalog, initialise, Widget) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/listboxAnalog~ListboxAnalog
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 * @private
		 */
		function ListboxAnalog() {
			/**
			 * Select items immediately on navigation.
			 * @var
			 * @protected
			 * @type Boolean
			 * @override
			 */
			this.selectOnNavigate = true;
			/**
			 * The selection mode is mixed: list boxes may be single or multiple as per select elements.
			 * @var
			 * @protected
			 * @type int
			 * @override
			 */
			this.exclusiveSelect = this.SELECT_MODE.MIXED;
			/**
			 * The defnition of a grouped item in a listbox analog. This is the equivalent of an option element.
			 * @var
			 * @public
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.ITEM = new Widget("", "", {"role": "option"});

			/**
			 * Holds a reference to the last activated member of any identified listBox analog keyed on the listbox
			 * id with the property value being the id of the last activated item. Needed for correct implementation of
			 * group selection (such as with SHIFT+ Click).
			 * @var
			 * @type {Object}
			 * @protected
			 * @override
			 */
			this.lastActivated = {};

			/**
			 * According to the WAI-ARIA spec listbox MUST contain option and option must be contained by listbox.
			 * However the rdf is broken on this point with regard to looking up what 'option' is contained by. So this
			 * is a fill for that bug.
			 *
			 * @constant
			 * @public
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.CONTAINER = new Widget("", "", {"role": "listbox"});
		}

		ListboxAnalog.prototype = ariaAnalog;
		var /** @alias module:wc/ui/listboxAnalog */ instance = new ListboxAnalog();
		instance.constructor = ListboxAnalog;
		initialise.register(instance);
		return instance;
	});
