/**
 * Fix for Chrome / Safari failure to move focus when the user clicks with the mouse on any inputs of type
 * "button", "file", "submit", "radio", "checkbox", or "range"; any anchor; or on non-form element with a tabIndex.
 *
 * Verified this is still the behaviour of Safari in 2023, v16.4
 *
 * Includes searching for the nearest focusable ancestor. This was done, along with a touchstart listener, to fix an
 * issue in webkit mobile browsers (especially noticeable in Safari on iOS).
 * @module
 * @private
 */

import focus from "wc/dom/focus";
import event from "wc/dom/event";
import timers from "wc/timers";

console.log("Adding webkit focus fix for mouse users.");
event.add(window, { type: "mousedown", listener: mouseDownEvent, capture: true });
event.add(window, { type: "click", listener: clickEvent /* , capture: true */ });
event.add(window, { type: "touchstart", listener: mouseDownEvent, capture: true });

function mouseDownEvent($event) {
	const activeElement = document.activeElement;
	let targetElement = $event.target;
	if (targetElement !== activeElement) {  // clicked away from focused element
		if (!focus.canFocus(targetElement)) {
			targetElement = targetElement.closest("a, button, [tabindex='0']");
		}
		if (targetElement && needsFocusFix(targetElement) && targetElement !== activeElement) {
			tryFocusFix(targetElement);
		}
	}
}

function clickEvent($event) {
	const target = $event.target,
		activeElement = document.activeElement;
	if (!$event.defaultPrevented && target !== activeElement) {
		if ((target.tabIndex === 0 && !focus.isNativelyFocusable(target.tagName)) ||
			(target.matches("input") && needsFocusFix(target))) {
			tryFocusFix(target);
		}
	}
}

function needsFocusFix(element) {
	let result = false;
	const inputTypesNeedFix = ["button", "file", "submit", "radio", "checkbox", "range"];
	if (inputTypesNeedFix.indexOf(element.type) >= 0 || element.matches("a")) {
		result = true;
	} else if (!focus.isNativelyFocusable(element.tagName)) {
		result = element.tabIndex > -1;
	}
	return result;
}

function tryFocusFix(element) {
	timers.setTimeout(function() {
		try {
			element.focus();
		} catch (ex) {
			console.error("Error applying webkit focus fix", ex);
		}
	}, 0);
}
