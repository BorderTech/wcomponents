define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget", "wc/dom/shed"],
	function(event, initialise, Widget, shed) {
		"use strict";

		/**
		 * Provides disable-ability to HTML `a` elements.
		 *
		 * @module
		 * @requires module:wc/dom/event
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/Widget
		 */
		var instance = new DisabledLink();
		initialise.register(instance);
		return instance;

		/**
		 * @constructor
		 * @alias module:wc/ui/disabledLink~DisabledLink
		 * @private
		 */
		function DisabledLink() {
			var ANCHOR = new Widget("a");

			/**
			 * Click event listener: prevent navigation if the link is "disabled".
			 *
			 * @function
			 * @private
			 * @param {Event} $event A click event.
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = ANCHOR.findAncestor($event.target)) && shed.isDisabled(element)) {
					$event.preventDefault();
				}
			}

			/**
			 * Set up event listeners on initialise. Should never be called manually: public for use by initialise.register.
			 *
			 * @function @alias module:wc/ui/disabledLink.initialise
			 * @public
			 * @param {Element} element document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent, -1);
			};
		}
	});
