/**
 * @module
 * @requires module:wc/dom/focus
 */
define(["wc/dom/focus"],/** @param focus wc/dom/focus @ignore */function(focus) {
	"use strict";
	/**
	 * Determines if an element is the nearest focusable (and therefore potentially interactive) element to an event
	 * target. It will also return true if the event target has no focusable ancestors since then the element itself
	 * may as well be deemed interactive. NOTE: **all** interactive controls **must** be focusable to meet a11y
	 * requirements.
	 *
	 * The point of this is to prevent a {@link module:wc/dom/Widget#findAncestor} call from hijacking a click meant
	 * for an element 'closer' to the target. For example preventing a table row selection from being invoked by
	 * clicking a button in a selectable row or a collapsible being toggled if the click is on a button in its
	 * summary.
	 *
	 * @function module:wc/dom/isAcceptableTarget
	 * @param {Element} element The element we are expecting to be the ultimate target of the event.
	 * @param {Element} target The actual event.target element.
	 * @returns {Boolean} true if element is target or the first focusable ancestor of target or if element has no
	 *    focusable ancestors.
	 */
	function isAcceptable(element, target) {
		var firstActiveAncestor,
			result = (element === target);

		if (!result) {
			if ((firstActiveAncestor = focus.getFocusableAncestor(target))) {
				result = firstActiveAncestor === element;
			}
			else {
				// element has no focusable ancestors (and therefore no clickable ancestors) and is therefore may as well be clickable itself
				result = true;
			}
		}

		return result;
	}

	return isAcceptable;
});
