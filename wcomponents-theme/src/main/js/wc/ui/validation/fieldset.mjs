/**
 * Provides functionality to undertake client validation for WFieldSet.
 */

import i18n from "wc/i18n/i18n";
import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import isComplete from "wc/ui/validation/isComplete";
import validationManager from "wc/ui/validation/validationManager";
import required from "wc/ui/validation/required";
import fieldset from "wc/ui/fieldset";
import feedback from "wc/ui/feedback";

const fieldsetSelector = `${fieldset.getWidget().toString()}.wc-fieldset`;
const invalidSelector = "wc_req[aria-invalid='true']";
const INITED_KEY = "validation.fieldset.init";

/**
 * This is an Array filter function which should return true only if the fieldset is NOT in a
 * successful state. A fieldset is successful if at least one interactive control within the fieldset is
 * complete. Therefor it is not successful only if EVERY interactive control is not complete.
 *
 * @function
 * @private
 * @param {Element} element A FIELDSET element
 * @returns {boolean} true if the fieldset is not complete.
 */
function filterFieldsets(element) {
	return !isComplete.isContainerComplete(element);
}

/**
 * Fieldset required state validation a fieldset is successful if at least one interactive control within
 * the fieldset is complete.
 *
 * @function
 * @private
 * @param {Element} container The DOM element being validated.
 * @returns {boolean} true if container is valid.
 */
function validate(container) {
	let result = true;
	const elements = required.getRequired(container, fieldsetSelector, required.CONSTRAINTS.CLASSNAME);
	// are any not complete?
	const incomplete = elements ? elements.filter(filterFieldsets) : null;
	if (incomplete.length) {
		result = false;
		incomplete.forEach(function (next) {
			const message = i18n.get("validation_common_incompletegroup", validationManager.getLabelText(next));
			feedback.flagError({
				element: next,
				message
			});
		});
	}
	return result;
}

/**
 * This function determines if a fieldset needs to be revalidated and if it does then it resets the
 * validation. *NOTE:* WFieldSet only needs validation if "required".
 *
 * * If something is shown or enabled inside an invalid fieldset it may be populated, making the fieldset valid;
 * * if something is hidden or disabled inside an invalid fieldset it may make the fieldset 'empty' thereby making the fieldset valid.
 *
 * In both cases we need to revalidate to make sure.
 *
 * If something changes inside an invalid fieldset we also need to revalidate the fieldset. This is done by having this module subscribe
 * to validationManager.
 *
 * @function
 * @private
 * @param {Element} element a control which may be inside an invalid fieldset.
 */
function revalidate(element) {
	let result = true;
	let container = element.matches(invalidSelector) ? element : element.closest(invalidSelector);

	while (container) {
		let initiallyInvalid = validationManager.isInvalid(container);
		result = validate(container);

		if (result) {
			if (initiallyInvalid) {
				validationManager.setOK(container);
			}
			container = container.parentElement.closest(invalidSelector);
		} else {
			break;  // if the innermost invalid fieldset is still invalid there is no point traversing
		}
	}
}

/**
 * Subscriber for {@link module:wc/dom/shed} functions which affect the validity of fieldsets.
 *
 * @function
 * @private
 * @param {CustomEvent & { target: HTMLElement }} element The element acted on by shed.
 */
function validationShedSubscriber({ target }) {
	const targetFieldset = target ? target.closest(fieldsetSelector) : null;
	if (targetFieldset) {
		if (validationManager.isValidateOnChange()) {
			if (validationManager.isInvalid(targetFieldset)) {
				revalidate(targetFieldset);
			} else {
				validate(targetFieldset);
			}
		} else {
			revalidate(targetFieldset);
		}
	}
}

/**
 *
 * @param {UIEvent & { target: HTMLElement, currentTarget: HTMLElement }} $event
 */
function changeEvent($event) {
	/* var element = $event.target,
		targetFieldset;
	if (element && validationManager.isValidateOnChange() && (targetFieldset = element.closest(fieldsetSelector))) {
		if (validationManager.isInvalid(targetFieldset)) {
			revalidate(targetFieldset);
		} else {
			validate(targetFieldset);
		}
	} */
	const element = $event.currentTarget;
	if (!(element && validationManager.isValidateOnChange())) {
		return;
	}
	if (validationManager.isInvalid(element)) {
		revalidate(element);
	} else {
		validate(element);
	}
}

/**
 * @param {FocusEvent & { target: HTMLElement }} $event
 */
function focusEvent({ target }) {
	const targetFieldset = (target && validationManager.isValidateOnChange()) ? target.closest(fieldsetSelector) : null;
	if (targetFieldset && !targetFieldset[INITED_KEY]) {
		targetFieldset[INITED_KEY] = true;
		event.add(targetFieldset, "change", changeEvent, 1);
	}
}

initialise.register({
	/**
	 * Initialise callback to set up event listeners.
	 * @function module:wc/ui/validation/textArea.initialise
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, pos: 1, capture: true });
	},

	/**
	 * Initialise callback.
	 *
	 * @function module:wc/ui/validation/fieldset.postInit
	 */
	postInit: function() {
		validationManager.subscribe(validate);
		validationManager.subscribe(revalidate, true);
		event.add(document.body, shed.actions.SELECT, validationShedSubscriber);
		event.add(document.body, shed.actions.DESELECT, validationShedSubscriber);
		event.add(document.body, shed.actions.ENABLE, validationShedSubscriber);
		event.add(document.body, shed.actions.DISABLE, validationShedSubscriber);
		event.add(document.body, shed.actions.SHOW, validationShedSubscriber);
		event.add(document.body, shed.actions.HIDE, validationShedSubscriber);
	}
});

