/**
 * @module
 * @requires module:wc/dom/focus
 * @requires module:wc/dom/Widget
 */
define(["wc/dom/focus", "wc/dom/Widget"], /** @param focus wc/dom/focus @param Widget wc/dom/Widget @ignore */ function(focus, Widget) {
	"use strict";
	var LABEL;
	/**
	 * Provides a generic way to determine if an event is targetted on, or in, a label element. We need to prevent
	 * some events if the event originates inside a label  element when that label is inside an interested component
	 * (for example if a label is inside a selectable table row).
	 *
	 * @function module:wc/dom/isEventInLabel
	 * @param {Element} target The target of a UI event.
	 * @returns {Boolean} true if the event target is (or is inside) a label and the closest focusable element (if
	 *    any) is not inside the label.
	 */
	function isInLabel(target) {
		var firstActiveElement,
			label;
		LABEL = LABEL || new Widget("label");

		if (!(label = LABEL.findAncestor(target))) {
			// no label ancestor (or self) so definitely not in a label.
			return false;
		}

		if (!(firstActiveElement = focus.getFocusableAncestor(target))) {
			return true;  // we have a label ancestor but not a focusable ancestor, so in a label but not a nested control.
		}

		return !!(firstActiveElement.compareDocumentPosition(label) & Node.DOCUMENT_POSITION_CONTAINED_BY);
	}
	return isInLabel;
});
