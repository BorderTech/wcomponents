import getLabelsForElement from "wc/dom/getLabelsForElement.mjs";
import getVisibleText from "wc/ui/getVisibleText.mjs";

/**
 * Intermediary for {@link module:wc/dom/getLabelsForElement} which gets only the first label (in source
 * order). This can also return the content of the 'label' omitting access-key tooltip content.
 *
 * @function module:wc/ui/getFirstLabelForElement
 * @param {Element} element The element for which we want to find labels.
 * @param {Boolean} [contentOnly] Set true if you only want the text content of the label. This will omit the
 *    content in an access-key tooltip and the content of a WLabel hint.
 * @param {Boolean} [keepHint] If truthy and getting contentOnly then keep the label "hint". This is not the
 *   default to retain backwards compatibility.
 * @returns {HTMLElement|String} The first labelling element (in source order) or the text content of that
 *    element.
 */
export default function getFirstLabelForElement(element, contentOnly, keepHint) {
	const labels = getLabelsForElement(element);
	let result;
	if (labels?.length) {
		result = labels[0];
		if (contentOnly) {
			result = getVisibleText(result, !keepHint);
		}
	}
	return result;
}
