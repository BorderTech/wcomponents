/**
 * Utilities for working with the wc date interchange format.
 *
 * This is closely related to the ISO_8601 format YYYY-MM-DD with the difference that we allow any parts of the date
 * to be absent.
 *
 * The "interchange" nature refers primarily to interchange between client and server in an unambiguous format that
 * supports both full and partial dates.
 *
 * This is actually really useful within the client code itself and I suggest it be used for passing dates between
 * various objects and functions. A JS date is not suitable because it does not support partial dates and
 * {@link module:wc/date/Parser} (which to some degree had this intent) is too complex and heavy. The xfer date string,
 * on the other hand, is extremely simple and easy to use and understand.
 *
 * Note: for the sake of interoperability with ISO_8601 it's probably a good idea to support the alternate
 * format without separators (YYYYMMDD) at least when consuming dates (perhaps not producing them).
 *
 * @module
 * @requires module:wc/date/today
 * @requires external:lib/sprintf
 * @todo port all of our date utils to work with xfer date strings unless they exclusively work with full dates only.
 */
define(["wc/date/today", "lib/sprintf"],
	/** @param $today wc/date/today @param sprintf lib/sprintf @ignore */
	function($today, sprintf) {
		"use strict";

		/**
		 * @constructor
		 * @private
		 * @alias module:wc/date/interchange~DateInterchange
		 */
		function DateInterchange() {
			var PLACEHOLDER = "?",
				NON_NUMERRIC_RE = /[^\d]/g,
				dmPlaceholder = sprintf.sprintf("%'?2s", ""),
				yPlaceholder = sprintf.sprintf("%'?4s", ""),
				FULL_DATE_TEMPLATE = "%04d-%02d-%02d",
				PARTIAL_DATE_TEMPLATE = "%04s-%02s-%02s",
				XFER_DATE_RE = /([\d\?]{4})-?([\d\?]{2})-?([\d\?]{2})(?:T(\d{2}):(\d{2}):(\d{2}))?/;

			/**
			 * Split the transfer format into its constituent parts. Any missing parts of the date will be replaced with
			 * default values.
			 *
			 * @function
			 * @private
			 * @param {string} xfr A transfer date string.
			 * @param  {boolean} defaults If true default values will  be used to fill missing date parts (otherwise
			 *    they will be null).
			 * @returns {Array} [YYYY, MM, DD]
			 */
			function splitXferDate(xfr, defaults) {
				var result, day, month, year, hour, minute, second, today = $today.get(),  // the use of wc/date/today is to make this unit testable on boundary dates
					parsed = xfr.match(XFER_DATE_RE),
					defaultValues = {
						1: today.getFullYear(),
						2: 1,
						3: 1,
						4: 0,
						5: 0,
						6: 0
					},
					getVal = function (idx) {
						var next = parsed[idx];
						return (next && next.indexOf(PLACEHOLDER) < 0) ? next : (defaults ? defaultValues[idx] : null);
					};

				if (parsed) {
					year = getVal(1);
					month = getVal(2);
					day = getVal(3);
					hour = getVal(4);
					minute = getVal(5);
					second = getVal(6);
					result = [year, month, day, hour, minute, second];
				}
				return result;
			}

			/**
			 * Determines if the date string contains all year month and day values.
			 *
			 * @function
			 * @alias module:wc/date/interchange.isComplete
			 * @static
			 * @param {String} xfr A transfer date string.
			 * @returns {Boolean} true if this is a complete date string.
			 */
			this.isComplete = function(xfr) {
				var tmp,
					result = false;
				if (xfr) {
					tmp = xfr.replace(NON_NUMERRIC_RE, "");
					if (tmp.length === 8) {
						result = true;
					}
				}
				return result;
			};

			/**
			 * Indicates if the argument represents a valid date.
			 * @function
			 * @alias module:wc/date/interchange.isValid
			 * @static
			 * @param {String} xfr A transfer date string.
			 * @returns {Boolean} true if this is a valid transfer date string.
			 */
			this.isValid = function(xfr) {
				return !!(xfr && XFER_DATE_RE.test(xfr));
			};

			/**
			 * Converts a date object to transfer format.
			 *
			 * @function
			 * @alias module:wc/date/interchange.fromDate
			 * @static
			 * @param {Date} date The date to convert.
			 * @param {boolean} includeTime If true the time part of the date will be included.
			 * @returns {String} The given date converted to a transfer date string.
			 */
			this.fromDate = function(date, includeTime) {
				var template = includeTime ? "%04d-%02d-%02dT%02d:%02d:%02d" : FULL_DATE_TEMPLATE;
				return sprintf.sprintf(template, date.getFullYear(), (date.getMonth() + 1), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
			};

			/**
			 * Get a javascript date instance which represents the given transfer date string. Note, any missing parts
			 * of the date will be filled with default values.
			 *
			 * @function
			 * @alias module:wc/date/interchange.toDate
			 * @static
			 * @param {String} xfr A transfer date string.
			 * @returns {?Date} A date represented by the xfr string.
			 */
			this.toDate = function(xfr) {
				var result,
					parts = splitXferDate(xfr, true);
				if (parts) {
					result = new Date(parts[0], parts[1] - 1, parts[2], parts[3], parts[4]);
				}
				return result;
			};

			/**
			 * Get a transfer date string representation of this object.
			 *
			 * @function
			 * @alias module:wc/date/interchange.fromValues
			 * @static
			 * @param {module:wc/date/interchange~dateFromValuesObject} obj The object containing values to be
			 *    converted to a transfer format date.
			 * @returns {String} The given object's values converted to a transfer date string.
			 */
			this.fromValues = function(obj) {
				var y = obj.year || yPlaceholder,  // there is no year zero
					m = obj.month || dmPlaceholder,  // there is no month zero
					d = obj.day || dmPlaceholder;  // there is no day zero
				return sprintf.sprintf(PARTIAL_DATE_TEMPLATE, y, m, d);
			};

			/**
			 * Converts a transfer date to an object with year, month and day propeties.
			 *
			 * @function
			 * @alias module:wc/date/interchange.toValues
			 * @static
			 * @param {String} xfr A transfer date string.
			 * @returns {module:wc/date/interchange~toValuesReturnObject} An object representing the three segments of
			 *    the transfer format: year, month and day. Any missing parts of the date will be null.
			 */
			this.toValues = function(xfr) {
				var result,
					parts = splitXferDate(xfr, false);
				if (parts) {
					result = {
						year: parts[0],
						month: parts[1],
						day: parts[2],
						hour: parts[3],
						minute: parts[4],
						second: parts[5]
					};
				}
				return result;
			};
		}
		return /** @alias module:wc/date/interchange */ new DateInterchange();

		/**
		 * @typedef {Object} module:wc/date/interchange~dateFromValuesObject
		 * @property {(number|String)} [year] The full (four? digit) year (possibly as a String).
		 * @property {(number|String)} [month] The month number (possibly as a String), 1 indexed.
		 * @property {(number|String)} [day] The day number (possibly as a String), 1 indexed.
		 */

		/**
		 * @typedef {Object} module:wc/date/interchange~toValuesReturnObject
		 * @property {?String} year The full year as a String. Years between 0 and 999 are padded to four characters
		 *    with leading zeros
		 * @property {?String} month The month number as a String (note 1 indexed).
		 * @property {?String} day The day number as a String (note 1 indexed).
		 */
	});
