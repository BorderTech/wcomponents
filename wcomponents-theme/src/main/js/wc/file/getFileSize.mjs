/**
 * @module
 *
* Get the size of a file in bytes.
*
* @function
* @alias module:wc/file/getFileSize
* @param {module:wc/file/MultiFileUploader~fileInfo} fileInfo Information about the file or files to check.
* @returns {number[]} An array where each item is the file size on bytes of the file at the corresponding index in the files collection.
* If the file size cou/d not be determined the size will be -1.
*/
function getFileSize(fileInfo) {
	const result = [];
	if (fileInfo) {
		// assume it's a "file input" dom element
		if (fileInfo.files) {
			for (let i = 0; i < fileInfo.files.length; i++) {
				let next = (fileInfo.files.item) ? fileInfo.files.item(i) : fileInfo.files[i];
				// note: "fileSize" has been deprecated in favor of "size"
				result[i] = (next.size || next.size === 0) ? next.size : next["fileSize"];
			}
		} else {
			throw new TypeError("fileInfo does not have a files collection");
		}
	}
	return result;
}
export default getFileSize;
