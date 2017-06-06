define(["wc/has"], function(has) {
	"use strict";
	if (!has("array-foreach")) {
		Array.prototype.forEach = forEach;
	}

	/*
	 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:forEach
	 * native versions: JavaScript 1.6 (Gecko 1.8b2 and later)
	 */
	function forEach(callback, thisObject) {
		var i, l;
		for (i = 0, l = this.length; i < l; i++) {
			callback.call(thisObject, this[i], i, this);
		}
	}
	return forEach;
});
