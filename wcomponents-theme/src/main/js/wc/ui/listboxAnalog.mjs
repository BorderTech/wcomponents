import ariaAnalog from "wc/dom/ariaAnalog";
import initialise from "wc/dom/initialise";
import focus from "wc/dom/focus";
import getFilteredGroup from "wc/dom/getFilteredGroup";
import shed from "wc/dom/shed";

let instance;

/**
 * @constructor
 * @alias module:wc/ui/listboxAnalog~ListboxAnalog
 * @extends AriaAnalog
 * @private
 */
function ListboxAnalog() {
	/**
	 * Select items immediately on navigation.
	 * @function
	 * @protected
	 * @returns {Boolean} always true for this analog.
	 * @override
	 */
	this.selectOnNavigate = () => true;
	/**
	 * The selection mode is mixed: list boxes may be single or multiple as per select elements.
	 * @var
	 * @protected
	 * @type {number}
	 * @override
	 */
	this.exclusiveSelect = this.SELECT_MODE.MIXED;
	/**
	 * The definition of a grouped item in a listbox analog. This is the equivalent of an option element.
	 * @var
	 * @public
	 * @type {string}
	 * @override
	 */
	this.ITEM = "[role='option']";

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
	 * However, the rdf is broken on this point with regard to looking up what 'option' is contained by. So this
	 * is a fill for that bug.
	 *
	 * @constant
	 * @public
	 * @type {string}
	 * @override
	 */
	this.CONTAINER = "[role='listbox']";

	/**
	 * Given an option in a listbox and a printable character, find the next option (if any) which starts
	 * with that character.
	 *
	 * @function
	 * @private
	 * @param {HTMLElement} listbox The container for the list of options, already calculated in the calling
	 *     function so just passed through for convenience.
	 * @param {HTMLElement} start The element from which we start the search. This will not return even if
	 *     it starts with the character we want.
	 * @param {String} keyName The character we are searching for.
	 * @returns {HTMLElement} The next available option which starts with keyName (if any), or undefined.
	 */
	function getTextTarget(listbox, start, keyName) {
		const options = instance.getAvailableOptions(listbox),
			startIdx = options.indexOf(start);
		let result;
		if (startIdx > -1) {
			for (let i = startIdx + 1; i < options.length; ++i) {
				let next = options[i];
				let txt = next.textContent;
				if (txt && txt[0].toLocaleLowerCase() === keyName) {
					result = next;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Handle a keyboard event.
	 * @param {KeyboardEvent} $event
	 */
	this.keydownEvent = function($event) {
		const PRINTABLE_RE = /[ -~]/,
			keyCode = $event.key,
			target = $event.target;
		this.constructor.prototype.keydownEvent.call(this, $event);
		if ($event.defaultPrevented || !(target instanceof HTMLElement)) {
			return;
		}

		/*
			Here we are trying to determine if a printable character was pressed.
			Using the Regular Expression restricts it to English, well ascii codes 32 to 126.
			We could simply use the length = 1 check in to handle unicode characters too.
		 */
		if (keyCode.length === 1 && PRINTABLE_RE.test(keyCode)) {

			/* printable char pressed: find the next matching option */
			const listbox = target.closest(this.CONTAINER);
			if (listbox && listbox instanceof  HTMLElement) {
				const element = getTextTarget(listbox, target, keyCode.toLocaleLowerCase());
				if (target) {
					focus.setFocusRequest(element);
				}
			}
		}
	};

	/**
	 * Get visible, enabled options in a listbox.
	 *
	 * @function
	 * @alias module:wc/ui/listboxAnalog.getAvailableOptions
	 * @public
	 * @param {HTMLElement} listbox an instance of a listbox
	 * @returns {HTMLElement[]} the available options in listbox
	 */
	this.getAvailableOptions = function(listbox) {
		return getFilteredGroup(listbox, {
			filter: (getFilteredGroup.FILTERS.visible|getFilteredGroup.FILTERS.enabled),
			containerWd: this.CONTAINER,
			itemWd: this.ITEM,
			shedAttributeOnly: true
		});
	};

	/**
	 * Deselect all options in a listbox.
	 *
	 * @function
	 * @alias module:wc/ui/listboxAnalog.clearAllOptions
	 * @public
	 * @param {HTMLElement} listbox an instance of a listbox
	 */
	this.clearAllOptions = function(listbox) {
		const options = listbox ? getFilteredGroup(listbox, {
			containerWd: this.CONTAINER,
			itemWd: this.ITEM,
			shedAttributeOnly: true
		}): null;
		if (options) {
			options.forEach(function(next) {
				shed.deselect(next, true);  // do not publish they are already hidden and are not important for anything else.
				next.tabIndex = 0;
			});
		}
	};


	/**
	 * Get the value of an option in a listbox analog.
	 *
	 * @function
	 * @alias module:wc/ui/listboxAnalog.getOptionValue
	 * @public
	 * @param {HTMLElement} option the option in which we are interested
	 * @param {boolean} [lowerCase] if true return a lowercase version of the value
	 * @param {boolean} [forceText] if true get the textContent in preference to the value
	 * @returns {String} the value of the option.
	 */
	this.getOptionValue = function (option, lowerCase, forceText) {
		const txt = forceText ? option.textContent :
			(option.hasAttribute(this.VALUE_ATTRIB) ? option.getAttribute(this.VALUE_ATTRIB) : option.textContent);
		return lowerCase ? txt.toLocaleLowerCase() : txt;
	};
}

ListboxAnalog.prototype = ariaAnalog;
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
 * @see http://www.w3.org/TR/wai-aria-practices/#Listbox
 */
instance = new ListboxAnalog();
instance.constructor = ListboxAnalog;
export default initialise.register(instance);
