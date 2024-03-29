import initialise from "wc/dom/initialise.mjs";
import event from "wc/dom/event.mjs";
import shed from "wc/dom/shed.mjs";
import i18n from "wc/i18n/i18n.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import required from "wc/ui/validation/required.mjs";
import feedback from "wc/ui/feedback.mjs";
import sprintf from "wc/string/sprintf.mjs";

const NUM_FIELD = "input[type='number']",
	MAX = "max",
	MIN = "min",
	BOOTSTRAPPED = "validation.numberField.bs",
	MAX_FIELD = `${NUM_FIELD}[max]`,
	MIN_FIELD = `${NUM_FIELD}[min]`,
	CONSTRAINED = [MAX_FIELD, MIN_FIELD].join();

/**
 * An array filter function which tests an individual number field to see if it meets constraints of min and
 * max. It is a filter, and we are interested in invalid fields, so we return true on invalid.
 * @function
 * @private
 * @param {HTMLInputElement} element A WNumberField
 * @returns {boolean} false if the field is valid
 */
function isInvalid(element) {
	let result = false;
	const value = element.value;
	let messageKey = "";

	if (value !== "" && !validationManager.isExempt(element)) {
		let min, max;
		if (isNaN(Number(value))) {
			messageKey = "validation_number_nan";
		} else if (element.matches(CONSTRAINED)) {
			max = element.getAttribute(MAX);
			min = element.getAttribute(MIN);
			messageKey = checkMax(element, value, min, max);
			if (!messageKey) {
				messageKey = checkMin(element, value, min);
			}
		}
		if (messageKey) {
			result = true;
			getMessage(messageKey).then(message => {
				message = sprintf(message, validationManager.getLabelText(element), (min || max), max);
				feedback.flagError({ element, message });
			});
		}
	}
	return result;
}

/**
 * Check the max constraint.
 * @param {HTMLInputElement} element The number field.
 * @param {string} value The current value of the number field.
 * @param {string} min The min constraint.
 * @param {string} max The max constraint.
 * @private
 * @function
 * @return {string} An error message key if there is an error.
 */
function checkMax(element, value, min, max) {
	let msgKey = "";
	if (value !== "" && element.matches(MAX_FIELD)) {
		const minNumeric = Number(min);
		const maxNumeric = Number(max);
		const valNumeric = Number(value);
		if (isNaN(valNumeric)) {
			msgKey = isNaN(minNumeric) ? "validation_number_nanwithmax" : "validation_number_nanwithrange";
		} else if (valNumeric > maxNumeric) {
			// if value < min it cannot be > max
			msgKey = isNaN(minNumeric) ? "validation_number_overmax" : "validation_number_outofrange";
		}
	}
	return msgKey;
}

/**
 * Translates a message key to a message.
 * @param {string} key
 * @return {Promise<string>}
 */
function getMessage(key) {
	return /** @type {Promise<string>} */(i18n.translate(key));
}

/**
 * Check the min constraint.
 * @param {HTMLInputElement} element The number field.
 * @param {string} value The current value of the number field.
 * @param {string} min The min constraint.
 * @private
 * @function
 * @returns {string} An error message key if there is an error.
 */
function checkMin(element, value, min) {
	let msgKey = "";
	if (value !== "" && element.matches(MIN_FIELD)) {
		const minNumeric = Number(min);
		const valNumeric = Number(value);
		if (isNaN(valNumeric)) {
			msgKey = "validation_number_nanwithmin";
		} else if (valNumeric < minNumeric) {
			msgKey = "validation_number_undermin";
		}
	}
	return msgKey;
}

/**
 * Validate all WNumberFields in a given container, which could be a WNumberFIeld itself.
 * @function
 * @private
 * @param {Element} container Any element.
 * @returns {Boolean} true if the container is valid.
 */
function validate(container) {
	let result = true;
	const validInputs = required.doItAllForMe(container, NUM_FIELD);
	const candidates = container.matches(NUM_FIELD) ? [container] : Array.from(container.querySelectorAll(NUM_FIELD));
	if (candidates.length) {
		const invalid = candidates.filter(isInvalid);
		result = (invalid.length === 0);
	}
	result = validInputs && result;
	if (!result) {
		console.log(`${import.meta.url} failed validation`);
	}
	return result;
}

/**
 * Re-validate WNumberFields when they change.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLInputElement }}  $event A change event.
 */
function changeEvent($event) {
	const target = $event.target;

	if (validationManager.isValidateOnChange()) {
		if (validationManager.isInvalid(target)) {
			validationManager.revalidationHelper(target, validate);
			return;
		}
		validate(target);
		return;
	}
	validationManager.revalidationHelper(target, validate);
}

/**
 * @param {UIEvent & { target: HTMLInputElement }}  $event A ui event.
 */
function blurEvent($event) {
	const target = $event.target;
	if (!target.value && shed.isMandatory(target)) {
		validate(target);
	}
}

/**
 * A focus listener to attach change events to WNumberFields in browsers which cannot capture.
 * @function
 * @private
 * @param {FocusEvent & { target: HTMLInputElement }} $event A wrapped focus[in] event.
 */
function focusEvent($event) {
	const target = $event.target;
	if (!$event.defaultPrevented && target.matches(NUM_FIELD) && !target[BOOTSTRAPPED]) {
		target[BOOTSTRAPPED] = true;
		event.add(target, "change", changeEvent, 1);
		if (validationManager.isValidateOnBlur()) {
			event.add(target, { type: "blur", listener: blurEvent, pos: 1, capture: true });
		}
	}
}

/**
 * Provides functionality to undertake client validation of WNumberField.
 */
initialise.register({
	/**
	 * Initialisation callback to attach event listeners.
	 * @function module:wc/ui/validation/numberField.initialise
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, pos: 1, capture: true });
	},
	/**
	 * Initialisation callback to do late subscription.
	 * @function module:wc/ui/validation/numberField.postInit
	 */
	postInit: () => validationManager.subscribe(validate)
});
