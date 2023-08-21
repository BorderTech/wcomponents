/**
 * Indicates if an input is numeric (eg "7" but not "seven").
 *
 * @function module:wc/isNumeric
 * @param {*} [n] The input to test.
 * @returns {Boolean} true if n is numeric.
 */
function isNum(n) {
	let result = false;
	if (n !== null) {
		n *= 1;
		result = !isNaN(n);
	}
	return result;
}
export default isNum;
