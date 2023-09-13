// TODO delete / rewrite this module
import ajax from "wc/ajax/ajax.mjs";
import prefetch from "wc/loader/prefetch.mjs";
import wcconfig from "wc/config.mjs";
import Observer from "wc/Observer.mjs";

let baseUrl, observer;

const instance = {
	/**
	 * Politely suggest to the browser that it may wish to prefetch this resource if it finds a convenient moment.
	 * This will help perceived performance by attempting to load this resource into the browser cache before it is explicitly
	 * required by the suer.
	 * @param {String} fileName The file name of the resource to load.
	 */
	preload: function(fileName) {
		const url = this.getResourceUrl(fileName);
		prefetch.request(url, "fetch");
	},

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
	 * @returns {Document|Promise} The loaded file or a Promise resolved with the loaded file if async is true.
	 */
	load: function(fileName, asText, async) {
		const url = this.getResourceUrl(fileName);
		let result;
		if (async) {
			let pendingCount;
			if (!observer) {
				// initialise on first use
				observer = new Observer();
				pendingCount = 0;
			} else {
				pendingCount = observer.subscriberCount(url);
			}
			result = new Promise(function (win) {
				observer.subscribe(win, { group: url });
			});
			if (pendingCount < 1) {
				console.log("Loading async " + url);
				// There is no currently pending request for this URL
				this._fetch(url, asText, async).then(function(theResource) {
					try {
						observer.setFilter(url);
						return observer.notify(theResource);
					} finally {
						observer.reset(url);  // clear all of the subscribers to this URL
					}
				});
			}
		} else {
			console.log("Loading sync " + url);
			result = this._fetch(url, asText, async);
		}
		return result;
	},

	/**
	 * Gets the URL to a resource in the theme resource directory.
	 * @param {string} [fileName] The file name of a resource in the resource directory.
	 * @returns {string} The URL to the resource.
	 */
	getResourceUrl: function(fileName) {
		const config = getConfig();
		let path, cachebuster, url;
		if (config) {
			path = config.resourceBaseUrl;
			cachebuster = config.cachebuster;
		}
		baseUrl = baseUrl || path || "";  // resource/";
		if (fileName) {
			url = baseUrl + fileName + "?" + cachebuster;
		} else {
			url = baseUrl;
		}
		return url;
	},

	/**
	 * Allows other modules to get the cachebuster used by the resource loader.
	 * @returns {String} the cachebuster if present.
	 */
	getCacheBuster: function() {
		const config = getConfig();
		if (config?.cachebuster) {
			return config.cachebuster;
		}
		return null;
	},

	/**
	 * Exposed for testing purposes - this is the function that does the actual ajax call.
	 * @private
	 */
	_fetch: function(url, asText, async) {
		return ajax.loadXmlDoc(url, null, asText, async);
	}
};

function getConfig() {
	return wcconfig.get("wc/loader/resource");
}

export default instance;
