/**
 * Provides functionality to undertake client validation of WNumberField.
 *
 * @module ${validation.core.path.name}/numberField
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/event
 * @requires module:wc/dom/Widget
 * @requires module:wc/i18n/i18n
 * @requires module:${validation.core.path.name}/validationManager
 * @requires module:${validation.core.path.name}/required
 * @requires module:wc/ui/getFirstLabelForElement
 * @requires external:sprintf/sprintf
 * @requires module:wc/ui/numberField
 */
define(["wc/dom/attribute",
		"wc/dom/initialise",
		"wc/dom/event",
		"wc/dom/Widget",
		"wc/i18n/i18n",
		"${validation.core.path.name}/validationManager",
		"${validation.core.path.name}/required",
		"wc/ui/getFirstLabelForElement",
		"sprintf/sprintf",
		"wc/ui/numberField"],
	/** @param attribute wc/dom/attribute @param initialise wc/dom/initialise @param event wc/dom/event @param Widget wc/dom/Widget @param i18n wc/i18n/i18n @param validationManager ${validation.core.path.name}/validationManager @param required ${validation.core.path.name}/required @param getFirstLabelForElement wc/ui/getFirstLabelForElement @param sprintf sprintf/sprintf @param numberField wc/ui/numberField @ignore */
	function(attribute, initialise, event, Widget, i18n, validationManager, required, getFirstLabelForElement, sprintf, numberField) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:${validation.core.path.name}/numberField~ValidationNumberField
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
						if (MIN_FIELD.isOneOfMe(element)) {
							min = element.getAttribute(MIN);
							if (isNaN(value)) {
								message = i18n.get("${validation.numberField.i18n.notNumeric.min}");
								result = true;
							}
							else if ((result = value < parseFloat(min))) {
								message = i18n.get("${validation.numberField.i18n.underMin}");
							}
						}
						if (MAX_FIELD.isOneOfMe(element)) {
							max = element.getAttribute(MAX);
							if (isNaN(value)) {
								message = min ? i18n.get("${validation.numberField.i18n.notNumeric.minMax}") : i18n.get("${validation.numberField.i18n.notNumeric.max}");
								result = true;
							}
							else if ((result = result || value > parseFloat(max))) {
								// if value < min it cannot be > max
								message = min ? i18n.get("${validation.numberField.i18n.outOfRange}") : i18n.get("${validation.numberField.i18n.overMax}");
							}
						}
					}
					else if (isNaN(value)) {
						message = i18n.get("${validation.numberField.i18n.notNumeric}");
						result = true;
					}

					if (result) {
						label = getFirstLabelForElement(element, true) || element.title || i18n.get("${validation.core.i18n.unlabelledQualifier}");
						message = sprintf.sprintf(message, label, (min || max), max);
						validationManager.flagError({element: element, message: message});
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
			 * @function module:${validation.core.path.name}/numberField.initialise
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
			 * @function module:${validation.core.path.name}/numberField.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
			};
		}

		var /** @alias module:${validation.core.path.name}/numberField */ instance = new ValidationNumberField();
		initialise.register(instance);
		return instance;
	});
