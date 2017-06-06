/**
 * A debugging mode only helper to implement a nice toString on Function.prototype.bind if it is available. This helps
 * debugging "bound" functions by giving them a better toString than "function () { [native code] }" which makes life
 * very difficult in the debugger! The toString is the toString of the original function (the one which was bound).
 *
 * @ignore
 */
(function() {
	"use strict";
	var nativeBind = Function.prototype.bind;

	if (nativeBind) {
		/**
		 * @function
		 * @alias module"wc/debug/bind
		 * @returns {Function} The applied native Function.prototype.bind with the new toString method added.
		 * @ignore
		 */
		Function.prototype.bind = function() {
			var func = this,
				result = nativeBind.apply(func, arguments);
			result.toString = function() {
				return func.toString();
			};
			return result;
		};
	}
})();
