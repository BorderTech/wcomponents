/**
 * Provides functionality to undertake client validation of WNumberField.
 *
 * @module wc/ui/validation/numberField
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/event
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:wc/ui/validation/validationManager
 * @requires module:wc/ui/validation/required
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires external:lib/sprintf
 * @requires module:wc/ui/numberField
 */
define(["wc/dom/attribute",
		"wc/dom/initialise",
		"wc/dom/event",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"wc/ui/validation/validationManager",
		"wc/ui/validation/required",
		"wc/ui/getFirstLabelForElement",
		"lib/sprintf",
		"wc/ui/numberField"],
	/** @param attribute wc/dom/attribute @param initialise wc/dom/initialise @param event wc/dom/event @param Widget wc/dom/Widget @param i18n wc/i18n/i18n @param validationManager wc/ui/validation/validationManager @param required wc/ui/validation/required @param getFirstLabelForElement wc/ui/getFirstLabelForElement @param sprintf lib/sprintf @param numberField wc/ui/numberField @ignore */
	function(attribute, initialise, event, Widget, i18n, validationManager, required, getFirstLabelForElement, sprintf, numberField) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/numberField~ValidationNumberField
		 * @private
		 */
		function ValidationNumberField() {
			var NUM_FIELD = numberField.getWidget(),
				MAX = "max",
				MIN = "min",
				BOOTSTRAPPED = "validation.numberField.bs",
				MAX_FIELD = NUM_FIELD.extend("", {max: null}),
				MIN_FIELD = NUM_FIELD.extend("", {min: null}),
				CONSTRAINED = [MAX_FIELD, MIN_FIELD];

			/**
			 * An array filter function which tests an individual number field to see if it meets constraints of min and
			 * max. It is s filter and we are interested in invalid fields so we return true on invalid.
			 * @function
			 * @private
			 * @param {Element} element A WNumberField
			 * @returns {boolean} false if the field is valid
			 */
			function isInvalid(element) {
				var result = false, min, max,
					value = numberField.getValueAsNumber(element),
					label, message;

				if (value !== "" && !validationManager.isExempt(element)) {
					if (Widget.isOneOfMe(element, CONSTRAINED)) {
						max = element.getAttribute(MAX);
						min = element.getAttribute(MIN);
						message = checkMax(element, value, min, max);
						if (!message) {
							message = checkMin(element, value, min);
						}
					}
					else if (isNaN(value)) {
						message = i18n.get("${validation.numberField.i18n.notNumeric}");
					}
					if (message) {
						result = true;
						label = getFirstLabelForElement(element, true) || element.title || i18n.get("${validation.core.i18n.unlabelledQualifier}");
						message = sprintf.sprintf(message, label, (min || max), max);
						validationManager.flagError({element: element, message: message});
					}
				}
				return result;
			}

			/**
			 * Check the max constraint.
			 * @param element The number field.
			 * @param value The current value of the number field.
			 * @param min The min constraint.
			 * @param max The max constraint.
			 * @private
			 * @function
			 * @returns {string} An error message if there is an error.
			 */
			function checkMax(element, value, min, max) {
				var result;
				if (MAX_FIELD.isOneOfMe(element)) {
					if (isNaN(value)) {
						result = min ? i18n.get("${validation.numberField.i18n.notNumeric.minMax}") : i18n.get("${validation.numberField.i18n.notNumeric.max}");
					}
					else if (value > parseFloat(max)) {
						// if value < min it cannot be > max
						result = min ? i18n.get("${validation.numberField.i18n.outOfRange}") : i18n.get("${validation.numberField.i18n.overMax}");
					}
				}
				return result;
			}

			/**
			 * Check the min constraint.
			 * @param element The number field.
			 * @param value The current value of the number field.
			 * @param min The min constraint.
			 * @private
			 * @function
			 * @returns {string} An error message if there is an error.
			 */
			function checkMin(element, value, min) {
				var result;
				if (MIN_FIELD.isOneOfMe(element)) {
					if (isNaN(value)) {
						result = i18n.get("${validation.numberField.i18n.notNumeric.min}");
					}
					else if (value < parseFloat(min)) {
						result = i18n.get("${validation.numberField.i18n.underMin}");
					}
				}
				return result;
			}

			/**
			 * Validate all WNumberFields in a given container, which could be a WNumberFIeld itself.
			 * @function
			 * @private
			 * @param {Element} container Any element.
			 * @returns {Boolean} true if the container is valid.
			 */
			function validate(container) {
				var result = true,
					candidates,
					invalid,
					validInputs = required.doItAllForMe(container, NUM_FIELD);
				candidates = (NUM_FIELD.isOneOfMe(container)) ? [container] : Widget.findDescendants(container, NUM_FIELD);
				if (candidates && candidates.length) {
					invalid = Array.prototype.filter.call(candidates, isInvalid);
					result = (invalid.length === 0);
				}
				return (validInputs && result);
			}

			/**
			 * Re-validate WNumberFields when they change.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event A wrapped change event.
			 */
			function changeEvent($event) {
				validationManager.revalidationHelper($event.target, validate);
			}

			/**
			 * A focus listener to attach change events to WNumberFields in browsers which cannot capture.
			 * @function
			 * @private
			 * @param {module:wc/dom/event} $event A wrapped focus[in] event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if (!$event.defaultPrevented && numberField.isOneOfMe(element) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, event.TYPE.change, changeEvent, 1);
				}
			}

			/**
			 * Intialisation callback to attach event listeners.
			 * @function module:wc/ui/validation/numberField.initialise
			 * @param {Element} element The element being iitialised, usually document.body.
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
			 * Intialisation callback to do late subscription.
			 * @function module:wc/ui/validation/numberField.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:wc/ui/validation/numberField */ instance = new ValidationNumberField();
		initialise.register(instance);
		return instance;
	});
