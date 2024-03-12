import wcconfig from "wc/config.mjs";

let baseUrl;

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
				url += `?${cachebuster}`;
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
	},

	getUrlFromImportMap: function(name) {
		const importMaps = /** @type {HTMLScriptElement[]} */(Array.from(document.querySelectorAll("script[type='importmap']")));
		const importMap = importMaps.map(element => JSON.parse(element.text)).find(next => {
			return next["imports"][name];
		});
		return importMap ? importMap["imports"][name] : null;
	}

};

function getConfig() {
	return wcconfig.get("wc/loader/resource");
}

export default instance;
