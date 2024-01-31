import wcconfig from "wc/config.mjs";

let baseUrl, observer;

const instance = {

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
		// using ../ works for URL schemes without `origin` (e.g. `file`)
		baseUrl = baseUrl || path || new URL("../../../resource/", import.meta.url.toString()) || "";  // resource/";
		if (fileName) {
			url = baseUrl + fileName;
			if (cachebuster) {
				url += `?${cachebuster}`
			}
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
	}
};

function getConfig() {
	return wcconfig.get("wc/loader/resource");
}

export default instance;
