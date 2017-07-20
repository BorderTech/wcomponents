/**
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Provides a search for arrays.
	 *
	 * @function module:wc/array/search
	 * @public
	 * @param {Array} array The array to search
	 * @param {(RegExp|String)} regexp A regular expression used to define the search criteria. If a String it is
	 *    implicitly converted to a RegExp by using new RegExp(obj).
	 * @returns {int} The index of the first match of regexp in array, -1 if not found.
	 *
	 * @example
	 * search(["foo", "bar", "fu", "baaaa"], /[aAeEiI]{2,}/);
	 * //will return 3
	 */
	return function(array, regexp) {
		var next;
		for (var i = 0, len = array.length; i < len; i++) {
			next = array[i];
			if (next && (next.constructor === String || (next = next.toString ? next.toString() : null))) {
				if (next.search(regexp) > -1) {
					return i;
				}
			}
		}
		return -1;
	};
});
