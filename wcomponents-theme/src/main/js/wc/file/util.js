define(["wc/dom/uid", "wc/file/getMimeType", "wc/config"], function (uid, getMimeType, wcconfig) {
	"use strict";

	var instance = new FileUtil();

		/**
		 * Used when checking the newly created file is named with the correct extension.
		 * If it does not already match any in the array then the extension at index zero will be appended.
		 **/
	FileUtil.prototype.mimeToExt = {
		"image/jpeg": ["jpeg", "jpg"],
		"image/bmp": ["bmp"],
		"image/png": ["png"],
		"image/gif": ["gif"],
		"image/x-icon": ["ico"],
		"image/webp": ["webp"],
		"image/svg+xml": ["svg"],
		"image/tiff": ["tif", "tiff"],
		"text/plain": ["bas", "c", "h", "txt", "log", "bat"],
		"text/tab-separated-values": ["tsv"],
		"text/richtext": ["rtx"],
		"text/html": ["htm", "html", "stm"],
		"text/xml": ["xml"],
		"application/msword": ["doc", "dot"],
		"application/vnd.ms-powerpoint": ["pot,", "pps", "ppt"],
		"application/rtf": ["rtf"],
		"application/pdf": ["pdf"],
		"application/vnd.ms-excel": ["xla", "xlc", "xlm", "xls", "xlt", "xlw"]
	};

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
		 * If there are multiple possible extensions the the first will be used.
		 * @param {Blob} file The file to check.
		 */
		this.fixFileExtension = function (file) {
			var expectedExtension,
				info = getMimeType({
					files: [file]
				});
			if (info && info.length) {
				info = info[0];

				// get any configuration overrides.
				var myMimeTypes = wcconfig.get("wc/file/myMimeTypes");
				if (myMimeTypes) {
					instance.mimeToExt = myMimeTypes;
				}

				expectedExtension = instance.mimeToExt[info.mime];
				if (info.mime && expectedExtension) {
					if (expectedExtension.indexOf(info.ext) < 0) {
						file.name += "." + expectedExtension[0];
					}
				}

			}
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
