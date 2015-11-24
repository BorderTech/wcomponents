(function(global) {
	/**
	 * A meta-module which provides compatibility layer for window.console where there is no console or the console is
	 * incomplete or buggy. This module **does not** return anything.
	 *
	 * Stops calls to console methods from throwing exceptions in browsers that don't have a console (any IE unless the
	 * developer tools have been opened).
	 *
	 * We do not add every possible console method since there is no official standard, we just add the ones we use in
	 * our code.
	 *
	 * Note: IE's console does not have debug, time or timeEnd methods. FF4's console is the same!
	 *
	 * This is really needed for:
	 * * All Internet Explorers (IE10 is latest IE at time of writing) (if nobody has opened the developer tools)
	 *   IE10 has log, info, warn and error but not debug.
	 * * Firefox 3.6 and lower
	*
	 * @module
	 * @private
	 * @requires module:wc/has
	 */
	define(["wc/has"], /** @param has wc/has @ignore */ function(has) {
		"use strict";
			/**
			 * @var {String[]} methods The function names of console methods we have to add.
			 * @private
			 */
		var methods = ["log", "debug", "info", "warn", "error"],
			$console,
			timers,
			UNDEFINED = "undefined",
			c = "console";

		/**
		 * Polyfill for any missing window.console.time function.
		 * @function time
		 * @private
		 * @param {String} id The identifier for the timer.
		 */
		function time(id) {
			timers = timers || {};
			timers[id] = new Date();
		}

		/**
		 * Simple no-op function to use when we can't do anything else!
		 * @function
		 * @private
		 * @ignore
		 */
		function noop() {}

		/**
		 * Polyfill for any missing window.console.time function. IE8 console doesn't have 'debug' or 'time' and 'timeEnd'.
		 * @function timeEnd
		 * @private
		 * @param {String} id The identifier for the timer.
		 */
		function timeEnd(id) {
			var end = new Date(),
				start;
			if (timers) {
				start = timers[id];
				if (start) {
					timers[id] = null;
					global[c].info("%s: %dms", id, end - start);
				}
			}
		}

		/*
		 * Returns a wrapped Internet Explorer 8 and 9 fake console function.
		 * @function logFuncWrapperFactory
		 * @private
		 * @param {String} lvl The console output level.
		 * @returns {Function} The wrapped function.
		 */
		function logFuncWrapperFactory(lvl) {
			var func = global[c][lvl];
			return function() {
				Function.prototype.apply.call(func, global[c], arguments);  // wow
			};
		}

		/**
		 * Fix IEs console IE8 and IE9 by implementing the used functions (as defined in
		 * {@link module:wc/compat/console~methods}) as functions. The problem we are solving here is that the console
		 * functions in Internet Explorer do not:
		 * * have "Function" as a constructor
		 * * have regular function methods (eg call/apply)
		 * * inherit new function methods from the prototype chain (eg Function.prototype.bind)
		 *
		 * IE10 doesn't need this. I am crying with joy as I write this. Look at this console output in IE10:
		 * <pre> >> console.log.constructor
		 *  function Function() { [native code] }</pre>
		 *
		 * @function wrapPretendFunctions
		 * @private
		 */
		function wrapPretendFunctions() {
			var i, next;
			for (i = 0; i < methods.length; i++) {
				next = methods[i];
				if (typeof global[c][next] !== UNDEFINED && typeof global[c][next].constructor === UNDEFINED) {
					global[c][next] = logFuncWrapperFactory(next);  // ahh, that's better
				}
			}
		}

		/**
		 * @constructor
		 * @alias module:wc/compat/console~FakeConsole
		 * @private
		 */
		function FakeConsole() {
			for (var i = 0; i < methods.length; i++) {
				this[methods[i]] = noop;
			}
		}

		/**
		 * Provides the time function to the fake console.
		 * @function
		 * @see {@link module:wc/compat/console~time}
		 */
		FakeConsole.prototype.time = time;

		/**
		 * Provides the timeEnd function to the fake console.
		 * @function
		 * @see {@link module:wc/compat/console~timeEnd}
		 */
		FakeConsole.prototype.timeEnd = timeEnd;

		if (!has("native-console")) {
			/*
			 * Setting the console this way in IE8/IE9 prevents the IE native console from blatting
			 * our noop console if the IE console is opened after page load.
			 */
			if (typeof Object.defineProperty !== UNDEFINED) {
				Object.defineProperty(global, c, {
					get: function() {
						return $console || ($console = new FakeConsole());
					}
				});
			}
			else {
				global[c] = new FakeConsole();
			}
		}
		else {  // IE console, FF4 console, FF25 console
			wrapPretendFunctions();  // if we are in IE8 or IE9 we need to wrap the existing fake functions before we do anything
			if (!has("native-console-debug")) {
				global[c].debug = global[c].log;
			}
			if (!has("native-console-time")) {
				global[c].time = time;
				global[c].timeEnd = timeEnd;
			}
		}
	});
})(this);
