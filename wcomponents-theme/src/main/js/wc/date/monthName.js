/**
 * Module which knows the names of the months for the locale in use. Month names are available as full names (for
 * example January, janvier) and usual abbreviation (for example Jan, Janv) and ascii-fied versions of these.
 *
 * @module
 * @requires module:wc/i18n/i18n
 * @requires module:wc/i18n/asciify
 */
define(["wc/i18n/i18n", "wc/i18n/asciify"],
	/** @param i18n wc/i18n/i18n @param asciify wc/i18n/asciify @ignore */
	function(i18n, asciify) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/date/monthName~Month
		 * @private
		 */
		function Month() {
			var months,
				monthsAbbr,
				monthsAscii,
				monthsAbbrAscii;

			/**
			 * Set up the month arrays on first use.
			 *
			 * @function
			 * @private
			 */
			function initialise() {
				var hasAsciiVersion = false;
				months = i18n.get(["month0", "month1", "month2", "month3", "month4", "month5", "month6", "month7",
					"month8", "month9", "monthA", "monthB"]);

				monthsAbbr = i18n.get(["monthabbr0", "monthabbr1", "monthabbr2", "monthabbr3", "monthabbr4",
					"monthabbr5", "monthabbr6", "monthabbr7", "monthabbr8", "monthabbr9", "monthabbrA", "monthabbrB"]);

				monthsAscii = new Array(months.length);
				months.forEach(function(mnthname, idx) {
					var ascii = asciify(mnthname);
					hasAsciiVersion |= (ascii !== mnthname);
					monthsAscii[idx] = ascii;
				});
				if (hasAsciiVersion) {
					// console.log("Building ascii versions of month names");
					monthsAbbrAscii = monthsAbbr.map(asciify);
				} else {
					monthsAscii = null;
					monthsAbbrAscii = null;
				}
				// console.log("Initialised month names on first use", months);
			}

			/**
			 * Get the month names.
			 *
			 * @function
			 * @alias module:wc/date/monthName.get
			 * @static
			 * @param {Boolean} [abbreviated] If true will return the standard abbreviated form of the  month name. This
			 * is generally the shortest form of the month name which:
			 * <ol><li>Is the shortest form of the month name without being ambiguous with other month names.</li>
			 * <li>Is not so short that it is no longer obviously a month name</li></ol>
			 * For example "O" for "October" meets #1 but not #2
			 *
			 * <p>Note that while this could be determined programatically we wish to allow for convention in
			 * various locales and languages.</p>
			 *
			 * <p>For example in French août and avril are not abbreviated to three letters even though this
			 * would meet the criteria above.</p>
			 *
			 * @param {Boolean} [asciified] If true will return asciified versions of the month names.
			 *
			 * @returns {String[]} The names of the months in order where index zero is January, index 11 is December.
			 *    The returned Array is your very own special instance which you can play with to your heart's content
			 *    without affecting any other users of this function.
			 */
			this.get = function(abbreviated, asciified) {
				var result;
				if (!months) {
					initialise();
				}
				if (asciified && monthsAscii) {
					result = abbreviated ? monthsAbbrAscii : monthsAscii;
				} else {
					result = abbreviated ? monthsAbbr : months;
				}
				return result.concat();
			};

			/**
			 * Determine if there are asciified month names.
			 *
			 * @function
			 * @alias module:wc/date/monthName.hasAsciiVersion
			 * @static
			 * @returns {Boolean} true if there are asciified month names (i.e. the month names differ when asciified).
			 */
			this.hasAsciiVersion = function() {
				if (!months) {
					initialise();
				}
				return !!monthsAscii;
			};
		}
		return /** @alias module:wc/date/monthName */new Month();
	});
