/**
 * This is now badly named because it returns both mimetype and extension, it should be something like "getFileType"
 * @module
 * @requires module:wc/xml/xpath
 * @requires module:wc/loader/resource
 */
define(["wc/xml/xpath", "wc/loader/resource"],
	/** @param xpath wc/xml/xpath @param loader wc/loader/resource @ignore */
	function(xpath, loader) {
		"use strict";
		var EXTENSION_RE = /\.([a-z0-9]+)$/i;

		/**
		 * @typedef {Object} module:wc/file/getMimeType~fileType
		 * @param {string} mime The MIME type of the file, if it can be determined.
		 * @param {string} ext The file extension (without the dot), if it has one.
		 */

		/**
		 * You are in a browser with a weak file selector API (probably Internet Explorer).
		 * @function
		 * @private
		 * @param {String} extension The file extension.
		 * @returns {?String} The guessed MIME type of the file extension.
		 */
		function getMimeTypeFromXmlMap(extension) {
			var result,
				element;
			if (extension) {
				result = loader.load("mimemap.xml");
				if (result) {
					element = xpath.query("//mime[ext[@name='" + extension + "']]", true, result);
					if (element) {
						result = element.getAttribute("type");
					}
					else {
						console.info("Could not find extension in MIME type file", extension);
						result = null;
					}
				}
			}
			return result;
		}

		/**
		 *
		 * @param {String} fileSpec The name/path of the file to be tested.
		 * @returns {String} The extension of the file or ""
		 */
		function getExtension(fileSpec) {
			var extension;
			if (fileSpec) {
				extension = fileSpec.match(EXTENSION_RE);
			}
			if (extension && extension.length) {
				return extension[1].toLowerCase();
			}
			return "";
		}

		/**
		* Returns the mime type of the selected file.
		*
		* @function
		* @alias module:wc/file/getMimeType
		* @param {module:wc/file/MultiFileUploader~fileInfo} fileInfo The files to check
		* @returns {module:wc/file/getMimeType~fileType[]} The MIME type and extension of each file if it can be determined.
		*/
		function getMimeType(fileInfo) {
			var result, len, i, next, ext;
			if (typeof fileInfo.files !== "undefined") {
				result = [];
				len = fileInfo.files.length;
				for (i = 0; i < len; i++) {
					next = fileInfo.files.item(i);
					ext = getExtension(next.name);
					result[i] = {
						mime: next.type,
						ext: ext
					};
				}
			}
			else {
				ext = getExtension(fileInfo.value);
				result = [{
					mime: getMimeTypeFromXmlMap(ext),
					ext: ext
				}];  // TODO remove this when we no longer need legacy Internet Explorer support.
			}
			return result;
		}

		return getMimeType;
	});
