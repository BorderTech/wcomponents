/**
 * Provides non-implementation specific ajax functionality. Beef it up as you need to.
 *
 * @module
 *
 * @requires module:wc/Observer
 * @requires module:wc/global
 * @requires module:wc/xml/xmlString
 * @requires module:wc/timers
 * @requires module:wc/dom/uid
 *
 * @todo Document private members
 * TODO totally redo this module
 */
define(["wc/Observer", "wc/global", "wc/xml/xmlString", "wc/timers", "wc/dom/uid", "require"],
	function(Observer, global, xmlString, timers, uid, require) {
		"use strict";
		const queue = [],
			/**
			 * AJAX request limit:
			 *  Exists primarily for Internet Explorer bugs, IE could not handle more than about 8 pending ajax requests.
			 *  Firefox (15) can also be swamped (but it takes a lot more, can handle about 80). Now applied to all
			 *  browsers for the sake of consistency.
			 * @constant {int} limit
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
				if (request.uid) {
					markStart = request.uid + "_start";
					markEnd = request.uid + "_end";
					mark = global.performance.getEntriesByName(markStart);
					if (mark && mark.length) {
						global.performance.mark(markEnd);
						global.performance.measure(request.url, markStart, markEnd);
						global.performance.clearMarks(markStart);
						global.performance.clearMarks(markEnd);
					} else {
						console.warn("could not find start mark", markStart);
					}
				} else {
					console.warn("request has not uid", request);
				}
			}

			/**
			 * Get a standard XMLHTTPRequest. This one line is here as it is used in {@link getMsRequest} as well as the
			 * proper {@link generateXBrowserRequest}.
			 * @function
			 * @private
			 * @returns A XMLHTTPRequest.
			 */
			function getW3cRequest() {
				return new global["XMLHttpRequest"]();
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
				if (request && request.readyState === 4) {
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
							if (errorHandler && errorHandler.getErrorMessage) {
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
				const request = ajax.getXBrowserRequestFactory()(),
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
				global.performance.mark(request.uid + "_start");
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
			 * Synchronously loads an XML document.
			 * CACHE IS ON
			 *
			 * Beware, IE8 has some "gotchas" which can easily catch you out, here is the scenario:
			 * 1. foo.xsl is served up from a servlet like so: /theme?f=xslt/all.xsl
			 * 2. The response type is text/xsl
			 * 3. This foo.xsl servlet is first used by the browser when the page loads due to the processing instruction:
			 * <?xml-stylesheet type="text/xsl" href="/theme?f=xslt/all.xsl"?>
			 * 4. The foo.xsl servlet is then called via AJAX.
			 *
			 * Despite the fact that IE8 has already used foo.xsl to transform the whole page, when it loads the same
			 * xsl via AJAX it does not recognise it as XML, responseXML is empty and responseText is populated.
			 *
			 * @function
			 * @alias module:wc/ajax/ajax.loadXmlDoc
			 * @public
			 * @param {String} uri Url to the xml document.
			 * @param {Function} [callback] Optionally provide a callback.
			 * @param {boolean} [asText] If true send the request with responseType.TEXT.
			 * @param {boolean} asPromise Experimental
			 * @returns {Object} an XML DOM loaded from the URI.
			 */
			this.loadXmlDoc = function(uri, callback, asText, asPromise) {
				const responseType = (asText ? this.responseType.TEXT : this.responseType.XML),
					request = {
						url: uri,
						async: !!asPromise,
						cache: true,  // cache should be forever, cache is broken by changing URL (querystring)
						responseType: responseType,
						forceMime: "text/xml"
					};
				if (asPromise) {
					return loadXmlDocAsync(request, callback, asText);
				}
				return loadXmlDocSync(request, callback, asText);
			};

			function loadXmlDocAsync(request, callback, asText) {
				function executor(win) {
					request.callback = callbackWrapper;
					function callbackWrapper(response) {
						let innerResult = response;
						if (!asText) {
							if (!(innerResult && innerResult.documentElement)) {
								/*
								 * For older versions of Internet Explorer which don't support forcing content type
								 * Also for older versions of MSXML which do not know that content types ending in "+xml" are xml,
								 * for example application/rdf+xml. For example using MSXML ActiveX version 3.0.
								 */
								innerResult = xmlString.from(this.responseText);
							}
						}
						if (callback) {
							callback(innerResult);
						}
						if (win) {
							win(innerResult);
						}
					}
				}
				const result = new Promise(executor);
				ajax.simpleRequest(request);
				return result;
			}

			function loadXmlDocSync(request, callback, asText) {
				let result;
				request.callback = callbackWrapper;

				function callbackWrapper(response) {
					result = response;

					if (!asText) {
						if (!(result && result.documentElement)) {
							/*
							 * For older versions of Internet Explorer which don't support forcing content type
							 * Also for older versions of MSXML which do not know that content types ending in "+xml" are xml,
							 * for example application/rdf+xml. For example using MSXML ActiveX version 3.0.
							 */
							result = xmlString.from(this.responseText);
						}
					}
					if (callback) {
						try {
							callback.call(this, response);
						} catch (ex) {
							console.error("Error in callback ", ex);
						}
					}
				}
				ajax.simpleRequest(request);
				return result;
			}

			/**
			 * Factory to return the appropriate function to make an XMLHTTPRequest.
			 * @function
			 * @alias module:wc/ajax/ajax.getXBrowserRequestFactory
			 * @public
			 * @returns {XMLHttpRequest}
			 */
			this.getXBrowserRequestFactory = function() {
				return getW3cRequest;
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
			const cb = function (err) {
				if (callback) {
					callback(err);
				}
			};
			if (!handleError) {
				require(["wc/ajax/handleError"], function(arg) {
					handleError = arg;
					cb(handleError);
				}, errback);
			} else {
				cb(handleError);
			}
		}
		return ajax;

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
	});
