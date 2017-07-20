define(["wc/has"], function(has) {
	"use strict";
	/*
	 * Implemented in: 	JavaScript 1.6 (Gecko 1.8b2 and later).
	 * Compatibility code taken from here:
	 * https://developer.mozilla.org/en/Core_JavaScript_1.5_Reference/Global_Objects/Array/every
	 */

	if (!has("array-every")) {
		Array.prototype.every = every;
	}

	function every(fun /* , thisp */) {
		var len = this.length >>> 0,
			thisp, i;
		if (typeof fun !== "function") {
			throw new TypeError();
		}
		thisp = arguments[1];
		for (i = 0; i < len; i++) {
			if (i in this && !fun.call(thisp, this[i], i, this)) {
				return false;
			}
		}
		return true;
	}
	return every;
});
