/**
 * @module
 */
define(function() {
	"use strict";

	/**
	 * Gets the boundingClientRect for an element and returns an object containing the box along with width and height
	 * (these are most often used and are not supported in old versions of IE).
	 *
	 * @alias module:wc/dom/getBox
	 * @param {Element} element The element of which we need the box.
	 * @param {Boolean} [round] Should we round the result before returning it?
	 * @returns {module:wc/dom/getBox~box} A dimension object as per getBoundingClientRect. Properties width and height
	 *   are added if they are undefined. NOTE: IE8 will throw an error if you try to add width and/or height properties
	 *   directly to the result of getBoundingClientRect, this is why we return another object.
	 */
	function getBox(element, round) {
		var result = {},
			box = element.getBoundingClientRect(),
			o;
		for (o in box) {
			result[o] = box[o];
		}
		if (typeof box.height === "undefined") {
			result.height = box.bottom - box.top;
		}
		if (typeof box.width === "undefined") {
			result.width = box.right - box.left;
		}

		if (round) {
			for (o in result) {
				result[o] = Math.round(result[o]);
			}
		}
		return result;
	}

	return getBox;

	/**
	 * @typedef {Object} module:wc/dom/getBox~box
	 * @property {number} left The left edge of the box.
	 * @property {number} right The right edge of the box.
	 * @property {number} top The top edge of the box.
	 * @property {number} bottom The bottom edge of the box.
	 * @property {number} width The width of the box in pixels.
	 * @property {number} height The height of the box in pixels.
	 */
});
