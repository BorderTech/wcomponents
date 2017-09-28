define(["wc/i18n/i18n", "wc/file/getFileSize", "lib/sprintf"], function(i18n, getFileSize, sprintf) {
	var KB = Math.pow(10, 3), /* NOTE: see IEC 80000-13 a kilo-byte is 1000 bytes, NOT 1024 bytes */
		MB = Math.pow(10, 6),
		GB = Math.pow(10, 9),
		ROUND_SIG_FIG = 1;

	/**
	 * Check the file size and return an error message if there is a problem.
	 * @function
	 * @private
	 * @param {Element} element A file input element.
	 * @param {Object} [testObj] The pseudo-file element to pass to test functions.
	 * @return {?string} An error message if there is a problem otherwise falsey.
	 */
	function checkFileSize(element, testObj) {
		var i, message, roundTo, maxFileSizeHR, fileSizeHR, units,
			maxFileSize = parseInt(element.getAttribute("data-wc-maxfilesize"), 10),
			fileIsToBig = function (size) {
				return maxFileSize < size;
			},
			fileSizes = getFileSize(testObj || element);
		if (maxFileSize && fileSizes.length > 0 && fileSizes.some(fileIsToBig)) {
			message = [];
			for (i = 0; i < fileSizes.length; i++) {
				if (fileIsToBig(fileSizes[i])) {
					/* make the units human readable */
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
					message.push(sprintf.sprintf(i18n.get("file_toolarge"), fileSizeHR, maxFileSizeHR, units));
				}
			}
			message = message.join("\n");
		}
		return message;
	}

	/**
	 * Rounds a numerical filesize value to something acceptable to display to the user.
	 * @param {Number} value The number to round.
	 * @returns {Number} The rounded version of the value.
	 */
	function round(value) {
		var intPart = parseInt(value, 10),
			modPart,
			exp;
		if (intPart === value) {
			return value;
		}
		exp = Math.pow(10, ROUND_SIG_FIG);
		modPart = Math.round((value % 1) * exp);
		return intPart + (modPart / exp);
	}

	return {
		check: checkFileSize,
		get: getFileSize
	};
});
