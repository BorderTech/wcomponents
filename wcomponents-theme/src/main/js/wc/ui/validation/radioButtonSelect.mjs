import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import required from "wc/ui/validation/required.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import radioButtonSelect from "wc/ui/radioButtonSelect.mjs";

/**
 * Provides functionality to undertake client validation of WRadioButtonSelect.
 */
const instance = {
	/**
	 * Late setup - subscribers to shed and validation manager.
	 * @function module:wc/ui/validation/radioButtonSelect.postInit
	 */
	postInit: function () {
		validationManager.subscribe(validate);
		shed.subscribe(shed.actions.SELECT, validationShedSubscriber);
	}
};

/**
 * Function to validate WRadioButtonSelects in a container.
 * @function
 * @private
 * @param {Element} container Any container element, may be a WRadioButtonSelect.
 * @returns {boolean} true if the container is valid.
 */
function validate(container) {
	const result = required.complexValidationHelper({
		container: container,
		widget: radioButtonSelect.getWidget(),
		constraint: required.CONSTRAINTS.CLASSNAME,
		position: "beforeEnd"
	});
	if (!result) {
		console.log(`${import.meta.url} failed validation`);
	}
	return result;
}

/**
 * Subscribe to shed.actions.SELECT to remove invalid flag when a selection is made. This is simpler than
 * most re-validation because of the single-select nature of a WRadioButtonSelect.
 * @function
 * @private
 * @param {Element} element A html element which has been selected.
 */
function validationShedSubscriber(element) {
	const isRadioButtonSelect = element.matches(radioButtonSelect.getInputWidget().toString());
	const container = isRadioButtonSelect ? element.closest(radioButtonSelect.getWidget().toString()) : null;
	if (container && validationManager.isInvalid(container)) {
		validationManager.setOK(container);
	}
}

initialise.register(instance);
