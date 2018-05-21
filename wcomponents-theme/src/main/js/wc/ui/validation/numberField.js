define(["wc/dom/attribute",
	"wc/dom/initialise",
	"wc/dom/event",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/i18n/i18n",
	"wc/ui/validation/validationManager",
	"wc/ui/validation/required",
	"wc/ui/feedback",
	"lib/sprintf",
	"wc/ui/numberField"],
	function(attribute, initialise, event, shed, Widget, i18n, validationManager, required, feedback, sprintf, numberField) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/numberField~ValidationNumberField
		 * @private
		 */
		function ValidationNumberField() {
			var NUM_FIELD = numberField.getWidget().clone(),
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
					message;

				if (value !== "" && !validationManager.isExempt(element)) {
					if (isNaN(value)) {
						message = i18n.get("validation_number_nan");
					} else if (Widget.isOneOfMe(element, CONSTRAINED)) {
						max = element.getAttribute(MAX);
						min = element.getAttribute(MIN);
						message = checkMax(element, value, min, max);
						if (!message) {
							message = checkMin(element, value, min);
						}
					}
					if (message) {
						result = true;
						message = sprintf.sprintf(message, validationManager.getLabelText(element), (min || max), max);
						feedback.flagError({element: element, message: message});
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
						result = min ? i18n.get("validation_number_nanwithrange") : i18n.get("validation_number_nanwithmax");
					} else if (value > parseFloat(max)) {
						// if value < min it cannot be > max
						result = min ? i18n.get("validation_number_outofrange") : i18n.get("validation_number_overmax");
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
						result = i18n.get("validation_number_nanwithmin");
					} else if (value < parseFloat(min)) {
						result = i18n.get("validation_number_undermin");
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
				candidates = (numberField.isOneOfMe(container)) ? [container] : Widget.findDescendants(container, NUM_FIELD);
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
				var element = $event.target;

				if (validationManager.isValidateOnChange()) {
					if (validationManager.isInvalid(element)) {
						validationManager.revalidationHelper(element, validate);
						return;
					}
					validate(element);
					return;
				}
				validationManager.revalidationHelper(element, validate);
			}

			function blurEvent($event) {
				var element = $event.target;
				if (!element.value && shed.isMandatory(element)) {
					validate(element);
				}
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
					if (validationManager.isValidateOnBlur()) {
						if (event.canCapture) {
							event.add(element, event.TYPE.blur, blurEvent, 1, null, true);
						} else {
							event.add(element, event.TYPE.focusout, blurEvent);
						}
					}
				}
			}

			/**
			 * Intialisation callback to attach event listeners.
			 * @function module:wc/ui/validation/numberField.initialise
			 * @param {Element} element The element being iitialised, usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.focus, focusEvent, 1, null, true);
				} else {
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

		/**
		 * Provides functionality to undertake client validation of WNumberField.
		 *
		 * @module
		 * @requires wc/dom/attribute
		 * @requires wc/dom/initialise
		 * @requires wc/dom/event
		 * @requires wc/dom/Widget
		 * @requires wc/i18n/i18n
		 * @requires wc/ui/validation/validationManager
		 * @requires wc/ui/validation/required
		 * @requires wc/ui/feedback
		 * @requires external:lib/sprintf
		 * @requires wc/ui/numberField
		 */
		var instance = new ValidationNumberField();
		initialise.register(instance);
		return instance;
	});
