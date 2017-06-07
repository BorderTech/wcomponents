define(["wc/has"], function(has) {
	"use strict";
	if (!has("array-lastindexof")) {
		Array.prototype.lastIndexOf = lastIndexOf;
	}
	/*
	 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:lastIndexOf
	 * native versions: JavaScript 1.6 (Gecko 1.8b2 and later)
	 */
	function lastIndexOf(item, from) {
		var length = this.length;

		if (from === undefined || from >= length) {
			from = length - 1;
		} else if (from < 0) {
			from = from + length;
		}
		for (; from > -1; from--) {
			if (this[from] === item) {
				return from;
			}
		}
		return -1;
	}
	return lastIndexOf;
});
