define(["wc/loader/resource"],
	function(loader) {
		"use strict";
		var EXTENSION_RE = /\.([a-z0-9]+)$/i,
			extensionMap;

		/**
		 * Build a map of MIME types keyed on file extensions. This is only needed on first use
		 * we then keep the object for faster future reference.
		 *
		 * @function
		 * @private
		 */
		function buildExtMap() {
			var allExtensions,
				o,
				mimeMap;
			if (extensionMap) {
				// already done.
				return;
			}

			// REMEMBER we are only here because we have a rubbish browser.
			// If it doesn't have a file API it won't have Promise and we
			// want to get a direct result to the UI anyway so we load
			// SYNCHRONOUSLY.
			mimeMap = loader.load("mimemap.json", true);

			if (!mimeMap) {
				throw new ReferenceError("Could not load MIME map resource");
			}

			mimeMap = JSON.parse(mimeMap);
			extensionMap = {};
			for (o in mimeMap) {
				if (mimeMap.hasOwnProperty(o)) {
					// get the extension[s] for each MIME type.
					if (!(allExtensions = mimeMap[o])) {
						continue;
					}
					if (Array.isArray(allExtensions)) {
						// this had better always be true
						allExtensions.forEach(function(ext) {
							var extension;
							if (ext && ext.constructor === String) {
								extension = ext.toLowerCase();
								extensionMap[extension] = o;
							}
						});
					} else {
						throw new TypeError("Each MIME type should have an array of String extensions.");
					}
				}
			}
		}

		/**
		 * You are in a browser with a weak file selector API (probably Internet Explorer).
		 * Try to guess the MIME type from the file extension.
		 * @function
		 * @private
		 * @param {String} extension The file extension.
		 * @returns {String[]} The guessed MIME type(s) of the file extension.
		 */
		function guessMimeType(extension) {
			if (extension) {
				if (extension.constructor !== String) {
					throw new TypeError("Lookup by extension without a String extension.");
				}
				// If we have already built the extension map the rest is easy.
				// we build the map on first use which is a bit of a hit
				// but given we are starting a file upload in an old browser
				// the user probably wouldn't notice anyway.

				if (!extensionMap) {
					buildExtMap();
				}

				if (extensionMap) {
					return extensionMap[extension];
				}
			}
			return null;
		}

		/**
		 * @function
		 * @private
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
		* @todo remove the guess this when we no longer need legacy support.
		*/
		function getMimeType(fileInfo) {
			var result, len, i, next, ext;
			if (typeof fileInfo.files !== "undefined") {
				result = [];
				len = fileInfo.files.length;
				for (i = 0; i < len; i++) {
					next = fileInfo.files[i];
					ext = getExtension(next.name);
					result[i] = {
						mime: next.type,
						ext: ext
					};
				}
				return result;
			}
			// TODO remove this when we no longer need legacy support.
			ext = getExtension(fileInfo.value);
			return [{
				mime: guessMimeType(ext),
				ext: ext
			}];
		}

		/**
		 * Publicise guessMimeType for testing use _only_. This is the only way we can test this code in
		 * decent browsers!
		 * @ignore
		 */
		getMimeType._guess = guessMimeType;

		/**
		 * This is now badly named because it returns both mimetype and extension, it should be something like "getFileType"
		 * @module
		 * @requires module:wc/loader/resource
		 */
		return getMimeType;

		/**
		 * @typedef {Object} module:wc/file/getMimeType~fileType
		 * @param {string} mime The MIME type of the file, if it can be determined.
		 * @param {string} ext The file extension (without the dot), if it has one.
		 */
	});
