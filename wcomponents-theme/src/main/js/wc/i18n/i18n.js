/**
 * @module
 * @requires external:lib/sprintf
 * @requires module:wc/array/toArray
 * @requires module:wc/array/unique
 */
define(["lib/sprintf", "wc/array/toArray", "wc/array/unique", "wc/config"],

	function(sprintf, toArray, unique, wcconfig) {
		"use strict";
		var instance = new I18n();
		/**
		 * WARNING This module is not usable unless it is loaded as a plugin (with a bang like so "wc/i18n/i18n!") before subsequent use.
		 * Either all modules must use it as a plugin OR this can happen in a bootstrapping phase.
		 *
		 * Manages the loading of i18n "messages" from the relevant i18n "resource bundle".
		 *
		 * @constructor
		 * @alias module:wc/i18n/i18n~I18n
		 * @private
		 */
		function I18n() {
			var bundle, i18nConfig, NOT_FOUND_RETURN_VALUE = "";

			/**
			 * Resolves when this module is initialized.
			 * @param config Configuration options.
			 * @param {Function} [parentRequire] If calling as an AMD loader plugin provide a parentRequire.
			 * @returns Promise
			 */
			this.initialize = function(config, parentRequire) {
				i18nConfig = config || {};
				return loadJs(parentRequire);
			};

			/**
			 * Look up a particular key.
			 * @function
			 * @private
			 * @param {string} key A message key, i.e. the key of an i18n key/value pair.
			 * @returns {string} The message value, i.e. the value of an i18n key/value pair.
			 */
			function lookup(key) {
				if (bundle && key in bundle) {
					return bundle[key];
				}
				return NOT_FOUND_RETURN_VALUE;
			}

			/*
			 * Loads a resource bundle containing messages for the given locale.
			 * Will attempt to fall back to default locales if possible.
			 * @param {Function} [parentRequire] If calling as an AMD loader plugin provide a parentRequire.
			 */
			function loadJs(parentRequire) {
				var result = new Promise(function(resolve, reject) {
					var attempted = [],
						locales =[],
						win = function(obj) {
							bundle = obj;
							resolve(bundle);
						},
						tryLoadNext = function() {
							var nextLocale;
							if (locales.length) {
								nextLocale = locales.shift();
								attempted.push(nextLocale);
								console.log("Attempting to load locale", nextLocale);
								loadLocale(nextLocale, parentRequire || require, win, tryLoadNext);
							}
							else {
								reject("Could not find any i18n resource bundles " + attempted.join());
							}
						};
					if (i18nConfig.locale) {
						locales.push(i18nConfig.locale);
					}
					addDefaultLocales(locales);
					if (locales.length > 1) {
						locales = unique(locales);
					}
					tryLoadNext();
				});
				return result;
			}

			/**
			 * Adds default locale/s to the array of locales provided.
			 * Default locales will be added to the end of the array.
			 * Note that this may result in duplicates being added to the array.
			 * @param {string[]} locales
			 */
			function addDefaultLocales(locales) {
				var i, next, defaultLocales = ["${default.i18n.locale}", (navigator.language || navigator.browserLanguage)];
				if (navigator.languages) {
					defaultLocales = defaultLocales.concat(navigator.languages);
				}
				defaultLocales = unique(defaultLocales);
				for (i = 0; i < defaultLocales.length; i++) {
					next = defaultLocales[i];
					if (next) {
						locales.push(next);
					}
				}
			}

			/**
			 * Attempts to load  a resource bundle for the given locale.
			 *
			 * WARNING - DO NOT PROMISIFY THIS FUNCTION: returning a promise created huge problems on iOS
			 *
			 * @param {string} locale The locale name.
			 * @param {Function} require The AMD loader to use.
			 * @param {Function} success Will be called with the locale bundle if loaded.
			 * @param {Function} fail Will be called if the locale bundle was not loaded.
			 */
			function loadLocale(locale, require, success, fail) {
				var lose = function() {
					console.info("Could not find i18n bundle for ", locale);
					fail(locale);
				};
				if (locale) {
					require(["wc/i18n/" + locale], function(obj) {
						if (obj) {
							success(obj);
						}
						else {
							lose();
						}
					}, lose);
				}
				else {
					fail("Can not load null locale");
				}
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
				if (!config || !config.isBuild) {
					instance.initialize(wcconfig.get("wc/i18n/i18n"), parentRequire).then(callback);
				}
				else {
					callback();
				}
			};
		}
		return /** @alias module:wc/i18n/i18n */ instance;
	});
