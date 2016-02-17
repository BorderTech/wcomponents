/**
 * Provides print button functionality. Almost completely worthless.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/shed
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget", "wc/dom/shed"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param shed wc/dom/shed @ignore */
	function(event, initialise, Widget, shed) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/printButton~PrintButton
		 * @private
		 */
		function PrintButton() {
			var PRINT = new Widget("button", "wc-printbutton");

			/**
			 * Click listener to invoke the print dialog. Kill me now!
			 * @function
			 * @private
			 * @param {Event} $event The click event.
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = PRINT.findAncestor($event.target)) && !shed.isDisabled(element)) {
					$event.preventDefault();
					window.print();
				}
			}

			/**
			 * Get the definition of a print button.
			 * @function
			 * @public
			 * @returns {module:wc/dom/Widget} The print button widget.
			 */
			this.getWidget = function() {
				return PRINT;
			};

			/**
			 * Initialise print button functionality with a click listener.
			 * @function module:wc/ui/printButton.initialise
			 * @public
			 * @param {Element} element The element being initialised: document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent);
			};
		}

		var /** @alias module:wc/ui/printButton */ instance = new PrintButton();
		initialise.register(instance);
		return instance;
	});
