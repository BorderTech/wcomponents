/**
 * Provides functionality to undertake client validation of WTextArea.
 *
 * @module
 */

import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import i18n from "wc/i18n/i18n.mjs";
import sprintf from "wc/string/sprintf.mjs";
import required from "wc/ui/validation/required.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import feedback from "wc/ui/feedback.mjs";
import textArea from "wc/ui/textArea.mjs";

const TEXTAREA = textArea.getWidget();


/**
* Undertake required validation for WTextArea.
*
* @function
* @private
* @param {Element} container The element being validated.
* @returns {Boolean} true if all required WTextAreas in container are complete.
*/
function _validateRequired(container) {
	const obj = {
		container,
		widget: TEXTAREA
	};
	return required.complexValidationHelper(obj);
}


/**
 * Tests an individual WTextArea to see if it meets constraints of minLength and maxLength.
 * This is an array filter so returns false if the field is valid.
 *
 * @function
 * @private
 * @param {HTMLTextAreaElement} element a WTextArea
 * @returns {Boolean} true if the field is invalid.
 */
function doContraintValidityTest(element) {
	let result = false;
	const value = element.value;
	if (value && !validationManager.isExempt(element)) {
		const size = textArea.getLength(element);
		let mask = textArea.getMaxlength(element);
		let flag;
		if (mask && size > mask) {
			result = true;
			flag = i18n.get("validation_textarea_overmax", "%s", mask, size);
		} else {
			mask = textArea.getMinlength(element);
			if (mask && size < mask) {
				result = true;
				flag = i18n.get("validation_text_belowmin", "%s", mask);
			}
		}

		if (result) {
			const message = sprintf(flag, validationManager.getLabelText(element));
			feedback.flagError({ element, message });
		}
	}
	return result;
}

/**
 * Validate all WTextAreas in a given container.
 *
 * @function
 * @private
 * @param {Element} container A DOM node, preferably one containing constrained text areas.
 * @returns {boolean} true if container is valid.
 */
function validate(container) {
	const _required = _validateRequired(container);
	if (!_required) {
		return false;
	}
	const _widget = textArea.getWidget(true),
		candidates = (container.matches(_widget) ? [container] : container.querySelectorAll(_widget));
	const invalid = Array.from(candidates).filter(doContraintValidityTest);
	const result = invalid.length === 0;
	if (!result) {
		console.log(`${import.meta.url} failed validation`);
	}
	return result;
}

/**
 * Regular (non-constrained) text areas get a change event listener to revalidate mandatory and ancestor fieldsets.
 *
 * @function
 * @private
 * @param {UIEvent & { target: HTMLTextAreaElement }} $event A change event.
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
 * @param {UIEvent & { target: HTMLTextAreaElement }} $event
 */
function blurEvent({ target }) {
	if (!target.value && shed.isMandatory(target)) {
		validate(target);
	}
}

/**
 * Use first focus to attach other event listeners.
 *
 * @function
 * @private
 * @param {UIEvent & { target: HTMLElement }} $event A change event.
 */
function focusEvent({ target }) {
	const INITED_KEY = "validation.textArea.init";
	if (!target[INITED_KEY] && target.matches(TEXTAREA)) {
		target[INITED_KEY] = true;
		event.add(target, "change", changeEvent, 1);
		if (validationManager.isValidateOnBlur()) {
			event.add(target, { type: "blur", listener: blurEvent, pos: 1, capture: true });
		}
	}
}

initialise.register({
	/**
	 * Initialise callback to set up event listeners.
	 * @function module:wc/ui/validation/textArea.initialise
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: element => event.add(element, { type: "focus", listener: focusEvent, pos: 1, capture: true }),

	/**
	 * Late initialisation to attach validation manager subscriber.
	 *
	 * @function module:wc/ui/validation/textArea.postInit
	 */
	postInit: () => validationManager.subscribe(validate)
});
