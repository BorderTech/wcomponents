define(["wc/ajax/ajax",
		"wc/urlParser",
		"wc/dom/tag",
		"wc/Observer",
		"wc/dom/getAncestorOrSelf",
		"wc/dom/toDocFragment",
		"wc/timers"],
	function(ajax, urlParser, tag, Observer, getAncestorOrSelf, toDocFragment, timers) {
		"use strict";

		/**
		 * @constructor
		 * @alias module:wc/ui/listLoader~ListLoader
		 * @private
		 */
		function ListLoader() {
			var LIST_ID_PARAM = "wc_data",
				observer,
				pending = {},
				prefetched = {};

			/**
			 * Queues an ajax request to fetch a given datalist.
			 *
			 * This method guarantees that rapid fire requests will not translate to rapid fire
			 * HTTP requests. Instead, rapid fire requests to the same URL will be blocked while
			 * a request is pending. Once the response is received all queued callbacks will then
			 * be notified (for performance reasons we don't bother to make actual requests for the
			 * blocked requests, we just call them as if the request had been made).
			 *
			 * @function
			 * @private
			 * @param {Object} config
			 * @param {String} config.url The URL of the datalist.
			 * @param {Function} config.callback The callback to call with the datalist once it has been fetched
			 * @param {Function} config.onerror The callback to call if an error occurs
			 */
			function queueRequest(config) {
				var groupWin = config.url,
					groupLose = config.url + "_error";
				/*
				 * This function should never be called directly (therefore it is an inner/private function).
				 * Instead requests must always be queued through queueRequest.
				 */
				function sendRequest() {
					var promiseDone = function(group, response) {
							try {
								observer.setFilter(group);  // WARNING! IT IS CRITICAL!!! (in IE) THAT THIS CALL HAPPENS AFTER THE TRANSFORMATION!
								observer.notify(response);
							}
							finally {
								delete pending[config.url];
								observer.reset(groupWin);
								observer.reset(groupLose);
								config = null;
							}
						},
						request = {
							url: config.url,
							onError: function(err) {
								promiseDone(groupLose, err);
							},
							callback: function(srcTree) {
								if ((srcTree === null || srcTree.documentElement === null) && this.responseText) {
									promiseDone(groupWin, toDocFragment(this.responseText));
								}
								else {
									promiseDone(groupWin, toDocFragment(srcTree));
								}
							},
							cache: true,  // cache should be forever, cache is broken by changing URL (querystring)
							responseType: ajax.responseType.TEXT
						};
					console.log("Requesting datalist: ", config.url);
					ajax.simpleRequest(request);
				}

				if (!observer) {
					observer = new Observer();
				}
				observer.subscribe(config, { group: groupWin, method: "callback" });  // add this callback to the list of subscribers for this URL
				observer.subscribe(config, { group: groupLose, method: "onerror" });  // add this callback to the list of subscribers for this URL
				if (!pending.hasOwnProperty(config.url)) {
					sendRequest();
				}
				else {
					console.log("Queuing request while pending: ", config.url);
				}
			}

			/**
			 * Get the data list url.
			 * @function
			 * @private
			 * @param {Element} element Provide an element which will serve as the reference point for finding the URL.
			 * @returns {String} The base URL used to fetch the datalist.
			 */
			function getUrl(element) {
				var result, form = getAncestorOrSelf(element, tag.FORM);
				if (form) {
					result = form.getAttribute("data-wc-datalisturl");
				}
				return result;
			}

			/**
			 * Load a data list for a particular element.
			 * The promise will be resolved with the datalist specified by the id.
			 *
			 * @function module:wc/ui/listLoader.load
			 * @param {String} id The id of the datalist to load
			 * @param {Element} element Provide an element which will serve as the reference point for finding the URL.
			 * @param {boolean} [prefetch] If true treat this request as a prefetch for performance optimization rather than a genuine load.
			 */
			this.load = function(id, element, prefetch) {
				var result = new Promise(function(win, lose) {
					var urlParsed, qsSeparator, url = getUrl(element);
					if (id && id.constructor === String) {
						if (url) {
							urlParsed = urlParser.parse(url);
							if (urlParsed && urlParsed.search) {
								qsSeparator = "&";
							}
							else {
								qsSeparator = "?";
							}
							url += qsSeparator + LIST_ID_PARAM + "=" + encodeURIComponent(id);  // yes it needs the encode
							if (!prefetch || !prefetched[url]) {
								prefetched[url] = true;
								queueRequest({
									url: url,
									id: id,
									callback: win,
									onerror: lose,
									element: element
								});
							}
							else {
								timers.setTimeout(win, 0);
								console.log("Already prefetched: ", url);
							}
						}
						else {
							timers.setTimeout(lose, 0, "Can not find URL");
						}
					}
					else {
						timers.setTimeout(lose, 0, "param 'id' must be a string");
					}
				});
				return result;
			};
		}

		/**
		 * Loads named datalists, allowing other classes to reuse the same datalists. The purpose of this functionality is a
		 * performance enhancement. It allows for significantly reduced page sizes (compared to embedding datalists in
		 * non-cachable pages).

		 * Could store a checksum of the datalist against the full url (including querystring) so we can make sure applications
		 * are correctly assigning identifiers to lists. This could be done in a timeout so it does not slow down the actual
		 * user interaction.
		 *
		 * Could store datalists in memory once they have been fetched so that if the same list is fetched again on the page
		 * it would not even need to hit browser cache.
		 *
		 * @module
		 * @requires module:wc/ajax/ajax
		 * @requires module:wc/urlParser
		 * @requires module:wc/dom/tag
		 * @requires module:wc/Observer
		 * @requires module:wc/dom/getAncestorOrSelf
		 * @requires module:wc/dom/toDocFragment
		 */
		return new ListLoader();
	});
