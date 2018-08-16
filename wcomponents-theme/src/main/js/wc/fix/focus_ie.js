require(["wc/has"], function(has) {
	"use strict";
	if (has("trident") || has("ie")) {
		require(["wc/dom/Widget", "wc/dom/isAcceptableTarget", "wc/dom/focus", "wc/dom/event", "wc/timers"],
			function( Widget, isAcceptableTarget, focus, event, timers) {
				var FOCUS_WIDGET;
				console.log("Adding IE11 focus fix for mouse users.");
				event.add(window, event.TYPE.mousedown, mouseDownEvent, null, null, true);
				if (has("event-ontouchstart")) {
					event.add(window, event.TYPE.touchstart, mouseDownEvent, null, null, true);
				}

				function mouseDownEvent($event) {
					var expectedTarget,
						targetElement = $event.target;
					if (!focus.canFocus(targetElement)) {
						FOCUS_WIDGET = FOCUS_WIDGET || new Widget("", "", {"tabIndex": "0"});
						expectedTarget = FOCUS_WIDGET.findAncestor($event.target);
						if (!(expectedTarget && isAcceptableTarget(expectedTarget, targetElement))) {
							return;
						}
						targetElement = expectedTarget;
					}
					if (targetElement && needsFocusFix(targetElement)) {
						tryFocusFix(targetElement);
					}
				}

				function needsFocusFix(element) {
					if (focus.isNativelyFocusable(element.tagName)) {
						return false;
					}
					return element.tabIndex > -1;
				}

				function tryFocusFix(element) {
					timers.setTimeout(function() {
						try {
							element.focus();
						} catch (ex) {
							console.error("Error applying IE11 focus fix", ex);
						}
					}, 0);
				}
				/**
				 * Fix for IE 11 failure to move focus when the user clicks with the mouse on elements with non-negative tabindex which
				 * are not natively focusable.
				 * @module
				 * @private
				 *
				 * @requires module:wc.has
				 * @requires module:wc/dom/Widget
				 * @requires module:wc/dom/isAcceptableTarget
				 * @requires module:wc/dom/focus
				 * @requires module:wc/dom/event
				 * @requires module:wc/timers
				 */
			}
		);
	}
});
