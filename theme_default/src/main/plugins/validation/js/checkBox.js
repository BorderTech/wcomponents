/**
 * <p>Provides functionality to undertake client validation of check boxes.</p>
 * <p><strong>NOTE:</strong> this is for individual WCheckBoxes marked 'required'; check boxes in a WCheckBoxSelect are
 * never individually marked required.</p>
 *
 * @module ${validation.core.path.name}/checkBox
 * @requires module:wc/wc/dom/initialise
 * @requires module:wc/wc/dom/Widget
 * @requires module:wc/wc/dom/shed
 * @requires module:${validation.core.path.name}/required
 * @requires module:${validation.core.path.name}/validationManager
 * @requires module:wc/wc/ui/getFirstLabelForElement
 */
define(["wc/dom/initialise",
		"wc/dom/Widget",
		"wc/dom/shed",
		"${validation.core.path.name}/required",
		"${validation.core.path.name}/validationManager",
		"wc/ui/getFirstLabelForElement"],
	/** @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param required ${validation.core.path.name}/required @param validationManager ${validation.core.path.name}/validationManager @param getFirstLabelForElement wc/ui/getFirstLabelForElement @ignore */
	function(initialise, Widget, shed, required, validationManager, getFirstLabelForElement) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/checkBox~ValidationCheckBox
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
			 * function for {@link module:${validation.core.path.name}/validationManager}.
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
			 * @function module:${validation.core.path.name}/checkBox.initialise
			 * @public
			 */
			this.initialise = function() {
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:${validation.core.path.name}/checkBox */ instance = new ValidationCheckBox();
		initialise.register(instance);
		return instance;
	});
