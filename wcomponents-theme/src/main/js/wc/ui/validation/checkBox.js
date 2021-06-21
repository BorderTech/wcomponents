define(["wc/dom/initialise",
	"wc/dom/event",
	"wc/dom/Widget",
	"wc/dom/shed",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager"],
function(initialise, event, Widget, shed, required, validationManager) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/ui/validation/checkBox~ValidationCheckBox
	 * @private
	 */
	function ValidationCheckBox() {
		var REQUIRED = new Widget("input", "", {"type": "checkbox", "required": null});

		/**
		 * Validate mandatory check boxes using the {@link ./required} module. This is a subscriber
		 * function for {@link module:wc/ui/validation/validationManager}.
		 * @function
		 * @private
		 * @param {Element} container The DOM element being validated.
		 * @returns {boolean} true if valid.
		 */
		function validate(container) {
			var obj = {container: container,
				widget: REQUIRED
			};
			return required.complexValidationHelper(obj);
		}

		/**
		 * Listens for {@link module:wc/wc/dom/shed} select/deselect events to manage validity and feedback.
		 * @function
		 * @private
		 * @param {Event} $event The shed event that fired.
		 */
		function shedListener($event) {
			var element = $event.target,
				action = $event.type;
			if (element && REQUIRED.isOneOfMe(element)) {
				if (action === shed.events.SELECT) {
					if (validationManager.isInvalid(element)) {
						validationManager.setOK(element);
					}
				} else if (validationManager.isValidateOnChange()) {
					validate(element);
				}
			}
		}


		/**
		 * Initialise function to set up check boxes for validation.
		 * @function module:wc/ui/validation/checkBox.initialise
		 * @public
		 */
		this.initialise = function() {
			event.add(document.body, shed.events.SELECT, shedListener);
			event.add(document.body, shed.events.DESELECT, shedListener);
			validationManager.subscribe(validate);
		};
	}

	/**
	 * Provides functionality to undertake client validation of check boxes.
	 *
	 * **NOTE:** this is for individual WCheckBoxes marked 'required'; check boxes in a WCheckBoxSelect are never individually marked required.
	 *
	 * @module
	 * @requires wc/wc/dom/initialise
	 * @requires wc/wc/dom/Widget
	 * @requires wc/wc/dom/shed
	 * @requires wc/ui/validation/required
	 * @requires wc/ui/validation/validationManager
	 */
	return initialise.register(new ValidationCheckBox());
});
