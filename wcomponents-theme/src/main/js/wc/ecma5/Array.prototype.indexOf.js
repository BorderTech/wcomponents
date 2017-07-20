define(["wc/has"], function(has) {
	"use strict";
	if (!has("array-indexof")) {
		Array.prototype.indexOf = indexOf;
	}

	/*
	 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:indexOf
	 * native versions: JavaScript 1.6 (Gecko 1.8b2 and later)
	 */
	function indexOf(item, from) {
		var length = this.length;
		if (from === undefined) {
			from = 0;
		} else if (from < 0) {
			from = from + length;
		}
		for (; from < length; from++) {
			if (this[from] === item) {
				return from;
			}
		}
		return -1;
	}
	return indexOf;
});
