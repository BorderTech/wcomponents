import i18n from "wc/i18n/i18n";
import getFileSize from "wc/file/getFileSize";
const instance = {
		check: checkFileSize,
		get: getFileSize,
		getMax: getMax
	},
	KB = Math.pow(10, 3),  /* NOTE: see IEC 80000-13 a kilobyte is 1000 bytes, NOT 1024 bytes */
	MB = Math.pow(10, 6),
	GB = Math.pow(10, 9),
	ROUND_SIG_FIG = 1;

/**
 * Check the file size and return an error message if there is a problem.
 * @function
 * @private
 * @param {Object} args File size args, as shown below:
 * @param {Element} args.element A file input element.
 * @param {Object} [args.testObj] The pseudo-file element to pass to test functions.
 *@param {string} [args.msgId] The i18n message ID, if not provided the default "file_toolarge" is used.
 * @returns {?string} An error message if there is a problem otherwise falsy.
 */
function checkFileSize(args) {
	const message = [];
	const element = args.element,
		maxFileSize = instance.getMax(element),
		msgId = args.msgId || "file_toolarge",
		fileIsToBig = function (size) {
			return maxFileSize < size;
		},
		fileSizes = instance.get(args.testObj || element);
	if (maxFileSize && fileSizes.length > 0 && fileSizes.some(fileIsToBig)) {
		for (let i = 0; i < fileSizes.length; i++) {
			if (fileIsToBig(fileSizes[i])) {
				/* make the units human-readable */
				let roundTo, units, maxFileSizeHR, fileSizeHR;
				if (maxFileSize >= GB) {
					roundTo = GB;
					units = i18n.get("file_size_gb");
				} else if (maxFileSize >= MB) {
					roundTo = MB;
					units = i18n.get("file_size_mb");
				} else if (maxFileSize >= KB) {
					roundTo = KB;
					units = i18n.get("file_size_kb");
				}

				if (roundTo) {
					maxFileSizeHR = round(maxFileSize / roundTo);
					fileSizeHR = round(fileSizes[i] / roundTo);
				} else {
					maxFileSizeHR = maxFileSize;
					fileSizeHR = fileSizes[i];
					units = i18n.get("file_size_");
				}
				let nextMessage = i18n.get(msgId, fileSizeHR, maxFileSizeHR, units);
				message.push(nextMessage);
			}
		}
	}
	return message.join("\n");
}

/**
 * Returns the maximum file size accepted.
 * @param {Element} element The file selector being used to upload the file.
 * @returns {number} The maximum file size accepted, in bytes.
 * Falsy (including zero) means unconstrained.
 */
function getMax(element) {
	if (element) {
		return parseInt(element.getAttribute("data-wc-maxfilesize")) || 0;
	}
	return 0;
}

/**
 * Rounds a numerical filesize value to something acceptable to display to the user.
 * @param {Number} value The number to round.
 * @returns {Number} The rounded version of the value.
 */
function round(value) {
	const intPart = typeof value === "string" ? parseInt(value, 10) : value;
	if (intPart === value) {
		return value;
	}
	const exp = Math.pow(10, ROUND_SIG_FIG);
	const modPart = Math.round((value % 1) * exp);
	return intPart + (modPart / exp);
}

export default instance;
