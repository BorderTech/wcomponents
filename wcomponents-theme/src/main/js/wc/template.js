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
			var engine;

			/**
			 * If handlebars templates are rendered on the server we may never need to fetch the handlebars JS.
			 * This allows lazy loading and instantiation.
			 * @param {function} cb Called with the Handlebars engine once it is loaded.
			 */
			function getEngine(cb) {
				if (engine) {
					cb(engine);
				} else {
					require(["lib/mustache/mustache.min"], function(arg) {
						engine = arg;
						cb(engine);
					});
				}
			}

			/**
			 * Returns a compiled template from the source which may be a text node, element node or string.
			 * @function
			 * @private
			 * @param {Node|String} template
			 * @param {Function} callback Called with a compiled template.
			 */
			function getCompiledTemplate(template, callback) {
				getEngine(function(Mustache) {
					var result;
					if (template.constructor === String) {
						result = template;
					} else if (template.nodeType === Node.TEXT_NODE) {
						result = textContent.get(template);
					} else {
						result = template.innerHTML;
					}
					if (result) {
						Mustache.parse(result);
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
						translatedString;
					if (compiledTemplate.constructor === Function) {
						translatedString = compiledTemplate(context);
					} else {
						translatedString = engine.render(compiledTemplate, context);

					}
					if (target.nodeType === Node.TEXT_NODE) {
						target.nodeValue = translatedString;
					} else if (position) {
						target.insertAdjacentHTML(position, translatedString);
					} else {
						target.innerHTML = translatedString;
					}
					if (callback) {
						try {
							callback(compiledTemplate);
						} catch (ignore) {
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
				var processSource = function(source) {
						processContainer(source, params.target, params.context, params.position, params.callback);
					},
					onerror = function(err) {
						if (params.errback) {
							params.errback(err);
						} else {
							console.warn("error processing template " + err);
						}
					};
				if (!has("ie") || has("ie") > 9) {
					if (params && params.source) {
						if (params.loadSource) {
							require(["wc/loader/resource"], function(loader) {
								loader.load(params.source, true, true).then(processSource, onerror);
							});
						} else {
							processSource(params.source);
						}
					} else if (document.body) {
						processContainer(document.body);
					}
				}
			};
		}

		/**
		 * Provides processing of Handlebars templates.
		 *
		 * @module
		 * @requires module:wc/dom/textContent
		 * @requires external:lib/mustache/mustache.min
		 */
		var instance = new Template();
		return instance;

		/**
		 * @typedef {Object} module:wc/template~params
		 * @property {Node|String} source The template source, can be an element or text node which contain the template or a string.
		 * @property {boolean} [loadSource] If true the source is a template to be loaded asynchronously.
		 * @property {Node} [target] The element that will be updated with the result of the translation; if not provided then container will be used as the target, if it is a Node.
		 * @property {Object} [context] The context that will be passed to the compiled template.
		 * @property {string} [position] insertAdjacentHTML position
		 * @property {function} [callback] called when processed, passed the compiled template.
		 * @property {function} [errback] called if something goes wrong
		 */
	});
