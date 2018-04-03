define(["wc/dom/initialise",
	"wc/dom/Widget",
	"wc/dom/shed",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager"],
	function(initialise, Widget, shed, required, validationManager) {
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
			 * Subscriber to {@link module:wc/wc/dom/shed} select/deselect to manage validity and feedback.
			 * @function
			 * @private
			 * @param {Element} element The DOM element being selected.
			 * @param {String} action The shed action.
			 */
			function shedSubscriber(element, action) {
				if (element && REQUIRED.isOneOfMe(element)) {
					if (action === shed.actions.SELECT) {
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
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
				shed.subscribe(shed.actions.DESELECT, shedSubscriber);
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
		var instance = new ValidationCheckBox();
		initialise.register(instance);
		return instance;
	});
