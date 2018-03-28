define(["wc/dom/initialise",
	"wc/dom/shed",
	"wc/ui/validation/required",
	"wc/ui/validation/validationManager",
	"wc/ui/radioButtonSelect"],
	function(initialise, shed, required, validationManager, radioButtonSelect) {
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
					constraint: required.CONSTRAINTS.CLASSNAME,
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
				if (radioButtonSelect.getInputWidget().isOneOfMe(element) &&
					(container = radioButtonSelect.getWidget().findAncestor(element)) &&
					validationManager.isInvalid(container)) {
					validationManager.setOK(container);
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

		/**
		 * Provides functionality to undertake client validation of WRadioButtonSelect.
		 *
		 * @module
		 * @requires wc/dom/initialise
		 * @requires wc/dom/shed
		 * @requires wc/ui/validation/required
		 * @requires wc/ui/validation/validationManager
		 * @requires wc/ui/radioButtonSelect
		 *
		 */
		var instance = new ValidationRadioButtonGroup();
		initialise.register(instance);
		return instance;
	});
