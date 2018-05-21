define(["wc/dom/attribute", "wc/dom/event", "wc/dom/initialise", "wc/dom/Widget", "wc/dom/shed"],
	function(attribute, event, initialise, Widget, shed) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/dom/cbrShedPublisher~CheckRadioPublisher
		 * @private
		 */
		function CheckboxRadioPublisher() {
			var RADIO = new Widget("input", "", {type: "radio"}),
				CHECKBOX = new Widget("input", "", {type: "checkbox"}),
				WIDGETS = [CHECKBOX, RADIO],
				BS = "wc/dom/cbrShedPubliser-bootstrapped";

			/**
			 * NOTE: this may not work in IE < 11 but has been tested in IE 11 and it is fine there:
			 * changeEvents on checkboxes and radios fire reliably when the state changes, not when focus is lost.
			 * @function
			 * @private
			 * @param {Event} $event a wrapped change event
			 */
			function changeEvent($event) {
				var element = $event.target,
					action;
				if (element && Widget.isOneOfMe(element, WIDGETS)) {
					action = shed.isSelected(element) ? shed.actions.SELECT : shed.actions.DESELECT;
					shed.publish(element, action);
				}
			}

			/**
			 * Focus listener to bootstrap inputs on first focus.
			 *
			 * @function
			 * @private
			 * @param {Event} $event The focus event.
			 */
			function focusEvent($event) {
				var element = $event.target;
				if (element && Widget.isOneOfMe(element, WIDGETS) && !attribute.get(element, BS)) {
					attribute.set(element, BS, true);
					event.add(element, event.TYPE.change, changeEvent);
				}
			}

			/**
			 * Set up event listeners in intialisation.
			 *
			 * @function
			 * @alias module:wc/dom/cbrShedPublisher.initialise
			 * @param {Element} element The element being initialised: usually document.body.
			 */
			this.initialise = function(element) {
				if (event.canCapture) {
					event.add(element, event.TYPE.change, changeEvent);
				} else {
					event.add(element, event.TYPE.focusin, focusEvent);
				}
			};

			/**
			 * Get the Widget(s) to describe a checkbox, radio button or both.
			 * @param {String} [whichOne] which widget to get:
			 *    "cb" will fetch the CHECKBOX widget;
			 *    "r" will fetch the RADIO widget;
			 *    anything else will fetch an array containing both.
			 * @returns {Widget|Widget[]}
			 */
			this.getWidget = function(whichOne) {
				if (whichOne === "cb") {
					return CHECKBOX;
				}
				if (whichOne === "r") {
					return RADIO;
				}
				return WIDGETS;
			};
		}

		/**
		 * Provides a mechanism to publish {@link module"wc/dom/shed"} events on native selectable controls (radio buttons and
		 * check boxes) which are not instrinsically 'sheddy'. This allows us to use shed subscribers to do stuff on select/
		 * deselect of these native controls as if they were ARIA widgets.
		 *
		 * @module
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/shed
		 * @requires module:wc/has
		 *
		 * @todo re-order code, document private members.
		 */
		var instance = new CheckboxRadioPublisher();
		initialise.register(instance);
		return instance;
	});
