define(["lib/sprintf", "wc/array/toArray", "wc/config", "wc/mixin", "wc/ajax/ajax",
	"wc/loader/resource", "wc/has", "wc/dom/initialise"],
	function(sprintf, toArray, wcconfig, mixin, ajax, resource, has, initialise) {
		"use strict";

		/**
		 * Manages the loading of i18n "messages" from the relevant i18n "resource bundle".
		 *
		 * WARNING i18n depends on at least one asynchronously loaded resource (the message bundle).
		 * If you try to use it before async resources have loaded "stuff" will break.
		 *
		 * The easiest way to solve this is to call the async "translate" method, unfortunately
		 * this means the calling method is async too and this can quickly branch out into EVERYTHING
		 * being async.
		 *
		 * Alternatively ensure you do not use i18n "too early".
		 * If you register with "wc/dom/initialise" you should be fine, though if you use i18n in the "preInit" phase,
		 * it may cause a race because that's where i18n does its own initialization.
		 *
		 * @module
		 * @requires external:lib/sprintf
		 * @requires module:wc/array/toArray
		 * @requires module:wc/config
		 * @requires module:wc/mixin
		 * @requires external:lib/i18next
		 * @requires module:wc/ajax/ajax
		 * @requires module:wc/loader/resource
		 */
		var instance = new I18n();

		/**
		 * @constructor
		 * @alias module:wc/i18n/i18n~I18n
		 * @private
		 */
		function I18n() {

			var noop = function(key) {
					console.warn("Calling i18n before inited ", key);
					return "";
				},
				GOOG_RE = /^(.+)-x-mtfrom-(.+)$/;

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
				} else if ((googParsed = GOOG_RE.exec(result))) {
					result = googParsed[1];
				}
				return result;
			};

			/**
			 * Initialize this module.
			 * @function module:wc/i18n/i18n.initialize
			 * @public
			 * @param {Object} [config] Configuration options, if provided FORCES initialize even if it has already run.
			 * @returns {Promise} resolved when COMPLETELY initialised.
			 */
			this.initialize = function(config) {
				return new Promise(function(win, lose) {
					// If we're not in an old version of Internet Explorer
					if (!has("ie") || has("ie") > 9) {
						if (config || instance.get === noop) {
							require(["lib/i18next"], function(engine) {  // Should we prefetch this? Does this make it load too late? Does it NEED to be in the layer?
								var useConfig = config || wcconfig.get("wc/i18n/i18n") || {};
								initI18next(engine, useConfig, function(err, translate) {
									if (translate) {
										instance.get = translatorFactory(translate);
									}
									if (err) {
										lose(err);
									} else {
										win();
									}

								});
							});
						} else {
							win();
						}
					} else {
						win();
					}
				});
			};

			/**
			 * This will be set to something useful when is inited.
			 * @deprecated Use translate method instead.
			 */
			this.get = noop;

			/**
			 * Gets an internationalized string/message from the resource bundle.
			 *
			 * @function module:wc/i18n/i18n.get
			 * @public
			 * @param {String|String[]} key A message key, i.e. the key of an i18n key/value pair.
			 *    If an array is provided then each item is taken to be a key. The promise will be resolved with
			 *    an array of translations in the order they appeared in the key array.
			 *    Each key will be passed the same arguments, it probably mainly makes sense when there are no args.
			 * @param {*} [args]* 0..n additional arguments will be used to printf format the string before it is
			 *    returned. Note: It's up to the caller to ensure the correct args (type, number etc...) are passed to
			 *    printf formatted messages.
			 * @returns {Promise} resolved with {String} The message value, i.e. the value of an i18n key/value pair.
			 *     If not found will return an empty string.
			 */
			this.translate = function(/* key, args */) {
				var outerArgs = arguments;
				return instance.initialize().then(function() {
					return instance.get.apply(instance, outerArgs);
				});
			};

			/*
			 * Handles the requirejs plugin lifecycle. (TODO no longer necessary?)
			 * For information {@see http://requirejs.org/docs/plugins.html#apiload}
			 * @function  module:wc/i18n/i18n.load
			 * @deprecated
			 * @public
			 */
			this.load = function (id, parentRequire, callback, config) {
				if (!config || !config.isBuild) {
					console.warn("Calling i18n as a loader plugin is deprecated");
					instance.initialize().then(callback, callback);
				} else {
					callback();
				}
			};

			function translatorFactory(funcTranslate) {
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
				function translator(key/* , args */) {
					var args, result, printfArgs;
					if (arguments.length > 1) {
						args = toArray(arguments);
						args.shift();
					}
					if (Array.isArray(key)) {
						result = key.map(function(nextKey) {
							var innerArgs = [nextKey];
							if (args) {
								innerArgs = innerArgs.concat(args);
							}
							return translator.apply(instance, innerArgs);
						});
					} else {
						result = (key && funcTranslate) ? funcTranslate(key) : "";
						if (result && args) {
							printfArgs = [result].concat(args);
							result = sprintf.sprintf.apply(this, printfArgs);
						}
					}
					return result;
				}
				return translator;
			}
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
		 * @param engine The instance of i18next to initialise.
		 * @param config Configuration options.
		 * @param {Function} [callback] Called when initialized, if the first arg is not falsey it's an error.
		 */
		function initI18next(engine, config, callback) {
			var options = getOptions(config),
				backend = new Backend();
			try {
				engine.use(backend).init(options, callback);
			} catch (ex) {
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
						} catch (ex) {
							callback(ex, response);
						}
					},
					onError: callback
				});
			};
		}

		initialise.register({
			preInit: function() {
				return instance.initialize();  // Totes important, return a promise!
			}
		});

		return instance;
	});
