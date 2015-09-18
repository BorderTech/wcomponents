/**
 * @module
 * @requires module:wc/date/isLeapYear
 */
define(["wc/date/isLeapYear"], /** @param isLeapYear wc/date/isLeapYear @ignore */ function(isLeapYear) {
	"use strict";
	/**
	 * Get the number of days in a given month of a given year.
	 *
	 * @example var today = new Date();
	 * daysInMonth(today.getFullYear(), today.getMonth()+1);//month is not zero based (why not??)
	 * //will return the number of days in the current month.
	 *
	 * @function
	 * @alias module:wc/date/daysInMonth
	 * @param {number} year The 2 or 4 digit year to check.
	 * @param {number} month The month - this <strong>not</strong> zero based, ie january = 1, december = 12
	 * @returns {?int} The number of days in the month if we can detemine it.
	 */
	function daysInMonth(year, month) {
		var result;
		if (month === 2) {
			result = (year || year === 0) ? (isLeapYear(year) ? 29 : 28) : 29;
		}
		else {
			result = [undefined, 31, undefined, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
		}
		return result;
	}
	return daysInMonth;
});
