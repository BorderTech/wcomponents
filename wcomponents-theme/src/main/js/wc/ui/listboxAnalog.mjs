import AriaAnalog from "wc/dom/ariaAnalog.mjs";
import initialise from "wc/dom/initialise.mjs";
import focus from "wc/dom/focus.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import shed from "wc/dom/shed.mjs";

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
 *
 * @alias module:wc/ui/listboxAnalog~ListboxAnalog
 */
class ListboxAnalog extends AriaAnalog {
	/**
	 * Select items immediately on navigation.
	 * @function
	 * @returns {Boolean} always true for this analog.
	 * @override
	 */
	selectOnNavigate = () => true;
	/**
	 * The selection mode is mixed: list boxes may be single or multiple as per select elements.
	 * @var
	 * @type {number}
	 * @override
	 */
	exclusiveSelect = this.SELECT_MODE.MIXED;
	/**
	 * The definition of a grouped item in a listbox analog. This is the equivalent of an option element.
	 * @var
	 * @type {string}
	 * @override
	 */
	ITEM = "[role='option']";

	/**
	 * Holds a reference to the last activated member of any identified listBox analog keyed on the listbox
	 * id with the property value being the id of the last activated item. Needed for correct implementation of
	 * group selection (such as with SHIFT+ Click).
	 * @var
	 * @type {Object}
	 * @override
	 */
	lastActivated = {};

	/**
	 * According to the WAI-ARIA spec listbox MUST contain option and option must be contained by listbox.
	 * However, the rdf is broken on this point with regard to looking up what 'option' is contained by. So this
	 * is a fill for that bug.
	 *
	 * @constant
	 * @type {string}
	 * @override
	 */
	CONTAINER = "[role='listbox']";

	/**
	 * Handle a keyboard event.
	 * @param {KeyboardEvent & { target: HTMLElement }} $event
	 */
	keydownEvent($event) {
		const PRINTABLE_RE = /[ -~]/,
			keyCode = $event.key,
			target = $event.target;
		super.keydownEvent($event);
		if ($event.defaultPrevented || target?.nodeType !== Node.ELEMENT_NODE) {
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
			if (listbox?.nodeType === Node.ELEMENT_NODE) {
				const element = getTextTarget(this.getAvailableOptions(listbox), target, keyCode.toLocaleLowerCase());
				if (target) {
					focus.setFocusRequest(element);
				}
			}
		}
	}

	/**
	 * Get visible, enabled options in a listbox.
	 *
	 * @function
	 * @alias module:wc/ui/listboxAnalog.getAvailableOptions
	 * @param {Element} listbox an instance of a listbox
	 * @returns {HTMLElement[]} the available options in listbox
	 */
	getAvailableOptions(listbox) {
		return /** @type {HTMLElement[]} */(getFilteredGroup(listbox, {
			filter: (getFilteredGroup.FILTERS.visible|getFilteredGroup.FILTERS.enabled),
			containerWd: this.CONTAINER,
			itemWd: this.ITEM,
			shedAttributeOnly: true
		}));
	}

	/**
	 * Deselect all options in a listbox.
	 *
	 * @function
	 * @alias module:wc/ui/listboxAnalog.clearAllOptions
	 * @param {Element} listbox an instance of a listbox
	 */
	clearAllOptions(listbox) {
		const options = listbox ? /** @type {HTMLElement[]} */(getFilteredGroup(listbox, {
			containerWd: this.CONTAINER,
			itemWd: this.ITEM,
			shedAttributeOnly: true
		})) : [];
		options.forEach(function(next) {
			shed.deselect(next, true);  // do not publish they are already hidden and are not important for anything else.
			next.tabIndex = 0;
		});
	}


	/**
	 * Get the value of an option in a listbox analog.
	 *
	 * @function
	 * @alias module:wc/ui/listboxAnalog.getOptionValue
	 * @param {Element} option the option in which we are interested
	 * @param {boolean} [lowerCase] if true return a lowercase version of the value
	 * @param {boolean} [forceText] if true get the textContent in preference to the value
	 * @returns {String} the value of the option.
	 */
	getOptionValue(option, lowerCase, forceText) {
		const txt = forceText ? option.textContent :
			(option.hasAttribute(this.VALUE_ATTRIB) ? option.getAttribute(this.VALUE_ATTRIB) : option.textContent);
		return lowerCase ? txt.toLocaleLowerCase() : txt;
	}
}

/**
 * Given an option in a listbox and a printable character, find the next option (if any) which starts
 * with that character.
 *
 * @function
 * @private
 * @param {HTMLElement[]} options The container for the list of options, already calculated in the calling
 *     function so just passed through for convenience.
 * @param {HTMLElement} start The element from which we start the search. This will not return even if
 *     it starts with the character we want.
 * @param {String} keyName The character we are searching for.
 * @returns {HTMLElement} The next available option which starts with keyName (if any), or undefined.
 */
function getTextTarget(options, start, keyName) {
	const startIdx = options.indexOf(start);
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

export default initialise.register(new ListboxAnalog());
