/**
 * Provides functionality which interrupts clicks on form submitting and validating buttons if the validation
 * area is not in a valid state.
 *
 * @see {@link module:wc/dom/cancelUpdate}
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
		"wc/ui/validation/validationManager"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param focus wc/dom/focus @param validationManager wc/ui/validation/validationManager @ignore */
	function(event, initialise, Widget, focus, validationManager) {
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
			 * @param {Element} form a HTML Form element.
			 * @returns {boolean} true if the form (or sub-form) is invalid.
			 */
			function isInvalid(submitter, form) {
				var validationId = submitter.getAttribute("${wc.ui.button.attribute.validates}"),
					validationContainer;
				if (validationId) {
					validationContainer = document.getElementById(validationId);
				}
				validationContainer = validationContainer || form;
				return !validationManager.isValid(validationContainer);
			}

			/**
			 * Click event handler. Prevents default if the click is on a validating button and the form or validation
			 * container is not in a valid state.
			 * @function
			 * @private
			 * @param {module:wc/wc/dom/event} $event A wrapped click event.
			 */
			function clickEvent($event) {
				var element = $event.target, form, button;
				if (!$event.defaultPrevented) {
					button = SUBMIT_CONTROL.findAncestor(element);

					if (button && !NO_VALIDATE_BUTTON.isOneOfMe(button) && focus.canFocus(button)) {
						form = FORM.findAncestor(button);
						if (form && isInvalid(button, form)) {
							$event.preventDefault();
						}
					}
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
				event.add(element, event.TYPE.click, clickEvent, 1);
			};
		}

		var /** @alias module:wc/ui/validation/cancelUpdate */ instance = new ValidationCancelUpdateControl();
		initialise.register(instance);
		return instance;
	});
