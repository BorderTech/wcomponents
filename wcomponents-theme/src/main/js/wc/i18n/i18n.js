define(["lib/sprintf", "wc/array/toArray", "wc/config", "wc/mixin", "wc/ajax/ajax", "wc/loader/resource", "wc/template", "wc/has"],
	function(sprintf, toArray, wcconfig, mixin, ajax, resource, template, has) {
		"use strict";
		var funcTranslate;

		/**
		 * Manages the loading of i18n "messages" from the relevant i18n "resource bundle".
		 *
		 * WARNING This module is not usable unless it is loaded as a plugin (with a bang like so "wc/i18n/i18n!") before subsequent use.
		 * Either all modules must use it as a plugin OR this can happen in a bootstrapping phase.
		 *
		 * @module
		 * @requires external:lib/sprintf
		 * @requires module:wc/array/toArray
		 * @requires module:wc/config
		 * @requires module:wc/mixin
		 * @requires external:lib/i18next
		 * @requires module:wc/ajax/ajax
		 * @requires module:wc/loader/resource
		 * @requires module:wc/template
		 */
		var i18next, instance = new I18n();

		/**
		 * @constructor
		 * @alias module:wc/i18n/i18n~I18n
		 * @private
		 */
		function I18n() {

			var GOOG_RE = /^(.+)-x-mtfrom-(.+)$/;

			/**
			 * The language to use when no preference has been explicitly specified.
			 * In the unlikely event this ever needs to be changed bear in mind the server
			 * side i18n also has a similar hardcoded setting.
			 */
			this._DEFAULT_LANG = "en";

			/**
			 * Determine the language of the document.
			 * @param Element [element] Optionally provide a context element which will take precedence over the documentElement.
			 * @returns {String} the current document language.
			 */
			this._getLang = function(element) {
				/*
				 * Handles a special case for Google Tranlate, full details here: https://github.com/BorderTech/wcomponents/issues/994
				 * Format is: toLang-x-mtfrom-fromLang
				 */
				var result, docElement, doc = document, googParsed;
				if (element) {
					result = element.lang;
				}
				if (!result && doc) {
					docElement = doc.documentElement;
					if (docElement) {
						result = docElement.lang;  // should we consider xml:lang (which takes precedence over lang)?
					}
				}
				if (!result) {
					result = this._DEFAULT_LANG;
				}
				else if ((googParsed = GOOG_RE.exec(result))) {
					result = googParsed[1];
				}
				return result;
			};

			/**
			 * Initialize this module.
			 * @function module:wc/i18n/i18n.initialize
			 * @public
			 * @param {Object} [config] Configuration options.
			 * @param {Function} [callback] Called when initialized.
			 */
			this.initialize = function(config, callback) {
				if (!has("ie") || has("ie") > 9) {
					require(["lib/i18next"], function(engine) {  // Should we prefetch this? Does this make it load too late? Does it NEED to be in the layer?
						i18next = engine;
						initI18next(config || {}, function(err, translate) {
							if (translate) {
								funcTranslate = translate;
							}
							if (err) {
								console.error(err);
							}
							callback();
						});
					});
					// Register the i18n Handlebars helper.
					template.registerHelper(function(i18n_key) {
						return instance.get(i18n_key);
					}, "t", template.PROCESS.SAFE_STRING);
				}
				else {
					callback();
				}
			};

			/**
			 * Gets an internationalized string/message from the resource bundle.
			 *
			 * @function module:wc/i18n/i18n.get
			 * @public
			 * @param {String} key A message key, i.e. the key of an i18n key/value pair.
			 * @param {*} [args]* 0..n additional arguments will be used to printf format the string before it is
			 *    returned. Note: It's up to the caller to ensure the correct args (type, number etc...) are passed to
			 *    printf formatted messages.
			 * @returns {String} The message value, i.e. the value of an i18n key/value pair. If not found will return an empty string.
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
			 * @function  module:wc/i18n/i18n.load
			 * @public
			 */
			this.load = function (id, parentRequire, callback, config) {
				if (!config || !config.isBuild) {
					instance.initialize(wcconfig.get("wc/i18n/i18n"), callback);
				}
				else {
					callback();
				}
			};

			/**
			 * Pass-through for i18next.t.
			 *
			 * @function  module:wc/i18n/i18n.t
			 * @public
			 * @param {*} [arguments]* 0..n arguments passed to i18next.t
			 * @return {String} The internationalised version of the input.
			 */
			this.t = function() {
				if (i18next) {
					return i18next.t.apply(i18next, arguments);
				}
			};
		}

		/**
		 * Gets i18next options taking into account defaults and overrides provided by the caller.
		 * @function
		 * @private
		 * @param {Object} i18nConfig Override default options by setting corresponding properties on this object.
		 */
		function getOptions(i18nConfig) {
			var basePath = i18nConfig.basePath || resource.getResourceUrl(),
				currentLanguage = instance._getLang(),
				cachebuster = resource.getCacheBuster(),
				nsResource = "{{ns}}/{{lng}}.json" + (cachebuster ? "?" + cachebuster : ""),
				defaultOptions = {
					load: "currentOnly",
					initImmediate: true,
					lng: currentLanguage,
					fallbackLng: instance._DEFAULT_LANG,
					backend: {
						loadPath: basePath + nsResource
					}
				},
				result = mixin(defaultOptions, {});
			result = mixin(i18nConfig.options, result);
			return result;
		}

		/**
		 * Initialize the underlying i18next instance.
		 * @function
		 * @private
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
		 * Provides an XHR backend for i18next (one that works on PhantomJS).
		 * All public methods implement the i18next backend interface, see i18next documentation (if you can find any).
		 * @constructor
		 * @private
		 */
		function Backend() {
			this.type = "backend";

			this.init = function(services, backendOptions /* , i18nextOptions */) {
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


		// I18n.call(instance.get);
		mixin(instance, instance.get);

		return instance.get;
	});
