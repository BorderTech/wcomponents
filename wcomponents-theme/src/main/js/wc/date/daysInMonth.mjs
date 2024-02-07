/**
 * Get the number of days in a given month of a given year.
 *
 * @example var today = new Date();
 * daysInMonth(today.getFullYear(), today.getMonth()+1);  // month is not zero based (why not??)(because it isn't!!)
 * // will return the number of days in the current month.
 *
 * @function
 * @alias module:wc/date/daysInMonth
 * @param {number} year The 2 or 4 digit year to check.
 * @param {number} month The month - this NOT zero based, ie january = 1, december = 12
 * @returns {number} The number of days in the month if we can determine it.
 */
export default function daysInMonth(year, month) {
	return new Date(year, month, 0).getDate();
}
