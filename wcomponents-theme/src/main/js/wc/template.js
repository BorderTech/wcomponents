define(["wc/dom/textContent", "wc/has"],
	function(textContent, has) {
		"use strict";

		/**
		 * Creates a template module.
		 * @constructor
		 * @private
		 * @alias module:wc/template~Template
		 */
		function Template() {
			var engine,
				helperQueue = [];

			/**
			 * If handlebars templates are rendered on the server we may never need to fetch the handlebars JS.
			 * This allows lazy loading and instantiation.
			 * @param {function} cb Called with the Handlebars engine once it is loaded.
			 */
			function getEngine(cb) {
				if (engine) {
					cb(engine);
				}
				else {
					require(["lib/handlebars/handlebars"], function(arg) {
						engine = arg;
						processHelperQueue();
						cb(engine);
					});
				}
			}

			function processHelperQueue() {
				var next;
				while (helperQueue.length) {
					try {
						next = helperQueue.shift();
						if (next) {
							instance.registerHelper(next.callback, next.token, next.type);
						}
					}
					catch (ignore) {
						console.warn(ignore);
					}
				}
			}

			/**
			 * @var {Object} module:wc/template.PROCESS Handlebars processing types available for use in module:wc/template.registerHelper
			 * @public
			 *
			 * @property {number} SAFE_STRING process and return a new Handlebars.SafeString
			 * @property {number} ESCAPE_EXPRESSION process and return the result of Handlebars.escapeExpression
			 */
			this.PROCESS = {
				"SAFE_STRING": 1,
				"ESCAPE_EXPRESSION": 2
			};

			/**
			 * Returns a compiled template from the source which may be a text node, element node or string.
			 * @function
			 * @private
			 * @param {Node|String} template
			 * @param {Function} callback Called with a compiled template.
			 */
			function getCompiledTemplate(template, callback) {
				getEngine(function(Handlebars) {
					var result;
					if (template.constructor === String) {
						result = Handlebars.compile(template);
					}
					else if (template.nodeType === Node.TEXT_NODE) {
						result = Handlebars.compile(textContent.get(template));
					}
					else if (template.constructor === Function) {
						// it's actually aleady a compiled template
						result = template;
					}
					else {
						result = Handlebars.compile(template.innerHTML);
					}
					callback(result);
				});
			}

			/**
			 * Process a template (DOM element or string) to a target element.
			 * If no target is provided then the source element will be treated as the target.
			 *
			 * @function
			 * @private
			 * @param {Node|String} source The template or its container Node.
			 * @param {Node} [targetContainer] The element that will be updated with the result of the translation.
			 * @param {Object} [contextObject] The context to pass to the compiled template.
			 * @param {string} [position] insertAdjacentHTML position
			 * @param {function} [callback] called when processed, passed the compiled template.
			 */
			function processContainer(source, targetContainer, contextObject, position, callback) {
				getCompiledTemplate(source, function(compiledTemplate) {
					var target = targetContainer || source,
						context = contextObject || {},
						translatedString = compiledTemplate(context);

					if (target.nodeType === Node.TEXT_NODE) {
						target = translatedString;
					}
					else if (position) {
						target.insertAdjacentHTML(position, translatedString);
					}
					else {
						target.innerHTML = translatedString;
					}
					if (callback) {
						try {
							callback(compiledTemplate);
						}
						catch (ignore) {
							console.warn(ignore);
						}
					}
				});

			}

			/**
			 * Convert templates in a given container **or** document.body.
			 * @function module:wc/template.process
			 * @public
			 * @param {module:wc/template~params} [params] will process document.body if undefined
			 */
			this.process = function(params) {
				if (!has("ie") || has("ie") > 9) {
					if (params && params.source) {
						processContainer(params.source, params.target, params.context, params.position, params.callback);
					}
					else if (document.body) {
						processContainer(document.body);
					}
				}
			};

			/**
			 * Generate a **simple** handlebars helper with optional result processing.
			 *
			 * @function
			 * @private
			 * @param {Function} func a function which returns a string or node list
			 * @param {module:wc/template.PROCESS} processType the type of result processing we need
			 * @param Handlebars The handlebars engine.
			 * @returns {Function} a Handlebars helper callback function with one argument which is the simple key of the template helper
			 */
			function helperCallbackFactory(func, processType, Handlebars) {
				return function(key) {
					var result = func(key);

					if (processType === instance.PROCESS.SAFE_STRING) {
						return new Handlebars.SafeString(result);
					}

					if (processType === instance.PROCESS.ESCAPE_EXPRESSION) {
						return Handlebars.escapeExpression(result);
					}
				};
			}

			/**
			 * Register a Handlebars helper.
			 *
			 * @function  module:wc/template.registerHelper
			 * @public
			 * @param {Function|Object} callback a function which returns a string or node list or an Object which defines one or more helpers.
			 * @param {String} [token] the template identifier used by this helper not required if callback is an object as per
			 *   http://handlebarsjs.com/reference.html#base-registerHelper
			 * @param {module:wc/template.PROCESS} [type] the type of Handlebars processing to use. If not set then the callback will be
			 *   registered directly.
			 *
			 * @example To register a simple handler which replaces a template and returns a new Handlebars.SafeString
			 * <pre><code>
			 * // the module which is registering the callback should know how to handle the raw input. Let us assume it has a function
			 * // `getValue({String} key)` and a module member HANDLEBARS_HELPER_TOKEN
			 *
			 * require(["wc/template"], function (template) {
			 *   var HANDLEBARS_HELPER_TOKEN = "myToken";
			 *
			 *   function getValue(key){
			 *     var result = "";
			 *     // do something to get result as a String
			 *     return result;
			 *   }
			 *
			 *   template.registerHelper(HANDLEBARS_HELPER_TOKEN, getValue, template.PROCESS.SAFE_STRING);
			 * });
			 *</code></pre>
			 *
			 *@example to register a handler which deals with its own context to generate a full name in common English format (given name first):
			 * <pre><code>
			 * require(["wc/template"], function (template) {
			 *   var HANDLEBARS_HELPER_TOKEN = "fullname";
			 *
			 *   function getValue(context){
			 *     return context.givenName + " " + context.familyName;
			 *   }
			 *
			 *   template.registerHelper(HANDLEBARS_HELPER_TOKEN, getValue);
			 * });
			 *</code></pre>
			 *
			 */
			this.registerHelper = function(callback, token, type) {
				if (!has("ie") || has("ie") > 9) {
					if (engine) {
						if (typeof callback === "object") {
							engine.registerHelper(callback);
							return;
						}

						if (!token) {
							throw new TypeError("Handlebars helper must be identified.");
						}

						if (type) {
							engine.registerHelper(token, helperCallbackFactory(callback, type, engine));
							return;
						}
						engine.registerHelper(token, callback);
					}
					else {
						helperQueue.push({
							callback: callback,
							token: token,
							type: type
						});
					}
				}
			};

			/**
			 * Unregister a Handlebars helper
			 * @param {String} token the identifier of the helper to unregister
			 */
			this.unregisterHelper = function(token) {
				if (token) {
					if (engine) {
						engine.unregisterHelper(token);
					}
					else {
						helperQueue = helperQueue.filter(function(helper) {
							return helper && helper.token !== token;
						});
					}
				}
			};
		}

		/**
		 * Provides processing of Handlebars templates.
		 *
		 * @module
		 * @requires module:wc/dom/textContent
		 * @requires external:lib/handlebars/handlebars
		 */
		var instance = new Template();
		return instance;

		/**
		 * @typedef {Object} module:wc/template~params
		 * @property {Node|String} source The template source, can be an element or text node which contain the template or a string.
		 * @property {Node} [target] The element that will be updated with the result of the translation; if not provided then container will be used as the target, if it is a Node.
		 * @property {Object} [context] The context that will be passed to the compiled template.
		 * @property {string} [position] insertAdjacentHTML position
		 * @property {function} [callback] called when processed, passed the compiled template.
		 */
	});
