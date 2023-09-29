/**
 * File util module, to perform file operations like converting data to blob,
 * blob to file, fixing file extension.
 *
 * This module defines are a set of mime type to extension mapping, the user could choose
 * to override this map by 'wc/config' using id "wc/file/customMimeToExt".
 * @module
 */

import uid from "wc/dom/uid.mjs";
import getMimeType from "wc/file/getMimeType.mjs";
import wcconfig from "wc/config.mjs";

/**
 * Map of mimetype to extension, used when checking the newly created file is named
 * with the correct extension.
 * TODO replace with 3rd-party util
 **/
const mimeToExt = {
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

export default {
	/**
	 * Converts a generic binary blob to a File blob.
	 * @param {Blob} blob The binary blob.
	 * @param {Object} [config] Attempt to set some of the file properties such as "type", "name".
	 * @returns {File} The File blob.
	 * TODO revisit this
	 */
	blobToFile: function (blob, config) {
		const filePropertyBag = {
			type: blob.type,
			lastModified: new Date()
		};
		let name;
		if (config) {
			if (!filePropertyBag.type) {
				filePropertyBag.type = config.type;
			}
			name = config.name;
		}

		if (!blob.type) {
			// noinspection JSAnnotator
			// @ts-ignore
			blob.type = filePropertyBag.type;
		}
		blob["lastModifiedDate"] = filePropertyBag.lastModified;
		blob["lastModified"] = filePropertyBag.lastModified.getTime();
		if (!name) {
			name = uid();
			// @ts-ignore
			blob.name = name;
			this.fixFileExtension(blob);
		} else {
			// @ts-ignore
			blob.name = name;
		}
		// @ts-ignore
		return blob;
	},

	/**
	 * Ensures that the file name ends with an extension that matches its mime type.
	 * If the file name does not match the mime type then the appropriate extension will be appended.
	 * If there are multiple possible extensions the first will be used.
	 * @param {File|Blob} file The file to check.
	 * @returns {File} The fixed file.
	 * TODO revisit this
	 */
	fixFileExtension: function (file) {
		let metadata = getMimeType({
			files: [file]
		});
		if (metadata?.length) {
			metadata = metadata[0];

			const expectedExtension = this.getMimeToExtMap()[metadata.mime];
			if (metadata.mime && expectedExtension) {
				if (expectedExtension.indexOf(metadata.ext) < 0) {
					// @ts-ignore
					file.name += "." + expectedExtension[0];
				}
			}
		}
		return /** @type File */ (file);
	},

	/**
	 * Getter for mimetype to extension map.
	 * It returns default initialised map, if not overridden by 'wc/config'.
	 * To override, set with id "wc/file/customMimeToExt"
	 * @return {{ string: string[] }}} mime to extension map.
	 */
	getMimeToExtMap: function () {
		// get any configuration overrides.
		const customMimeToExt = wcconfig.get("wc/file/customMimeToExt");
		if (customMimeToExt) {
			return customMimeToExt;
		}
		// @ts-ignore
		return mimeToExt;
	},

	/**
	 * Converts a data url to a binary blob.
	 * @param {string} dataURI textual representation of file.
	 * @returns {Blob} The binary blob.
	 */
	dataURItoBlob: function (dataURI) {
		// convert base64/URLEncoded data component to raw binary data held in a string
		let byteString;
		if (dataURI.split(",")[0].indexOf("base64") >= 0) {
			byteString = atob(dataURI.split(",")[1]);
		} else {
			byteString = unescape(dataURI.split(",")[1]);  // TODO unescape deprecated
		}

		// separate out the mime component
		const mimeString = dataURI.split(",")[0].split(":")[1].split(";")[0];

		// write the bytes of the string to a typed array
		const ia = new window.Uint8Array(byteString.length);
		for (let i = 0; i < byteString.length; i++) {
			ia[i] = byteString.charCodeAt(i);
		}
		// Must use window here to not get the NodeJS Blob in tests
		return new window.Blob([ia], { type: mimeString });
	}
};
