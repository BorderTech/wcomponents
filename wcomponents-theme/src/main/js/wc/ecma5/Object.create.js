define(["wc/has"], function(has) {
	"use strict";
	if (!has("object-create")) {
		/*
		 * Implement http://www.ecma-international.org/ecma-262/5.1/#sec-15.2.3.5
		 * Object.create
		 *
		 * Polyfill adapted from MDN
		 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create
		 */
		Object.create = (function () {
			var Temp = function () {};
			return function (prototype) {
				if (arguments.length > 1) {
					throw Error("Second argument not supported");
				}
				if (prototype !== Object(prototype) && prototype !== null) {
					throw TypeError("Argument must be an object or null");
				}
				if (prototype === null) {
					throw Error("null [[Prototype]] not supported");
				}
				Temp.prototype = prototype;
				var result = new Temp();
				Temp.prototype = null;
				return result;
			};
		})();
	}
});
