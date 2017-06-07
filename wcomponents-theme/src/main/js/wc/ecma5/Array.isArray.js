define(["wc/has"], function(has) {
	"use strict";
	if (!has("array-isarray")) {
		Array.isArray = isArray;
	}

	/*
	 * @see http://www.ecma-international.org/publications/standards/Ecma-262.htm
	 * Compatibility code based heavily on:
	 * https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Array/isArray
	 */
	function isArray(o) {
		return Object.prototype.toString.call(o) === "[object Array]";
	}
	return isArray;
});
