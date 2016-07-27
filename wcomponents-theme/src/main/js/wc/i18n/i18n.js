/**
 * @module
 * @requires external:lib/sprintf
 * @requires module:wc/array/toArray
 * @requires module:wc/array/unique
 */
define(["lib/sprintf", "wc/array/toArray", "wc/config", "lib/i18next", "lib/i18nextXHRBackend", "wc/ajax/ajax", "wc/loader/resource"],

	function(sprintf, toArray, wcconfig, i18next, i18nextXHRBackend, ajax, resource) {
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
			var i18nConfig,
				NOT_FOUND_RETURN_VALUE = "";

			/**
			 * Resolves when this module is initialized.
			 * @param config Configuration options.
			 * @param {Function} [callback] Called when initialized.
			 */
			this.initialize = function(config, callback) {
				i18nConfig = config || {};
				initI18next(callback);
			};

			function addCacheBuster(url, i18nConfig) {
				var cacheBuster = i18nConfig.cachebuster || "";
				if (cacheBuster) {
					url += "?" + cacheBuster;
				}
				return url;
			}

			function getOptions() {
				var basePath = i18nConfig.basePath || resource.getResourceUrl(),
					defaultOptions = {
						load: "currentOnly",
						initImmediate: true,
						ns: [
							"translation"
						],
						defaultNS: [
							"translation"
						],
						lng: "${default.i18n.locale}",
						fallbackLng: "${default.i18n.locale}",
						backend: {
							loadPath: basePath + "{{ns}}/{{lng}}.json",
							ajax: function (url, options, callback, data) {
								var cb = function(response) {
									var xhr = this;
									callback(response, xhr);
								};
								ajax.simpleRequest({
									url: addCacheBuster(url, i18nConfig),
									cache: true,
									callback: cb,
									onError: cb
								});
							}
						}
					},
					result = Object.assign({}, defaultOptions, i18nConfig.options);
				return result;
			}

			function initI18next(callback) {
				var options = getOptions();
				i18next.use(i18nextXHRBackend);
				i18next.init(options, callback);
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
					result = key ? i18next.t(key) : NOT_FOUND_RETURN_VALUE;
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
					instance.initialize(wcconfig.get("wc/i18n/i18n"), callback);
				}
				else {
					callback();
				}
			};
		}
		return /** @alias module:wc/i18n/i18n */ instance;
	});
