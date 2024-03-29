import group from "wc/dom/group.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";
import cbrShedPublisher from "wc/dom/cbrShedPublisher.mjs";
import "wc/ui/fieldset.mjs";

// Note `wc/ui/fieldset` is implicitly required to handle various aspects of managing the wrapper element.
const radioSelector = cbrShedPublisher.getWidget("r").toString();
const radioButtonSelectSelector = "fieldset.wc-radiobuttonselect";

/**
 * Provides functionality for groups of radio buttons generated by a WRadioButtonSelect.
 */
const instance = {
	/**
	 * Get the widget which describes a radioButtonSelect.
	 * @function
	 * @public
	 * @returns {string}
	 */
	getWidget: () => radioButtonSelectSelector,

	/**
	 * Get the widget which describes a single control in a radioButtonSelect.
	 * @function module:wc/ui/radioButtonSelect.getInputWidget
	 * @returns {string}
	 */
	getInputWidget: () => radioSelector,

	/**
	 * Allow an external component to set selection of a radio button in a RadioButtonSelect using a value.
	 * @function module:wc/ui/radioButtonSelect.setSelectionByValue
	 * @public
	 * @param {Element} element The radioButtonSelect.
	 * @param {String} value the value of the radio button to select.
	 */
	setSelectionByValue: function(element, value) {
		if (element.matches(radioButtonSelectSelector)) {
			if (!value) {
				// deselectAll
				getFilteredGroup(element).forEach((next) => shed.deselect(next));  // should be only one
			} else {
				const _group = group.getGroup(element, radioSelector);
				for (let i = 0; i < _group.length; ++i) {
					let option = _group[i];
					if (option.value === value) {
						shed.select(option);
						break;
					}
				}
			}
		}
	},

	/**
	 * Late setup - subscribers to {@link module:wc/dom/shed}.
	 * @function module:wc/ui/radioButtonSelect.postInit
	 * @public
	 */
	postInit: function () {
		shed.subscribe(shed.actions.MANDATORY, shedSubscriber);
		shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
	}
};
/**
 * Listen for mandatory/optional and set the group's radio buttons.
 * @function
 * @private
 * @param {Element} element The element being acted upon.
 * @param {String} action One of the {@link module:wc/dom/shed~actions}: MANDATORY or OPTIONAL
 */
function shedSubscriber(element, action) {
	if (element && element.matches(radioButtonSelectSelector)) {
		group.getGroup(element, radioSelector).forEach(next => shed[action](next));
	}
}

export default initialise.register(instance);
