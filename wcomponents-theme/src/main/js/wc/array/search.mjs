/**
 * Provides a search for arrays.
 *
 * @function module:wc/array/search
 * @public
 * @param {Array} array The array to search
 * @param {(RegExp|String)} regexp A regular expression used to define the search criteria. If a String it is
 *    implicitly converted to a RegExp by using new RegExp(obj).
 * @returns {number} The index of the first match of regexp in array, -1 if not found.
 *
 * @example
 * search(["foo", "bar", "fu", "baaaa"], /[aAeEiI]{2,}/);
 * //will return 3
 */
export default function search(array, regexp) {
	// this whole module can probably be replaced with array.findIndex
	const len = array.length;
	for (let i = 0; i < len; i++) {
		let next = array[i];
		if (next && (next.constructor === String || (next = next.toString ? next.toString() : null))) {
			if (next.search(regexp) > -1) {
				return i;
			}
		}
	}
	return -1;
}
