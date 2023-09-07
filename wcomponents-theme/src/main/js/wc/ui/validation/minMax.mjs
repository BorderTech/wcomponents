import getFirstLabelForElement from "wc/ui/getFirstLabelForElement";
import i18n from "wc/i18n/i18n";
import sprintf from "lib/sprintf";
import validationManager from "wc/ui/validation/validationManager";
import feedback from "wc/ui/feedback";

const MIN = "data-wc-min",
	MAX = "data-wc-max";
/**
 * Provides all of the min and max selection constraint validation for multi-selectable controls.
 *
 * @todo Split this up to remove nested functions.
 *
 * @function
 * @alias module:wc/ui/validation/minMax
 * @param {{container: Element, widget: string, selectedFunc: (function((HTMLElement|HTMLElement[]), module:wc/dom/getFilteredGroup~config=): HTMLElement[] | {filtered: HTMLElement[], unfiltered: HTMLElement[]})}} conf Contains the validator configuration options.
 * @returns {boolean} true if the tested component meets its constraints (including if their are no constraints).
 */
export default function minMax(conf) {
	const container = conf.container,
		widget = conf.widget?.toString(),
		selectedFunc = conf.selectedFunc,
		filter = conf.filter || _filter,
		selectionElementFunc = conf.selectFunc,
		flagFunc = conf.flag || flagError,
		minText = conf.minText || "validation_common_undermin",
		maxText = conf.maxText || "validation_common_overmax";
	if (!(widget && container)) {
		return true;
	}

	let selectables;
	/**
	 * Array filter function for typical selection constraint testing.
	 * @function
	 * @private
	 * @param {HTMLElement} selectable an instance of the selectable component being tested
	 * @returns {boolean} true if the selectable does not meet its constraints
	 */
	function _filter(selectable) {
		let isInvalid = false,
			min = Number(selectable.getAttribute(MIN)),
			max = Number(selectable.getAttribute(MAX)),
			count = 0, limit, flag, testElement = selectable, listLabel, selections;

		if (selectionElementFunc) {
			testElement = selectionElementFunc(selectable);
		}

		if ((min || max)) {
			selections = selectedFunc(testElement);
			if (selections) {
				count = selections.length;
			}
			if (count) {
				if (min && count < min) {
					isInvalid = true;
					limit = min;
					flag = i18n.get(minText);
				} else if (max && count > max) {
					isInvalid = true;
					limit = max;
					flag = i18n.get(maxText);
				}
			}
		}
		if (isInvalid) {
			if (testElement !== selectable) {
				listLabel = getFirstLabelForElement(testElement, true) || testElement.title;
			}
			flagFunc(selectable, flag, limit, listLabel);
		}
		return isInvalid;
	}

	/**
	 * Default error flagging function for min/max constraint validation.
	 * @function
	 * @private
	 * @param {Element} selectable the component being tested.
	 * @param {String} flag The error message frame (sprintf formatted).
	 * @param {number} limit The number of the constraint.
	 * @param {String} [secondaryLabel] The text content of an inner label to add context to complex error
	 *    messages. This is used for validation of WMultiSelectPair.
	 */
	function flagError(selectable, flag, limit, secondaryLabel) {
		const labelText = validationManager.getLabelText(selectable);
		const message = /** @type {string} */(sprintf.sprintf(flag, labelText, limit, secondaryLabel));

		feedback.flagError({ element: selectable, message });
	}

	if (container.matches(widget)) {
		selectables = [container].filter(filter);
	} else {
		selectables = Array.from(container.querySelectorAll(widget)).filter(filter);
	}
	return !selectables?.length;

}

/**
 * The configuration object for the module's return function.
 * @typedef {Object} module:wc/ui/validation/minMax~config
 * @property {HTMLElement} container That which is being validated. Usually a FORM element.
 * @property {string} widget Description of the component being tested.
 * @property {Function} selectedFunc Function to get the list of selections from the test element.
 * @property {Function} [filter] Function used as an array filter to find and flag errors.
 * @property {Function} [selectionElementFunc] Function to determine the element which holds the selection if
 *    not the output of widget.findDescendants(container).
 * @property {Function} [flagFunc] Function used to flag the error message if not this functions' inbuilt
 *    flag function.
 * @property {String} [minText] The i18n argument for errors where fewer than min options are selected.
 * @property {String} [maxText] The i18n argument for errors where more than max options are selected.
 */
