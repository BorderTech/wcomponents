/**
 * @module
 */
define(function() {
	"use strict";

	// mootools 1.2
	//  /([-.*+?^{}$()|[\]\/\\])/g
	var RE_RE = /([.*+?^{}$()|[\]\/\\])/g,
		NO_WILDCARD_RE = /([.+?^{}$()|[\]\/\\])/g,
		WILDCARD_RE = /\*/g,
		REPLACER = "\\$1";

	/**
	 * Escapes any characters in this string that have a special meaning in regular expression syntax.
	 * Regular expression characters include: . (period)
	 *
	 * @function module:wc/string/escapeRe
	 * @param {String} string The string in which we wish to escape regex characters.
	 * @param {Boolean} allowWildcard If true asterisk will not be escaped, it will be replaced with ".*" meaning it
	 *    will be converted to a wildcard match.
	 * @returns {String} The escaped string
	 * @example escapeRe("kungfu*");// returns "kungfu\*"
	 * escapeRe("kungfu*", true);// returns "kungfu.*"
	 */
	function escapeRe(string, allowWildcard) {
		var result;
		if (allowWildcard) {
			result = string.replace(NO_WILDCARD_RE, REPLACER).replace(WILDCARD_RE, ".*");
		}
		else {
			result = string.replace(RE_RE, REPLACER);
		}
		return result;
	}
	return escapeRe;
});
