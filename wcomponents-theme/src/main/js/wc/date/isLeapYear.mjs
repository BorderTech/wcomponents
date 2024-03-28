/**
 * Provides a means to determine if a particular year is a leap year.
 *
 * @function
 * @alias module:wc/date/isLeapYear
 * @param {Number} year The <strong>4 digit</strong> year to check. To convert a 2 digit year into a 4 digit one see
 *    {@link module:wc/date/expandYear}.
 * @returns {Boolean} true if year is a leap year.
 */
function isLeapYear (year) {
	const yearDivisibleBy4 = (year % 4 === 0);
	return yearDivisibleBy4 && ((year % 100 !== 0) || (year % 400 === 0));
}
export default isLeapYear;
