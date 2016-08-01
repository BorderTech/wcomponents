/**
 * @module
 * @requires external:lib/sprintf
 * @requires module:wc/array/toArray
 * @requires module:wc/array/unique
 */
define(["lib/sprintf", "wc/array/toArray", "wc/config", "wc/mixin", "lib/i18next", "wc/ajax/ajax", "wc/loader/resource"],

	function(sprintf, toArray, wcconfig, mixin, i18next, ajax, resource) {
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
			var funcTranslate;

			/**
			 * Initialize this module.
			 * @param [config] Configuration options.
			 * @param {Function} [callback] Called when initialized.
			 */
			this.initialize = function(config, callback) {
				initI18next(config || {}, function(err, translate) {
					if (translate) {
						funcTranslate = translate;
					}
					if (err) {
						console.error(err);
					}
					callback();
				});
			};

			/**
			 * Gets i18next options taking into account defaults and overrides provided by the caller.
			 * @param i18nConfig Override default options by setting corresponding properties on this object.
			 */
			function getOptions(i18nConfig) {
				var basePath = i18nConfig.basePath || resource.getResourceUrl(),
					defaultOptions = {
						load: "currentOnly",
						initImmediate: true,
						lng: "${default.i18n.locale}",
						fallbackLng: "${default.i18n.locale}",
						backend: {
							loadPath: basePath + "{{ns}}/{{lng}}.json"
						}
					},
					result = mixin(defaultOptions, {});
				result = mixin(i18nConfig.options, result);
				return result;
			}

			/**
			 * Initialize the underlying i18next instance.
			 * @param config Configuration options.
			 * @param {Function} [callback] Called when initialized.
			 */
			function initI18next(config, callback) {
				var options = getOptions(config),
					backend = new Backend();
				try {
					i18next.use(backend).init(options, callback);
				}
				catch (ex) {
					callback(ex);
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
					result = (key && funcTranslate) ? funcTranslate(key) : "";
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

		/**
		 * Provides an XHR backend for i18next (one that works on PhantomJS).
		 * All public methods implement the i18next backend interface, see i18next documentaion (if you can find any).
		 * @constructor
		 */
		function Backend() {
			this.type = "backend";

			this.init = function(services, backendOptions, i18nextOptions) {
				this.services = services;
				this.options = backendOptions;
			};

			this.read = function(language, namespace, callback) {
				var cacheBuster = this.options.cacheBuster || this.options.cachebuster || "",
					url = this.services.interpolator.interpolate(this.options.loadPath, { lng: language, ns: namespace });
				if (cacheBuster) {
					url += "?" + cacheBuster;
				}

				ajax.simpleRequest({
					url: url,
					cache: true,
					callback: function(response) {
						try {
							var data = JSON.parse(response);
							callback(null, data);
						}
						catch (ex) {
							callback(ex, response);
						}
					},
					onError: callback
				});
			};
		}
		return /** @alias module:wc/i18n/i18n */ instance;
	});
