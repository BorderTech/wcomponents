/**
 * Stop click events on disabled controls in IE (even IE11 does this).
 *
 * @module
 * @private
 * @requires module:wc/dom/event
 * @requires module:wc/dom/initialise
 * @requires module:wc/dom/shed
 */
define(["wc/dom/event", "wc/dom/initialise", "wc/dom/shed"],
/** @param event wc/dom/event @param initialise wc/dom/initialise @param shed wc/dom/shed @ignore */
function(event, initialise, shed) {
	"use strict";
	function DisabledControl() {
		/**
		 * Add event listeners early.
		 * @param {Element} element document.body.
		 * @ignore
		 */
		this.initialise = 	function(element) {
			event.add(element, event.TYPE.focus, genericEvent, -100);
			event.add(element, event.TYPE.click, genericEvent, -100);
			event.add(element, event.TYPE.dblclick, genericEvent, -100);
		};

		/**
		 * If the event target is disabled don't let the event continue! Should be easy eh?
		 * @param {Event} $event The event to test.
		 * @ignore
		 */
		function genericEvent($event) {
			var element = $event.target;  // IE frequently does not set the event target/srcElement!!
			if (element && (!$event.defaultPrevented && shed.isDisabled(element))) {
				$event.preventDefault();
			}
		}
	}
	var disabledControlFix = new DisabledControl();
	initialise.addBodyListener(disabledControlFix);
	return /** @alias module:wc/fix/disabledControl_ieAll */ disabledControlFix;
});
