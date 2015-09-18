/**
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Copy a date object. At first glance seems a bit of sledgehammer to crack a walnut but we have seen
	 * noobs copy dates in parts which can lead to rollover problems.
	 *
	 * @example
	 *    var date = new Date("1999","5","25"),
	 *        clonedDate = copy(date);
	 *    console.log(clonedDate === date?'you will never get here':'you will always get here');
	 *    console.log(clonedDate.getTime() === date.getTime()?'you will always get here':'you will never get here');
	 *
	 * @function
	 * @alias module:wc/date/copy
	 * @param {Date} date The date to copy.
	 * @returns {Date} A clone of the date.
	 */
	function copy(date) {
		var result = new Date();
		result.setTime(date.getTime());
		return result;
	}
	return copy;
});
