/**
 * Provides Number Field functionality, including polyfills. This module provides useful typed getters.
 *
 * @module
 * @requires module:wc/dom/attribute
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/event
 * @requires module:wc/isNumeric
 * @requires module:wc/dom/shed
 * @requires module:wc/dom/Widget
 * @requires module:wc/timers
 *
 * @todo document private members
 */
define(["wc/dom/attribute",
	"wc/dom/initialise",
	"wc/dom/event",
	"wc/isNumeric",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/timers"],
function(attribute, initialise, event, isNumeric, shed, Widget, timers) {
	"use strict";
	var instance;

	/**
	 * @constructor
	 * @alias module:wc/ui/numberField~NumberField
	 * @private
	 */
	function NumberField() {
		var NUM_FIELD = new Widget("input", "", {"type": "number"}),
			MAX = "max",
			MIN = "min",
			BOOTSTRAPPED = "wc/ui/numberField.bootstrapped";

		/**
		 * Increase/decrease the value in the number field by step.
		 * @function
		 * @private
		 * @param {Element} element The element to step.
		 * @param {Boolean} [up] If true increment otherwise decrement.
		 */
		function stepValue(element, up) {
			const value = instance.getValueAsNumber(element);
			let step = element.getAttribute("step") || "1",
				min = element.getAttribute(MIN),
				max = element.getAttribute(MAX),
				dec, sigFig, factor;

			/**
			 * This checks if the value has already been stepped which will be the case when the browser has
			 * native keyboard support for number fields. If it has not then the step is applied.
			 * @function
			 * @private
			 */
			function checkDoIncrement() {
				const nowWhatsMyValue = instance.getValueAsNumber(element);
				if (value === nowWhatsMyValue) {
					let tempVal = value - step;

					if (min && (tempVal < min)) {
						tempVal = min;
					} else if (max && (tempVal > max)) {
						tempVal = max;
					}

					if (factor) {
						tempVal = Math.round(tempVal * factor) / factor;
					}
					element.value = tempVal;
					event.fire(element, "change");
				}
			}

			if (min) {
				min = parseFloat(min);
			}
			if (max) {
				max = parseFloat(max);
			}

			if ((dec = step.indexOf(".")) && (sigFig = step.substr(dec))) {
				factor = 10 * sigFig.length;
			}

			if (up) {
				step = 0 - step;
			}
			timers.setTimeout(checkDoIncrement, 0);
		}


		/**
		 * Operate number fields via the keyboard.
		 * @function
		 * @private
		 * @param {KeyboardEvent} $event A keydown event.
		 */
		function keydownEvent($event) {
			const element = $event.target;
			if (!$event.defaultPrevented && !$event.altKey && !($event.ctrlKey || $event.metaKey) && (instance.isOneOfMe(element)) && !shed.isDisabled(element)) {
				switch ($event.code) {
					case "ArrowUp":
						stepValue(element, true);
						break;
					case "ArrowDown":
						stepValue(element, false);
						break;
				}
			}
		}

		/**
		 * Bootstrapping focus listener: if "one of me" is focused then wire it up.
		 * @function
		 * @private
		 * @param $event a focus/focusin event
		 */
		function focusEvent($event) {
			const element = $event.target;
			if (!$event.defaultPrevented) {
				if (instance.isOneOfMe(element) && !attribute.get(element, BOOTSTRAPPED)) {
					attribute.set(element, BOOTSTRAPPED, true);
					event.add(element, "keydown", keydownEvent);
				}
			}
		}

		/**
		 * Get the description of a number field.
		 * @function module:wc/ui/numberField.getWidget
		 * @returns {module:wc/dom/Widget} The widget which describes a number field.
		 * */
		this.getWidget = function() {
			return NUM_FIELD;
		};

		/**
		 * Initialise functionality by wiring up bootstrap listeners on the BODY.
		 * @function module:wc/ui/numberField.initialise
		 * @param {Element} element The element being initialised: usually document.body.
		 */
		this.initialise = function (element) {
			if (event.canCapture) {
				event.add(element, { type: "focus", listener: focusEvent, capture: true });
			} else {
				event.add(element, "focusin", focusEvent);
			}
		};

		/**
		 * Return the value of the INPUT element as a number. THis is an almost typed getter in that it will
		 * return an empty string if the element has no value, which is expected in HTML.
		 * @function module:wc/ui/numberField.getValueAsNumber
		 * @public
		 * @param {Element} element a number field input element
		 * @returns {Number|NaN|string} the number currently represented by this field. If the field contains a
		 *    value but it is not numeric returns NaN. If the field contains no value returns empty string "".
		 * @todo refactor to simply getValue?
		 */
		this.getValueAsNumber = function(element) {
			var result = element.value || "";
			if (result && (result = result.trim())) {
				// can't just use parseFloat because it accepts the first number before a space, test with isNumeric
				if (isNumeric(result)) {
					return parseFloat(result);
				}
				return NaN;
			}
			return result;
		};

		/**
		 * Is an element a number field?
		 * @function module:wc/ui/numberField.isOneOfMe
		 * @param {Element} element the DOM node to test.
		 * @returns {Boolean} true if the element is a number field.
		 */
		this.isOneOfMe = function(element) {
			return element && NUM_FIELD.isOneOfMe(element);
		};

		// public for testing
		/** @ignore */
		this._keydownEvent = keydownEvent;
	}

	/** @alias module:wc/ui/numberField */ instance = new NumberField();
	return initialise.register(instance);
});
