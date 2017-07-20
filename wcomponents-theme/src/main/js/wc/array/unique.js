/**
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Provide a mechanism to remove repeated items from an Array. Especially handy after concatenating groups of
	 * components. Does not modify the argument, instead returns a new array. Wll work with any array-like object -
	 * man, it would even work on a string (just needs length). Accepts a compare function for custom equality testing.
	 *
	 * @function module:wc/array/unique
	 * @public
	 * @param {Array} array An array (or array-like structure such as a NodeList)
	 * @param {Function} [compare] A function used to determine if two array elements are equal.This function should
	 *    return ZERO if the two elements are equal. While this may not seem logical it allows reuse of array sort
	 *    functions to determine equality.
	 *
	 * @returns {Array} A new array representing the array arg but with duplicate items removed.
	 *
	 * @example unique(["foo","bar","fu","bar"]);
	 * //will return ["foo","bar","fu"]
	 *
	 * @example require(["wc/array/unique"], function(unique) {
	 *    var myArray = [...],
	 *        uniqueArray = myArray.unique();
	 *    //do stuff with the unique array - for example is it the same length as the original one indicating that there are no double-ups.
	 * });
	 */
	return function(array, compare) {
		var i, j, found, next, result = [];
		for (i = 0; i < array.length; i++) {
			next = array[i];
			found = false;
			for (j = 0; j < result.length; j++) {
				if ((compare && compare(next, result[j]) === 0) || (!compare && next === result[j])) {
					found = true;
					break;
				}
			}
			if (!found) {
				result[result.length] = next;
			}
		}
		return result;
	};
});
