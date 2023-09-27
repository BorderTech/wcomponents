/**
 * @function
 * @private
 * @param {String} fileSpec The name/path of the file to be tested.
 * @returns {String} The extension of the file or ""
 */
function getExtension(fileSpec) {
	const EXTENSION_RE = /\.([a-z0-9]+)$/i;
	const extension = fileSpec?.match(EXTENSION_RE);
	if (extension?.length) {
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
	return /** @type {module:wc/file/getMimeType~fileType[]} */ Array.from(fileInfo.files).map(
		/**
		 * @param {File} file
		 * @return {{ ext: string, mime: string }}
		 * */
		file => {
			return {
				ext: getExtension(file.name),
				mime: file.type
			};
		});
}

/**
 * This is now badly named because it returns both mimetype and extension, it should be something like "getFileType"
 * @module
 */
export default getMimeType;

/**
 * @typedef {Object} module:wc/file/getMimeType~fileType
 * @param {string} mime The MIME type of the file, if it can be determined.
 * @param {string} ext The file extension (without the dot), if it has one.
 */
