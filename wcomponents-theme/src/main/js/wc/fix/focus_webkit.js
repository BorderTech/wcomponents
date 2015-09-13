/**
 * Fix for Chrome / Safari failure to move focus when the user clicks with the mouse on any inputs of type
 * "button", "file", "submit", "radio", "checkbox", or "range"; any anchor; or on non-form element with a tabIndex.
 *
 * Includes searching for the nearest focusable ancestor. This was done, along with a touchstart listener, to fix an
 * issue in webkit mobile browsers (especially noticeable in Safari on iOS).
 * @module
 * @private
 *
 * @requires module:wc.has
 * @requires module:wc/dom/getAncestorOrSelf
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/tag"
 * @requires module:wc/dom/event
 * @requires module:wc/timers
 */
require(["wc/has"], /** @param has @ignore */ function(has) {
	"use strict";
	if (has("webkit") && !has("edge")) {
		require(["wc/dom/getAncestorOrSelf", "wc/dom/Widget", "wc/dom/focus", "wc/dom/tag", "wc/dom/event", "wc/timers"],
			function(getAncestorOrSelf, Widget, focus, tag, event, timers) {
				var FOCUS_WIDGET;
				console.log("Adding webkit focus fix for mouse users.");
				event.add(window, event.TYPE.mousedown, mouseDownEvent, null, null, true);
				event.add(window, event.TYPE.click, clickEvent/* , null, null, true */);
				if (has("event-ontouchstart")) {
					event.add(window, event.TYPE.touchstart, mouseDownEvent, null, null, true);
				}
				function mouseDownEvent($event) {
					var activeElement = document.activeElement,
						targetElement = $event.target;
					if (targetElement !== activeElement) {  // clicked away from focused element
						if (!focus.canFocus(targetElement)) {
							FOCUS_WIDGET = FOCUS_WIDGET || new Widget("", "", {"tabIndex": "0"});
							targetElement = getAncestorOrSelf($event.target, tag.A) ||
										getAncestorOrSelf($event.target, tag.BUTTON) ||
										FOCUS_WIDGET.findAncestor($event.target);
						}
						if (targetElement && needsFocusFix(targetElement) && targetElement !== activeElement) {
							tryFocusFix(targetElement);
						}
					}
				}

				function clickEvent($event) {
					var target = $event.target,
						activeElement = document.activeElement;
					if (!$event.defaultPrevented && target !== activeElement) {
						if ((target.tabIndex === 0 && !focus.isNativelyFocusable(target.tagName)) ||
							(target.tagName === tag.INPUT && needsFocusFix(target))) {
							tryFocusFix(target);
						}
					}
				}

				function needsFocusFix(element) {
					var result = false,
						inputTypesNeedFix = ["button", "file", "submit", "radio", "checkbox", "range"];
					if (inputTypesNeedFix.indexOf(element.type) >= 0 || element.tagName === tag.A) {
						result = true;
					}
					else if (!focus.isNativelyFocusable(element.tagName)) {
						result = element.tabIndex > -1;
					}
					return result;
				}

				function tryFocusFix(element) {
					timers.setTimeout(function() {
						try {
							element.focus();
						}
						catch (ex) {
							console.error("Error applying webkit focus fix", ex);
						}
					}, 0);
				}
			}
		);
	}
});
