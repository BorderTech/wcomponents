/**
 * Module which knows the names of the months for the locale in use. Month names are available as full names (for
 * example January, janvier) and usual abbreviation (for example Jan, Janv) and "asciified" versions of these.
 *
 */

import i18n from "wc/i18n/i18n";
import asciify from "wc/i18n/asciify";

let months,
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
	let needsAsciiVersion = false;
	let monthKeys = [];  // month0, ..., monthB
	let monthAbbrKeys = [];  // monthabbr0, ..., monthabbrB
	for (let i = 0; i < 12; i++) {
		let nextNum = Number(i).toString(16).toUpperCase();
		monthKeys.push(`month${nextNum}`);
		monthAbbrKeys.push(`monthabbr${nextNum}`);
	}
	months = i18n.get(monthKeys);
	monthsAbbr = i18n.get(monthAbbrKeys);

	monthsAscii = new Array(months.length);
	months.forEach(function(mnthname, idx) {
		const ascii = asciify(mnthname);
		needsAsciiVersion |= (ascii !== mnthname);
		monthsAscii[idx] = ascii;
	});
	if (needsAsciiVersion) {
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
function get(abbreviated, asciified) {
	let result;
	if (!months) {
		initialise();
	}
	if (asciified && monthsAscii) {
		result = abbreviated ? monthsAbbrAscii : monthsAscii;
	} else {
		result = abbreviated ? monthsAbbr : months;
	}
	return result.concat();
}

/**
 * Determine if there are asciified month names.
 *
 * @function
 * @alias module:wc/date/monthName.hasAsciiVersion
 * @static
 * @returns {Boolean} true if there are asciified month names (i.e. the month names differ when asciified).
 */
function hasAsciiVersion() {
	if (!months) {
		initialise();
	}
	return !!monthsAscii;
}

export default {
	get,
	hasAsciiVersion
};
