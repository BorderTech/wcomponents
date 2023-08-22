import today from "wc/date/today";
import $pivot from "wc/date/pivot";

/**
 * Convert 2 digit year to 4 digit year, expand using a sliding window. Year must be a number between 0 and 99
 * (inclusive).
 *
 * @function
 * @alias module:wc/date/expandYear
 * @param {(String|number)} year 2 digit year. If you pass in a larger number then the last two digits of that
 *    number will be used.
 * @returns {number} 4 digit year
 */
function expand(year) {
	const current = today.get().getFullYear();
	let	century = current.toString().substring(0, 2),
		pivot = $pivot.get() + parseFloat(current.toString().substring(2, 4));

	year = parseInt(year, 10);
	year %= 100;  // 3456 becomes 56

	if (pivot >= 100) {
		++century;
		pivot -= 100;
	}
	if (year > pivot) {
		--century;
	}
	return century * 100 + year;
}

export default expand;
