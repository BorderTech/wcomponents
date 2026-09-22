/**
 * Module to provide the internationalised "name" for the days of the week for the locale in use. NOTE: we assume there
 * are seven days in a week. This may be a little bold for an i18n helper!
 *
 * @example dayName.get()[1];  // is "Monday" in English
 */

import i18n from "wc/i18n/i18n";

const cache = {};

/**
 * For a given locale returns the names of the days of the week;
 * @param {string} locale
 * @return {string[]}
 */
function getDayNames(locale) {
	const referenceDate = new Date(2024, 5, 9);  // Sunday
	const result = [];
	for (let i = 0; i < 7; i++) {
		result.push(referenceDate.toLocaleDateString(locale, { weekday: "long" }));
		referenceDate.setDate(referenceDate.getDate() + 1);
	}
	return result;
}

/**
 * Get the names of the days of the week in order such that index zero is Sunday (equivalent), index six is
 * Saturday (equivalent). Note this is not an assumption which matters as the array is not used directly to
 * output a week but can be manipulated on an as-needs basis, and we need to have the same i18n property name
 * match a particular day in all locales. The returned Array is your very own special instance which you can
 * play with to your heart's content without affecting any other users of this function.
 *
 * @function
 * @alias module:wc/date/dayName.get
 * @public
 * @static
 * @param {boolean} [startOnMonday] If true the first day in the array will be Monday instead of Sunday.
 * @returns {String[]} The names of the days in order such that index zero is Sunday, index six is Saturday (if startOnMonday is true then zero is Monday, six is Sunday).
 *
 */
function get(startOnMonday) {
	const lang = i18n._getLang();
	// Computing instead of using bundle: i18n.get(["day0", "day1", "day2", "day3", "day4", "day5", "day6"]);
	const days = cache[lang] || (cache[lang] = getDayNames(lang));
	const result = [...days];
	if (startOnMonday) {
		result.push(result.shift());
	}
	return result;
}

export default { get };
