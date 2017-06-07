define(["wc/has"], function(has) {
	"use strict";
	if (!has("array-map")) {
		Array.prototype.map = map;
	}

	/*
	 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:map
	 * native versions: JavaScript 1.6 (Gecko 1.8b2 and later)
	 */
	function map(callback, thisObject) {
		var result = [];

		for (var i = 0, l = this.length; i < l; i++) {
			result[i] = callback.call(thisObject, this[i], i, this);
		}
		return result;
	}
	return map;
});
