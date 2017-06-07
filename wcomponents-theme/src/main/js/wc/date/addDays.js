/**
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Provides a generic means to add and subtract a number of days from a date object in a way which modifies the
	 * original object. Adds or subtracts (if days argument is negative) the number of days specified. The date is
	 * rolled to ensure it represents an actual day.
	 *
	 * @example
	 * var myDate = new Date('01 JAN 2050');
	 * addDays(35, myDate);
	 * // myDate is now '05 FEB 2050' as JAN has 31 days
	 *
	 * @example
	 * var thisTimeLastWeek = addDays(-7, new Date());
	 * //thisTimeLastWeek is now seven days before now.
	 *
	 * @function
	 * @alias module:wc/date/addDays
	 * @param {Number} days The number of days to add or subtract.
	 * @param {Date} date The date object on which the operation will be performed.
	 */
	function add(days, date) {
		date.setDate(date.getDate() + days);
	}

	return add;
});
