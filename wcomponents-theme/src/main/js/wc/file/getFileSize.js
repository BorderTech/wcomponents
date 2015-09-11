/**
 * @module
* @requires module:wc/has
 */
define(["wc/has"], /** @param has wc/has @ignore */ function(has) {
	"use strict";
	var FILE_SIZE_UNKNOWN = -1;


	/**
	 * IE9 and earlier did not support the file api.
	 * IE10 and all other modern browsers do.
	 * @function
	 * @private
	 * @param {String} filePath The path to a file on the client system.
	 */
	function ieGetFileSize(filePath) {
		var result = FILE_SIZE_UNKNOWN,
			fso,
			file;
		if (filePath) {
			try {
				fso = new window.ActiveXObject("Scripting.FileSystemObject");
				file = fso.getFile(filePath);
				result = file.size;
			}
			catch (ex) {
				/*
				 * You will end up here in Internet Explorer 9 or earlier if the security
				 * settings disable "Script ActiveX controls marked safe for scripting".
				 * In the security settings for the relevant zone click "Custom level..."
				 * and change this setting to "Enable".
				 */
				console.info("Zone security prevented determination of file size.", ex);
			}
		}
		return result;
	}

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
					}
					else {
						next = fileInfo.files[i].size;
						if (!next && next !== 0) {
							next = fileInfo.files[i].fileSize;
						}
					}
					result[i] = next;
				}
			}
			else if (has("activex")) {
				// TODO remove this - it is for legacy Internet Explorer and does not truly fit the API
				result = [ieGetFileSize(fileInfo.value)];  // assumes fileInfo is a fileselector DOM element
			}
			else {
				throw new TypeError("fileInfo does not have a files collection");
			}
		}
		return result;
	}
	return getFileSize;
});
