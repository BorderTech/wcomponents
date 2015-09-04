/**
 * Provides disable-ability to HTML A elements.
 *
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @todo re-order code, document private members.
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @ignore */
	function(event, initialise, Widget) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/dom/disabledLink~DisabledLink
		 * @private
		 */
		function DisabledLink() {
			var ANCHOR = new Widget("a"),
				isDisabled;

			/**
			 * Click event listener: prevent navigation if the link is "disabled".
			 *
			 * @function
			 * @private
			 * @param {Event} $event A click event.
			 */
			function clickEvent($event) {
				var element;
				if (!$event.defaultPrevented && isDisabled && (element = ANCHOR.findAncestor($event.target)) && isDisabled(element)) {
					$event.preventDefault();
				}
			}

			/**
			 * Set a function to use to determine is a link is disabled. This is the crux of this module. It is here to
			 * prevent circular dependencies and various races, especially in slower versions of IE. Whilst this
			 * function is public it should never be called by a module other than {@link module:wc/dom/shed}
			 *
			 * @function @alias module:wc/dom/disabledLink.setDisabled
			 * @public
			 * @param {Function} func The function which is used to determine if a link is disabled, it is ONLY set to
			 * {@link module:wc/dom/shed#isDisabled}.
			 */
			this.setDisabled = function(func) {
				isDisabled = func;
			};

			/**
			 * Set up event listeners on initialise.
			 *
			 * @function @alias module:wc/dom/disabledLink.initialise
			 * @param {Element} element document.body.
			 */
			this.initialise = function(element) {
				event.add(element, event.TYPE.click, clickEvent, -50);
			};
		}

		var /** @alias module:wc/dom/disabledLink */ instance = new DisabledLink();
		initialise.register(instance);
		return instance;
	});
