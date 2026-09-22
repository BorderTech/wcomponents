/**
 * Module which knows the names of the months for the locale in use. Month names are available as full names (for
 * example January, janvier) and usual abbreviation (for example Jan, Janv) and "asciified" versions of these.
 *
 */

import i18n from "wc/i18n/i18n";
import asciify from "wc/i18n/asciify";

const cache = {};

/**
 * Set up the month arrays on first use.
 *
 * @function
 * @private
 * @return {{ months: string[], monthsAbbr: string[], monthsAbbrAscii?: string[], monthsAscii?: string[] }}
 */
function initialise() {
	const lang = i18n._getLang();
	let result = cache[lang];
	if (result) {
		return result;
	}
	result = cache[lang] = {};

	result.months = getMonthNames(lang, false);
	result.monthsAbbr = getMonthNames(lang, true);
	let needsAsciiVersion = false;
	let monthsAscii = new Array(result.months.length);
	result.months.forEach((mnthname, idx) => {
		const ascii = asciify(mnthname);
		needsAsciiVersion = needsAsciiVersion || (ascii !== mnthname);
		monthsAscii[idx] = ascii;
	});

	if (needsAsciiVersion) {
		result.monthsAbbrAscii = result.monthsAbbr.map(asciify);
		result.monthsAscii = monthsAscii;
	}
	return result;
}

/**
 * For a given locale returns the names of the months of the year;
 * @param {string} locale
 * @param {boolean} short If true will return abbreviated month names
 * @return {string[]}
 */
function getMonthNames(locale, short) {
	const referenceDate = new Date(2000, 0, 15);  // January
	const result = [];
	const type = short ? "short" : "long";
	for (let i = 0; i < 12; i++) {
		let name = referenceDate.toLocaleDateString(locale, { month: type });
		name = name.replace(/\.$/, "");
		result.push(name);
		referenceDate.setMonth(referenceDate.getMonth() + 1);
	}
	return result;
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
	const variants = initialise();
	let result;
	if (asciified && variants.monthsAscii) {
		result = abbreviated ? variants.monthsAbbrAscii : variants.monthsAscii;
	} else {
		result = abbreviated ? variants.monthsAbbr : variants.months;
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
	const variants = initialise();
	return !!variants.monthsAscii;
}

export default {
	get,
	hasAsciiVersion
};
