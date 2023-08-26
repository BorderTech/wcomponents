/**
* Provides functionality which interrupts clicks on form submitting and validating buttons if the validation
* area is not in a valid state.
*
* @see {@link module:wc/ui/cancelUpdate}
*/

import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import focus from "wc/dom/focus";
import validationManager from "wc/ui/validation/validationManager";
import ajaxRegion from "wc/ui/ajaxRegion";

const formSelector = "form";
const submitControlSelector = "button[type='submit']";
const noValidateButtonSelector = `${submitControlSelector}[formnovalidate]`;

/**
 * Determines if a form or subsection thereof is in a 'valid' state.
 * @function
 * @private
 * @param {HTMLButtonElement} submitter The control which has instigated the data submission. This is used to determine
 *     if we need to validate a sub-form based on a property of the submitter.
 * @returns {boolean} true if the form (or sub-form) is invalid.
 */
function isInvalid(submitter) {
	const validationId = submitter.getAttribute("data-wc-validate");
	let validationContainer;
	if (validationId) {
		validationContainer = document.getElementById(validationId);
	} else if (ajaxRegion.getTrigger(submitter)) {
		// if a submitter is an ajax trigger and does not have a validating region
		// we do not validate.
		return false;
	}
	validationContainer = validationContainer || submitter.closest(formSelector);
	return validationContainer ? !validationManager.isValid(validationContainer) : false;
}

/**
 * Click event handler. Prevents default if the click is on a validating button and the form or validation
 * container is not in a valid state.
 * @function
 * @private
 * @param {MouseEvent} $event A wrapped click event.
 */
function clickEvent($event) {
	const {
		target,
		defaultPrevented
	} = $event;

	if (defaultPrevented) {
		return;
	}
	const button = target.closest(submitControlSelector);

	if (button && !button.matches(noValidateButtonSelector) && focus.canFocus(button) && isInvalid(button)) {
		$event.preventDefault();
	}
}

/**
 * Initialisation function to add a click handler. The handler is added as a late handler as we want other
 * handlers to do any state changes and have the opportunity to cancel the event before we bother with
 * handling it.
 *
 * @function  module:wc/ui/validation/cancelUpdate.initialise
 * @param {HTMLElement} element The HTML element being initialised, usually document.body.
 */
initialise.register({ initialise: element => event.add(element, "click", clickEvent, 1) });
