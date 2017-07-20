/**
 * Provides functionality for a confirmation button.
 * @module
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/Widget
 * @requires module:wc/ui/cancelButton
 * @requires module:wc/dom/focus
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/Widget", "wc/ui/cancelButton", "wc/dom/focus"],
	/** @param event wc/dom/event @param initialise wc/dom/initialise @param Widget wc/dom/Widget @param cancelButton wc/ui/cancelButton @param focus wc/dom/focus @ignore */
	function(event, initialise, Widget, cancelButton, focus) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/confirm~Confirm
		 * @private
		 */
		function Confirm() {
			var CONFIRM_WD = new Widget("button", "", {"data-wc-btnmsg": null}),
				CANCEL_BUTTON = cancelButton.getWidget();

			/**
			 * Use a confirm to ensure the user really want to undertake an action.
			 * @function
			 * @private
			 * @param {Event} $event The click event.
			 */
			function clickEvent($event) {
				var target, message;
				if (!$event.defaultPrevented && (target = CONFIRM_WD.findAncestor($event.target)) && !CANCEL_BUTTON.isOneOfMe(target) && focus.canFocus(target)) {
					message = target.getAttribute("data-wc-btnmsg");
					if (message) {
						var doContinue = window.confirm(message);
						if (!doContinue) {
							$event.preventDefault();  // $event.cancel();
							// console.info("Cancelled event");
							focus.setFocusRequest(target);
						}
					} else {
						console.warn("No message found for element", target);
					}
				}
			}

			/**
			 * Get the description of a confirm button.
			 * @function module:wc/ui/confirm.getWidget
			 * @public
			 * @returns {module:wc/dom/Widget} THe descriptor for a confirmation button.
			 */
			this.getWidget = function() {
				return CONFIRM_WD;
			};

			/**
			 * Initialise all confirm buttons by adding a click event listener to the body.
			 * @function module:wc/ui/confirm.initialise
			 * @public
			 * @param {Element} element The element being initialised - document.body.
			 */
			this.initialise = function(element) {
				// rule of thumb - any event listener that has the potential to cancel the event should probably be high priority
				event.add(element, event.TYPE.click, clickEvent, -1);
			};
		}
		var /** @alias module:wc/ui/confirm */ instance = new Confirm();
		initialise.register(instance);
		return instance;
	});
