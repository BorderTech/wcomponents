/**
 * Module to provide an ARIA role of checkbox with useful functionality. That is: to make something which is not a
 * checkbox behave like a check box based on its role: {@link http://www.w3.org/TR/wai-aria-practices/#checkbox}.
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
define(["wc/dom/ariaAnalog", "wc/dom/initialise", "wc/dom/Widget", "wc/dom/shed", "wc/dom/formUpdateManager"],
	/** @param ariaAnalog wc/dom/ariaAnalog @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @param formUpdateManager wc/dom/formUpdateManager*/
	function(ariaAnalog, initialise, Widget, shed, formUpdateManager) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/checkboxAnalog~CheckboxAnalog
		 * @extends module:wc/dom/ariaAnalog~AriaAnalog
		 * @private
		 */
		function CheckboxAnalog() {

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
				var SELECTED_ITEM = this.ITEM.extend("", {"aria-checked": "true"}),
					items = SELECTED_ITEM.findDescendants(form);

				if (items.length) {
					Array.prototype.forEach.call(items, function(next) {
						if (next.hasAttribute("data-wc-value") && !shed.isDisabled(next)) {
							formUpdateManager.writeStateField(container, next.getAttribute("data-wc-name"), next.getAttribute("data-wc-value"));
						}
					});
				}
			};
		}

		CheckboxAnalog.prototype = ariaAnalog;
		var /** @alias module:wc/ui/checkboxAnalog */ instance = new CheckboxAnalog();
		instance.constructor = CheckboxAnalog;
		initialise.register(instance);
		return instance;
	});
