import shed from "wc/dom/shed.mjs";
import tooltip from "wc/ui/tooltip.mjs";
import label from "wc/ui/label.mjs";

/**
 * Funny old TreeWalker filter: we want to get all the nodes we can remove from element, so we ACCEPT anything
 * which is disabled or hidden.
 *
 * @function
 * @private
 * @param {Element} element The start element
 * @returns {Number} NodeFilter.FILTER_ACCEPT if the node is hidden (and can therefore be removed).
 */
function treeWalkerFilter(element) {
	if (shed.isHidden(element, false, true)) {
		return NodeFilter.FILTER_ACCEPT;
	}

	return NodeFilter.FILTER_SKIP;
}

/**
 * Remove "invisible" descendants from an element.
 * These are element nodes which will not appear in the UI.
 *
 * @function
 * @private
 * @param {Element} clone the element from which we are removing invisible descendants
 */
function removeInvisible(clone) {
	const tw = clone.ownerDocument.createTreeWalker(clone.ownerDocument.body, NodeFilter.SHOW_ELEMENT, treeWalkerFilter);
	tw.currentNode = clone;
	let _el;
	while ((_el = tw.nextNode())) {
		tw.currentNode = _el.parentNode;
		_el.parentNode.removeChild(_el);
	}
}

/**
 * @function module:wc/ui/getVisibleText
 * @param {Element} element The element for which we want to find the text.
 * @param {Boolean} [removeHint] If truthy also remove any HINT (applies only to labels).
 * @param {Boolean} [trim] if truthy then trim the content before returning it
 * @returns {String} The text content of the element without HINT or TOOLTIP.
 */
function getVisibleText(element, removeHint, trim) {
	/** @type {HTMLElement} */
	const clone = /** @type {HTMLElement} */ (element.cloneNode(true));

	// ToolTip is not necessarily invisible at the time of calling (may have ALT/META key pressed).
	let removableChild = tooltip.getTooltip(clone);
	if (removableChild) {
		clone.removeChild(removableChild);
	}

	if (removeHint) {  // HINT is never "invisible"
		while ((removableChild = label.getHint(clone))) {
			removableChild.parentNode.removeChild(removableChild);
		}
	}

	removeInvisible(clone);
	let content = clone.textContent;
	return trim && content ? content.trim() : content;
}

export default getVisibleText;
