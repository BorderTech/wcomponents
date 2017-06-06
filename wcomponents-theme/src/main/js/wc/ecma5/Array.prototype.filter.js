define(["wc/has"], function(has) {
	"use strict";
/*
 * Implemented in: 	JavaScript 1.6 (Gecko 1.8b2 and later).
 * Compatibility code taken from here:
 * https://developer.mozilla.org/en/Core_JavaScript_1.5_Reference/Global_Objects/Array/filter
 */

	if (!has("array-filter")) {
		Array.prototype.filter = filter;
	}

	function filter(fun /* , thisp */) {
		var len = this.length >>> 0, res, thisp, i, val;
		if (typeof fun !== "function") {
			throw new TypeError();
		}
		res = [];
		thisp = arguments[1];
		for (i = 0; i < len; i++) {
			if (i in this) {
				val = this[i]; // in case fun mutates this
				if (fun.call(thisp, val, i, this)) {
					res.push(val);
				}
			}
		}
		return res;
	}
});
