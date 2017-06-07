/**
 * A little module to ease getting the text content of an element.
 * @module
 * @requires module:wc/has
 */
define(["wc/has"], /** @param has wc/has @ignore */ function(has) {
	"use strict";
	has.add("element-textcontent", function(g, d, el) {
		return (typeof el.textContent !== "undefined");
	});

	/**
	 * @constructor
	 * @alias module:wc/dom/textContent~TextContent
	 * @private
	 */
	function TextContent() {
		var textContentProperty = has("element-textcontent") ? "textContent" : "innerText";

		/**
		 * Get the text content of an element
		 *
		 * @function module:wc/dom/textContent.get
		 * @param {Element} element The element from which we want the text content.
		 * @returns {String} The element's text content.
		 */
		this.get = function(element) {
			return element[textContentProperty];
		};

		/**
		 * Set the text content of an element. NOTE: this will replace any existing content (including any [inner]HTML).
		 *
		 * @function module:wc/dom/textContent.set
		 * @param {Element} element The element into which we want to set the text content.
		 * @param {String} value The content we want the element to contain.
		 */
		this.set = function (element, value) {
			element[textContentProperty] = value;
		};
	}
	return /** @alias module:wc/dom/textContent */ new TextContent();
});
