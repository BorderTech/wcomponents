import initialise from "wc/dom/initialise";
import i18n from "wc/i18n/i18n";
import event from "wc/dom/event";
import shed from "wc/dom/shed";
import sprintf from "lib/sprintf";
import dateField from "wc/ui/dateField";
import required from "wc/ui/validation/required";
import validationManager from "wc/ui/validation/validationManager";
import feedback from "wc/ui/feedback";
import wcconfig from "wc/config";

const textSelector = "input[type='text'],input:not([type])";
const emailSelector = "input[type='email']";
// input types which are not needed for validation other than mandatory-ness.
const passwordSelector = "input[type='password']";
const telSelector = "input[type='tel']";
const fileSelector = "input[type='file']";
const patternSelector = "input[pattern]";
const minSelector = "input[minlength]";
const withPatternSelectors = [patternSelector, minSelector, emailSelector].join();
const input_selectors = [passwordSelector, telSelector, fileSelector, emailSelector, textSelector].join();
const DEFAULT_RX = /^(?:".+"|[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~]+)@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)+$/;
let rxString = "";

/**
 * Test for an input which we are interested in.
 * @param {HTMLElement} element The component to test.
 * @returns {Boolean} true if the element is an input which we need to test.
 */
function isValidatingInput(element) {
	return element.matches(input_selectors) || (element.matches(textSelector) && !dateField.isOneOfMe(element));
}

/**
 * Adds an error message to a component.
 * @function
 * @private
 * @param {HTMLElement} element The DOM element with the error.
 * @param {string} flag The framework for the error message in sprintf format.
 */
function _flagError(element, flag) {
	const message = sprintf.sprintf(flag, validationManager.getLabelText(element));
	feedback.flagError({ element: element, message: message });
}

/**
 * Array filter function which tests an individual field to see if it meets constraints (min and pattern).
 * @param {HTMLInputElement} element A constrained field.
 * @returns {Boolean} true if the field is invalid.
 */
function isInvalid(element) {
	let result = false;
	const value = element.value;

	if (value && !validationManager.isExempt(element)) {
		let regexp, patternFlag, flag = "";
		const concatenator = i18n.get("validation_concatenator");
		// min length
		let mask = element.getAttribute("minlength");
		if (mask && value.length < parseInt(mask, 10)) {
			result = true;
			flag = i18n.get("validation_text_belowmin", "%s", mask);
		}
		// pattern (first email)
		if (element.matches(emailSelector)) {
			if (rxString === "") {
				const conf = wcconfig.get("wc/ui/validation/textField", {
					rx: null
				});
				rxString = conf["rx"];
			}

			regexp = rxString ? new RegExp(rxString) : DEFAULT_RX;
			patternFlag = i18n.get("validation_email_format");
		} else {
			mask = element.getAttribute("pattern");
			if (mask) {
				try {
					regexp = new RegExp(`^(?:${mask})$`);
					patternFlag = i18n.get("validation_common_pattern");
				} catch (e) {
					regexp = null;
					// console.log("cannot convert input mask to regular expression, assuming valid");
				}
			}
		}
		if (regexp && !(regexp.test(value))) {
			if (flag) {
				patternFlag = patternFlag.replace("%s ", "");
				flag = sprintf.sprintf(concatenator, flag, patternFlag);
			} else {
				flag = patternFlag;
			}
			result = true;
		}
		if (result) {
			_flagError(element, flag);
		}
	}
	return result;
}

/**
 * Validates all the constrained fields we are interested in, within a given container.
 * @param {HTMLElement} container An element, preferably one containing form controls with input masks, otherwise
 *    why are we here?
 * @returns {boolean} true if the container is valid.
 */
function validate(container) {
	let validConstrained = true;
	const helperObj = {
		container: container,
		widget: input_selectors,
		/**
		 * @param {HTMLInputElement} next
		 * @return {boolean}
		 */
		filter: next => !(next.value || dateField.isOneOfMe(next))
	};

	/* This does 'required' validation for all text-style inputs apart from date fields. */
	/* This does 'required' validation for all text-style inputs apart from date fields. */
	const _requiredTextFields = required.complexValidationHelper(helperObj);

	// do the constraint tests
	const candidates = container.matches(withPatternSelectors) ? [container] : container.querySelectorAll(withPatternSelectors);
	if (candidates && candidates.length) {
		validConstrained = ((Array.from(candidates).filter(isInvalid)).length === 0);
	}
	return _requiredTextFields && validConstrained;
}

/**
 * Change event listener to revalidate.
 * @param {UIEvent & { target: HTMLInputElement }} $event A wrapped change event.
 */
function changeEvent({ target }) {
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
 * Blur event listener to revalidate.
 * @param {UIEvent & { target: HTMLInputElement }} $event
 */
function blurEvent({ target }) {
	if (!target.value && shed.isMandatory(target)) {
		validate(target);
	}
}

/**
 * Focus event listener to attach change events on first focus.
 * @param {FocusEvent & { target: HTMLInputElement }} $event A wrapped focus event.
 */
function focusEvent({ target, defaultPrevented }) {
	const BOOTSTRAPPED = "validation.textInput.bs";
	if (!defaultPrevented && !target[BOOTSTRAPPED] && isValidatingInput(target)) {
		target[BOOTSTRAPPED] = true;
		event.add(target, "change", changeEvent, 1);
		if (validationManager.isValidateOnBlur()) {
			event.add(target, { type: "blur", listener: blurEvent, pos: 1, capture: true });
		}
	}
}


initialise.register({
	/**
	 * Initialise callback to attach event listeners.
	 * @param {HTMLElement} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, pos: 1, capture: true });
	},

	/**
	 * Late initialisation callback to wire up validation manager subscriber.
	 */
	postInit: () => validationManager.subscribe(validate)
});
/**
 * @typedef {Object} module:wc/ui/validation/textField.config Optional module configuration.
 * @property {String} rx The email regular expression as a string.
 */
