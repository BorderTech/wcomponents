/**
 * Provides a mechanism to serialize and deserialize a HTML element or NodeList.
 *
 * @module
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/isSuccessfulElement
 * @requires module:wc/dom/getFilteredGroup
 * @todo re-order code, document private memebers.
 */
define(["wc/dom/tag", "wc/dom/isSuccessfulElement", "wc/dom/getFilteredGroup"],
	/** @param tag wc/dom/tag @param isSuccessfulElement wc/dom/isSuccessfulElement @param getFilteredGroup wc/dom/getFilteredGroup @ignore */
	function(tag, isSuccessfulElement, getFilteredGroup) {
		"use strict";

		/**
		 * Compares serialized form objects to determine if they are different.
		 * @param {Object.<string, string[]>} stateA The first form state.
		 * @param {Object.<string, string[]>} stateB The second form state.
		 * @returns {boolean} true if the serialized form objects are different.
		 */
		Serialize.prototype.areDifferent = function(stateA, stateB) {
			var different = (!stateA || !stateB),
				keysStateA, stateALen,
				keysStateB, stateBLen;
			if (!different) {
				keysStateA = Object.keys(stateA);
				keysStateB = Object.keys(stateB);
				stateALen = keysStateA.length;
				stateBLen = keysStateB.length;
				different = stateALen !== stateBLen;
				if (!different && stateBLen > 0) {
					different = !keysStateB.every(function(key) {
						var same = false,
							stateBVal = stateB[key],
							stateAVal = stateA[key];
						if (stateBVal && stateAVal && stateBVal.length === stateAVal.length) {
							stateBVal.sort();
							stateAVal.sort();
							same = stateBVal.every(function(val, idx) {
								var result = stateAVal[idx] === val;
								if (!result) {
									console.log("Param has changed at idx", key, idx, stateAVal[idx], val);
								}
								return result;
							});
						}
						else {
							console.log("Param has changed", key, stateAVal, stateBVal);
						}
						return same;
					});
				}
				else {
					console.log("Form params changed", keysStateA, keysStateB);
				}
			}
			return different;
		};

		/**
		 * @constructor
		 * @alias module:wc/dom/serialize~Serialize
		 * @private
		 */
		function Serialize() {
			var NV_SEPARATOR = "=";

			function encodeName(name) {
				var result;
				result = encodeURIComponent(name);
				result = result.replace(/%5B%5D$/, "[]");
				return result;
			}

			function decodeName(name) {
				var result = name.replace(/\[\]$/, "%5B%5D");
				result = decodeURIComponent(result);
				return result;
			}

			function getValue(element) {
				var result = null,
					tagName = element.tagName;
				if (tagName === tag.INPUT || tagName === tag.TEXTAREA) {
					result = element.value;
				}
				else if (tagName === tag.OPTION) {
					result = element.getAttribute("value");
					if (result === null) {  // standards
						result = element.text;
					}
					else if (result === "") {
						if (element.outerHTML) {
							if (!element.outerHTML.match(/value="[^"]*/gi)) {  // is ok that is IE only, only IE will end up here
								result = element.text;
							}
						}
						else {
							result = element.value;
						}
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
			 * @param {String} inStr the serialised form (format name=value&name-val2&name3&name4=&name5=val5)
			 * @returns {Object}
			 */
			function deserializeToObject(inStr) {
				var i, len,
					pairSeparator = "&",
					nvSeparator = NV_SEPARATOR,
					result = {}, nvArray = [], twoDArray = [],
					tempName, tempVal;

				nvArray = inStr.split(pairSeparator);

				for (i = 0, len = nvArray.length; i < len; ++i) {
					twoDArray = nvArray[i].split(nvSeparator);
					tempName = twoDArray[0];
					tempVal = (twoDArray.length === 2) ? twoDArray[1] : null;  // if there is no = then the correct value is null
					if (result[tempName]) {
						result[tempName].push(tempVal);
					}
					else {
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
			 * @param {(String|Array)} value The decoded component values (may be an array).
			 */
			function addToDom(container, name, value) {
				var tempField = document.createElement("input");
				tempField.name = name;
				tempField.type = "hidden";
				tempField.value = value;
				container.appendChild(tempField);
			}

			/**
			 * Serialize an HTML Form or NodeList to the format used in a HTTP GET request queryString.
			 *
			 * @function  module:wc/dom/serialize.serialize
			 * @param {(NodeList|HTMLForm)} nodeList The nodes to serialise or an HTML Form.
			 * @param {Boolean} [includeButtons] If true all button name:value pairs are included.
			 * @param {Boolean} [returnAsObject] If true return an object not a string. Each property of the object
			 *    represents name/value pair. The name/value pairs will be URI encoded.
			 * @param {function} filter A funtion that will be passed an element and can veto inclusion in the serialization if it
			 *    returns false.
			 * @returns {string|Object.<string, string[]>}
			 */
			this.serialize = function (nodeList, includeButtons, returnAsObject, filter) {
				var sb = [],
					i,
					len,
					next,
					items,
					j,
					nextChild,
					result,
					value;
				if (nodeList.tagName && nodeList.tagName === tag.FORM) {
					nodeList = nodeList.elements;
				}
				for (i = 0, len = nodeList.length; i < len; i++) {
					next = nodeList[i];
					try {
						if (filter && filter(next) === false) {
							continue;
						}
					}
					catch (ex) {
						console.error(ex);
					}
					if (isSuccessfulElement(next, includeButtons)) {
						if (next.tagName === tag.SELECT) {
							items = getFilteredGroup(next);
							for (j = 0; j < items.length; ++j) {
								nextChild = items[j];
								value = getValue(nextChild);
								if (value !== null) {
									// If a control doesn't have a stateB value when the form is submitted, user agents are not required to treat it as a successful control.
									sb[sb.length] = encodeName(next.name) + NV_SEPARATOR + encodeURIComponent(value);
								}
							}
						}
						else {
							value = getValue(next);
							if (value !== null) {
								// If a control doesn't have a stateB value when the form is submitted, user agents are not required to treat it as a successful control.
								sb[sb.length] = encodeName(next.name) + NV_SEPARATOR + encodeURIComponent(value);
							}
						}
					}
				}
				result = sb.join("&");  // .replace(/%20/g, "+");  // verdict seems to have changed on this one
				if (returnAsObject) {
					result = deserializeToObject(result, false);
				}
				return result;
			};

			/**
			 * Deserialize to hidden input fields in the DOM (or simply to an object).
			 * Useful for compressing chunks of non-viewable content such as the content of a dynamic tab.
			 *
			 * @function module:wc/dom/serialize.deserialize
			 * @param {(String|Object)} input The thing to deserialize, either a String or a serialized object.
			 * @param {Element} [container] The DOM element in which to create the hidden fields, if not provided then
			 *    this step will be skipped and you probably are just deserializing a string to its object form.
			 * @returns {Object} An object with the same form as that returned by {@link module:wc/dom/serialize#serialise}
			 *    EXCEPT that the name/value pairs will be URI decoded.
			 */
			this.deserialize = function(input, container) {
				var n, result = {}, name, value, i, nextVal;
				if (input.constructor === String) {
					input = deserializeToObject(input, true);
				}
				for (n in input) {
					name = decodeName(n);
					value = input[n];
					if (Array.isArray(value)) {  // should always be this
						result[name] = [];
						for (i = 0; i < value.length; i++) {
							nextVal = decodeURIComponent(value[i]);
							result[name].push(nextVal);
							if (container) {
								addToDom(container, name, nextVal);
							}
						}
					}
					else {
						result[name] = value = decodeURIComponent(value);
						if (container) {
							addToDom(container, name, value);
						}
					}
				}
				return result;
			};

		}
		return /** @alias module:wc/dom/serialize */ new Serialize();
	});
