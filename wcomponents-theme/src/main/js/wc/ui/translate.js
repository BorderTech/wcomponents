define(["wc/i18n/i18n", "wc/dom/initialise", "wc/dom/textContent", "lib/handlebars/handlebars"],
	function(i18n, initialise, textContent, Handlebars) {
		"use strict";

		// Register the Handlebars helper for translation once.
		Handlebars.registerHelper("t",
			function(i18n_key) {
				var result = i18n.get(i18n_key);
				return new Handlebars.SafeString(result);
			});

		/**
		 * Creates a translation module.
		 * @constructor
		 * @private
		 * @alias module:wc/ui/translate~Translate
		 */
		function Translate() {

			/**
			 * Translate all i18n tokens in a given container **or** document.body.
			 * @function module:wc/ui/translate.translate
			 * @public
			 * @param {module:wc/ui/translate~params} [params] will translate document.body if undefined
			 */
			this.translate = function(params) {
				var forms;
				if (params && params.source) {
					translate(params.source, params.target, params.context);
				}
				else if (document.body) {
					forms = document.body.getElementsByTagName("form");
					Array.prototype.forEach.call(forms, translate);
				}
			};

			/**
			 * Translates all i18n tokens from a given source (DOM element or string) to a target element.
			 * If no target is provided then the source element will be treated as the target.
			 * @param {Node|String} source The template or its container Node.
			 * @param {Node} [targetContainer] The element that will be updated with the result of the translation.
			 * @param {Object} [contextObject] The context to pass to the compiled template.
			 */
			function translate(source, targetContainer, contextObject) {
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
			 * Returns a compiled template from the source which may be a text node, element node or string.
			 * @param {Node|String} template
			 * @returns {Function} A compiled template.
			 */
			function getCompiledTemplate(template) {
				if (template.constructor === String) {
					return Handlebars.compile(template);
				}
				else if (template.nodeType === Node.TEXT_NODE) {
					return Handlebars.compile(textContent.get(template));
				}
				else if (template.constructor === Function) {
					// it's actually aleady a compiled template
					return template;
				}
				else {
					return Handlebars.compile(template.innerHTML);
				}
				return "";
			}

			/**
			 * Very early initialisation to do page-load-time i18n.
			 * @function module:wc/ui/translate.preInit
			 * @public
			 */
			this.preInit = function() {
				instance.translate();
			};
		}

		/**
		 * Provides translation of i18n tokens in a container.
		 *
		 * @module
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/dom/initialise
		 * @requires external:lib/handlebars/handlebars
		 */
		var instance = new Translate();
		initialise.register(instance);
		return instance;

		/**
		 * @typedef {Object} module:wc/ui/translate~params
		 * @property {Node|String} source The template source, can be an element or text node which contain the template or a string.
		 * @property {Node} [target] The element that will be updated with the result of the translation; if not provided then container will be used as the target, if it is a Node.
		 * @property {Object} [context] The context that will be passed to the compiled template.
		 */
	});