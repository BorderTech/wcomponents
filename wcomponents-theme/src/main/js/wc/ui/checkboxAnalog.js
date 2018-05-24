define(["wc/dom/ariaAnalog", "wc/dom/initialise", "wc/dom/Widget", "wc/dom/shed", "wc/dom/formUpdateManager"],
	function(ariaAnalog, initialise, Widget, shed, formUpdateManager) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/checkboxAnalog~CheckboxAnalog
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 * @private
		 */
		function CheckboxAnalog() {
			var BUTTON_VAL_ATTRIB = "value",
				BUTTON_NAME_ATTRIB = "name";

			/**
			 * The description of a group item. This makes this class concrete.
			 * @var
			 * @type {module:wc/dom/Widget}
			 * @public
			 * @override
			 */
			this.ITEM = new Widget("", "", {role: "checkbox"});

			/**
			 * Custrom controls have to report their state.
			 * @function
			 * @public
			 * @override
			 * @param {Element} form The form or subform which state we are writing.
			 * @param {Element} container The container to whch state is written.
			 */
			this.writeState = function(form, container) {
				Array.prototype.forEach.call(this.ITEM.findDescendants(form), function(next) {
					var name, val;
					if (!shed.isDisabled(next)) {
						name = next.hasAttribute(this.VALUE_ATTRIB) ? "data-wc-name" : BUTTON_NAME_ATTRIB;
						val = next.hasAttribute(this.VALUE_ATTRIB) ? this.VALUE_ATTRIB : BUTTON_VAL_ATTRIB;
						formUpdateManager.writeStateField(container, next.getAttribute(name), shed.isSelected(next) ? next.getAttribute(val) : "");
					}
				}, this);
			};
		}

		CheckboxAnalog.prototype = ariaAnalog;
		/**
		 * Module to provide an ARIA role of checkbox with useful functionality. That is: to make something which is not a
		 * checkbox behave like a check box based on its role: http://www.w3.org/TR/wai-aria-practices/#checkbox.
		 * Strictly speaking checkbox should not get arrow key navigation.
		 *
		 * @module
		 * @extends module:wc/dom/ariaAnalog
		 *
		 * @requires module:wc/dom/ariaAnalog
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/shed
		 * @requires module:wc/dom/formUpdateManager
		 */
		var instance = new CheckboxAnalog();
		instance.constructor = CheckboxAnalog;
		initialise.register(instance);
		return instance;
	});
