/**
 * @module
 * @requires module:wc/date/today
 * @requires module:wc/date/pivot
 */
define(["wc/date/today", "wc/date/pivot"], /** @param today wc/date/today @param $pivot wc/date/pivot @ignore */ function(today, $pivot) {
	"use strict";
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
		var current = today.get().getFullYear(),
			century = current.toString().substr(0, 2),
			pivot = $pivot.get() + parseFloat(current.toString().substr(2, 2));

		year = parseInt(year, 10);
		year %= 100;  // 3456 becomes 56

		if (pivot >= 100) {
			++century;
			pivot -= 100;
		}
		if (year > pivot) {
			--century;
		}
		return parseFloat(century * 100 + year);
	}

	return expand;
});
