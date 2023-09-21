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
 * });
 */
export default function unique(array, compare) {
	const comparator = compare || ((/** @type {any} */ a, /** @type {any} */ b) => a !== b);
	const result = [];
	for (const next of array) {
		let found = false;
		for (const element of result) {
			if (!comparator(next, element)) {
				found = true;
				break;
			}
		}
		if (!found) {
			result.push(next);
		}
	}
	return result;
}
