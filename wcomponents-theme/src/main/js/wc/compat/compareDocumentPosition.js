/**
 * @module
 * @private
 * @requires module:wc/has
 */
define(["wc/has"],/** @param has wc/has @ignore */ function(has) {
	"use strict";
	/**
	 * Provides a compatibility layer for compareDocumentPosition
	 * \([DOM level 3](http://www.w3.org/TR/DOM-Level-3-Core/core.html#Node3-compareDocumentPosition)\).</p>
	 *
	 * This is for IE8. Every other major browser at time of writing has implemented compareDocumentPosition, and this
	 * compatibility code may not even run in other browsers. Whilst this will patch IE8 it will not work for IE7 and
	 * earlier because they do not have a prototype chain for DOM elements. IE7 and earlier could use it like this:<br>
	 * <pre>
	 require(["wc/compat/compareDocumentPosition"], function(compareDocumentPosition){
	 element.compareDocumentPosition = compareDocumentPosition;
	 });</pre>
	 *
	 * Based on code from here: <http://ejohn.org/blog/comparing-document-position/>
	 *
	 * @function
	 * @private
	 * @alias module:wc/compat/compareDocumentPosition
	 * @see http://www.w3.org/TR/DOM-Level-3-Core/core.html#Node3-compareDocumentPosition
	 * @param {Element} b The element being compared.
	 * @returns {number} The bitmask equivalent of a native compareDocumentPosition.
	 */
	function compareDocumentPosition(b) {
		var result, a = this;
		if (a.ownerDocument === b.ownerDocument) {
			result = (a !== b && a.contains(b) && 16) +
					(a !== b && b.contains(a) && 8) +
					(a.sourceIndex >= 0 && b.sourceIndex >= 0 ?
			(a.sourceIndex < b.sourceIndex && 4) + (a.sourceIndex > b.sourceIndex && 2) : 1) + 0;
		} else {
			result = 0;
		}
		return result;
	}

	if (!has("dom-comparedocumentposition") && (typeof window.Element !== "undefined")) {
		window.Element.prototype.compareDocumentPosition = compareDocumentPosition;
	}

	return compareDocumentPosition;
});
