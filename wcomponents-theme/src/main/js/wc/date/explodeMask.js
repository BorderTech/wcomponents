/**
 * @module
 * @requires module:wc/string/escapeRe
 * @requires module:wc/date/pattern
 */
define(["wc/string/escapeRe", "wc/date/pattern"],
	/** @param escapeRe wc/string/escapeRe @param patterns wc/date/pattern @ignore */
	function(escapeRe, patterns) {
		"use strict";
		/**
		 * Provides the ability to convert string date masks into a set of regular expressions which can be used to
		 * format date-like user input into date-like potential matches. In general you should not need to call this
		 * module directly but user a {@link module:wc/date/Parser} instance when you need to parse dates.
		 *
		 * The user sets a 'mask' in a string form, e.g. 'DD MON YYYY'. In order to use this efficiently we first
		 * convert it into a regular expression. Each unique mask will get its own regular expression. The regular
		 * expression pieces come from the rePieces variable. Each RegExp piece defines a sub match. The parse()
		 * function does the match on the string and from it extracts the day, month and year as the sub matches. Cruft
		 * we are not interested in keeping (such as the separator) is not stored as a submatch. To to know which
		 * submatch represents which part of the date we build an array of constants along with the regular expression.
		 *
		 * So to break it down: a mask will look like this 'DD MON YYYY'. We will store the mask as an index pointing to
		 * an object representing the masks 'compiled' form:
		 * <pre><code> 'DD MON YYYY' = { re: /blahblah/g, matches:[PIECE.DAY, PIECE.MONTH, PIECE.YEAR] }
		 * 'YYYY/MM/DD'  = { re: /blahblah/g, matches:[PIECE.YEAR, PIECE.MONTH, PIECE.DAY] }</code></pre>
		 *
		 * Using this info we can separate out the logic of building and defining the regular expression from the actual
		 * parser, making it more usable and definitely more customisable.
		 *
		 * **NOTE WELL**<br />
		 * This function modifies the {@link module:wc/date/pattern} object by adding a compiled form of the key object
		 * as the property patternAsRe. For example: pattern["yyyy"].patternAsRe is the regular expression /yyyy/.
		 *
		 * @function
		 * @alias module:wc/date/explodeMask
		 * @param {String} mask The date format mask to explode.
		 * @param {Boolean} [strictSequence] If false non matching characters are allowed in between patterns. General
		 *    rule of thumb is: true for parsing, false for formatting.
		 * @returns {module:wc/module:wc/date/pattern[]} An array of the 'best' patterns for the mask.
		 */
		function explodeMask (mask, strictSequence) {
			var result = [],
				best,
				next,
				pattern,
				patternAsRe;

			// loop matching all regular expressions and keeping the earliest longest match
			while (true) {
				next = null;
				best = null;

				// for..in Date.patterns
				for (pattern in patterns) {
					// the pattern key is a simple string. however we need a regular expression.
					// So let's 'compile' it into a regular expression
					// For future speed we shall store the compiled form on the pattern as the 'patternAsRe' property
					patternAsRe = patterns[pattern].patternAsRe;
					if (!patternAsRe) { // doesnt exist, lets make it
						patternAsRe = new RegExp(escapeRe(pattern));
						patterns[pattern].patternAsRe = patternAsRe;
					}

					patternAsRe.lastIndex = 0; // safty reset so that each match commences from the beginning of the string. should be reset anyway but a rogue 'g' flag on the regexp could stuff it up
					next = patternAsRe.exec(mask);
					// keep the first, earliest longest match
					if (next) {
						// accept: no values to compete with || accept: earlier || accept: matches a longer part of the mask
						if (!best || next.index < best.index || next.index === best.index && next[0].length > best[0].length) {
							best = next;
							best.pattern = pattern;
						}
					}
				}

				// strict sequence check
				if (strictSequence && best && best.index !== 0) {
					result = [];
					break;
				}
				// no more matches
				if (best === null) {
					break;
				}

				// push our match onto the list of found items
				result.push(best.pattern);

				// continue from the end of the last match
				mask = mask.substr(best.index + best[0].length);
			}
			// all done
			return result;
		}
		return explodeMask;
	});
