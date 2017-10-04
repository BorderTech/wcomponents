/**
 * Provides functionality to undertake client validation of WRadioButtonSelect.
 *
 * @module wc/ui/validation/radioButtonSelect
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 * @requires module:wc/ui/validation/required
 * @requires module:wc/ui/validation/validationManager
 * @requires module:wc/ui/radioButtonSelect
 *
 */
define(["wc/dom/initialise",
	"wc/dom/shed",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager",
	"wc/ui/validation/feedback",
	"wc/ui/radioButtonSelect"],
	function(initialise, shed, required, validationManager, feedback, radioButtonSelect) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/radioButtonSelect~ValidationRadioButtonGroup
		 * @private
		 */
		function ValidationRadioButtonGroup() {
			/**
			 * Function to validate WRadioButtonSelects in a container.
			 * @function
			 * @private
			 * @param {Element} container Any container element, may be a WRadioButtonSelect.
			 * @returns {boolean} true if the container is valid.
			 */
			function validate(container) {
				return required.complexValidationHelper({container: container,
					widget: radioButtonSelect.getWidget(),
					constraint: required.CONSTRAINTS.ARIA,
					position: "beforeEnd"
				});
			}

			/**
			 * Subscribe to shed.actions.SELECT to remove invalid flag when a selection is made. This is simpler than
			 * most re-validation because of the single-select nature of a WRadioButtonSelect.
			 * @function
			 * @private
			 * @param {Element} element A html element which has been selected.
			 */
			function validationShedSubscriber(element) {
				var container;
				if (radioButtonSelect.getInputWidget().isOneOfMe(element) && (container = radioButtonSelect.getWidget().findAncestor(element)) && validationManager.isInvalid(container)) {
					feedback.setOK(container);
				}
			}

			/**
			 * Late setup - subscribers to shed and validation manager.
			 * @function module:wc/ui/validation/radioButtonSelect.postInit
			 */
			this.postInit = function () {
				validationManager.subscribe(validate);
				shed.subscribe(shed.actions.SELECT, validationShedSubscriber);
			};
		}

		var /** @alias module:wc/ui/validation/radioButtonSelect */ instance = new ValidationRadioButtonGroup();
		initialise.register(instance);
		return instance;
	});
