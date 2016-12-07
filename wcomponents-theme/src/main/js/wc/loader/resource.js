/**
 * @module
 * @requires module:wc/ajax/ajax
 * @requires module:wc/loader/prefetch
 */
define(["wc/ajax/ajax", "wc/loader/prefetch", "wc/config", "module"],
	function(ajax, prefetch, wcconfig, module) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/loader/resource~Loader
		 * @private
		 */
		function Loader() {
			var baseUrl;

			/**
			 * Politely suggest to the browser that it may wish to prefetch this resource if it finds a convenient moment.
			 * This will help perceived performance by attempting to load this resource into the browser cache before it is explicitly
			 * required by the suer.
			 * @param {String} fileName The file name of the resource to load.
			 */
			this.preload = function(fileName) {
				var url = this.getResourceUrl(fileName);
				prefetch.request(url);
			};

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
				var url = this.getResourceUrl(fileName);
				console.log("Loading " + url);
				return ajax.loadXmlDoc(url, null, asText, async);
			};

			/**
			 * Gets the URL to a resource in the theme resource directory.
			 * @param {string} fileName The file name of a resource in the resource directory.
			 * @returns {string} The URL to the resource.
			 */
			this.getResourceUrl = function(fileName) {
				var url, path, idx, cachebuster, config = getConfig();
				if (config) {
					path = config.resourceBaseUrl;
					cachebuster = config.cachebuster;
				}
				else {
					idx = module.uri.indexOf(module.id);
					path = module.uri.substring(0, idx);
					path = path.replace(/\/[^\/]+\/$/, "/${resource.target.dir.name}/");
				}

				baseUrl = baseUrl || path || "";  // ${resource.target.dir.name}/";
				if (fileName) {
					url = baseUrl + fileName + "?" + cachebuster;
				}
				else {
					url = baseUrl;
				}
				return url;
			};

			/**
			 * Allows other modules to get the cachebuster used by the resource loader.
			 * @returns {?String} the cachebuster if present.
			 */
			this.getCacheBuster = function() {
				var config = getConfig();
				if (config && config.cachebuster) {
					return config.cachebuster;
				}
				return null;
			};

			function getConfig() {
				var config = wcconfig.get("wc/loader/resource");
				if (!config) {
					if (window.System && window.System.config) {
						config = window.System.config;
					}
					else if (module && module.config) {
						config = module.config();
					}
				}
				return config;
			}

		}
		return /** @alias module:wc/loader/resource */ new Loader();
	});
