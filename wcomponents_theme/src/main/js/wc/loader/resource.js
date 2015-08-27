/**
 * @module
 * @requires module:wc/ajax/ajax
 */
define(["wc/ajax/ajax", "module"],
	/** @param ajax wc/ajax/ajax @param module module @ignore */
	function(ajax, module) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/loader/resource~Loader
		 * @private
		 */
		function Loader() {
			var baseUrl;

			/**
			 * Loads static XML resources from the xml directory in the "theme". This allows large data structures to be excluded
			 * from the main codebase and loaded on demand.
			 *
			 * This best suits data that has a chance of never being loaded, for example a table that maps mime types to file
			 * extensions will never be loaded if:
			 *
			 * * no file uploaders are in use;
			 * * the browser supports the file api that can tell you the mime type without needing a lookup table.
			 *
			 * Note, the server should set the correct cache headers so these XML resources are cached forever.
			 *
			 * @function module:wc/loader/resource.load
			 * @param {String} fileName The file name of the resource to load.
			 * @param {Boolean} [asText] True to load as a text file rather than as an XML file.
			 * @param {Boolean} [async] True to load the resource asynchronously (in which case returns a Promise);
			 * @returns {?Document|Promise} The loaded file or a Promise resolved with the loaded file if async is true.
			 */
			this.load = function(fileName, asText, async) {
				var path, idx, cachebuster;
				if (module && module.config) {
					path = module.config().xmlBaseUrl;
					cachebuster = module.config().cachebuster;
				}
				else {
					idx = module.uri.indexOf(module.id);
					path = module.uri.substring(0, idx);
					path = path.replace(/\/[^\/]+\/$/, "/${xml.target.dir.name}/");
				}

				baseUrl = baseUrl || path || "";  // ${xml.target.dir.name}/";
				var url = baseUrl + fileName + "?" + cachebuster;
				console.log("Loading " + url);
				return ajax.loadXmlDoc(url, null, asText, async);
			};
		}
		return /** @alias module:wc/loader/resource */ new Loader();
	});
