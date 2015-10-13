/**
 * @module
 * @requires module:wc/date/today
 * @requires module:wc/date/pattern
 * @requires module:wc/date/interchange
 * @requires module:wc/date/explodeMask
 */
define(["wc/date/today",
		"wc/date/pattern",
		"wc/date/interchange",
		"wc/date/explodeMask"],
	/** @param $today wc/date/today @param $pattern wc/date/pattern @param interchange wc/date/interchange @param explodeMask wc/date/explodeMask @ignore */
	function($today, $pattern, interchange, explodeMask) {
		"use strict";
		var maskCache = null;

		/**
		 * Represents a single match.
		 *
		 * @constructor
		 * @private
		 * @alias module:wc/date/Parser~Match
		 */
		function Match() {
			/**
			* Get a transfer date formatted string representing this match.
			*
			* @function
			* @public
			* @returns {String} The transfer date.
			*/
			this.toXfer = function() {
				return interchange.fromValues(this);
			};

			/**
			* Get a Javascript Date instance representing this match. Any missing values are populated with default values.
			*
			* @function
			* @public
			* @returns {Date} The date represented by the match.
			*/
			this.toDate = function() {
				var xfer = this.toString();
				return interchange.toDate(xfer);
			};
		}

		/**
		 * Provides a module to parse dates to pretty much any given mask. For samples of masks see
		 * {@link module:wc/ui/dateField} - there are a lot of them.
		 *
		 * The Parser class is used to extend the capabilities of the standard JavaScript Parser. It allows more control
		 * over what may be interpreted as a date.
		 *
		 * @constructor
		 * @alias module:wc/date/Parser
		 */
		function Parser() {
			var rolling = false,
				masks = null,
				expandYearIntoPast = false;

			/**
			 * This function loops through all the masks for the date and compiles the regular expression used to match
			 * it. It also returns an array of the patterns that the regular expression submatches represent.
			 * The result is in the following form:
			 * [
			 *  {mask: '', compiled://, pattern:[pattern1, pattern2, etc]},
			 *  ...etc
			 * ]
			 *
			 * For performance we store the result in the variable maskCache so that when the same mask is later used it
			 * will be retrieved from the cache not recalculated.
			 *
			 * @function
			 * @private
			 * @returns {Array} The patterns represented by the compiled masks.
			 */
			function getCompiledMasks() {
				var result = [],
					next = null,
					i,
					patterns,
					re,
					j,
					input;
				for (i = 0; i < masks.length; i++) {
					next = masks[i];

					// check the cache
					if (maskCache && next in maskCache) {
						result.push(maskCache[next]);
						continue;
					}
					// not in the cache, build it
					patterns = explodeMask(next, true);

					// compile the pattern pieces into a regular expression
					re = "^";
					for (j = 0; j < patterns.length; j++) {
						input = $pattern[patterns[j]]["input"];
						if (input.constructor === Function) {
							input = input();
						}
						re += input;
					}
					re += "$";
					re = new RegExp(re);

					// associate the regular expression with the exploded mask (patterns array).
					// This is so that when we do the match we can determine what each submatch represents
					result.push({mask: next, compiled: re, pattern: patterns});

					// store in the maskCache
					if (!maskCache) {
						maskCache = {};
					}
					maskCache[next] = result[result.length - 1];
				}
				return result;
			}

			/**
			 * Parse a string representing user input of a date-like piece of data to a list of possible matches.
			 * @function
			 * @param {String} string The date-like (or partial date-like) input string.
			 * @returns {module:wc/date/Parser#parsedDate[]} List of possible matches like so:
			 * <pre>
			 * [
			 *	 {day:1,  month:12, year: 2005},
			 *	 {day:14, month:4,  year: 2000},
			 *	 // etc
			 * ]
			 * </pre>
			 */
			this.parse = function (string) {
				// trim leading & trailing spaces
				string = string.toString().trim();

				var result = [],
					mask,
					match,
					i,
					l,
					SEPARATOR = "/",
					masks = getCompiledMasks(),
					next,
					patternBits,
					normalise,
					j,
					key,
					pattern,
					check,
					today,
					rolled;
				// 'normalise' each date related value
				for (i = 0, l = masks.length; i < l; i++) {
					mask = masks[i];
					match = string.match(mask.compiled);
					if (match === null) {
						continue;
					}
					// this object is populated with the date info before being
					// pushed into the result array
					next = new Match();

					// store all the raw values
					patternBits = mask.pattern;
					normalise = [];
					j = patternBits.length;
					while (j--) {
						key = patternBits[j];
						pattern = $pattern[key];
						normalise.push(pattern.normalise);
						next[pattern.name] = match[j + 1];
					}

					// normalise them
					j = normalise.length;
					while (j--) {
						normalise[j](next, this);
					}
					// if we have a full date
					if (next.day && next.month && next.year) {
						// only check and remove complete dates
						check = new Date(next.month + SEPARATOR + next.day + SEPARATOR + next.year);

						// check if year expanded to the future, roll back the
						// century until we are in the past;
						if (this.isExpandYearIntoPast()) {
							today = $today.get();
							while (check > today) {
								check = new Date(next.month + SEPARATOR + next.day + SEPARATOR + (next.year = next.year - 100));
							}
						}

						// check for date rolling
						if (!this.isRolling()) {
							rolled = next.day !== check.getDate() || next.month !== (check.getMonth() + 1) || next.year !== check.getFullYear();
							// dont add this to the list of successful matches
							if (rolled) {
								continue;
							}
						}
						else {
							// if we are rolling we return the 'rolled' date
							next.day = check.getDate();
							next.month = check.getMonth() + 1;
							next.year = check.getFullYear();
						}
						next.date = check;
					}
					// add to the successful matches list
					result.push(next);
				}
				return result;
			};

			/**
			 * Date Rolling:
			 * If a date is parsed as 40/02/2000 Rolling determines if it is to be rejected as a match, or 'rolled'
			 * forward to a valid date (in this case 11/03/2000).
			 *
			 * @function
			 * @param {Boolean} arg Indicates if dates should be rolled forwards.
			 */
			this.setRolling = function (arg) {
				rolling = arg;
			};

			/**
			 * Is the date allowed to roll?
			 *
			 * @function
			 * @returns {Boolean} true is rolling is enabled.
			 */
			this.isRolling = function () {
				return rolling;
			};

			/**
			 * <p>An array of date masks used to determine what is actually matched as a date.
			 * An example list might be:
			 * ['D/M/YY', 'D/M/YYYY', 'D/MON/YY', 'D/MON/YYYY', 'DDMMYY', 'DMYYYY', 'DMONYY', 'DMONYYYY']
			 * To match common British, Australian, etc dates.
			 * This list is easily customisable. Here are some common lists:</p>
			 * <dl>
			 *	<dt>Little endian forms, starting with the day</dt>
			 *	<dd>['D/M/YY', 'D/M/YYYY', 'D/MON/YY', 'D/MON/YYYY', 'DDMMYY', 'DMYYYY', 'DMONYY', 'DMONYYYY']</dd>
			 *	<dt>Middle endian forms, starting with the month</dt>
			 *	<dd>['D/M/YY', 'D/M/YYYY', 'D/MON/YY', 'D/MON/YYYY', 'DMYY', 'DMYYYY', 'DMONYY', 'DMONYYYY']</dd>
			 *	<dt>Big endian forms, starting with the year</dt>
			 *	<dd>['YYYY/M/D', 'YYYY/MON/D', 'YYYYMMDD', 'YYYYMOND']</dd>
			 *	<dt>ISO 8601 (selected forms)</dt>
			 *	<dd>['YYYY-MM-DD', 'YYYY-MM', 'YYYYMMDD', 'YYYY', 'YYYY-DDD', 'YYYYDDD', 'YYY-W-WD','YYY-W', 'YYYWD', 'YYYW']</dd>
			 * </dl>
			 *
			 * <p>As you can see it is possible (and sometimes necessary) to define a date that is incomplete. For
			 * example the mask 'YYYY-WW' may be used to seek input for the week of a year, but since no day was
			 * required it cannot be used to match an actual day in history. In this case the date property of the
			 * parse() result will not be set. See Parser.parse() for more information.</p>
			 *
			 * <p><strong>Important!</strong><br>
			 * by default no masks are set, no not any. This is to FORCE those parsing dates to at least think about
			 * what masks are relevant to their users: exploding masks is expensive and i18n matters!</p>
			 *
			 * @function
			 * @param {Array} arg The masks we want this parser to use.
			 */
			this.setMasks = function(arg) {
				masks = arg;
			};

			/**
			 * Get the current parser instance's masks.
			 *
			 * @function
			 * @returns {Array} The masks in use.
			 */
			this.getMasks = function() {
				return masks || null;
			};

			/**
			 * Sets the rolling period such that a date with a two digit yearis always rolled to the past. This is used,
			 * for example, to set whether the date is matching a birth date or a 'regular' date. A birth date differs
			 * from a standard date in that 2 digit years expand into 4 digit years slightly differently. If, following
			 * the normal pivot rules, the 4 digit year would put the date in the future, then the century is rolled
			 * back to ensure the date is in the past. E.g. 07 might be converted to 2007, however since a birth date
			 * cannot be in the future 1907 will be returned instead.
			 *
			 * @function
			 * @param {Boolean} arg Set true to always roll back two digit years.
			 */
			this.setExpandYearIntoPast = function(arg) {
				expandYearIntoPast = arg;
			};

			/**
			 * Should two digit years always be rolled to the past?
			 *
			 * @function
			 * @returns {Boolean}
			 */
			this.isExpandYearIntoPast = function () {
				return expandYearIntoPast;
			};
		}

		return Parser;

		/**
		 * @typedef {Object} module:wc/date/Parser#parsedDate
		 * @property {Number} day The day
		 * @property {Number} month The month (NOTE: 1 based!!!!)
		 * @property {Number} year The year
		 * @property {?Date} date The Date object representing the match's date.
		 */
	});
