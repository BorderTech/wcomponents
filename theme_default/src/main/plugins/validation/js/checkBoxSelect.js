/**
 * Provides functionality to undertake client validation of WCheckBoxSelect. Extends {@link module:wc/validation/ariaAnalog}.
 *
 * @module ${validation.core.path.name}/checkBoxSelect
 * @extends module:${validation.core.path.name}/ariaAnalog
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/getFilteredGroup
 * @requires module:${validation.core.path.name}/ariaAnalog
 * @requires module:wc/"${validation.core.path.name}/validationManager
 * @requires module:${validation.core.path.name}/required
 * @requires module:${validation.core.path.name}/minMax
 * @requires module:wc/ui/checkBoxSelect
 */
define(["wc/dom/initialise",
		"wc/dom/getFilteredGroup",
		"${validation.core.path.name}/ariaAnalog",
		"${validation.core.path.name}/validationManager",
		"${validation.core.path.name}/required",
		"${validation.core.path.name}/minMax",
		"wc/ui/checkBoxSelect"],
	/** @param initialise dom/initialise @param getFilteredGroup dom/getFilteredGroup @param ariaAnalog ${validation.core.path.name}/ariaAnalog @param validationManager ${validation.core.path.name}/validationManager @param required ${validation.core.path.name}/required @param minMax ${validation.core.path.name}/minMax @param checkBoxSelect ui/checkBoxSelect @ignore */
	function(initialise, getFilteredGroup, ariaAnalog, validationManager, required, minMax, checkBoxSelect) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/checkBoxSelect~ValidationCheckBoxSelect
		 * @extends module:${validation.core.path.name}/ariaAnalog~ValidationAriaAnalog
		 * @private
		 */
		function ValidationCheckBoxSelect() {
			/**
			 * The description of the analog, same as {@link module:wc/ui/checkBoxSelect~CheckBoxSelect.ITEM}
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.ITEM = checkBoxSelect.ITEM;
			/**
			 * CONTAINER The description of the analog's containing element, same as checkBoxSelect#CONTAINER
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @override
			 */
			this.CONTAINER = checkBoxSelect.CONTAINER;
			/**
			 * The selection mode, same as {@link module:wc/ui/checkBoxSelect#exclusiveSelect}
			 * @var
			 * @type {int}
			 * @override
			 */
			this.exclusiveSelect = checkBoxSelect.exclusiveSelect;
			/**
			 * We cannot get a handle to {@link module:wc/dom/ariaAnalog} in the prototype so we always have to define
			 * this.
			 * @see {@link module:wc/ui/checkBoxSelect~SELECT_MODE}
			 * @var
			 * @type {int[]}
			 * @override
			 */
			this.SELECT_MODE = checkBoxSelect.SELECT_MODE;

			/**
			 * Determines whether a container is valid.
			 * @function
			 * @protected
			 * @override
			 * @param {Element} container The container being validated, may be a WCheckBoxSelect root container or an
			 *    element which contains WCheckBoxSelects such as a form.
			 * @returns {Boolean} true if valid.
			 */
			this.validate = function(container) {
				var result = true,
					obj = {container: container,
							widget: instance.CONTAINER,
							constraint: required.CONSTRAINTS.CLASSNAME,
							position: "beforeEnd"},
					_required = required.complexValidationHelper(obj);

				// add a selectedFunc to be able to do min/max validation
				obj.selectedFunc = function(fs) {
					return getFilteredGroup(fs, {itemWd: instance.ITEM}) || [];
				};

				result = minMax(obj);
				return _required && result;
			};

			/**
			 * Re-validate a previously invalid WCheckBoxSelect when the component's selection is changed.
			 *
			 * @function
			 * @protected
			 * @override
			 * @param {Element} element A WCheckBoxSelect
			 */
			this.revalidate = function(element) {
				validationManager.revalidationHelper(element, this.validate.bind(this));
			};
		}

		ValidationCheckBoxSelect.prototype = ariaAnalog;

		var /** @alias module:${validation.core.path.name}/checkBoxSelect */ instance = new ValidationCheckBoxSelect();
		instance.constructor = ValidationCheckBoxSelect;
		initialise.register(instance);
		return instance;
	});
