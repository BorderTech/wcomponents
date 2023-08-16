/**
 * @module
 */
define([], function() {
	"use strict";


	/**
	* Get the size of a file in bytes.
	*
	* @function
	* @alias module:wc/file/getFileSize
	* @param {module:wc/file/MultiFileUploader~fileInfo} fileInfo Information about the file or files to check.
	* @returns {number[]} An array where each item is the file size on bytes of the file at the corresponding index in the files collection.
	* If the file size coud not be determined the size will be -1.
	*/
	function getFileSize(fileInfo) {
		var i, result, len, next;
		if (fileInfo) {
			// assume it's a "file input" dom element
			if (fileInfo.files) {
				result = [];
				len = fileInfo.files.length;
				for (i = 0; i < len; i++) {
					// note: "fileSize" has been deprecated in favor of "size"
					if (fileInfo.files.item) {
						next = fileInfo.files.item(i).size;
						if (!next && next !== 0) {
							next = fileInfo.files.item(i).fileSize;
						}
					} else {
						next = fileInfo.files[i].size;
						if (!next && next !== 0) {
							next = fileInfo.files[i].fileSize;
						}
					}
					result[i] = next;
				}
			} else {
				throw new TypeError("fileInfo does not have a files collection");
			}
		}
		return result;
	}
	return getFileSize;
});
