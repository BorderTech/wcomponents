/**
 * Provides a Rich Text Field implementation using tinyMCE.
 *
 * @module
 * @requires module:wc/dom/initialise
 * @requires external:tinyMCE
 */
define(["wc/dom/initialise", "tinyMCE"],
	/** @param initialise wc/dom/initialise @param tinyMCE tinyMCE  @ignore */
	function(initialise, tinyMCE) {
		"use strict";

		/**
		 * Call when DOM is ready to initialise rich text fields.
		 *
		 * @function
		 * @private
		 * @param {String[]} idArr An array of RTF ids.
		 */
		function processNow(idArr) {
			var id;
			while ((id = idArr.shift())) {
				tinyMCE.init({
					selector: ("textarea#" + id)
				});
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
