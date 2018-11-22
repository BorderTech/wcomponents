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
			var index, source, nextKey, output;
			if (target === undefined || target === null) {
				throw new TypeError("Cannot convert undefined or null to object");
			}

			output = Object(target);
			for (index = 1; index < arguments.length; index++) {
				source = arguments[index];
				if (source !== undefined && source !== null) {
					for (nextKey in source) {
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
