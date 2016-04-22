/**
 * @module
 * @requires module:wc/date/interchange
 * @requires module:wc/date/monthName
 */
define(["wc/date/interchange", "wc/date/monthName"],
	/** @param interchange wc/date/interchange @param monthName wc/date/monthName @ignore */
	function(interchange, monthName) {
		"use strict";
		var FORMAT_RE = /y{2,4}|d+|MON|M{2,4}|H+|m+|h+|a+|s+/g,
			NORMALIZE_WHITESPACE_RE = /\s{2,}/g;

		/**
		 * Provides a means to format one date format to another. The input date format is in the implementation's
		 * transfer date format (should be the unambiguous yyyy-mm-dd) and the output is determined by a mask.
		 * @constructor
		 * @alias module:wc/date/Format
		 *
		 * @param {String} mask The mask used for formatting. If the any part of the mask is not understood then the
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
		Format.prototype.format = function (xfer) {
			var failFlag = false,
				date = interchange.toValues(xfer),
				result = date ? this.mask.replace(FORMAT_RE, replaceDatePart) : "";

			/**
			 * String replace function for use in date formatting.
			 * @function replaceDatePart
			 * @private
			 * @param {String} part A part of a transfer date.
			 * @returns {(Number|String)} The number representing the part of the date or a string containing a single
			 *    space if we cannot work out how to format the part.
			 */
			function replaceDatePart(part) {
				var result,
					shortForm = false;
				switch (part) {
					case "MM":
						result = date.month;
						break;
					case "MON":
					case "MMM":
						shortForm = true;
						/* falls through */
					case "MMMM":
						if (date.month) {
							result = monthName.get(shortForm)[date.month - 1];
						}
						shortForm = false;
						break;
					case "d":
						result = date.day ? date.day * 1 : null;
						break;
					case "dd":
						result = date.day;
						break;
					case "yy":
						result = date.year ? date.year.substr(2) : null;
						break;
					case "yyyy":
						result = date.year;
						break;
					case "HH":
						result = getHour(date, false, false);
						break;
					case "h":
						result = getHour(date, true, false);
						break;
					case "hh":
						result = getHour(date, true, true);
						break;
					case "mm":
						result = date.minute;
						break;
					case "a":
						result = (date.hour || date.hour === 0) ? (date.hour < 12 ? "AM" : "PM") : "";
						break;
					case "ss":
						result = date.second;
						break;
					default:
						failFlag = true;
						break;
				}
				return result || " ";
			}
			if (failFlag) {
				result = "";
			}
			else if (result) {
				result = result.trim();
				result = result.replace(NORMALIZE_WHITESPACE_RE, " ");
			}
			return result;
		};

		function getHour(date, twelve, pad) {
			var result = date.hour;
			if (result && (twelve || pad)) {
				result *= 1;
				if (result || result === 0) {
					if (twelve) {
						if (result > 12) {
							result -= 12;
						}
						else if (result === 0) {
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

		return Format;
	});
