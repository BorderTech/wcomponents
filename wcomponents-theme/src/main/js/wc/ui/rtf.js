/**
 * Provides a Rich Text Field implementation using tinyMCE.
 *
 * Optional module configuration.
 * The config member "initObj" can be set to an abject containing any tinyMCE cofiguration members **except**
 * selector. This allows customised RTF per implementation. This should be added in the template
 *
 * @module
 * @requires module:wc/dom/initialise
 * @requires external:tinyMCE
 */
define(["wc/dom/initialise", "tinyMCE", "module"],
	/** @param initialise wc/dom/initialise @param tinyMCE tinyMCE @param module @ignore */
	function(initialise, tinyMCE, module) {
		"use strict";

		/**
		 * Call when DOM is ready to initialise rich text fields.
		 *
		 * @function
		 * @private
		 * @param {String[]} idArr An array of RTF ids.
		 */
		function processNow(idArr) {
			var id, initObj = {};
			if (module && module.config()) {
				initObj = module.config().initObj || {};
			}
			while ((id = idArr.shift())) {
				initObj["selector"] = "textarea#" + id;
				tinyMCE.init(initObj);
			}
		}

		return /** @alias module:wc/ui/rtf */{
			/**
			 * Register Rich Text Fields that need to be initialised.
			 *
			 * @function
			 * @public
			 * @param {String[]} idArr An array of element ids.
			 */
			register: function(idArr) {
				if (idArr && idArr.length) {
					initialise.addCallback(function() {
						processNow(idArr);
					});
				}
			}
		};
	});
