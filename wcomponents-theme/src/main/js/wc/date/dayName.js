/**
 * Module to provide the internationalised "name" for the days of the week for the locale in use. NOTE: we assume there
 * are seven days in a week. This may be a little bold for an i18n helper!
 *
 * @example dayName.get()[1];// is Monday in English
 *
 * @module
 * @requires module:wc/i18n/i18n
 */
define(["wc/i18n/i18n"],/** @param i18n wc/i18n/i18n @ignore */function(i18n) {
	"use strict";

	function Day() {
		var days;
		/**
		 * Initialise the day name array on first use.
		 * @function initialise
		 * @private
		 */
		function initialise() {
			days = [
				i18n.get("${wc.date.dayName.0}"),
				i18n.get("${wc.date.dayName.1}"),
				i18n.get("${wc.date.dayName.2}"),
				i18n.get("${wc.date.dayName.3}"),
				i18n.get("${wc.date.dayName.4}"),
				i18n.get("${wc.date.dayName.5}"),
				i18n.get("${wc.date.dayName.6}")];
		}

		/**
		 * Get the names of the days of the week in order such that index zero is Sunday (equivalent), index six is
		 * Saturday (equivalent). Note this is not an assumption which matters as the array is not used directly to
		 * output a week but can be manipulated on an as-needs basis and we need to have the same i18n property name
		 * match a particular day in all locales. The returned Array is your very own special instance which you can
		 * play with to your heart's content without affecting any other users of this function.
		 *
		 * @function
		 * @alias module:wc/date/dayName.get
		 * @public
		 * @static
		 * @param {boolean} startOnMonday If true the first day in the array will be Monday instead of Sunday.
		 * @returns {String[]} The names of the days in order such that index zero is Sunday, index six is Saturday (if startOnMonday is true then zero is Monday, six is Sunday).
		 *
		 */
		this.get = function(startOnMonday) {
			var result;
			if (!days) {
				initialise();
			}
			result = days.concat();
			if (startOnMonday) {
				result.push(result.shift());
			}
			return result;
		};
	}

	return /** @alias module:wc/date/dayName */ new Day();
});
