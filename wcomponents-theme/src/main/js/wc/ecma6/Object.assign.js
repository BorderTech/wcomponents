define(["wc/has"], function (has) {
	"use strict";
	if (!has("object-assign")) {
		/*
		 * Implement http://www.ecma-international.org/ecma-262/6.0/#sec-object.assign
		 * Object.assign
		 *
		 * Polyfill adapted from MDN
		 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/assign
		 */
		Object.assign = function (target) {
			"use strict";
			if (target === undefined || target === null) {
				throw new TypeError("Cannot convert undefined or null to object");
			}

			var output = Object(target);
			for (var index = 1; index < arguments.length; index++) {
				var source = arguments[index];
				if (source !== undefined && source !== null) {
					for (var nextKey in source) {
						if (source.hasOwnProperty(nextKey)) {
							output[nextKey] = source[nextKey];
						}
					}
				}
			}
			return output;
		};
	}
});
