/**
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Compare two dates and return the difference. The time segment is ignored unless explicitly asked for.
	 *
	 * NOTE:
	 * diff(1,3) == 1-3 == -2; therefore diff(today, tomorrow) = -1 and diff(today, yesterday) = 1
	 *
	 * @function
	 * @alias module:wc/date/getDifference
	 * @param {Date} date1 A Date.
	 * @param {Date} date2 Another Date.
	 * @param {Boolean} [includeTime] If true then we also want the time difference and return millis.
	 * @returns {Number} The difference (0 if same day and !includeTime or if the Dates are absolutely identical).
	 */
	function getDiff(date1, date2, includeTime) {
		var result = null,
			divisor = 1;
		if (date1.constructor === Date && date2.constructor === Date) {
			if (!includeTime) {
				date1 = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());
				date2 = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());
				divisor = 86400000;
			}
			result = (date1 - date2) / divisor;
		} else {
			throw new TypeError("Cannot compare dates which are not of the same type");
		}
		return result;
	}
	return getDiff;
});
