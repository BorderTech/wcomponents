define(["wc/dom/textContent",
	"wc/dom/shed",
	"wc/ui/tooltip",
	"wc/ui/label"],
	function (textContent, shed, tooltip, label) {
		"use strict";

		/**
		 * Funny old treewalker filter: we want to get all the nodes we can remove from element so we ACCEPT anything
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
		 * Remove "invisible" descendants from an element. These are element nodes which will not appear in the UI.
		 *
		 * @function
		 * @private
		 * @param {Element} clone the element from which we are removing invisible descendants
		 * @returns {undefined}
		 */
		function removeInvisibles(clone) {
			var tw = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, treeWalkerFilter, false),
				_el;

			tw.currentNode = clone;
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
			var clone = element.cloneNode(true),
				removeableChild,
				content;

			// ToolTip is not necessarily invisible at the time of calling (may have ALT/META key pressed).
			if ((removeableChild = tooltip.getTooltip(clone))) {
				clone.removeChild(removeableChild);
			}

			if (removeHint) { // HINT is never "invisible"
				while ((removeableChild = label.getHint(clone))) {
					removeableChild.parentNode.removeChild(removeableChild);
				}
			}

			removeInvisibles(clone);
			content = textContent.get(clone);
			return trim && content ? content.trim() : content;
		}
		/**
		 * @module
		 * @requires module:wc/dom/Widget
		 * @requires module:wc/dom/textContent
		 * @requires module:wc/dom/shed
		 * @requires module:wc/ui/tooltip
		 * @requires module:wc/ui/label
		 */
		return getVisibleText;
	});

