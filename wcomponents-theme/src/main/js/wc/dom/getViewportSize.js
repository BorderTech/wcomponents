/**
 * Get the viewport size.
 * @module
 * @requires module:wc/dom/getBox
 */
define(["wc/dom/getBox"], /** @param getBox wc/dom/getBox @ignore */ function(getBox) {
	"use strict";
	/**
	 * Get the viewport size.
	 *
	 * @function module:wc/dom/getViewportSize
	 * @param {Boolean} [withoutScrollbars] If true then attempt to account for the scroll bar width.
	 * @returns {module:wc/dom/getViewportSize~result} The viewport size encapsulated in an object.
	 */
	return function (withoutScrollbars) {
		var result = {},
			box,
			DOCUMENT_ELEMENT = document.documentElement,
			SELF = window.self,
			thisViewportView = window.viewportView,
			WIDTH = "width",
			HEIGHT = "height";  // to improve compression

		if (withoutScrollbars) {
			result[WIDTH] = DOCUMENT_ELEMENT.clientWidth;
			result[HEIGHT] = DOCUMENT_ELEMENT.clientHeight;
		} else if (thisViewportView) {
			result[WIDTH] = thisViewportView.clientWidth;
			result[HEIGHT] = thisViewportView.clientHeight;
		} else if (typeof SELF.innerWidth !== "undefined") {
			result[WIDTH] = SELF.innerWidth;
			result[HEIGHT] = SELF.innerHeight;
		} else if (document.documentElement && document.documentElement.getBoundingClientRect) {
			box = getBox(DOCUMENT_ELEMENT);
			result[WIDTH] = box[WIDTH];
			result[HEIGHT] = box[HEIGHT];
		} else {
			result[WIDTH] = DOCUMENT_ELEMENT.clientWidth || document.body.clientWidth || 0;
			result[HEIGHT] = DOCUMENT_ELEMENT.clientHeight || document.body.clientHeight || 0;
		}
		return result;

		/**
		 * @typedef {Object} module:wc/dom/getViewportSize~result An object encapsulating the viewport size.
		 * @property {number} width The viewport width in pixels.
		 * @property {number} height The viewport height in pixels.
		 */
	};
});
