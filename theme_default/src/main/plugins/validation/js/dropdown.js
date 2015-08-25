/**
 * Provides functionality to undertake client validation of WDropdown (not Type.COMBO), WSingleSelect, and WMultiSelect.
 *
 * @module ${validation.core.path.name}/dropdown
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:${validation.core.path.name}/minMax
 * @requires module:${validation.core.path.name}/validationManager
 * @requires module:${validation.core.path.name}/required
 * @requires module:wc/dom/getFilteredGroup
 */
define(["wc/dom/attribute",
		"wc/dom/event",
		"wc/dom/initialise",
		"wc/dom/Widget",
		"${validation.core.path.name}/minMax",
		"${validation.core.path.name}/validationManager",
		"${validation.core.path.name}/required",
		"wc/dom/getFilteredGroup"],
	/** @param attribute wc/dom/attribute @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param minMax ${validation.core.path.name}/minMax @param validationManager ${validation.core.path.name}/validationManager @param required ${validation.core.path.name}/required @param getFilteredGroup wc/dom/getFilteredGroup @ignore */
	function(attribute, event, initialise, Widget, minMax, validationManager, required, getFilteredGroup) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/dropdown~Dropdown
		 * @private
		 */
		function ValidationDropDown() {
			var
				/**
				 * The description of a select element.
				 * @constant
				 * @private
				 * @type {module:wc/dom/Widget} */
				SELECT = new Widget("select"),
				/**
				 * The description of a multi-select element.
				 * @constant
				 * @type {module:wc/dom/Widget}
				 * @private
				 */
				MULTI_SELECT = SELECT.extend("", {"multiple": null});

			/**
			 * Validation function for select elements.
			 * @function
			 * @private
			 * @param {Element} container A DOM element: may be a SELECT or may (or may not) contain SELECTS.
			 * @returns {boolean} true if valid.
			 */
			function validate(container) {
				var _required = required.doItAllForMe(container, SELECT),
					constrained = true;

				if (!SELECT.isOneOfMe(container) || MULTI_SELECT.isOneOfMe(container)) {
					// do not bother with the expensive constraint checking if we are just (re)validating a single select
					constrained = minMax({container: container, widget: MULTI_SELECT, selectedFunc: getFilteredGroup});
				}
				return _required && constrained;
			}

			/**
			 * Change event handler to re-validate previously invalid selects.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event a wrapped change event as published by the WComponent event manager.
			 */
			function changeEvent($event) {
				validationManager.revalidationHelper($event.target, validate);
			}

			/**
			 * First focus wires up a change listener on an individual select element in browsers which cannot capture.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event A focusin event.
			 */
			function focusEvent($event) {
				var element = $event.target,
					BOOTSTRAPPED = "wc/ui/dropdown.bootstrapped";
				if (!$event.defaultPrevented && SELECT.isOneOfMe(element) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.change, changeEvent, 1);
				}
			}

			/**
			 * Wire up appropriate event listeners.
			 * @function module:${validation.core.path.name}/dropdown.initialise
			 * @param {Element} element The element being initialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.change, changeEvent, 1, null, true);
				}
				else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};

			/**
			 * Wire up subscribers in late initialisation.
			 * @function module:${validation.core.path.name}/dropdown.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:${validation.core.path.name}/dropdown */ instance = new ValidationDropDown();
		initialise.register(instance);
		return instance;
	});
