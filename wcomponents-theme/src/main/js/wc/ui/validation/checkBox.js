/**
 * <p>Provides functionality to undertake client validation of check boxes.</p>
 * <p><strong>NOTE:</strong> this is for individual WCheckBoxes marked 'required'; check boxes in a WCheckBoxSelect are
 * never individually marked required.</p>
 *
 * @module wc/ui/validation/checkBox
 * @requires module:wc/wc/dom/initialise
 * @requires module:wc/wc/dom/Widget
 * @requires module:wc/wc/dom/shed
 * @requires module:wc/ui/validation/required
 * @requires module:wc/ui/validation/validationManager
 * @requires module:wc/wc/ui/getFirstLabelForElement
 */
define(["wc/dom/initialise",
		"wc/dom/Widget",
		"wc/dom/shed",
		"wc/ui/validation/required",
		"wc/ui/validation/validationManager",
		"wc/ui/getFirstLabelForElement"],
	/** @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param required wc/ui/validation/required @param validationManager wc/ui/validation/validationManager @param getFirstLabelForElement wc/ui/getFirstLabelForElement @ignore */
	function(initialise, Widget, shed, required, validationManager, getFirstLabelForElement) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/checkBox~ValidationCheckBox
		 * @private
		 */
		function ValidationCheckBox() {
			var REQUIRED = new Widget("input", "", {"type": "checkbox", "required": null});

			/**
			 * Subscriber to {@link module:wc/wc/dom/shed} select so that when a mandatory check box is selected we can set
			 * it to valid if it was previously marked invalid.
			 * @function
			 * @private
			 * @param {Element} element The DOM element being selected.
			 */
			function shedSubscriber(element) {
				if (element && Widget.isOneOfMe(element, REQUIRED) && validationManager.isInvalid(element)) {
					validationManager.setOK(element);
				}
			}

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
							widget: REQUIRED,
							attachTo: getFirstLabelForElement
						};
				return required.complexValidationHelper(obj);
			}

			/**
			 * Initialise function to set up check boxes for validation.
			 * @function module:wc/ui/validation/checkBox.initialise
			 * @public
			 */
			this.initialise = function() {
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:wc/ui/validation/checkBox */ instance = new ValidationCheckBox();
		initialise.register(instance);
		return instance;
	});
