define(["wc/has"], function(has) {
	"use strict";
	/*
	 * Implement ECMA-262 (5th Edition)
	 * Date.now
	 * Compatibility code taken from MDC
	 * https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Date/now
	 */
	if (!has("date-now")) {
		Date.now = now;
	}
	function now() {
		return +new Date();
	}
	return now;
});
