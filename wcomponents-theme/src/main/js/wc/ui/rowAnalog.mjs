import ariaAnalog from "wc/dom/ariaAnalog";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import table from "wc/ui/table/common";
import icon from "wc/ui/icon";

RowAnalog.prototype = ariaAnalog;
/**
 * Provides ARIA based row in tree grid functionality (lists of selectable options - cf a select element).
 * @module module:wc/ui/RowAnalog
 */
const instance = new RowAnalog();
instance.constructor = RowAnalog;
/**
 * @constructor
 * @alias module:wc/ui/RowAnalog~RowAnalog
 * @private
 */
function RowAnalog() {
	const EXPANDER = "td.wc_table_sel_wrapper";
	/**
	 * The selection mode is mixed: list boxes may be single or multiple as per select elements.
	 * @var
	 * @type number
	 * @override
	 */
	this.exclusiveSelect = this.SELECT_MODE.MIXED;

	/**
	 * @override
	 */
	this.simpleSelection = true;

	/**
	 * The definition of a grouped item in a row analog. This is the equivalent of an option element.
	 * @var
	 * @public
	 * @type {string}
	 * @override
	 */
	this.ITEM = `${table.TR}[aria-selected]`;

	/**
	 * Holds a reference to the last activated member of any identified listBox analog keyed on the listbox
	 * id with the property value being the id of the last activated item. Needed for correct implementation of
	 * group selection (such as with SHIFT+ Click).
	 * @var
	 * @type {Object}
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
	this.CONTAINER = table.TABLE;

	/**
	 * Change icon on select/deselect.
	 * @function
	 * @public
	 * @override
	 * @param {Element} element the element being acted upon
	 * @param {String} action the shed action
	 */
	this.shedObserver = function(element, action) {
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

		this.constructor.prototype.shedObserver.call(this, element, action);
	};
}

initialise.register(instance);
export default instance;
