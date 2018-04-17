/*
 * Provides functionality to undertake client validation of WCheckBoxSelect. Extends {@link module:wc/validation/ariaAnalog}.
 *
 * @module wc/ui/validation/checkBoxSelect
 * @extends module:wc/ui/validation/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:wc/ui/validation/ariaAnalog
 * @requires module:wc/"wc/ui/validation/validationManager
 * @requires module:wc/ui/validation/required
 * @requires module:wc/ui/validation/minMax
 * @requires module:wc/ui/checkBoxSelect
 */
define(["wc/dom/initialise",
	"wc/dom/getFilteredGroup",
	"wc/dom/group",
	"wc/dom/shed",
	"wc/ui/validation/validationManager",
	"wc/ui/validation/required",
	"wc/ui/validation/minMax",
	"wc/ui/checkBoxSelect",
	"wc/ui/validation/isComplete"],
	function(initialise, getFilteredGroup, group, shed, validationManager, required, minMax, checkBoxSelect, isComplete) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/validation/checkBoxSelect~ValidationCheckBoxSelect
		 * @extends module:wc/ui/validation/ariaAnalog~ValidationAriaAnalog
		 * @private
		 */
		function ValidationCheckBoxSelect() {
			/**
			 * Determines whether a container is valid.
			 * @function
			 * @private
			 * @param {Element} container The container being validated, may be a WCheckBoxSelect root container or an
			 *    element which contains WCheckBoxSelects such as a form.
			 * @returns {Boolean} true if valid.
			 */
			function validate (container) {
				var result = true,
					obj = {container: container,
						widget: checkBoxSelect.CONTAINER,
						constraint: required.CONSTRAINTS.CLASSNAME,
						position: "beforeEnd"},
					_required = required.complexValidationHelper(obj);

				// add a selectedFunc to be able to do min/max validation
				obj.selectedFunc = function(fs) {
					return getFilteredGroup(fs, {itemWd: checkBoxSelect.ITEM}) || [];
				};

				result = minMax(obj);
				return _required && result;
			}

			/**
			 * Re-validate a previously invalid WCheckBoxSelect when the component's selection is changed.
			 *
			 * @function
			 * @private
			 * @param {Element} element A WCheckBoxSelect
			 */
			function revalidate (element) {
				validationManager.revalidationHelper(element, validate);
			}

			function shedSubscriber(element) {
				var container = group.getContainer(element, checkBoxSelect.CONTAINER);

				if (!container) {
					return;
				}

				if (validationManager.isValidateOnChange()) {
					if (validationManager.isInvalid(element)) {
						revalidate(container);
						return;
					}
					validate(container);
					return;
				}
				revalidate(container);
			}

			/**
			 * Filter function for isComplete.isCompleteHelper.
			 * @function
			 * @private
			 * @param {Element} next The component being passed in from the array in isComplete.isCompleteHelper.
			 * @returns {boolean} `true` if the component 'next' is complete (selected).
			 */
			function isCompleteFilter(next) {
				return shed.isSelected(next);
			}

			function isCompleteSubscriber (container) {
				return isComplete.isCompleteHelper(container, checkBoxSelect.ITEM, isCompleteFilter);
			}

			this.initialise = function() {
				validationManager.subscribe(validate);
				isComplete.subscribe(isCompleteSubscriber);
				shed.subscribe(shed.actions.SELECT, shedSubscriber);
				shed.subscribe(shed.actions.DESELECT, shedSubscriber);
			};
		}

		var /** @alias module:wc/ui/validation/checkBoxSelect */ instance = new ValidationCheckBoxSelect();
		initialise.register(instance);
		return instance;
	});
