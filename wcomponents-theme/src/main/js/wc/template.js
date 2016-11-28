define(["wc/dom/textContent", "lib/handlebars/handlebars", "wc/has"],
	function(textContent, Handlebars, has) {
		"use strict";

		/**
		 * Creates a template module.
		 * @constructor
		 * @private
		 * @alias module:wc/template~Template
		 */
		function Template() {
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
			 * @returns {Function} A compiled template.
			 */
			function getCompiledTemplate(template) {
				if (template.constructor === String) {
					return Handlebars.compile(template);
				}
				if (template.nodeType === Node.TEXT_NODE) {
					return Handlebars.compile(textContent.get(template));
				}
				if (template.constructor === Function) {
					// it's actually aleady a compiled template
					return template;
				}
				return Handlebars.compile(template.innerHTML);
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
			 */
			function processContainer(source, targetContainer, contextObject) {
				var compiledTemplate = getCompiledTemplate(source),
					target = targetContainer || source,
					context = contextObject || {},
					translatedString = compiledTemplate(context);

				if (target.nodeType === Node.TEXT_NODE) {
					target = translatedString;
				}
				else {
					target.innerHTML = translatedString;
				}
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
						processContainer(params.source, params.target, params.context);
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
			 * @returns {Function} a Handlebars helper callback function with one argument which is the simple key of the template helper
			 */
			function helperCallbackFactory(func, processType) {
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
					if (typeof callback === "object") {
						Handlebars.registerHelper(callback);
						return;
					}

					if (!token) {
						throw new TypeError("Handlebars helper must be identified.");
					}

					if (type) {
						Handlebars.registerHelper(token, helperCallbackFactory(callback, type));
						return;
					}
					Handlebars.registerHelper(token, callback);
				}
			};

			/**
			 * Unregister a Handlebars helper
			 * @param {String} token the identifier of the helper to unregister
			 * @returns {undefined}
			 */
			this.unregisterHelper = function(token) {
				Handlebars.unregisterHelper(token);
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
		 */
	});
