/*
 * NOTE: this is only used in wc/dom/getStyle and could potentially be merged with that module.
 */

const hex2rgbCache = {},
	literal2hexCache = {},
	HEX_HASH_RE = /^#/,
	HEX3_RE = /^#?([a-fA-F0-9]{3})$/,
	HEX6_RE = /^#?([a-fA-F0-9]{6})$/;

/**
 * Models color manipulation and conversion.
 */
const instance = {
	/**
	 * Is this a hex string? Includes 3 digit and 6 digit hex strings with or without a hash.
	 *
	 * @function
	 * @alias module:wc/dom/color.isHex
	 * @param {string} s The arg to test.
	 * @returns {Boolean} true if the arg is a hex string, otherwise false.
	 */
	isHex: function (s) {
		return (typeof s === "string" && (HEX6_RE.test(s) || HEX3_RE.test(s)));
	},

	/**
	 * Converts a HEX color representation to RGB format. This conversion routine caches its results on the (so
	 * far accurate) assumption that only a small number of colors are in use and therefore the same hex colors
	 * will be passed to this function over and over.
	 *
	 * @example hex2rgb("#FFFFFF") returns {r:255 g:255 b:255}
	 * @function
	 * @alias module:wc/dom/color.hex2rgb
	 * @param {string} hex The colour as a hex string.
	 * @throws {TypeError} Throws a type error if hex is not a string.
	 * @returns {{r: number, g: number, b: number}} The color as an object with properties r:red, g:green and b:blue.
	 */
	hex2rgb: function (hex) {
		let result;
		if (hex && typeof hex === "string") {
			result = hex2rgbCache[hex];  // check cache to see if we already know the answer
			if (!result) {  // if we don't have it in cache calculate it now
				result = hex2rgbCache[hex] = {};
				hex = hex.trim();
				hex = hex.replace(HEX_HASH_RE, "");
				if (hex.length < 6) {
					hex = convert3digitHexTo6(hex);
				}
				result["r"] = parseInt(hex.substring(0, 2), 16);
				result["g"] = parseInt(hex.substring(2, 4), 16);
				result["b"] = parseInt(hex.substring(4, 6), 16);
			}
		} else {
			throw new TypeError("hex can not be null / must be an instance of String");
		}
		return result;
	},

	/**
	 * Get a String hex colour definition of a colour literal. This method is for browsers that support
	 * getComputedStyle (e.g. not Internet Explorer 8)
	 *
	 * @function
	 * @alias module:wc/dom/color.getLiteral
	 * @param {string} c color literal.
	 * @returns {string} The hex string for the given color.
	 */
	getLiteral: function(c) {
		let result = literal2hexCache[c];
		if (!result) {
			let tmp = document.createElement("div");
			try {
				tmp.style.display = "none";
				tmp.style.color = c;
				document.body.appendChild(tmp);
				const style = getComputedStyle(tmp, null);
				const color = style.getPropertyValue("color");
				if (color) {
					result = this.rgb2hex(color);
				}
			} finally {
				document.body.removeChild(tmp);
			}
			literal2hexCache[c] = result || (result = null);  // cache result OR flag not to search again
		}
		return result;
	},

	/**
	 * Convert a rbg colour string (CSS format) to a hex string.
	 *
	 * @todo rewrite to use red green blue (use getPropertyCSSValue().getRGBColorValue() instead of getPropertyValue in getStyle)
	 *
	 * @function
	 * @alias module:wc/dom/color.rgb2hex
	 * @param {(string|Array<Number>|{r: string|number, g: string|number, b: string|number})} rgb String in the format "rgb(244,244,244)" or an array of
	 *    red/green/blue values, e.g. [244, 244, 244] or an object with "r", "g" and "b" properties
	 *    corresponding to red, green and blue values, e.g. {r:244, g:244, b:244}
	 * @todo rewrite to use red green blue
	 * @returns {string} The colour as a hex string.
	 */
	rgb2hex: function (rgb) {
		let hex;
		if (rgb) {
			hex = ["#"];
			let arrRgb;
			if (typeof rgb === "string" && rgb.startsWith("rgb")) {
				arrRgb = rgb.match(/\d+/g);
			} else if (Array.isArray(rgb)) {
				arrRgb = rgb;
			} else if (rgb.hasOwnProperty("r")) {
				arrRgb = [rgb["r"], rgb["g"], rgb["b"]];
			}
			if (arrRgb) {
				for (const num of arrRgb) {
					const hexNo = parseInt(num).toString(16);
					hex[hex.length] = (hexNo.length === 1) ? (hexNo + hexNo) : hexNo;
				}
			}
		}
		return ((hex && hex.length > 1) ? hex.join("") : null);
	}
};

/**
 * Convert a three digit hex string to a 6 digit hex string.
 * The return string has a HASH if the input has a HASH.
 *
 * @function
 * @private
 * @param {string} hex 3 digit hex string.
 * @returns {string} 6 digit version of hex arg.
 */
function convert3digitHexTo6(hex) {
	let matches = HEX3_RE.exec(hex);
	if (matches) {
		let result = Array.from(matches[1], n => n + n).join('');
		return matches[0].length === 4 ? `#${result}` : result;
	}
	return hex;
}

export default instance;
