/**
 * Provides a method for asciifying strings that contain non-ascii characters.
 *
 * <p>For example:</p>
 * <ul>
 * <li>"août" may become "aout"</li>
 * <li>"März" may become "Maerz"</li>
 * </ul>
 *
 * <p>Default mappings for common non-ascii characters are provided "out of the box", however they may be overriden
 * on a locale by locale basis using the i18n mechanism in WComponents.</p>
 *
 * <p>To override the default behavior you must provide a JSON "character map" as specified below:</p>
 * <ul>
 * <li>each property key contains one or more non-ascii characters which will be asciified to the property value</li>
 * <li>must be a valid JSON object (not an object literal, a proper JSON object, i.e. quote the keys)</li>
 * <li>only contains strings, so zero would be "0" not 0</li>
 * <li>there is no need to provide uppercase and lowercase; the case will be taken care of automagically</li>
 * </ul>
 *
 * <p>For example the ascii map may look like this:<br/>
 * <code>{"àâä": "a","èéêë": "e","îï": "i","ô": "o","ùûü": "u", "ç": "c", "æ": "ae","œ": "oe"}</code></p>
 *
 * <p>It is strongly suggested that you escape all unicode characters in the character map to ensure portability. The
 * example above then becomes:<br>
 * <code>{"\\u00e0\\u00e2\\u00e4": "a","\\u00e8\\u00e9\\u00ea\\u00eb": "e","\\u00ee\\u00ef": "i","\\u00f4": "o","\\u00f9\\u00fb\\u00fc": "u","\\u00e7": "c","\\u00e6": "ae","\\u0153": "oe"}</code></p>
 *
 *
 * @example asciify("août");  // returns "aout"
 * asciify("März");  // returns "Maerz"
 * asciify("Latin1");  // returns "Latin1" so why would you bother?
 *
 * @module
 * @author Rick Brown
 */

import i18n from "wc/i18n/i18n.mjs";

const lookupProp = "asciimap",
	cache = {};

let asciiMap;

/**
 * @function
 * @public
 * @alias module:wc/i18n/asciify
 * @param {String} s The string to asciify.
 * @returns {String} The asciified version.
 * @example asciify("café dude"); // returns "cafe dude" using the default character map
 */
function asciify(s) {
	let result = "";
	if (s) {
		for (let i = 0; i < s.length; i++) {
			let ascii = null;
			let next = s[i];
			if (next.charCodeAt(0) > 128) {
				ascii = cache[next] || (cache[next] = uniToAscii(next));
			}
			result += ascii || next;  // zero should not happen, the map should not contain numbers, it should contain strings
		}
	} else {
		result = s;
	}
	return result;
}

/**
 * Load the ascii map from the i18n properties for this locale and
 * initialise it ready for use.
 * @function getAsciiMap
 * @private
 * @returns {Object} The ascii map.
 */
function getAsciiMap() {
	const result = {};
	let map = i18n.get(lookupProp);
	try {
		if (map) {
			map = window.JSON.parse(map);
			// re-stringifying gives a view of unescaped unicode chars (it's auto-stripped in minified version).
			console.log("Got ascii map:", window.JSON.stringify(map));
			const unicodeChars = Object.keys(map);
			for (let i = 0; i < unicodeChars.length; i++) {
				let nextUni = unicodeChars[i];
				// make sure it's not an empty string or whitespace, this is untrusted input
				if (nextUni && (nextUni = nextUni.trim())) {
					let nextAscii = map[nextUni];
					// make sure it's not an empty string or whitespace, this is untrusted input
					if (nextAscii && (nextAscii = nextAscii.trim())) {
						result[nextUni.toLocaleUpperCase()] = nextAscii.toUpperCase();
						result[nextUni.toLocaleLowerCase()] = nextAscii.toLowerCase();
					}
				}
			}
			if (Object.freeze) {
				Object.freeze(result);
			}
		} else {
			console.warn("Could not find ascii map ", lookupProp);
		}
	} catch (ex) {
		// asciifying stuff is not likely to be mission-critical, so we'll consume errors here and warn
		console.warn(ex);
	}
	return result;
}

/**
 * Convert a unicode character to an asciified version, if possible.
 * @funtion uniToAscii
 * @private
 * @param {String} character A non-ascii character.
 * @returns {String} The asciified version or null if not found.
 */
function uniToAscii(character) {
	let result = null;
	const map = (asciiMap || (asciiMap = getAsciiMap())),
		unichars = Object.keys(map);
	for (let i = 0; i < unichars.length; i++) {
		let next = unichars[i];
		if (next?.indexOf(character) >= 0) {
			result = map[next];
		}
	}
	return result;
}
export default asciify;
