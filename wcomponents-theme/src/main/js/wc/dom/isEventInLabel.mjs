import focus from "wc/dom/focus";

/**
 * Provides a generic way to determine if an event is targeted on, or in, a label element. We need to prevent
 * some events if the event originates inside a label  element when that label is inside an interested component
 * (for example if a label is inside a selectable table row).
 *
 * @function module:wc/dom/isEventInLabel
 * @param {Element} target The target of a UI event.
 * @returns {Boolean} true if the event target is (or is inside) a label and the closest focusable element (if
 *    any) is not inside the label.
 */
function isInLabel(target) {
	const label = target?.closest("label");
	if (!label) {
		// no label ancestor (or self) so definitely not in a label.
		return false;
	}
	const firstActiveElement = focus.getFocusableAncestor(target);
	if (!firstActiveElement) {
		return true;  // we have a label ancestor but not a focusable ancestor, so in a label but not a nested control.
	}

	return !!(firstActiveElement.compareDocumentPosition(label) & Node.DOCUMENT_POSITION_CONTAINED_BY);
}
export default isInLabel;
