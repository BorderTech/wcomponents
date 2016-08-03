define(["wc/i18n/i18n", "wc/dom/initialise","lib/handlebars/handlebars"],
	function(i18n, initialise, Handlebars) {
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
			 * @param {Element} [container] the element to translate - use document.body if undefined
			 */
			this.translate = function(container) {
				var target = container ? [container] : document.body.getElementsByTagName("form");

				Array.prototype.forEach.call(target, function(next) {
					var isTextNode = next.nodeType === Node.TEXT_NODE,
						dirtyString = isTextNode ? next.textContent : next.innerHTML,
						compiledTemplate = Handlebars.compile(dirtyString);

					if (isTextNode) {
						next = compiledTemplate({});
					}
					else {
						next.innerHTML = compiledTemplate({});
					}
				});
			};

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
	});
