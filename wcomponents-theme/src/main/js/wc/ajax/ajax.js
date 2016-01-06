(function(global) {
	"use strict";
	/**
	 * Provides non-implementation specific ajax functionality. Beef it up as you need to.
	 *
	 * @module
	 *
	 * @requires module:wc/xml/xmlString
	 * @requires module:wc/timers
	 * @requires module:wc/has
	 *
	 * @todo Document private members
	 */
	define(["wc/xml/xmlString", "wc/timers", "wc/has", "wc/dom/uid", "require"],
	/** @param xmlString wc/xml/xmlString @param timers wc/timers @param has wc/has @param uid wc/dom/uid @param require require @ignore */
	function(xmlString, timers, has, uid, require) {

		/**
		 * @constructor
		 * @alias module:wc/ajax/ajax~Ajax
		 * @private
		 */
		function Ajax() {
			var xBrowserRequest;
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
				var element = document.body;
				if (decrement) {
					if (pending) {
						pending--;
					}
					else {
						console.warn("Cannot decrement ", pending);
					}
				}
				else {
					pending++;
				}
				if (element) {
					element.setAttribute(PENDING_AJAX_FLAG, !!pending);
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
			 * @param {XMLHTTPRequest} request The request that has just finished.
			 */
			function endProfile(request) {
				var markStart, markEnd, mark;
				if (request.uid) {
					markStart = request.uid + "_start";
					markEnd = request.uid + "_end";
					mark = global.performance.getEntriesByName(markStart);
					if (mark && mark.length) {
						global.performance.mark(markEnd);
						global.performance.measure(request.url, markStart, markEnd);
						global.performance.clearMarks(markStart);
						global.performance.clearMarks(markEnd);
					}
					else {
						console.warn("could not find start mark", markStart);
					}
				}
				else {
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
				return new global[W3C_IFACE]();
			}

			/**
			 * Since 2015 we prefer XMLHttpRequest despite the drawbacks discussed below because we need to use advanced modern features
			 * like FormData and progress events (i.e. file upload over AJAX).
			 *
			 * <p>Note that in IE we will reluctantly prefer Msxml2.XMLHTTP.6.0 over the W3C standard XMLHttpRequest
			 * This is because Msxml2.XMLHTTP.6.0 does MUCH faster XSLT. The difference is only noticeable with
			 * complex XSLT and large XML docs, but we are certainly noticing it in our web apps.</p>
			 *
			 * <p>We really only need this when loading XSL docs, however you can not mix docs loaded from different
			 * engines in IE, so the XML and XSL must all be loaded from the same engine.</p>
			 *
			 * <p>IE XSLT speed tests on IE8/XPsp3 (using a real page):<br />
			 * Msxml2.XMLHTTP.6.0:	1016<br />
			 * Msxml2.XMLHTTP.3.0:	4359<br />
			 * XMLHttpRequest:		4360<br />
			 * Microsoft.XMLHTTP:	4391</p>
			 *
			 * <p>On IE9(beta)/VistaSp2<br />
			 * Msxml2.XMLHTTP.6.0:	1182<br />
			 * Msxml2.XMLHTTP.3.0:	4606<br />
			 * XMLHttpRequest:		4617<br />
			 * Microsoft.XMLHTTP:	4626</p>
			 *
			 * <p>So for XSLT in IE we prefer, in this order:</p>
			 * <ol><li>Msxml2.XMLHTTP.6.0
			 * <li>XMLHttpRequest (cos it's the standard)
			 * <li>Microsoft.XMLHTTP (cos it's the standard IE fallback)</ol>
			 *
			 * <p>Note: Msxml2.XMLHTTP.6.0 seems to have some limitations on the number of rapid fire AJAX
			 * requests/responses it can handle. Our real world example bombed out with 20 requests,
			 * however we could replicate the issue with a smaller number by increasing the size of the
			 * response. Note that the result is a totally non-functioning IE, requiring user to restart IE.
			 * The scenario would occur when eager-loading data on page load.
			 * Microsoft.XMLHTTP also has this bug, I'd say all the engines in IE do except for XMLHttpRequest.</p>
			 *
			 * <p>The workaround we have implemented is to limit the number of pending AJAX requests in IE.
			 * Subsequent requests are queued.</p>
			 *
			 * <p>Note that I have excluded synchronous AJAX from the queueing so if the limit is set to N
			 * pending AJAX requests there can really be N+1 if a synchronous request comes along.</p>
			 *
			 * @function
			 * @private
			 * @returns {XMLHTTPRequest} A Microsoft proprietary XML HTTPRequest.
			 */
			function getMsRequest() {
				var result, supported;
				if (ieXmlHttpEngine === undefined) {
					if (has("ie") && (supported = getActiveX("Msxml2.XMLHTTP", ["6.0", "3.0"]))) {
						// This is intended for IE9 and earlier - ActiveX is better in ancient IE
						ieXmlHttpEngine = supported.engine;
					}
					else if (global[W3C_IFACE]) {
						// All browsers including IE10 and above
						ieXmlHttpEngine = W3C_IFACE;
					}
					else {
						ieXmlHttpEngine = null;
					}
					console.log("Using XMLHTTP engine: " + ieXmlHttpEngine);
				}

				if (ieXmlHttpEngine === W3C_IFACE) {
					result = getW3cRequest();
				}
				else if (ieXmlHttpEngine) {
					result = new global.ActiveXObject(ieXmlHttpEngine);
				}
				else {
					throw new Error("No AJAX support");
				}
				return result;
			}

			/**
			 * Generates a XMLHTTPRequest.
			 * @function
			 * @private
			 * @returns {XMLHTTPRequest} An XMLHTTPRequest relevant to the particular browser
			 */
			function generateXBrowserRequest() {
				var result;
				if (has("activex")) {  // do this test first, see comments on getMsRequest
					result = getMsRequest;
				}
				else if (global[W3C_IFACE]) {
					result = getW3cRequest;
				}
				else {
					console.error("User agent does not provide necessary XML support");
				}
				return result;
			}

			/**
			 * Called when the readystate of the request changes.
			 *
			 * @param request The XHR created vy ajaxRqst
			 * @param config The config object as passed to ajaxRqst
			 * @function
			 * @private
			 * @return {boolean} true when the request has been received.
			 */
			function stateChange(request, config) {
				// request can be null in some circumstances, don't remove the null check
				var done = false;
				if (request && request.readyState === 4) {
					try {
						done = true;
						if (request.status === 200 && config.callback) {
							config.callback.call(request, request[config.responseType]);
						}
						else if (config.onError) {
							config.onError.call(request, request.responseText || request.statusText);
						}
						if (markProfiles) {
							endProfile(config);
						}
					}
					finally {
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
			 * Configure the request before the XHR is 'open'.
			 *
			 * @param request The XHR created vy ajaxRqst
			 * @param config The config object as passed to ajaxRqst
			 * @function
			 * @private
			 */
			function applyPreOpenConfig(request, config) {
				var onAbort = (config.onAbort || config.onError),
					onTimeout = (config.onTimeout || config.onError);
				config.responseType = config.responseType || ajax.responseType.TEXT;
				try {
					if (onAbort) {
						request.onabort = onAbort;
					}

					if (onTimeout) {
						request.ontimeout = onTimeout;
					}

					if (config.onProgress && request.upload) {
						request.upload.onprogress = config.onProgress;
					}

					if (config.forceMime && request.overrideMimeType) {
						// this allows feature rich browsers to weather the storm when the server gets it wrong
						request.overrideMimeType(config.forceMime);
					}
				}
				catch (ex) {
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
				var trident = has("trident"),
					allowCaching = config.cache || false;
				if (trident && trident >= 6 && config.responseType === ajax.responseType.XML) {
					// IE10 and greater need this to prevent the XML dom that is not an XML dom
					try {
						request.responseType = "msxml-document";
					}
					catch (ignore) {
						// Do nothing
					}
				}
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
			 * @returns {XMLHTTPRequest} The XHR instance.
			 */
			function ajaxRqst(config) {
				var done,
					request = ajax.getXBrowserRequestFactory()(),
					onStateChange = function() {
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
			 * @returns {XMLHTTPRequest} The XHR instance.
			*/
			this.simpleRequest = function(request) {
				var result;
				request.async = (request.async === undefined) ? true : request.async;
				request.uid = uid();
				if (markProfiles) {
					global.performance.mark(request.uid + "_start");
				}
				if (!request.async) {
					result = ajaxRqst(request);
				}
				else if (pending < limit) {
					updatePending();
					result = ajaxRqst(request);
				}
				else {
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
				var responseType = (asText ? this.responseType.TEXT : this.responseType.XML),
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
				var result;
				function executor(win) {
					request.callback = callbackWrapper;
					function callbackWrapper(response) {
						var result = response;
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
							callback(result);
						}
						if (win) {
							win(result);
						}
					}
				}
				result = new Promise(executor);
				ajax.simpleRequest(request);
				return result;
			}

			function loadXmlDocSync(request, callback, asText) {
				var result;
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
						}
						catch (ex) {
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
			 * @returns {XMLHTTPRequest}
			 */
			this.getXBrowserRequestFactory = function() {
				return xBrowserRequest || (xBrowserRequest = generateXBrowserRequest());
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
				var s = "AJAX Limit: ";
				s += limit;
				s += "\nPending: " + (pending || 0);
				s += "\nQueued: " + (queue ? queue.length : 0);
				return s;
			};
		}
		var getActiveX,
			/**
			 * This constant names an attribute which only exists to make life easier for performance testers.
			 * @constant {String}
			 * @private
			 * @ignore
			 */
			PENDING_AJAX_FLAG = "data-wc-ajaxp",
			W3C_IFACE = "XMLHttpRequest",
			ieXmlHttpEngine,
			/**
			 * AJAX request limit:
			 *  Exists primarily for Internet Explorer bugs, IE could not handle more than about 8 pending ajax requests.
			 *  Firefox (15) can also be swamped (but it takes a lot more, can handle about 80). Now applied to all
			 *  browsers for the sake of consistency.
			 * @var {int} limit
			 * @private
			 */
			limit = (has("ie") ? 5 : (has("ff") ? 8 : 20)),
			markProfiles = has("global-performance-marking"),
			pending = 0,
			queue = [],
			/**
			 * The singleton returned by the module.
			 * @var
			 * @type {module:wc/ajax/ajax~Ajax}
			 * @alias module:wc/ajax/ajax */
			ajax = new Ajax();

		if (has("activex")) {
			getActiveX = require("wc/fix/getActiveX_ieAll");  // this can only work if "wc/fix/getActiveX_ieAll" is already loaded - the compat script must ensure that.
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
})(this);
