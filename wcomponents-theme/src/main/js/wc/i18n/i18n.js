/**
 * @module
 * @requires external:lib/sprintf
 * @requires module:wc/array/toArray
 * @requires module:wc/array/unique
 */
define(["lib/sprintf", "wc/array/toArray", "wc/array/unique", "module"],

	function(sprintf, toArray, unique, module) {
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
			var bundle, NOT_FOUND_RETURN_VALUE = "";

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
			 */
			function loadJs(id, parentRequire, callback, config) {
				var i18nConfig,
					attempted = [],
					locales =[],
					win = function(obj) {
						bundle = obj;
						callback(instance);
					},
					tryLoadNext = function(locale) {
						var nextLocale;
						attempted.push(locale);
						if (locales.length) {
							nextLocale = locales.shift();
							console.log("Attempting to load locale", nextLocale);
							loadLocale(parentRequire, nextLocale).then(win, tryLoadNext);
						}
						else {
							console.error("Could not find any i18n resource bundles", attempted.join());
						}
					};
				if (id) {
					locales.push(id);
				}
				if (config && config.config && (i18nConfig = config.config[module.id]) && i18nConfig.locale) {
					locales.push(i18nConfig.locale);
				}
				addDefaultLocales(locales);
				if (locales.length > 1) {
					locales = unique(locales);
				}
				tryLoadNext();
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
			 * @param {Function} parentRequire The require loader to use to load the resource.
			 * @param {string} locale The locale name.
			 * @returns {Promise} resolved with the resource bundle if found. Rejected if not found.
			 */
			function loadLocale(parentRequire, locale) {
				var promise = new Promise(function(resolve, reject) {
					var lose = function() {
						console.info("Could not find i18n bundle for ", locale);
						reject(locale);
					};
					if (locale) {
						parentRequire(["wc/i18n/" + locale], function(obj) {
							if (obj) {
								resolve(obj);
							}
							else {
								lose();
							}
						}, lose);
					}
					else {
						reject("Can not load null locale");
					}
				});
				return promise;
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
					loadJs(id, parentRequire, callback, config);
				}
				else {
					callback();
				}
			};
		}
		return /** @alias module:wc/i18n/i18n */ instance;
	});
