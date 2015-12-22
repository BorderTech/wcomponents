/**
 * @module
 * @requires external:lib/sprintf
 * @requires module:wc/xml/xpath
 * @requires module:wc/array/toArray
 */
define(["lib/sprintf", "wc/xml/xpath", "wc/array/toArray", "module"],
	/** @param sprintf lib/sprintf @param xpath wc/xml/xpath @param toArray wc/array/toArray @param module module @ignore */
	function(sprintf, xpath, toArray, module) {
		"use strict";
		var instance = new I18n();
		/**
		 * WARNING This module is not usable unless it is loaded as a plugin (with a bang like so "wc/i18n/i18n!") before subsequent use.
		 * Either all modules must use it as a plugin OR this can happen in a bootstrapping phase.
		 *
		 * Manages the loading of i18n "messages" from the relevant i18n "resource bundle".
		 *
		 * You may notice the "resource bundle" is actually the "wrapper" xsl stylesheet, and you may wonder why... Well
		 * here's the reasoning behind it:
		 *
		 * 1. The XSL will already be in cache. Since it was used to load the page in the browser by the time this
		 *    javascript needs it we can be sure it will be a "free" call straight to the browser cache.
		 * 2. The "resource bundle" would, regardless of whether we use the XSL, be a data structure of some kind,
		 *    either XML or JSON. Since the XSL wrapper is simply a small XML file why bother reformatting it into
		 *    something else? If we did do that it would be a separate resource we would have to load, increasing the
		 *    weight of the overall webapp and decreasing performance (particularly when the bundle had not been
		 *    cached).
		 *
		 * @constructor
		 * @alias module:wc/i18n/i18n~I18n
		 * @private
		 */
		function I18n() {
			/*
			 * EXPRESSION: the xpath is carefully crafted to extract the text from either of these structures:
			 * <xsl:param name="mnth0">January</xsl:param> OR <xsl:param name="mnth0"><xsl:text>January</xsl:text></xsl:param>
			 * We expect these parameters to always be coded using the <xsl:text> element however this is currently stripped by one of
			 * our xsl minification routines. While we could get rid of that routine it does make sense and could easily be reinstated
			 * down the track.
			 */
			var NOT_FOUND_RETURN_VALUE = "",
				// EXPRESSION = "//xsl:param[@name='%s']/descendant-or-self::*[last()]/text()",  // see notes above
				/*
				 * The commented out XPath above is great, but older versions of MSXML do not support "last()" (and many others) as
				 * well as baulking at the double colon ::.
				 * The less elegant xpath below is specially crafted to work in MSXML ActiveX versions from 3.0 up.
				 */
				EXPRESSION = "//xsl:param[@name='%s']/xsl:text/text()|//xsl:param[@name='%1$s' and not(xsl:text)]/text()",
				cache = {},  // cache messages when they are first resolved, for the lifespan of the page
				bundle;

			/**
			 * Look up a particular key.
			 * @function
			 * @private
			 * @param {string} key A message key, i.e. the key of an i18n key/value pair.
			 * @returns {string} The message value, i.e. the value of an i18n key/value pair.
			 */
			function lookup(key) {
				var result = cache[key];
				if (!result) {
					result = xpath.query(sprintf.sprintf(EXPRESSION, key), true, bundle);
					if (result) {
						result = (cache[key] = result.nodeValue);
					}
					else {
						// console.warn("Can not find match for key: ", key);
						result = NOT_FOUND_RETURN_VALUE;
					}
				}
				return result;
			}

//			/**
//			 * Look up a particular key.
//			 * @function
//			 * @private
//			 * @param {string} key A message key, i.e. the key of an i18n key/value pair.
//			 * @returns {string} The message value, i.e. the value of an i18n key/value pair.
//			 */
//			function lookup(key) {
//				var i, next, arr = bundle.properties.property, result = cache[key];
//				if (!result) {
//					for (i = 0; i < arr.length; i++) {
//						next = arr[i];
//						if (next.name === key) {
//							return (cache[key] = next.value);
//						}
//					}
//				}
//				return NOT_FOUND_RETURN_VALUE;
//			}
			/*
			 * Handles the requirejs plugin lifecycle.
			 * For information {@see http://requirejs.org/docs/plugins.html#apiload}
			 */
//			function loadJson(id, parentRequire, callback, config) {
//				var locale, i18nConfig;
//				if (id) {
//					locale = id;
//				}
//				else if (config && config.config && (i18nConfig = config.config[module.id])) {
//					locale = i18nConfig.locale || "en";
//				}
//				else {
//					locale = "en";
//				}
//				parentRequire(["i18n/" + locale], function(obj) {
//					bundle = obj;
//					callback(this);
//				});
//			}

			/*
			 * Handles the requirejs plugin lifecycle.
			 * For information {@see http://requirejs.org/docs/plugins.html#apiload}
			 */
			function loadXml(id, parentRequire, callback, config) {
				var url, idx, i18nConfig;
				if (config && config.config && (i18nConfig = config.config[module.id])) {
					url = i18nConfig.i18nBundleUrl;
				}
				else {
					idx = module.uri.indexOf(module.id);
					url = module.uri.substring(0, idx);
					url = url.replace(/\/[^\/]+\/$/, "/xslt/all.xsl");  // TODO, come up with a better default
				}
				parentRequire(["wc/ajax/ajax"], function(ajax) {
					ajax.loadXmlDoc(url, null, false, true).then(function(obj) {
						bundle = obj;
						callback(instance);
					});
				}, callback);
			}

			/**
			 * Gets an internationalized string/message from the resource bundle.
			 *
			 * @function module:wc/i18n/i18n.get
			 * @param {String} key A message key, i.e. the key of an i18n key/value pair.
			 * @param {*} [args]* 0..n additional arguments will be used to printf format the string before it is
			 *    returned. Note: It's up to the caller to ensure the correct args (type, number etc...) are passed to
			 *    printf formatted messages.
			 * @returns {String} The message value, i.e. the value of an i18n key/value pair. If not found will return
			 *    an empty string.
			 */
			this.get = function(key/* , args */) {
				var args,
					result = key ? lookup(key) : NOT_FOUND_RETURN_VALUE;
				if (result && arguments.length > 1) {
					args = toArray(arguments);
					args.shift();
					args.unshift(result);
					result = sprintf.sprintf.apply(this, args);
				}
				return result;
			};

			/*
			 * Handles the requirejs plugin lifecycle.
			 * For information {@see http://requirejs.org/docs/plugins.html#apiload}
			 */
			this.load = function (id, parentRequire, callback, config) {
				// loadJson(id, parentRequire, callback, config);
				if (!config || !config.isBuild) {
					loadXml(id, parentRequire, callback, config);
				}
				else {
					callback();
				}
			};
		}
		return /** @alias module:wc/i18n/i18n */ instance;
	});
