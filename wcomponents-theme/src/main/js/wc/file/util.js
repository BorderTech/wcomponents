/**
 * File util module, to perform file operations like converting data to blob,
 * blob to file, fixing file extension.
 * This module defines are a set of mime type to extension mapping, the user could choose
 * to override this map by 'wc/config' using id "wc/file/customMimeToExt".
 * @module
 */
define(["wc/dom/uid", "wc/file/getMimeType", "wc/config"], function (uid, getMimeType, wcconfig) {
	"use strict";

	var instance = new FileUtil(),

		/**
		 * Map of mimetype to extension, used when checking the newly created file is named
		 * with the correct extension.
		 **/
		mimeToExt = {
			"image/jpeg": ["jpeg", "jpg"],
			"image/bmp": ["bmp"],
			"image/png": ["png"],
			"image/gif": ["gif"],
			"image/x-icon": ["ico"],
			"image/webp": ["webp"],
			"image/svg+xml": ["svg"],
			"image/tiff": ["tif", "tiff"],
			"text/plain": ["txt", "bas", "c", "h", "log", "bat"],
			"text/csv": ["csv"],
			"text/html": ["html", "htm", "stm"],
			"application/xhtml+xml": ["xhtml"],
			"application/xml": ["xml"],
			"application/msword": ["doc", "dot"],
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document": ["docx"],
			"application/vnd.ms-powerpoint": ["ppt", "pot,", "pps"],
			"application/vnd.openxmlformats-officedocument.presentationml.presentation": ["pptx"],
			"application/rtf": ["rtf"],
			"application/pdf": ["pdf"],
			"application/vnd.ms-excel": ["xls", "xla", "xlc", "xlm", "xlt", "xlw"],
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": ["xlsx"],
			"application/vnd.visio": ["vsd"],
			"application/vnd.oasis.opendocument.presentation": ["odp"],
			"application/vnd.oasis.opendocument.spreadsheet": ["ods"],
			"application/vnd.oasis.opendocument.text": ["odt"]
		};

	/**
	 * Utility to create and modify file.
	 * @constructor
	 * @private
	 */
	function FileUtil() {
		/**
		 * Converts a generic binary blob to a File blob.
		 * @param {Blob} blob The binary blob.
		 * @param {Object} [config] Attempt to set some of the file properties such as "type", "name".
		 * @returns {File} The File blob.
		 */
		this.blobToFile = function (blob, config) {
			var name,
				filePropertyBag = {
					type: blob.type,
					lastModified: new Date()
				};
			if (config) {
				if (!filePropertyBag.type) {
					filePropertyBag.type = config.type;
				}
				name = config.name;
			}

			if (!blob.type) {
				// noinspection JSAnnotator
				blob.type = filePropertyBag.type;
			}
			blob.lastModifiedDate = filePropertyBag.lastModified;
			blob.lastModified = filePropertyBag.lastModified.getTime();
			if (!name) {
				name = uid();
				blob.name = name;
				instance.fixFileExtension(blob);
			} else {
				blob.name = name;
			}
			return blob;
		};

		/**
		 * Ensures that the file name ends with an extension that matches its mime type.
		 * If the file name does not match the mime type then the appropriate extension will be appended.
		 * If there are multiple possible extensions the first will be used.
		 * @param {Blob} file The file to check.
		 * @returns {Blob} The fixed file.
		 */
		this.fixFileExtension = function (file) {
			var expectedExtension,
				metadata = getMimeType({
					files: [file]
				});
			if (metadata && metadata.length) {
				metadata = metadata[0];

				expectedExtension = instance.getMimeToExtMap()[metadata.mime];
				if (metadata.mime && expectedExtension) {
					if (expectedExtension.indexOf(metadata.ext) < 0) {
						file.name += "." + expectedExtension[0];
					}
				}
			}
			return file;
		};

		/**
		 * Getter for mimetype to extension map.
		 * It returns default initialised map, if not overridden by 'wc/config'.
		 * To override, set with id "wc/file/customMimeToExt"
		 * @return {mimeToExt} mime to extension map.
		 */
		this.getMimeToExtMap = function () {
			// get any configuration overrides.
			var customMimeToExt = wcconfig.get("wc/file/customMimeToExt");
			if (customMimeToExt) {
				return customMimeToExt;
			}
			return mimeToExt;
		};

		/**
		 * Converts a data url to a binary blob.
		 * @param {string} dataURI textual representation of file.
		 * @returns {Blob} The binary blob.
		 */
		this.dataURItoBlob = function (dataURI) {
			// convert base64/URLEncoded data component to raw binary data held in a string
			var byteString, mimeString, ia, i;
			if (dataURI.split(",")[0].indexOf("base64") >= 0) {
				byteString = atob(dataURI.split(",")[1]);
			} else {
				byteString = unescape(dataURI.split(",")[1]);
			}

			// separate out the mime component
			mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];

			// write the bytes of the string to a typed array
			ia = new window.Uint8Array(byteString.length);
			for (i = 0; i < byteString.length; i++) {
				ia[i] = byteString.charCodeAt(i);
			}

			return new Blob([ia], {type: mimeString});
		};
	}

	return instance;

});
