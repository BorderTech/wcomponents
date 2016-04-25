/**
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Determine the left and top coordinates of a mouse event (click, mousedown etc).
	 *
	 * @function
	 * @alias module:wc/dom/getEventOffset
	 * @param {HTMLEvent} $event The event.
	 * @returns {module:wc/dom/getEventOffset~offsetObject} An object encapsulating the offset.
	 */
	function getOffset($event) {
		var result = {};
		result.X = $event.pageX ? $event.pageX : ($event.clientX + document.documentElement.scrollLeft);
		result.Y = $event.pageY ? $event.pageY : ($event.clientY + document.documentElement.scrollTop);
		return result;
	}
	return getOffset;

	/**
	 * @typedef {Object} module:wc/dom/getEventOffset~offsetObject Describes a mouse event offset.
	 * @property {int} offsetX The X coordinate of the mouse event offset.
	 * @property {int} offsetY The Y coordinate of the mouse event offset.
	 */
});
