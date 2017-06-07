define(["wc/has"], function(has) {
	"use strict";
	if (!has("object-keys")) {
		Object.keys = keys;
	}
	/*
	 * Implement ECMA-262 (5th Edition) 15.2.3.14
	 * Object.keys
	 *
	 * Compatibility code adapted from MDC
	 * https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Object/keys
	 */
	function keys(o) {
		var result = [],
			name;
		for (name in o) {
			if (o.hasOwnProperty(name)) {
				result[result.length] = name;
			}
		}
		return result;
	}
	return keys;
});
