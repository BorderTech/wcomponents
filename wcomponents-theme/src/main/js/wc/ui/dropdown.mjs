import group from "wc/dom/group";
import tag from "wc/dom/tag";
import shed from "wc/dom/shed";

/**
 * Provides some extra functionality on single selectable SELECT elements (WDropdown).
 */
const instance = {
	/**
	 * Allow an external component to set selection of a dropdown using a value.
	 * @function module:wc/ui/dropdown.setSelectionByValue
	 * @public
	 * @param {HTMLSelectElement} element The dropdown.
	 * @param {String} value the value of the option to select.
	 */
	setSelectionByValue: function(element, value) {
		if (element && element.tagName === tag.SELECT) {
			const _group = group.get(element);
			for (const option of _group) {
				if (option.value === value || option.text === value) {
					shed.select(option);
					break;
				}
			}
		}
	}
};

export default instance;
