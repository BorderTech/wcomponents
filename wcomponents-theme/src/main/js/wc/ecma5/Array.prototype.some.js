define(["wc/has"], function(has) {
	"use strict";
	if (!has("array-some")) {
		Array.prototype.some = some;
	}
	/*
	 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:some
	 * JavaScript 1.6 (Gecko 1.8b2 and later)
	 */
	function some(fun /* , thisp */) {
		var len = this.length,
			thisp, i;
		if (typeof fun !== "function") {
			throw new TypeError();
		}
		thisp = arguments[1];
		for (i = 0; i < len; i++) {
			if (i in this && fun.call(thisp, this[i], i, this)) {
				return true;
			}
		}
		return false;
	}
	return some;
});
