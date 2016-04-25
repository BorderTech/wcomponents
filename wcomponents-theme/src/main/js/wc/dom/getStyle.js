/**
 * @module
 * @requires module:wc/dom/color
 */
define(["wc/dom/color"], /** @param color wc/dom/color @ignore */ function(color) {
	"use strict";
	/**
	 * Get the current value of the named CSS rule. It is best practice to use the CSS naming convention rather
	 * than the JS one. So pass this: "font-style" instead of this "fontStyle", but it should work either way as we
	 * have added noob cancellation features.
	 *
	 * Note the return value will not include units of measurement, for example "0" not "0px". If you want units of
	 * measurement then set includeUnits to true.
	 *
	 * Colors will be returned as RGB (or RGBA) objects, for example {r:255 g:255 b:255} OR {r:255 g:255 b:255, a:0}
	 *
	 * @function module:wc/dom/getStyle
	 * @param {Element} element The element to test.
	 * @param {String} cssRule The cssRule we want to find the style of (eg, 'color', 'display', 'position')
	 * @param {Boolean} [includeUnits] If true include the unit part of the CSS response (eg 2em) otherwise strip it
	 *    (eg 2).
	 * @param {Boolean} [notAColor] A shorthand to cut off further testing if we KNOW when calling this function
	 *    that the style property we are after is definitely NOT a color.
	 * @returns {?(String|number)} The value of the CSS rule if found (even if "") If the cssRule is not found or
	 *    cannot be determined return null.
	 */
	function getStyle(element, cssRule, includeUnits, notAColor) {
		var re = /\-(\w)/g,
			testRe = /^\d+[A-Za-z]+$/,
			unitRe = /[A-Za-z]+$/g,
			jsStyleNameRe = /^([a-z]+)([A-Z])([a-z]+)$/,
			style,
			result = null,
			defaultView = document.defaultView;

		function replacer(string, p1) {
			return p1.toUpperCase();
		}

		if (cssRule) {
			if (defaultView && defaultView.getComputedStyle) { // DOM Level 2 Style: getComputedStyle
				if (jsStyleNameRe.test(cssRule)) {
					cssRule = (cssRule.replace(jsStyleNameRe, "$1-$2$3")).toLowerCase();
				}
				result = defaultView.getComputedStyle(element, "").getPropertyValue(cssRule);
			}
			else if (element.currentStyle) {  // IE
				result = element.currentStyle[cssRule.replace(re, replacer)];
			}

			if (result && result.constructor === String && isNaN(result)) {
				if (testRe.test(result)) {
					if (!includeUnits) {
						result = result.replace(unitRe, "");
					}
				}
				else if (!notAColor) {
					if (result === "transparent" || result === "rgba(0, 0, 0, 0)") {  // chromeframe returns an rgb string for transparent
						result = {r: 255, g: 255, b: 255, a: 0};
					}
					else if ((style = color.rgb2hex(result))) {  // is it an rgb string? eg "rgb(255,0,0)"
						// now we have a hex value of style, eg #ff0000, convert to RGB
						result = color.hex2rgb(style);
					}
					else if (color.isHex(result)) {  // is it a hex string?
						result = color.hex2rgb(result);  // convert hex to RGB object
					}
					else if ((style = color.getLiteral(result))) {  // is it a color literal? eg "red"
						// now we have a hex value of style, eg #ff0000, convert to RGB
						result = color.hex2rgb(style);
					}
				}
			}
		}
		return result;
	}
	return getStyle;
});
