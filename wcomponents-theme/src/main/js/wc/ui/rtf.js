/**
 * Provides a Rich Text Field implementation using tinyMCE.
 *
 * Optional module configuration.
 * The config member "initObj" can be set to an abject containing any tinyMCE cofiguration members **except**
 * selector. This allows customised RTF per implementation. This should be added in the template
 *
 * @module
 * @requires module:wc/dom/initialise
 * @requires module:wc/config
 * @requires module:wc/loader/style
 * @requires module:wc/mixin
 * @requires external:tinyMCE
 */
define(["wc/dom/initialise", "wc/config", "wc/loader/style", "wc/mixin", "tinyMCE"],
	function(initialise, wcconfig, styleLoader, mixin, tinyMCE) {
		"use strict";

		/**
		 * Call when DOM is ready to initialise rich text fields.
		 *
		 * @function
		 * @private
		 * @param {String[]} idArr An array of RTF ids.
		 */
		function processNow(idArr) {
			var id,
				initObj = {
					content_css: styleLoader.getMainCss(true),
					plugins: "autolink link image lists print preview paste"
				},
				config = wcconfig.get("wc/ui/rtf"),
				setupFunc = function (editor) {
					editor.on("change", function () {
						editor.save();
					});
				};
			if (config && config.initObj) {
				mixin(config.initObj, initObj);  // config values overwrite defaults coded here.
			}

			while ((id = idArr.shift())) {
				initObj["selector"] = "textarea#" + id;
				if (!initObj["setup"]) {
					initObj["setup"] = setupFunc;
				}
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
