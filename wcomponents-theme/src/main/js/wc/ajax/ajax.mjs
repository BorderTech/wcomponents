/**
 * Provides non-implementation specific ajax functionality. Beef it up as you need to.
 *
 * @module
 *
 * @todo Document private members
 * TODO totally redo this module
 */

import Observer from "wc/Observer.mjs";
import timers from "wc/timers.mjs";
import uid from "wc/dom/uid.mjs";

const queue = [],
	/**
	 * AJAX request limit:
	 *  Exists primarily for Internet Explorer bugs, IE could not handle more than about 8 pending ajax requests.
	 *  Firefox (15) can also be swamped (but it takes a lot more, can handle about 80). Now applied to all
	 *  browsers for the sake of consistency.
	 * @constant {number} limit
	 * @private
	 */
	limit = 20,
	/**
	 * The singleton returned by the module.
	 * @constant
	 * @type {module:wc/ajax/ajax~Ajax}
	 * @alias module:wc/ajax/ajax
	 */
	ajax = new Ajax();

let handleError,
	pending = 0;

/**
 * @constructor
 * @alias module:wc/ajax/ajax~Ajax
 * @private
 */
function Ajax() {
	let observer;

	this.subscribe = function(subscriber) {
		if (!observer) {
			observer = new Observer();
		}
		return observer.subscribe(subscriber);
	};

	/**
	 * Increments or decrements the pending count. Nothing else should write to the pending variable.
	 *
	 * Also provides an automation testing hook so tools can programatically determine if there are pending
	 * ajax requests or not.
	 *
	 * @function
	 * @private
	 * @param {boolean} [decrement] If true decrement the count, otherwise will be incremented.
	 */
	function updatePending(decrement) {
		if (decrement) {
			if (pending) {
				pending--;
			} else {
				console.warn("Cannot decrement ", pending);
			}
		} else {
			pending++;
		}
		if (observer) {
			observer.notify(!!pending);
		}
	}

	/**
	 * Check to see if we need to process any queued requests. This exists for Internet Explorer bugs.
	 * @function
	 * @private
	 */
	function checkQueuedRequests() {
		if (pending < limit && queue.length) {
			console.log("About to process queued IE AJAX. Queue length: ", queue.length);
			ajax.simpleRequest(queue.shift());
		}
	}

	/**
	 * When a request is "complete" we need to ensure that we record the duration of the AJAX request
	 * for the benefit of auditability / testability.
	 * @param {XMLHttpRequest} request The request that has just finished.
	 */
	function endProfile(request) {
		let markStart, markEnd, mark;
		if (request["uid"]) {
			markStart = request["uid"] + "_start";
			markEnd = request["uid"] + "_end";
			mark = globalThis.performance.getEntriesByName(markStart);
			if (mark?.length) {
				globalThis.performance.mark(markEnd);
				globalThis.performance.measure(request["url"], markStart, markEnd);
				globalThis.performance.clearMarks(markStart);
				globalThis.performance.clearMarks(markEnd);
			} else {
				console.warn("could not find start mark", markStart);
			}
		} else {
			console.warn("request has not uid", request);
		}
	}

	/**
	 * Called when the readystate of the request changes.
	 *
	 * @param request The XHR created by ajaxRqst
	 * @param config The config object as passed to ajaxRqst
	 * @function
	 * @private
	 * @returns {boolean} true when the request has been received.
	 */
	function stateChange(request, config) {
		// request can be null in some circumstances, don't remove the null check
		let done = false;
		if (request?.readyState === 4) {
			try {
				done = true;
				if (request.status === 200 && config.callback) {
					try {
						config.callback.call(request, request[config.responseType]);
					} catch (ex) {
						logErrorAndNotify(request, config, ex);
					}
				} else {
					logErrorAndNotify(request, config);
				}
				endProfile(config);
			} finally {
				if (config.async) {
					updatePending(true);
				}
				/*
				 * check queued requests in a timeout so as to decouple the callback from the next request.
				 * If you don't do this then the next request will be made from within the call chain of the current request and
				 *    that is extraordinarily confusing when you are viewing the call stack and trying to work out what is going on.
				 */
				timers.setTimeout(checkQueuedRequests, 0);
			}
		}
		return done;
	}

	/**
	 * Handles errors by notifying errbacks and logging helpful diagnostics.
	 * Note that XMLHTTPRequest provides no way of accessing the request headers.
	 * @param request The XHR created by ajaxRqst
	 * @param config The config object as passed to ajaxRqst
	 * @param [ex] The original exception (if available)
	 */
	function logErrorAndNotify(request, config, ex) {
		try {
			console.group("wc/ajax");
			if (ex) {
				console.error(ex);
			}
			console.error("request.status", request.status);
			console.error("request.readyState", request.readyState);
			console.error("response headers", request.getAllResponseHeaders());
			console.error("config", JSON.stringify(config));

		} catch (ignore) {
			// don't die if logging fails
			console.warn(ignore);
		} finally {
			console.groupEnd();
		}
		if (config.onError) {
			notifyError(request, config.onError);
		}
	}

	/**
	 * Invokes the error callback (possibly asynchronously) with a (hopefully) meaningful, internationalized message.
	 * @param request The XHR created by ajaxRqst
	 * @param {function} onError The error callback
	 */
	function notifyError(request, onError) {
		const fallbackMessage = "ERROR! Unable to communicate with server",
			doNotify = function (errorHandler) {
				let message;
				try {
					if (errorHandler?.getErrorMessage) {
						message = errorHandler.getErrorMessage(request);
					} else {
						message = fallbackMessage;
					}
				} finally {
					onError.call(request, message);
				}
			};
		fetchErrorHandler(doNotify, doNotify);
	}

	/**
	 * Configure the request before the XHR is 'open'.
	 *
	 * @param request The XHR created vy ajaxRqst
	 * @param config The config object as passed to ajaxRqst
	 * @function
	 * @private
	 */
	function applyPreOpenConfig(request, config) {
		config.responseType = config.responseType || ajax.responseType.TEXT;
		try {
			if (config.onAbort) {
				request.onabort = config.onAbort;
			} else {
				request.onabort = function() {
					logErrorAndNotify(request, config, new Error("The request was aborted"));
				};
			}
			if (config.onTimeout) {
				request.ontimeout = config.onTimeout;
			} else {
				request.ontimeout = function() {
					logErrorAndNotify(request, config, new Error("The request timed out"));
				};
			}

			if (config.onProgress && request.upload) {
				request.upload.onprogress = config.onProgress;
			}

			if (config.forceMime && request.overrideMimeType) {
				// this allows feature rich browsers to weather the storm when the server gets it wrong
				request.overrideMimeType(config.forceMime);
			}
		} catch (ex) {
			// comsume errors and try to proceed - this is most likely to happen in legacy IE
			console.warn(ex);
		}
	}

	/**
	 * Configure the request after the XHR is 'open'.
	 *
	 * @param request The XHR created vy ajaxRqst
	 * @param config The config object as passed to ajaxRqst
	 * @function
	 * @private
	 */
	function applyPostOpenConfig(request, config) {
		const allowCaching = config.cache || false;
		if (!allowCaching) {
			request.setRequestHeader("If-Modified-Since", "Fri, 31 Dec 1999 23:59:59 GMT");  // added by Rick Brown
		}
		if (typeof config.postData === "string") {
			// we do not want to be here if postData is an instance of FormData
			request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			// request.setRequestHeader("Connection", "close");  // removed by RB
		}
	}

	/**
	 * Executes AJAX requests.
	 *
	 * @private
	 * @alias module:wc/ajax/ajax~ajaxRqst
	 * @param {module:wc/ajax/ajax~Request} config Holds the details of the request to be sent.
	 * @returns {XMLHttpRequest} The XHR instance.
	 */
	function ajaxRqst(config) {
		let done;
		const request = new window.XMLHttpRequest(),
			onStateChange = function () {
				done = stateChange(request, config);
			};

		if (request) {
			request.onreadystatechange = onStateChange;
			applyPreOpenConfig(request, config);
			request.open(config.postData ? "POST" : "GET", config.url, config.async);
			applyPostOpenConfig(request, config);

			console.log("Sending request: ", config.url);
			request.send(config.postData || "");

			// the test for !async is not strictly necessary but should short-circuit unnecessary calls to statechange
			if (!config.async && !done && request.readyState) {
				// this block is for firefox(3.6) where SYNCHRONOUS requests do not fire readystate changes
				onStateChange();
			}
			return request;
		}
	}

	/**
	 * Make an AJAX request.
	 *
	 * @function
	 * @alias module:wc/ajax/ajax.simpleRequest
	 * @public
	 * @param {module:wc/ajax/ajax~Request} request Holds the details of the request to be sent.
	 * @returns {XMLHttpRequest} The XHR instance.
	 */
	this.simpleRequest = function(request) {
		let result;
		request.async = (request.async === undefined) ? true : request.async;
		request.uid = uid();
		globalThis.performance.mark(request.uid + "_start");
		if (!request.async) {
			result = ajaxRqst(request);
		} else if (pending < limit) {
			updatePending();
			result = ajaxRqst(request);
		} else {
			queue.push(request);
			console.log("Queued AJAX. Queue length: ", queue.length);
		}
		return result;
	};

	/**
	 * @var
	 * @type {Object}
	 * @public
	 * @property {String} XML Response type "responseXML"
	 * @property {String} TEXT Response type "responseText"
	 */
	this.responseType = {
		XML: "responseXML",
		TEXT: "responseText"
	};

	/**
	 * Get the object as a more-or-less meaningful string.
	 * @function
	 * @public
	 * @alias module:wc/ajax/ajax.toString
	 * @returns {String} The string representation of the object
	 */
	this.toString = function () {
		let s = "AJAX Limit: ";
		s += limit;
		s += "\nPending: " + (pending || 0);
		s += "\nQueued: " + (queue ? queue.length : 0);
		return s;
	};
}

/*
 * Prefetch the error handler.
 *
 * - Why not fetch it right up front? Because it is only required under exceptional conditions.
 * - Why not wait until an error occurs to fetch it? Because the error may also prevent modules being loaded.
 * - Why not use HTML5 link preloading to fetch it (loader/prefetch.js)?
 * Technical reasons: this would fetch the module but not its dependencies.
 * Non-technical reasons: "request counters" and "byte counters" do not understand that the preload would
 * utilize browser idle time to asynchronously load resources in a way that does not adversly affect the user.
 */
timers.setTimeout(fetchErrorHandler, 60000);

/**
 * Fetch the error handler module.
 * We delay fetching this module because it is only required under exceptional conditions.
 * @param {function} callback Will be called with the error handler module.
 * @param {function} errback May possibly be invoked if there is an error loading the module.
 */
function fetchErrorHandler(callback, errback) {
	const cb = err => {
		if (callback) {
			callback(err);
		}
	};
	if (!handleError) {
		import("wc/ajax/handleError.mjs").then(arg => {
			handleError = arg;
			cb(handleError);
		}).catch(errback);
	} else {
		cb(handleError);
	}
}
export default ajax;

/**
 * @typedef {Object} module:wc/ajax/ajax~Request
 * @property {String} url The URL to request
 * @property {Function} [callback] The callback function on success (callback scope will be the XMLHTTPRequest)
 * @property {Function} [onProgress] The callback function on progress events.
 * @property {Function} [onError] The callback function on error (callback scope will be the XMLHTTPRequest)
 * @property {Boolean} [cache] Should the result be cached?
 * @property {String} [postData] The encoded data to post.
 * @property {String} [responseType] One of {@link module:wc/ajax/ajax#responseType} XML or {@link module:wc/ajax/ajax#responseType} TEXT.
 * @property {Boolean} [async] Set false to make a synchronous request.
 * @property {Boolean} [forceMime] If true ignore content type header, use this instead (only on supported
 *    browsers, don't rely on this, get it right on the server).
 */

