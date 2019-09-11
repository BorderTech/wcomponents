define(["wc/has"], function (has) {
	"use strict";
	// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/repeat
	if (!has("string-repeat")) {
		String.prototype.repeat = repeat;
	}

	/**
	 * The repeat() method constructs and returns a new string which contains the specified number of copies of
	 * the string on which it was called, concatenated together.
	 * @param count An integer between 0 and +∞: [0, +∞), indicating the number of times to repeat the string in the
	 *    newly-created string that is to be returned.
	 * @returns {String} A new string containing the specified number of copies of the given string.
	 */
	function repeat(count) {
		var maxCount, str;
		if (this === null || this === undefined)
			throw new TypeError("can't convert " + this + " to object");

		str = "" + this;
		// To convert string to integer.
		count = +count;
		// Check NaN
		if (count !== count)
			count = 0;

		if (count < 0)
			throw new RangeError("repeat count must be non-negative");

		if (count === Infinity)
			throw new RangeError("repeat count must be less than infinity");

		count = Math.floor(count);
		if (str.length === 0 || count === 0)
			return "";

		// Ensuring count is a 31-bit integer allows us to heavily optimize the
		// main part. But anyway, most current (August 2014) browsers can't handle
		// strings 1 << 28 chars or longer, so:
		if (str.length * count >= 1 << 28)
			throw new RangeError("repeat count must not overflow maximum string size");

		maxCount = str.length * count;
		count = Math.floor(Math.log(count) / Math.log(2));
		while (count) {
			str += str;
			count--;
		}
		str += str.substring(0, maxCount - str.length);
		return str;
	}
});
