define(["wc/has"], function(has) {
	"use strict";
	var re = /^\s+|\s+$/g;

	if (!has("string-trim")) {
		/*
		 * Standardized in ECMA-262 5th Edition.
		 * Removes leading and trailing spaces from the string.
		 */
		String.prototype.trim = function() {
			return trim(this);
		};
	}

	function trim(s) {
		return s.replace(re, "");
	}
});
