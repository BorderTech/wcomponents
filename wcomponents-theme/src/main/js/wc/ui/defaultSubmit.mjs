import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";

/**
 * Provide a mechanism to allow any submit button to be deemed the default button of a form or even a section of a form
 * or even a specific input element. The default button is the submitting control of a form which is invoked when the
 * user hits the ENTER key in a submitting input element.
 *
 * NOTE: we insist that button elements (of type submit) have a value attribute.
 *
 */
const submitButtonQs = "button[type='submit']";
const SUBMIT_ATTRIB = "data-wc-submit";
const submitterQs = `[${SUBMIT_ATTRIB}]`;

/**
 * Event listener for keydown event. Used to determine the correct submit button to use as the form submit
 * based on the form submit or the over-ridden defaultSubmit of the input.
 * @function
 * @private
 * @param {KeyboardEvent & { target: HTMLInputElement }} $event the keydown event.
 */
function keyEvent($event) {
	const target = $event.target;
	if (!$event.defaultPrevented && $event.key === "Enter" && target.form && isPotentialSubmitter(target)) {
		const correctSubmit = findCorrectSubmit(target);
		if (correctSubmit) {
			$event.preventDefault();
			event.fire(correctSubmit, "click");
		}
	}
}

/**
 * Determine if an element has a default submit behaviour.
 * @function
 * @private
 * @param {Element} element The 'submitting' element.
 */
function isPotentialSubmitter(element) {
	return element.matches("select,input:not([type='file'])");
}

/**
 * Find the correct submit button to "click" when the ENTER key is pressed on element.
 * NOTE: we do not need this if the element is not in a form
 * @param {HTMLInputElement} element A form control element.
 * @returns {HTMLInputElement} The default submit for the element which may be the form's regular submit button.
 */
function findCorrectSubmit(element) {
	let result;
	const form = element.form;
	result = form ? element.closest(submitButtonQs) : null;
	if (!result) {
		let submitId = element.getAttribute(SUBMIT_ATTRIB);
		const container = submitId ? null : element.closest(submitterQs);
		if (container) {
			submitId = container.getAttribute(SUBMIT_ATTRIB);
		}
		if (submitId) {
			result = document.getElementById(submitId);
		} else if (form) {
			result = form.querySelector(submitButtonQs);
		}
	}
	return /** @type {HTMLInputElement} */ (result);
}

initialise.register({
	/**
	 * Initialise default submit functionality by wiring up focus listeners which will lazily attach other
	 * required listeners later.
	 * @param {HTMLBodyElement} element document.body
	 */
	initialise: function(element) {
		event.add(element, "keydown", keyEvent, -1);
	}
});
