/**
 * Provides functionality which interrupts clicks on form submitting and validating buttons if the validation
 * area is not in a valid state.
 *
 * @see {@link module:wc/ui/cancelUpdate}
 *
 * @module wc/ui/validation/cancelUpdate
 * @requires module:wc/wc/dom/event
 * @requires module:wc/wc/dom/initialise
 * @requires module:wc/wc/dom/Widget
 * @requires module:wc/wc/dom/focus
 * @requires module:wc/ui/validation/validationManager
 */
define(["wc/dom/event",
	"wc/dom/initialise",
	"wc/dom/Widget",
	"wc/dom/focus",
	"wc/ui/validation/validationManager",
	"wc/ui/ajaxRegion"],
function(event, initialise, Widget, focus, validationManager, ajaxRegion) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/ui/validation/cancelUpdate~ValidationCancelUpdateControl
	 * @private
	 */
	function ValidationCancelUpdateControl() {
		var FORM = new Widget("form"),
			SUBMIT_CONTROL = new Widget("button", "", {type: "submit"}),
			NO_VALIDATE_BUTTON = SUBMIT_CONTROL.extend("", {"formnovalidate": null});

		/**
		 * Determines if a form or sub-section thereof is in a 'valid' state.
		 * @function
		 * @private
		 * @param {Element} submitter The control which has instigated the data submission. This is used to determine
		 *     if we need to validate a sub-form based on a property of the submitter.
		 * @returns {boolean} true if the form (or sub-form) is invalid.
		 */
		function isInvalid(submitter) {
			var validationId = submitter.getAttribute("data-wc-validate"),
				validationContainer;
			if (validationId) {
				validationContainer = document.getElementById(validationId);
			} else if (ajaxRegion.getTrigger(submitter)) {
				// if a submitter is an ajax trigger and does not have a validating region
				// we do not validate.
				return false;
			}
			validationContainer = validationContainer || FORM.findAncestor(submitter);
			return validationContainer ? !validationManager.isValid(validationContainer) : false;
		}

		/**
		 * Click event handler. Prevents default if the click is on a validating button and the form or validation
		 * container is not in a valid state.
		 * @function
		 * @private
		 * @param {module:wc/wc/dom/event} $event A wrapped click event.
		 */
		function clickEvent($event) {
			var element = $event.target,
				button;

			if ($event.defaultPrevented) {
				return;
			}
			button = SUBMIT_CONTROL.findAncestor(element);

			if (button && !NO_VALIDATE_BUTTON.isOneOfMe(button) && focus.canFocus(button) && isInvalid(button)) {
				$event.preventDefault();
			}
		}

		/**
		 * Initialisation function to add a click handler. The handler is added as a late handler as we want other
		 * handlers to do any state changes and have the opportunity to cancel the event before we bother with
		 * handling it.
		 *
		 * @function  module:wc/ui/validation/cancelUpdate.initialise
		 * @param {Element} element The HTML element being initialised, usually document.body.
		 */
		this.initialise = function(element) {
			event.add(element, "click", clickEvent, 1);
		};
	}

	return /** @alias module:wc/ui/validation/cancelUpdate */ initialise.register(new ValidationCancelUpdateControl());
});
