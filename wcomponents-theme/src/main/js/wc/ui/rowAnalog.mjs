import AriaAnalog from "wc/dom/ariaAnalog.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import table from "wc/ui/table/common.mjs";
import icon from "wc/ui/icon.mjs";

const EXPANDER = "td.wc_table_sel_wrapper";

/**
 * Provides ARIA based row in tree grid functionality (lists of selectable options - cf a select element).
 *
 * @alias module:wc/ui/RowAnalog~RowAnalog
 */
class RowAnalog extends AriaAnalog {

	/**
	 * The selection mode is mixed: list boxes may be single or multiple as per select elements.
	 * @var
	 * @type number
	 * @override
	 */
	exclusiveSelect = this.SELECT_MODE.MIXED;

	/**
	 * @override
	 */
	simpleSelection = true;

	/**
	 * The definition of a grouped item in a row analog. This is the equivalent of an option element.
	 * @var
	 * @public
	 * @type {string}
	 * @override
	 */
	ITEM = `${table.TR}[aria-selected]`;

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
	 * @public
	 * @type {string}
	 * @override
	 */
	CONTAINER = table.TABLE;

	/**
	 * Change icon on select/deselect.
	 * @function
	 * @override
	 * @param {Element} element the element being acted upon
	 * @param {String} action the shed action
	 */
	shedObserver(element, action) {
		const { SELECT, DESELECT } = shed.actions;
		if (element && (action === SELECT || action === DESELECT) && element.matches(this.ITEM)) {
			const cell = Array.from(element.children).find(kid => kid.matches(EXPANDER));
			if (cell) {
				const isMultiSelect = this.isMultiSelect(element);
				let add, remove;
				if (action === SELECT) {
					add = isMultiSelect ? "fa-check-square-o" : "fa-dot-circle-o";
					remove = isMultiSelect ? "fa-square-o" : "fa-circle-o";
				} else {
					add = isMultiSelect ? "fa-square-o" : "fa-circle-o";
					remove = isMultiSelect ? "fa-check-square-o" : "fa-dot-circle-o";
				}
				icon.change(cell, add, remove);
			}
		}

		super.shedObserver(element, action);
	};
}

export default initialise.register(new RowAnalog());
