import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import required from "wc/ui/validation/required.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";

/**
 * Provides functionality to undertake client validation of check boxes.
 *
 * **NOTE:** this is for individual WCheckBoxes marked 'required'; check boxes in a WCheckBoxSelect are never individually marked required.
 *
 */

const requiredSelector = "input[type='checkbox'][required]";
/**
 * Validate mandatory check boxes using the {@link ./required} module. This is a subscriber
 * function for {@link module:wc/ui/validation/validationManager}.
 * @function
 * @private
 * @param {Element} container The DOM element being validated.
 * @returns {boolean} true if valid.
 */
function validate(container) {
	const obj = {
		container: container,
		widget: requiredSelector
	};
	return required.complexValidationHelper(obj);
}

/**
 * Subscriber to {@link module:wc/wc/dom/shed} select/deselect to manage validity and feedback.
 * @function
 * @private
 * @param {HTMLInputElement} element The DOM element being selected.
 * @param {String} action The shed action.
 */
function shedSubscriber(element, action) {
	if (element && element.matches(requiredSelector)) {
		if (action === shed.actions.SELECT) {
			if (validationManager.isInvalid(element)) {
				validationManager.setOK(element);
			}
		} else if (validationManager.isValidateOnChange()) {
			validate(element);
		}
	}
}

initialise.register({
	/**
	 * Initialise function to set up check boxes for validation.
	 * @function module:wc/ui/validation/checkBox.initialise
	 * @public
	 */
	initialise: () => {
		shed.subscribe(shed.actions.SELECT, shedSubscriber);
		shed.subscribe(shed.actions.DESELECT, shedSubscriber);
		validationManager.subscribe(validate);
	}
});
