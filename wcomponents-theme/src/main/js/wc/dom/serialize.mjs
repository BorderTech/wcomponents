/**
 * Provides a mechanism to serialize and deserialize a HTML element or NodeList.
 *
 * @module
 */
import isSuccessfulElement from "wc/dom/isSuccessfulElement.mjs";
const NV_SEPARATOR = "=";

const instance = {
	/**
	 * Compares serialized form objects to determine if they are different.
	 * @param {Object.<string, string[]>} stateA The first form state.
	 * @param {Object.<string, string[]>} stateB The second form state.
	 * @returns {boolean} true if the serialized form objects are different.
	 */
	areDifferent: function(stateA, stateB) {
		let different = (!stateA || !stateB);
		if (!different) {
			const keysStateA = Object.keys(stateA);
			const keysStateB = Object.keys(stateB);
			const stateALen = keysStateA.length;
			const stateBLen = keysStateB.length;
			different = stateALen !== stateBLen;
			if (!different && stateBLen > 0) {
				different = !keysStateB.every(key => {
					let same = false;
					const stateBVal = stateB[key];
					const stateAVal = stateA[key];
					if (stateBVal && stateAVal && stateBVal.length === stateAVal.length) {
						stateBVal.sort();
						stateAVal.sort();
						same = stateBVal.every((val, idx) => {
							let result = stateAVal[idx] === val;
							if (!result) {
								console.log("Param has changed at idx", key, idx, stateAVal[idx], val);
							}
							return result;
						});
					} else {
						console.log("Param has changed", key, stateAVal, stateBVal);
					}
					return same;
				});
			} else {
				console.log("Form params changed", keysStateA, keysStateB);
			}
		}
		return different;
	},

	/**
	 * Serialize an HTML Form or NodeList to the format used in a HTTP GET request queryString.
	 *
	 * @function  module:wc/dom/serialize.serialize
	 * @param {NodeList|HTMLElement[]|HTMLFormElement} nodeList The nodes to serialize or an HTML Form.
	 * @param {Boolean} [includeButtons] If true all button name:value pairs are included.
	 * @param {Boolean} [returnAsObject] If true return an object not a string. Each property of the object
	 *    represents name/value pair. The name/value pairs will be URI encoded.
	 * @param {function} [filter] A function that will be passed an element and can veto inclusion in the serialization if it
	 *    returns false.
	 * @returns {string|Object.<string, string[]>}
	 */
	serialize: function (nodeList, includeButtons, returnAsObject, filter) {
		const sb = [];
		const elements = nodeList instanceof HTMLFormElement ? nodeList.elements : nodeList;
		for (let i = 0; i < elements.length; i++) {
			let next = elements[i];
			try {
				if (filter && filter(next) === false) {
					continue;
				}
			} catch (ex) {
				console.error(ex);
			}
			if (next instanceof HTMLElement && isSuccessfulElement(next, includeButtons)) {
				let value;
				if (next instanceof HTMLSelectElement) {
					let items = next.selectedOptions;
					for (let j = 0; j < items.length; ++j) {
						value = getValue(items[j]);
						if (value !== null) {
							// If a control doesn't have a stateB value when the form is submitted, user agents are not required to treat it as a successful control.
							sb.push(encodeName(next.name) + NV_SEPARATOR + encodeURIComponent(value));
						}
					}
				} else {
					value = getValue(/** @type HTMLInputElement */ (next));
					if (value !== null) {
						// If a control doesn't have a stateB value when the form is submitted, user agents are not required to treat it as a successful control.
						sb.push(encodeName(next["name"]) + NV_SEPARATOR + encodeURIComponent(value));
					}
				}
			}
		}
		let result = sb.join("&");  // .replace(/%20/g, "+");  // verdict seems to have changed on this one
		if (returnAsObject) {
			result = deserializeToObject(result);
		}
		return result;
	},

	/**
	 * Deserialize to hidden input fields in the DOM (or simply to an object).
	 * Useful for compressing chunks of non-viewable content such as the content of a dynamic tab.
	 *
	 * @function module:wc/dom/serialize.deserialize
	 * @param {string|Object} input The thing to deserialize, either a String or a serialized object.
	 * @param {Element} [container] The DOM element in which to create the hidden fields, if not provided then
	 *    this step will be skipped, and you probably are just deserializing a string to its object form.
	 * @returns {Object} An object with the same form as that returned by {@link module:wc/dom/serialize#serialise}
	 *    EXCEPT that the name/value pairs will be URI decoded.
	 */
	deserialize: function(input, container) {
		const result = {};
		if (typeof input === "string") {
			input = deserializeToObject(input);
		}
		for (let n in input) {
			let name = decodeName(n);
			let value = input[n];
			if (Array.isArray(value)) {  // should always be this
				result[name] = [];
				for (let i = 0; i < value.length; i++) {
					let nextVal = decodeURIComponent(value[i]);
					result[name].push(nextVal);
					if (container) {
						addToDom(container, name, nextVal);
					}
				}
			} else {
				result[name] = value = decodeURIComponent(value);
				if (container) {
					addToDom(container, name, value);
				}
			}
		}
		return result;
	}
};

/**
 *
 * @param {string} name
 * @return {string}
 */
function encodeName(name) {
	let result = encodeURIComponent(name);
	result = result.replace(/%5B%5D$/, "[]");
	return result;
}

/**
 * @param {string} name
 * @return {string}
 */
function decodeName(name) {
	let result = name.replace(/\[]$/, "%5B%5D");
	result = decodeURIComponent(result);
	return result;
}

/**
 * @param {HTMLInputElement|HTMLOptionElement|HTMLTextAreaElement} element
 * @return {string|null}
 */
function getValue(element) {
	let result = null;
	if (element.matches("input,textarea")) {
		result = element.value;
	} else if (element.matches("option")) {
		result = element.getAttribute("value");
		if (result === null) {  // standards
			result = /** @type HTMLOptionElement */ (element).text;
		}
	}
	return result;
}

/**
 * Convert a serialised form to an object with properties `name:[values]` the value is always an array even
 * if there is only one value for that name.
 *
 * @function
 * @private
 * @param {string} inStr the serialised form (format name=value&name-val2&name3&name4=&name5=val5)
 * @returns {Object}
 */
function deserializeToObject(inStr) {
	const pairSeparator = "&",
		result = {};

	const nvArray = inStr.split(pairSeparator);
	for (let i = 0; i < nvArray.length; ++i) {
		let twoDArray = nvArray[i].split(NV_SEPARATOR);
		let tempName = twoDArray[0];
		let tempVal = (twoDArray.length === 2) ? twoDArray[1] : null;  // if there is no = then the correct value is null
		if (result[tempName]) {
			result[tempName].push(tempVal);
		} else {
			result[tempName] = [tempVal];
		}
	}
	return result;
}

/**
 * helper for deserializing.
 *
 * @function
 * @private
 * @param {Element} container Where the fields are put.
 * @param {String} name The decoded component name.
 * @param {String} value The decoded component values.
 */
function addToDom(container, name, value) {
	const tempField = document.createElement("input");
	tempField.name = name;
	tempField.type = "hidden";
	tempField.value = value;
	container.appendChild(tempField);
}

export default instance;
