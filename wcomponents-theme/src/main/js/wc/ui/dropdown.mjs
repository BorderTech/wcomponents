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
		if (element?.matches("select")) {
			const _group = Array.from(element.options);
			for (const option of _group) {
				if (option.value === value || option.text === value) {
					option.selected = true;
					break;
				}
			}
		}
	}
};

export default instance;
