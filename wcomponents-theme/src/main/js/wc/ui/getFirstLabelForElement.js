/**
 * @module
 * @requires module:wc/dom/getLabelsForElement
 * @requires module:wc/dom/Widget
 * @requires module:wc/dom/textContent
 * @requires module:wc/ui/tooltip */
define(["wc/dom/getLabelsForElement", "wc/dom/Widget", "wc/dom/textContent", "wc/ui/tooltip"],
	/** @param getLabelsForElement wc/dom/getLabelsForElement @param Widget wc/dom/Widget @param textContent wc/dom/textContent @param tooltip wc/ui/tooltip @ignore */
	function(getLabelsForElement, Widget, textContent, tooltip) {
		"use strict";
		var HINT;
		/**
		 * Intermediary for {@link module:wc/dom/getLabelsForElement} which gets only the first label (in source
		 * order). This can also return the content of the 'label' omitting access-key tooltip content.
		 *
		 * @function module:wc/ui/getFirstLabelForElement
		 * @param {Element} element The element for which we want to find labels.
		 * @param {Boolean} [contentOnly] Set true if you only want the text content of the label. This will omit the
		 *    content in an access-key tooltip and the content of a WLabel hint.
		 * @returns {?(Element|String)} The first labelling element (in source order) or the text content of that
		 *    element.
		 */
		function get (element, contentOnly) {
			var labels = getLabelsForElement(element),
				result,
				_label,
				removeableChild;
			if (labels && labels.length) {
				result = labels[0];
				if (contentOnly) {
					_label = result.cloneNode(true);
					HINT = HINT || new Widget("span", "hint");
					if ((removeableChild = HINT.findDescendant(_label))) {
						_label.removeChild(removeableChild);
					}
					if ((removeableChild = tooltip.getTooltip(_label))) {
						_label.removeChild(removeableChild);
					}
					result = textContent.get(_label);
				}
			}
			return result;
		}
		return get;
	});
