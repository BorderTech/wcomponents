define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget"],
	function(event, initialise, Widget) {
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
			 * Test is an element is a disabled link.
			 *
			 * NOTE: this is explicitly outside of {@link module:wc/dom/shed} to prevent a rather nasty set of downstream UI dependencies and circular
			 * dependencies.
			 *
			 * @function
			 * @param {Element} element the elemet to test - hopefully an a element.
			 * @returns {Boolean} true if element is an a element and is in a disabled state.
			 */
			function isDisabled(element) {
				if (!element || !ANCHOR.isOneOfMe(element)) {
					return false;
				}
				return element.getAttribute("aria-disabled") === "true";
			}

			/**
			 * Click event listener: prevent navigation if the link is "disabled".
			 *
			 * @function
			 * @private
			 * @param {Event} $event A click event.
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && (element = ANCHOR.findAncestor($event.target)) && isDisabled(element)) {
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
				event.add(element, event.TYPE.click, clickEvent, -50);
			};
		}
	});
