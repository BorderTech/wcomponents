(function(global) {
	/**
	 * This module wraps the functionality of native timers for the following main reasons:
	 *
	 * * Cross browser compatibility - in particular support for varargs to be passed to the callback/handler.
	 * * Unit testing - we can mess with our timers without affecting those used by other libraries or the unit
	 *   testing framework itself.
	 * * Automated testing - we can provide hooks to help tools determine if the page is "ready".
	 *
	 * Note that we **do NOT** accept a string as a callback/handler.
	 *
	 * By native timers we mean {@link http:  // www.whatwg.org/specs/web-apps/current-work/multipage/timers.html#timers}
	 *
	 * @module
	 * @todo document private members.
	 **/
	define(function() {
		"use strict";
	//	,hasNativeVarargSupport = false;
	//	global.setTimeout(function(varargs) {
	//		hasNativeVarargSupport = !!varargs;
	//	}, 0, true);

		/**
		 * @constructor
		 * @alias module:wc/timers~Timers
		 * @private
		 */
		function Timers() {
			/*
			 * PENDING_TIMEOUT_FLAG will be present and "true" if there are pending timeouts
			 * This does not include intervals or timeouts greater than PENDING_TIMEOUT_THRESHOLD.
			 */
			var subscribers = [],
				PENDING_TIMEOUT_THRESHOLD = 500,
				CB_HANDLE_PROP = "wchandle",
				pendingTimeouts = {},  // don't touch this
				ignoreThreshold = 0;  // anything below this will be ignored.

			/**
			 * Keeps track of pending timeouts to facilitate automated testing.
			 * Tools can programatically determine if there are pending timeouts to help determine if the page
			 * is "ready". This will normally include the document ready state, short running timeouts and pending
			 * ajax calls.
			 * @function
			 * @private
			 * @param {number} handle The handle of the timeout that is being set or cleared.
			 * @param {boolean} clear If true the timeout has completed or has been cleared.
			 */
			function updatePending(handle, clear) {
				var pendingCount;
				if (handle || handle === 0) {
					try {
						if (clear) {
							delete pendingTimeouts[handle];
						}
						else {
							pendingTimeouts[handle] = true;
						}
						pendingCount = Object.keys(pendingTimeouts);
						pendingCount = pendingCount.length;
						// console.log("Pending timeouts: ", pendingCount);
						notify(!!pendingCount);
					}
					catch (ignore) {  // don't let errors here break everything else - this is just a testing hook
						console.error(ignore);
					}
				}
			}


			/*
			 * Helper for the set methods.
			 */
			function setTimer(type, args) {
				var result,
					timeout = args[1],
					callback = callbackWrapperFactory(args);
				if (timeout >= ignoreThreshold) {
					result = global[type](callback, timeout);
					callback[CB_HANDLE_PROP] = result;
				}
				else {
					console.info("Ignoring timeout delay!", timeout);
					callback();
				}
				return result;
			}

			/*
			 * Helper for the clear methods.
			 */
			function clearTimer(handle, type) {
				if (!isNaN(handle)) {
					global[type](handle);
				}
			}

			/**
			 * This used to be just a polyfill for browsers that did not support varargs but since we are now providing a
			 * testing hook we need to use it in all browsers.
			 * @function
			 * @private
			 * @param {Arguments} outerArgs The args that invoked the timeout request.
			 */
			function callbackWrapperFactory(outerArgs) {
				var callbackWrapper = function() {
					var i, l, args = [],
						handler = outerArgs[0],
						handle = callbackWrapper[CB_HANDLE_PROP];
					try {
						for (i = 2, l = outerArgs.length; i < l; i++) {
							args[args.length] = outerArgs[i];
						}
						handler.apply(global, args); // notify the callback
						updatePending(handle, true);
					}
					finally { // memory leak paranoia
						args = null;
					}
				};
				return callbackWrapper;
			}

			/*
			 * Helper for the _subscribe method.
			 */
			function notify(pending) {
				var i, next;
				for (i = 0; i < subscribers.length; i++) {
					try {
						next = subscribers[i];
						next(pending);
					}
					catch (ex) {
						console.error(ex);
					}
				}
			}

			/**
			 * This is for internal use and forms part of the automation utilities provided to support automated testing.
			 * @param {Function} subscriber Will be called with boolean, true means there are pending timeouts, false means there are none.
			 */
			this._subscribe = function(subscriber) {
				if (subscriber) {
					subscribers.push(subscriber);
				}
			};

			/*
			 * Don't ever use this!
			 * This is for unit testing purposes. It allows you to turn timeouts off, so that callbacks are invoked
			 * immediately. Rather than an on/off flag we allow a threshold since there are probably some looooong
			 * delays that you may still want to let live while short-circuiting lesser delays.
			 * @param {number} threshold Any timeout less than this threshold will be ignored (i.e. executed immediately).
			 * @ignore
			 */
			this._setIgnoreThreshold = function(threshold) {
				ignoreThreshold = threshold;
			};

			/**
			 * Schedule a callback.
			 *
			 * @function module:wc/timers.setTimeout
			 * @param {Function} handler Your callback.
			 * @param {number} timeout The number of milliseconds before the handler is called.
			 * @param {...*} [args] arguments to be passed to handler when it is called.
			 * @returns {number} A handle by which this schedule can be identified.
			 */
			this.setTimeout = function(/* handler, timeout */) {
				var result = setTimer("setTimeout", arguments);
				if (arguments[1] < PENDING_TIMEOUT_THRESHOLD) {
					updatePending(result);
				}
				return result;
			};

			/**
			 * Schedule a callback to run repeatedly at an interval specific by the value passed to timeout.
			 * @function module:wc/timers.setInterval
			 * @param {Function} handler Your callback.
			 * @param {number} timeout The number of milliseconds before the handler is called.
			 * @param {...*} [args] arguments to be passed to handler when it is called.
			 * @returns {number} A handle by which this schedule can be identified.
			 */
			this.setInterval = function(/* handler, timeout */) {
				return setTimer("setInterval", arguments);
			};

			/**
			 * Cancel a scheduled callback.
			 *
			 * @function module:wc/timers.clearTimeout
			 * @param {number} handle The timeout handle for the scheduled function we want to clear.
			 */
			this.clearTimeout = function(handle) {
				clearTimer(handle, "clearTimeout");
				updatePending(handle, true);
			};

			/**
			 * Cancel a recurring scheduled callback.
			 *
			 * @function module:wc/timers.clearInterval
			 * @param {number} handle The timeout handle for the scheduled function we want to clear.
			 */
			this.clearInterval = function(handle) {
				clearTimer(handle, "clearInterval");
			};
		}
		return /** @alias module:wc/timers */ new Timers();
	});
}(this));