/**
 * @module
 */
import getMimeType from "wc/file/getMimeType.mjs";

/**
 * Returns true if the given files do not conflict with the file input's "accept" attribute.
 * If the files are given in the second parameter, it will check only those. Otherwise, it will check the files
 * selected in the file input.
 *
 * Based on the HTML5 "File Upload State" spec:
 *
 * * File type matches are case-insensitive.
 * * File types must be either:
 *    * a valid MIME type with no parameters;
 *    * the string audio/*;
 *    * the string video/*; or
 *    * the string image/*,
 *    * A string whose first character is a "." (U+002E) character (Indicates that files with the specified file extension are accepted).
 * * If "accept" is empty then returns true.
 * * If no file is selected then returns true.
 * * If the mimeType AND extension can not be determined then returns true.
 *
 * @function
 * @alias module:wc/file/accepted
 * @requires module:wc/file/getMimeType
 * @param {HTMLInputElement} element A "file input" element.
 * @param fileInfo The array of files to check, inside an object under "files" property
 */
export default function accepted(element, fileInfo) {
	let acceptedType = element.accept;

	if (!acceptedType) return true;

	let acceptedTypes = acceptedType.split(",");

	const checkFileArray = function (/** @type {module:wc/file/getMimeType~fileType} */fileType) {
		let mimeType = fileType.mime || "",
			extension = fileType.ext ? (`.${fileType.ext}`) : "",
			passed = (!mimeType && !extension) ||
				(extension && acceptedType.indexOf(extension) >= 0) ||
				(mimeType && acceptedType.indexOf(mimeType) >= 0);
		if (!passed) {  // maybe there is a case difference OR it's a wildcard mimetype or there is some whitespace to be trimmed?
			for (let i = 0; i < acceptedTypes.length; i++) {
				let next = acceptedTypes[i].toLowerCase();
				next = next.trim();
				if (extension) {
					extension = extension.toLowerCase();
					if (extension === next) {
						passed = true;
						break;
					}
				}
				if (mimeType) {
					mimeType = mimeType.toLowerCase();
					if (mimeType === next) {
						passed = true;
						break;
					} else if (next.indexOf("*") === next.length - 1) {
						next = next.substring(0, next.length - 1);
						if (mimeType.indexOf(next) === 0) {
							passed = true;
							break;
						}
					}
				}
			}
		}
		return passed;
	};

	if (fileInfo) return getMimeType(fileInfo).every(checkFileArray);
	return getMimeType(element).every(checkFileArray);
}
