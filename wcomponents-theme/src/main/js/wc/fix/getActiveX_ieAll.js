/**
 * @module
 * @private
 */
define(function() {
	"use strict";
	/**
	 * Get the supported activeX version, if any. Note, this is not strictly speaking a "fix", but it only ever
	 * needs to be loaded by IE.
	 *
	 * @function
	 * @alias module:wc/fix/getActiveX_ieAll
	 * @param {string} engine The name of the activeX engine
	 * @param {Array} [versions] Version numbers to test for IE support - tested in the
	 *    order they are provided, so if the first item in the Array is supported no further
	 *    versions will be tested. You get the "no version" version tested for free.
	 *
	 * @returns {?Object} {
	 *     version: The first ActiveX version supported by this version of IE,
	 *     engine: The engine string with the version number appended
	 * }
	 * @example require("wc/fix/getActiveX_ieAll")("MSXML2.FreeThreadedDomDocument", ["6.0", "3.0"]);
	 */
	return function (engine, versions) {
		var i = 0,
			nextVersion,
			nextEngine,
			result;
		do {
			nextVersion = versions ? versions[i] : "";
			nextEngine = nextVersion ? (engine + "." + nextVersion) : engine;
			try {
				/*eslint-disable */
				new window.ActiveXObject(nextEngine);
				/*eslint-enable */
				result = {
					version: nextVersion,
					engine: nextEngine
				};
				break;
			}
			catch (ex) {
				// try again
			}
		}
		while (versions && versions.length > i++);
		return result || null;
	};
});
