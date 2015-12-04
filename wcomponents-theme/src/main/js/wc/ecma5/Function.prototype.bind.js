(function(global) {
	define(["wc/has"], function(has) {
		"use strict";
		/*
		 * Taken from here BUT modified to fix what seems to be a bug in their code:
		 * https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Function/bind
		 *
		 * Also, added "toString" to help debugging.
		 *
		 * (Note I have flagged the bug on the discussion page on MDC)
		 */
		if (!has("function-bind")) {
			global.Function.prototype.bind = bind;
		}

		function bind(obj) {
			var slice = [].slice,
				args = slice.call(arguments, 1),
				self = this,
				Nop = function() {
					this.toString = function() {
						return self.toString();
					};
				},
				bound = function () {
					return self.apply( (Nop.prototype && this instanceof Nop) ? this : ( obj || (global || {} )),
							args.concat(slice.call(arguments)));
				};
			Nop.prototype = self.prototype;
			bound.prototype = new Nop();
			return bound;
		}
		return bind;
	});
})(this);
