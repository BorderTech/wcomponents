import interchange from "wc/date/interchange.mjs";
import monthName from "wc/date/monthName.mjs";

const FORMAT_RE = /y{2,4}|d+|MON|M{2,4}|H+|m+|h+|a+|s+/g,
	NORMALIZE_WHITESPACE_RE = /\s{2,}/g;

/**
 * Provides a means to format one date format to another. The input date format is in the implementation's
 * transfer date format (should be the unambiguous yyyy-mm-dd) and the output is determined by a mask.
 * @constructor
 * @alias module:wc/date/Format
 *
 * @param {String} mask The mask used for formatting. If any part of the mask is not understood then the
 * resulting formatted date will be an empty string.
 *
 * @throws {TypeError} Thrown if the mask is not provided (or is false equivalent).
 *
 * @example myFormatter = new Format("dd MON yyyy");//provides a formatter to dates of the form '31 Jan 2000'
 */
function Format(mask) {
	/**
	 * The formatting mask for this instance.
	 * @var
	 * @type String
	 * @public
	 */
	this.mask = mask;
	if (!mask) {
		throw new TypeError("mask must be provided");
	}
}

/**
 * Formats a date according to a mask. The mask is provided in the constructor and the date is in the
 * implementation transfer format (default YYYY-MM-DD). If the any part of the mask is not understood then the
 * result will be an empty string. If the date does not contain all the necessary parts required by the mask
 * then an attempt will be made to format the parts that are present. It is up to you and your mask to ensure
 * this is not ambiguous.
 *
 * @function
 * @public
 * @param {String} xfer The date to format as a wc "interchange" formatted date string.
 * @returns {String} The formatted date string or an empty string if the date could not be formatted.
 *
 * @todo Could this tie in with some of the existing date classes better (e.g. date.pattern, date.parser, date.explodeMask)?
 * @todo Add support for more format options in the mask.
 * @example var myFormatter = new Format("dd MON yyyy");//provides a formatter to dates of the form '31 Jan 2000'
 * myFormatter.format("2015-05-03");//output '03 May 2015'
 */
Format.prototype.format = function(xfer) {
	let failFlag = false;
	const date = interchange.toValues(xfer);
	let result = date ? this.mask.replace(FORMAT_RE, replaceDatePart) : "";

	/**
	 * String replace function for use in date formatting.
	 * @function replaceDatePart
	 * @private
	 * @param {String} part A part of a transfer date.
	 * @returns {String} The number representing the part of the date or a string containing a single
	 *    space if we cannot work out how to format the part.
	 */
	function replaceDatePart(part) {
		let res, shortForm = false;
		const hour = Number(date.hour);
		const day = Number(date.day);
		const month = Number(date.month);

		switch (part) {
			case "MM":
				res = date.month;
				break;
			case "MON":
			case "MMM":
				shortForm = true;
				/* falls through */
			case "MMMM":
				if (date.month) {
					res = monthName.get(shortForm)[month - 1];
				}
				shortForm = false;
				break;
			case "d":
				res = date.day ? day : null;
				break;
			case "dd":
				res = date.day;
				break;
			case "yy":
				res = date.year ? date.year.substring(2) : null;
				break;
			case "yyyy":
				res = date.year;
				break;
			case "HH":
				res = getHour(date, false, false);
				break;
			case "h":
				res = getHour(date, true, false);
				break;
			case "hh":
				res = getHour(date, true, true);
				break;
			case "mm":
				res = date.minute;
				break;
			case "a":
				if (hour || hour === 0) {
					res = hour < 12 ? "AM" : "PM";
				}
				break;
			case "ss":
				res = date.second;
				break;
			default:
				failFlag = true;
				break;
		}
		return res || " ";
	}

	if (failFlag) {
		result = "";
	} else if (result) {
		result = result.trim();
		result = result.replace(NORMALIZE_WHITESPACE_RE, " ");
	}
	return result;
};

function getHour(date, twelve, pad) {
	let result = date.hour;
	if (result && (twelve || pad)) {
		result *= 1;
		if (result || result === 0) {
			if (twelve) {
				if (result > 12) {
					result -= 12;
				} else if (result === 0) {
					result = 12;
				}
			}
			if (pad && result < 10) {
				result = "0" + result;
			}
		}
	}
	return result;
}

export default Format;
