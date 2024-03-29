/**
 * This module wraps the functionality of native timers for the following main reasons:
 *
 * * Automated testing (selenium) - helps tools determine if the page is "ready" THIS IS REALLY IMPORTANT!
 * * Cross browser compatibility - in particular support for varargs to be passed to the callback/handler.
 * * Unit testing - we can mess with our timers without affecting those used by other libraries or the unit
 *   testing framework itself.
 *
 * Note that we **do NOT** accept a string as a callback/handler.
 *
 * By native timers we mean http:// www.whatwg.org/specs/web-apps/current-work/multipage/timers.html#timers
 *
 **/

//	,hasNativeVarargSupport = false;
//	global.setTimeout(function(varargs) {
//		hasNativeVarargSupport = !!varargs;
//	}, 0, true);


/*
 * PENDING_TIMEOUT_FLAG will be present and "true" if there are pending timeouts
 * This does not include intervals or timeouts greater than PENDING_TIMEOUT_THRESHOLD.
 */
const subscribers = [],
	PENDING_TIMEOUT_THRESHOLD = 500,
	CB_HANDLE_PROP = "wchandle",
	pendingTimeouts = {};  // don't touch this
let ignoreThreshold = 0;  // anything below this will be ignored.


const instance = {
	/**
	 * This is for internal use and forms part of the automation utilities provided to support automated testing.
	 * @param {Function} subscriber Will be called with boolean, true means there are pending timeouts, false means there are none.
	 */
	_subscribe: function(subscriber) {
		if (subscriber) {
			subscribers.push(subscriber);
		}
	},

	/*
	 * Don't ever use this!
	 * This is for unit testing purposes. It allows you to turn timeouts off, so that callbacks are invoked
	 * immediately. Rather than an on/off flag we allow a threshold since there are probably some looooong
	 * delays that you may still want to let live while short-circuiting lesser delays.
	 * @param {number} threshold Any timeout less than this threshold will be ignored (i.e. executed immediately).
	 * @ignore
	 */
	_setIgnoreThreshold: function(threshold) {
		ignoreThreshold = threshold;
	},

	/**
	 * Schedule a callback.
	 *
	 * @param {Function} handler Your callback.
	 * @param {number} timeout The number of milliseconds before the handler is called.
	 * @param {...*} [args] arguments to be passed to handler when it is called.
	 * @returns {number} A handle by which this schedule can be identified.
	 */
	setTimeout: function(/* handler, timeout */) {
		const result = setTimer("setTimeout", arguments);
		if (arguments[1] < PENDING_TIMEOUT_THRESHOLD) {
			updatePending(result);
		}
		return result;
	},

	/**
	 * Schedule a callback to run repeatedly at an interval specific by the value passed to timeout.
	 * @function module:wc/timers.setInterval
	 * @param {Function} handler Your callback.
	 * @param {number} timeout The number of milliseconds before the handler is called.
	 * @param {...*} [args] arguments to be passed to handler when it is called.
	 * @returns {number} A handle by which this schedule can be identified.
	 */
	setInterval: function(/* handler, timeout */) {
		return setTimer("setInterval", arguments);
	},

	/**
	 * Cancel a scheduled callback.
	 *
	 * @param {number} handle The timeout handle for the scheduled function we want to clear.
	 */
	clearTimeout: function(handle) {
		clearTimer(handle, "clearTimeout");
		updatePending(handle, true);
	},

	/**
	 * Cancel a recurring scheduled callback.
	 *
	 * @param {number} handle The timeout handle for the scheduled function we want to clear.
	 */
	clearInterval: function(handle) {
		clearTimer(handle, "clearInterval");
	}
};

/**
 * Keeps track of pending timeouts to facilitate automated testing.
 * Tools can programatically determine if there are pending timeouts to help determine if the page
 * is "ready". This will normally include the document ready state, short running timeouts and pending
 * ajax calls.
 * @function
 * @private
 * @param {number} handle The handle of the timeout that is being set or cleared.
 * @param {boolean} [clear] If true the timeout has completed or has been cleared.
 */
function updatePending(handle, clear) {
	if (handle || handle === 0) {
		try {
			if (clear) {
				delete pendingTimeouts[handle];
			} else {
				pendingTimeouts[handle] = true;
			}
			const pendingCount = Object.keys(pendingTimeouts).length;
			notify(!!pendingCount);
		} catch (ignore) {  // don't let errors here break everything else - this is just a testing hook
			console.error(ignore);
		}
	}
}

/*
 * Helper for the set methods.
 */
function setTimer(type, args) {
	let result;
	const timeout = args[1],
		callback = callbackWrapperFactory(args);
	if (timeout >= ignoreThreshold) {
		result = globalThis[type](callback, timeout);
		callback[CB_HANDLE_PROP] = result;
	} else {
		console.info("Ignoring timeout delay!", timeout);
		callback();
	}
	return result;
}

/*
 * Helper for the clear methods.
 */
function clearTimer(handle, type) {
	globalThis[type](handle);
}

/**
 * This used to be just a polyfill for browsers that did not support varargs but since we are now providing a
 * testing hook we need to use it in all browsers.
 * @function
 * @private
 * @param {IArguments} outerArgs The args that invoked the timeout request.
 */
function callbackWrapperFactory(outerArgs) {
	const callbackWrapper = function() {
		const args = [],
			handler = outerArgs[0],
			handle = callbackWrapper[CB_HANDLE_PROP];
		for (let i = 2, l = outerArgs.length; i < l; i++) {
			args[args.length] = outerArgs[i];
		}
		handler.apply(globalThis, args);  // notify the callback
		updatePending(handle, true);
	};
	return callbackWrapper;
}

/*
 * Helper for the _subscribe method.
 */
function notify(pending) {
	for (const next of subscribers) {
		try {
			next(pending);
		} catch (ex) {
			console.error(ex);
		}
	}
}

export default instance;
