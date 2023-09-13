/**
 * Provides functionality to undertake client validation of WFileWidget and WMultiFileWidget.
 *
 * @module
 */

import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import i18n from "wc/i18n/i18n.mjs";
import getFirstLabelForElement from "wc/ui/getFirstLabelForElement.mjs";
import isComplete from "wc/ui/validation/isComplete.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import required from "wc/ui/validation/required.mjs";
import multiFileUploader from "wc/ui/multiFileUploader.mjs";

const CONTAINER = multiFileUploader.getWidget().toString(),
	INPUT_ELEMENT = multiFileUploader.getInputWidget().toString(),
	FILE_ELEMENT = ".wc-file";

/**
 * Validates all file upload controls within a container.
 * @function
 * @private
 * @param {Element} container The element being validated.
 * @returns {boolean} true if valid.
 */
function validate(container) {
	return required.complexValidationHelper({
		container,
		widget: CONTAINER,
		constraint: required.CONSTRAINTS.CLASSNAME,
		position: "beforeend",
		/**
		 * @param {HTMLElement} element
		 * @return {string}
		 */
		messageFunc : (element) => {
			const legend = getFirstLabelForElement(element, true) || element.title;
			return i18n.get("validation_multifile_incomplete", legend);
		}
	});
}

/**
 * A WMultiFileWidget which is required will be valid if a "file" is present, even if unselected.  A
 * WMultiFileWidget is complete if it has any file checkboxes in it or if the file input has a value
 * @function
 * @private
 * @param {Element} element The WMultiFileWidget to test.
 * @returns {boolean} true if complete.
 */
function amIComplete(element) {
	/** @type {HTMLInputElement} */
	const upload = element.querySelector(INPUT_ELEMENT);
	return !!(upload.value || element.querySelector(FILE_ELEMENT));
}

/**
 * Subscriber to {@link ./isComplete} used to indicate that the file uploads within a particular
 * container are complete.
 *
 * @function
 * @private
 * @param {Element} container The element being tested.
 * @returns {boolean} true if complete.
 */
function isThisComplete(container) {
	return isComplete.isCompleteHelper(container, CONTAINER, amIComplete);
}

/**
 * Change event on the file input. Somebody wants to upload a file... So we need to re-validate.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLElement }} $event A change event..
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
 * @param { UIEvent & { target: HTMLInputElement }} $event
 */
function blurEvent($event) {
	const element = $event.target;
	if (!element.value && shed.isMandatory(element)) {
		validate(element);
	}
}

/**
 * Focus handler for browsers which do not capture.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLInputElement }} $event A focus event.
 */
function focusEvent({ target }) {
	const INITED_KEY = "validation.multiFileUploader.inited";
	if (!target[INITED_KEY] && target.matches(INPUT_ELEMENT)) {
		target[INITED_KEY] = true;
		event.add(target, "change", changeEvent);
		if (validationManager.isValidateOnBlur()) {
			event.add(target, { type: "blur", listener: blurEvent, pos: 1, capture: true });
		}
	}
}

initialise.register({
	/**
	 * Initialisation callback to attach events.
	 * @function module:wc/ui/validation/fileUpload.initialise
	 * @param {Element} element The element being initialised.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, capture: true });
	},
	/**
	 * Late initialisation callback to attach subscribers.
	 * @function module:wc/ui/validation/fileUpload.postInit
	 */
	postInit: function() {
		validationManager.subscribe(validate);
		isComplete.subscribe(isThisComplete);
	}
});
