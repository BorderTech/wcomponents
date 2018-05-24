/*
 * NOTE: this is only used in wc/dom/getStyle and could potentially be merged with that module.
 */
define(["wc/loader/resource"],
	function(loader) {
		"use strict";
		var FILE_NAME = "colormap.json",
			lameMap;

		loader.preload(FILE_NAME);
		/**
		 * Models color manipulation and conversion.
		 *
		 * @constructor
		 * @alias module:wc/dom/color~Color
		 * @private
		 */
		function Color() {
			var hex2rgbCache = {},
				literal2hexCache = {},
				HEX_HASH_RE = /^#/,
				RGB_RE = /^rgb/,
				HEX3_RE = /^#?[a-fA-F0-9]{3}$/,
				HEX6_RE = /^#?[a-fA-F0-9]{6}$/;

			/**
			 * Attempt to get a String literal colour representation from a list. Used if we cannot get it any other way.
			 *
			 * @function
			 * @private
			 * @param {String} c color literal
			 * @returns {String} The hex string for the given color.
			 */
			function getLiteralFromMap(c) {
				var colormap;
				if (c) {
					if (!lameMap) {
						// This is a synchronous file load.
						colormap = loader.load(FILE_NAME, true);
						if (colormap) {
							lameMap = JSON.parse(colormap);
						}
					}
					if (lameMap) {
						return lameMap[c];
					}
				}
				return null;
			}

			/**
			 * Converts a HEX color representation to RGB format. This conversion routine caches its results on the (so
			 * far accurate) assumption that only a small number of colors are in use and therefore the same hex colors
			 * will be passed to this function over and over.
			 *
			 * @example hex2rgb(#FFFFFF") returns {r:255 g:255 b:255}
			 * @function
			 * @alias module:wc/dom/color.hex2rgb
			 * @param {Stering} hex The colour as a hex string.
			 * @throws {TypeError} Throws a type error if hex is not a string.
			 * @returns {Object} The color as an object with properties r:red, g:green and b:blue.
			 */
			this.hex2rgb = function (hex) {
				var result,
					r,
					g,
					b;
				if (hex && hex.constructor === String) {
					result = hex2rgbCache[hex];  // check cache to see if we already know the answer
					if (!result) {  // if we don't have it in cache calculate it now
						result = hex2rgbCache[hex] = {};
						hex = hex.trim();
						hex = hex.replace(HEX_HASH_RE, "");
						if (hex.length < 6) {
							hex = convert3digitHexTo6(hex);
						}
						r = parseInt(hex.substring(0, 2), 16);
						g = parseInt(hex.substring(2, 4), 16);
						b = parseInt(hex.substring(4, 6), 16);
						result.r = r;
						result.g = g;
						result.b = b;
					}
				} else {
					throw new TypeError("hex can not be null / must be an instance of String");
				}
				return result;
			};

			/**
			 * Get a String hex colour definition of a colour literal. This method is for browsers that support
			 * getComputedStyle (eg not Internet Explorer 8)
			 *
			 * @function
			 * @alias module:wc/dom/color.getLiteral
			 * @param {String} c color literal.
			 * @returns {String} The hex string for the given color.
			 */
			this.getLiteral = function(c) {
				var tmp,
					style,
					color,
					result = literal2hexCache[c];
				if (result === undefined) {
					if (typeof window.getComputedStyle !== "undefined") {
						try {
							tmp = document.createElement("div");
							tmp.style.display = "none";
							tmp.style.color = c;
							document.body.appendChild(tmp);
							style = window.getComputedStyle(tmp, null);
							color = style.getPropertyValue("color");
							if (color) {
								result = this.rgb2hex(color);
							}
						} finally {
							document.body.removeChild(tmp);
						}
					} else {
						// really? We really want to support this?
						result = getLiteralFromMap(c);
					}
					literal2hexCache[c] = result || null;  // cache result OR flag not to search again
				}
				return result;
			};

			/**
			 * Convert a rbg colour string (CSS format) to a hex string.
			 *
			 * @todo rewrite to use red green blue (use getPropertyCSSValue().getRGBColorValue() instead of getPropertyValue in getStyle)
			 *
			 * @function
			 * @alias module:wc/dom/color.rgb2hex
			 * @param {(String|Array<Number>|Object)} rgb String in the format "rgb(244,244,244)" or an array of
			 *    red/green/blue values, e.g. [244, 244, 244] or an object with "r", "g" and "b" properties
			 *    corresponding to red, green and blue values, e.g. {r:244, g:244, b:244}
			 * @todo rewrite to use red green blue
			 * @returns {String} The colour as a hex string.
			 */
			this.rgb2hex = function (rgb) {
				var hex,
					hexNo,
					arrRgb,
					i;
				if (rgb) {
					hex = ["#"];
					if (rgb.constructor === String && RGB_RE.test(rgb)) {
						arrRgb = rgb.match(/\d+/g);
					} else if (Array.isArray(rgb)) {
						arrRgb = rgb;
					} else if (rgb.constructor === Object) {
						arrRgb = [rgb.r, rgb.g, rgb.b];
					}
					if (arrRgb) {
						for (i = 0; i < arrRgb.length; i++) {
							hexNo = parseInt(arrRgb[i]).toString(16);
							hex[hex.length] = (hexNo.length === 1) ? (hexNo + hexNo) : hexNo;
						}
					}
				}
				return ((hex && hex.length > 1) ? hex.join("") : null);
			};

			/**
			 * Convert a three digit hex string to a 6 digit hex string. The return string has a HASH if the input has a HASH.
			 *
			 * @function
			 * @private
			 * @param {String} hex 3 digit hex string.
			 * @returns {String} 6 digit version of hex arg.
			 */
			function convert3digitHexTo6(hex) {
				var result,
					i,
					next;
				hex = hex.trim();
				if (HEX3_RE.test(hex)) {
					if (HEX_HASH_RE.test(hex)) {
						hex = hex.replace(HEX_HASH_RE, "");
						result = ["#"];
					} else {
						result = [];
					}
					for (i = 0; i < 3; i++) {
						next = hex.charAt(i);
						result[result.length] = next;
						result[result.length] = next;
					}
					return result.join("");
				}
				console.warn(hex, " is not a 3 digit hex string");
				return hex;
			}

			/**
			 * Is this a hex string? Includes 3 digit and 6 digit hex strings with or without a hash.
			 *
			 * @function
			 * @alias module:wc/dom/color.isHex
			 * @param {*} s The arg to test.
			 * @returns {Boolean} true if the arg is a hex string, otherwise false.
			 */
			this.isHex = function (s) {
				var result = false;
				if (s && s.constructor === String) {
					if (HEX6_RE.test(s) || HEX3_RE.test(s)) {
						result = true;
					}
				}
				return result;
			};
		}

		/*
		 * Provides colour conversion and manipulation tools.
		 *
		 * @module
		 * @requires module:wc/loader/resource
		 */
		return new Color();
	});
