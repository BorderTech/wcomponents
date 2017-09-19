define(["wc/has"], function(has) {
	"use strict";
	if (!has("array-reduceright")) {
		Array.prototype.reduceRight = reduceRight;
	}

	/*
	 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Objects:Array:reduceRight
	 * native versions: JavaScript 1.8 (Gecko 1.9a5 and later)
	 */
	function reduceRight(fun /* , initial */) {
		var rv, len = this.length;
		if (typeof fun !== "function") {
			throw new TypeError();
		}
		// no value to return if no initial value, empty array
		if (len === 0 && arguments.length === 1) {
			throw new TypeError();
		}
		var i = len - 1;
		if (arguments.length >= 2) {
			rv = arguments[1];
		} else {
			// Keep processing until explicit break
			// eslint-disable-next-line no-constant-condition
			do {
				if (i in this) {
					rv = this[i--];
					break;
				}

				// if array contains no values, no initial value to return
				if (--i < 0) {
					throw new TypeError();
				}
			}
			while (true);
		}

		for (; i >= 0; i--) {
			if (i in this) {
				rv = fun.call(null, rv, this[i], i, this);
			}
		}

		return rv;
	}
	return reduceRight;

});
