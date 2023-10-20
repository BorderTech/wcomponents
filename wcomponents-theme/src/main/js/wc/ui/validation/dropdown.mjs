import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";
import minMax from "wc/ui/validation/minMax.mjs";
import validationManager from "wc/ui/validation/validationManager.mjs";
import required from "wc/ui/validation/required.mjs";
import getFilteredGroup from "wc/dom/getFilteredGroup.mjs";

/**
 * Validation function for select elements.
 * @function
 * @private
 * @param {Element} container A DOM element: may be a SELECT or may (or may not) contain SELECTS.
 * @returns {boolean} true if valid.
 */
function validate(container) {
	const _required = required.doItAllForMe(container, "select");
	const widget = "select[multiple]";
	let constrained = true;

	if (!container.matches("select") || container.matches(widget)) {
		// do not bother with the expensive constraint checking if we are just (re)validating a single select
		constrained = minMax({ container, widget, selectedFunc: getFilteredGroup });
	}
	const result = _required && constrained;
	if (!result) {
		console.log(`${import.meta.url} failed validation`);
	}
	return result;
}

/**
 * Change event handler to re-validate previously invalid selects.
 * @function
 * @private
 * @param {UIEvent & { target: HTMLElement }} $event a wrapped change event as published by the WComponent event manager.
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
 *
 * @param {UIEvent & { target: HTMLElement }} $event
 */
function blurEvent({ target }) {
	if (shed.isMandatory(target) && !validationManager.isInvalid(target)) {
		validate(target);
	}
}

/**
 * First focus wires up a change listener on an individual select element in browsers which cannot capture.
 * @function
 * @private
 * @param {FocusEvent & { target: HTMLElement }} $event A focusin event.
 */
function focusEvent({ target, defaultPrevented }) {
	const BOOTSTRAPPED = "wc/ui/dropdown.bootstrapped";
	if (!defaultPrevented && target.matches("select") && !target[BOOTSTRAPPED]) {
		target[BOOTSTRAPPED] = true;
		event.add(target, "change", changeEvent, 1);
		if (validationManager.isValidateOnBlur()) {
			event.add(target, { type: "blur", listener: blurEvent, pos: 1, capture: true });
		}
	}
}

initialise.register({
	/**
	 * Wire up appropriate event listeners.
	 * @function module:wc/ui/validation/dropdown.initialise
	 * @param {Element} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, { type: "focus", listener: focusEvent, pos: 1, capture: true });
	},
	/**
	 * Wire up subscribers in late initialisation.
	 * @function module:wc/ui/validation/dropdown.postInit
	 */
	postInit: () => validationManager.subscribe(validate)
});

