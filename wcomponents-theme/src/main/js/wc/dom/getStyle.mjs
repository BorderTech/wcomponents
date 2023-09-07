import color from "wc/dom/color";

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
 * @alias module:wc/dom/getStyle
 * @param {Element} element The element to test.
 * @param {String} cssRule The cssRule we want to find the style of (eg, 'color', 'display', 'position')
 * @param {Boolean} [includeUnits] If true include the unit part of the CSS response (eg 2em) otherwise strip it (eg 2).
 * @param {Boolean} [notAColor] A shorthand to cut off further testing if we KNOW when calling this function that the style property we are after
 *    is definitely NOT a color.
 * @returns {string|number|null} The value of the CSS rule if found (even if "") If the cssRule is not found or cannot be determined return null.
 */
function getStyle(element, cssRule, includeUnits, notAColor) {
	let result = null;

	if (cssRule) {
		const jsStyleNameRe = /^([a-z]+)([A-Z])([a-z]+)$/;

		if (jsStyleNameRe.test(cssRule)) {
			cssRule = (cssRule.replace(jsStyleNameRe, "$1-$2$3")).toLowerCase();
		}

		result = document.defaultView.getComputedStyle(element, "").getPropertyValue(cssRule);

		if (result?.constructor === String && isNaN(result)) {
			const testRe = /^\d+[A-Za-z]+$/;
			let style;
			if (testRe.test(result)) {
				if (!includeUnits) {
					const unitRe = /[A-Za-z]+$/g;
					result = result.replace(unitRe, "");
				}
			} else if (!notAColor) {
				if (result === "transparent" || result === "rgba(0, 0, 0, 0)") {  // chromeframe returns an rgb string for transparent
					result = { r: 255, g: 255, b: 255, a: 0 };
				} else if ((style = color.rgb2hex(result))) {  // is it an rgb string? eg "rgb(255,0,0)"
					// now we have a hex value of style, eg #ff0000, convert to RGB
					result = color.hex2rgb(style);
				} else if (color.isHex(result)) {  // is it a hex string?
					result = color.hex2rgb(result);  // convert hex to RGB object
				} else if ((style = color.getLiteral(result))) {  // is it a color literal? eg "red"
					// now we have a hex value of style, eg #ff0000, convert to RGB
					result = color.hex2rgb(style);
				}
			}
		}
	}
	return result;
}

export default getStyle;
