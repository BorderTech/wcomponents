/**
 * @module
 * @requires module:wc/dom/tag
 * @requires module:wc/dom/classList
 */
define(["wc/dom/tag", "wc/dom/classList"], /** @param {wc/dom/tag} tag @param {wc/dom/classList} classList @ignore*/function(tag, classList) {
	"use strict";

	/*
	 * NOTE:
	 * Way back in 2010 we tried rewriting this using document.evaluate and XPATH including looking for ancestor
	 * with specific class. This was 10x SLOWER in gcf 5 and 3x SLOWER in ff3.6 than getting each ancestor by
	 * parentNode and doing a class test when we got a matching tagName even with deeply nested elements with
	 * multiple matching tagNames before the className was matched.
	 * BTW: gcf 5 was approx 6 - 7x faster than ff3.6 in a 10k iteration loop of lookups ff3.6 was 1.4X faster than
	 * IE8
	 */

	var /** @constant {RegExp} SPACE A space regular expression used when attribute matching. @private */ SPACE = /\s+/;

	/**
	 * Determine if an element has a className which matches a given value based on a given match logic.
	 *
	 * @function classNamesMatch
	 * @private
	 * @param {Element} element The element to test
	 * @param {(String|String[])} className The className/s to match
	 * @param {String} logic  Must be either "some" or "every" where "every" means the element must have every class
	 *    in the array; "some" means the element needs to have at least one match. Only has an effect if className
	 *    is an Array.
	 * @returns {Boolean}
	 */
	function classNamesMatch(element, className, logic) {
		var result = false;
		if (element.nodeType === Node.ELEMENT_NODE && ((Array.isArray(className) && className[logic](function($class) {
			return classList.contains(element, $class);
		})) || classList.contains(element, className))) {
			result = true;
		}
		return result;
	}

	/**
	 * Determine if an element has attributes which match one or more with optional values based on a given match
	 * logic. NOTE: IE8, Chrome and FF return null from getAttribute if the attribute is not present.
	 *
	 * @function attributesMatch
	 * @private
	 * @param {Element} element The element to test
	 * @param {Object} attributes An object describing each attribute to be matched. The properties of the object
	 *    are the HTML attributes to find. If a property has a null value then it matches if it is present,
	 *    otherwise it matches if the element has the attribute <em>and</em> that attribute's value matches the
	 *    property's value.
	 * @param {String} logic One of "some" or "every" where "every" means the element must match every attribute
	 *    described by the attributes object and "some" means the element needs to have at least one match.
	 * @returns {Boolean}
	 */
	function attributesMatch(element, attributes, logic) {
		var names = Object.keys(attributes),
			result = names[logic](function(name) {
				var expectedVal = attributes[name],
					actualVal = element.getAttribute(name),
					_result = false, i;
				if (expectedVal === null) {
					_result = element.hasAttribute(name);
				}
				else if (expectedVal === actualVal) {  // short cut
					_result = true;
				}
				else if (SPACE.test(actualVal)) {
					actualVal = actualVal.split(SPACE);
					if (SPACE.test(expectedVal)) {
						expectedVal = expectedVal.split(SPACE);
						if (expectedVal.length > actualVal.length) {
							_result = false;
						}
						else {
							for (i = 0; i < expectedVal.length; ++i) {
								_result = !!~actualVal.indexOf(expectedVal[i]);
								if (!_result) {
									break;
								}
							}
						}
					}
					else {
						_result = !!~actualVal.indexOf(expectedVal);
					}
				}
				else {
					_result = expectedVal === actualVal;
				}
				return _result;
			});
		return result;
	}

	/**
	 * Get nearest ancestor element which matches a set of specific criteria. The may be the element itself if it
	 * matches the criteria.
	 *
	 * Ok sorry about this but this function should take only one arg, a DTO which contains the other args.
	 * Making this change would not be backwards compatible so for now we will make it work both ways.
	 *
	 * @function
	 * @alias module:wc/dom/getAncestorOrSelf
	 * @param {module:wc/dom/getAncestorOrSelf~config|Element} arguments[0] A config object defining the type of
	 *    ancestor to find. **NOTE** This may be a start element but this signature is deprecated and supported for
	 *    backwards compatibilty only. If this argument is an element then other arguments must be included as
	 *    follows where at least one of arguments[1] or arguments[3] or arguments[5] is mandatory.
	 * @param {String} [arguments[1]] The tagName to find: deprecated.
	 * @param {String} [arguments[2]] The limit tagName to end traversal: deprecated.
	 * @param {String} [arguments[3]] The className to find: deprecated.
	 * @param {String} [arguments[4]=every] The class name logic being one of "some" or "every": deprecated.
	 * @param {Object} [arguments[5]] The element attributes to find: deprecated.
	 * @param {String} [arguments[6]=every] The attribute logic being one of "some" or "every": deprecated.
	 * @param {Boolean} [arguments[7]=false] If true find the outermost ancestor.
	 *
	 * @returns {?Element} The ancestor element (or the element itself) which matches the conditions defined in the
	 *    args.
	 *
	 * @throws {TypeError} Throws a TypeError if a start element is not supplied.
	 * @throws {TypeError} Throws a TypeError if the config object or (if the the deprecated signature is used the
	 *    arguments) do not include at least one of tagName and/or className and/or attributes.
	 */
	function getAncestorOrSelf(/* element, tagName, limitTagName, className, classNameLogic, attributes, attributeLogic */) {
		var element,
			tagName,
			limitTagName,
			className,
			next,
			result = null,
			nextTagName,
			limit = tag.BODY,
			classNameLogic,
			attributes,
			attributeLogic,
			outermost,
			DEFAULT_LOGIC = "every";

		if (arguments.length === 1) {
			element = arguments[0].element;
			tagName = arguments[0].tagName;
			limitTagName = arguments[0].limitTagName;
			className = arguments[0].className;
			classNameLogic = arguments[0].classNameLogic || DEFAULT_LOGIC;
			attributes = arguments[0].attributes;
			attributeLogic = arguments[0].attributeLogic || DEFAULT_LOGIC;
			outermost = arguments[0].outermost || false;
		}
		else {
			element = arguments[0];
			tagName = arguments[1];
			limitTagName = (arguments.length > 2) ? arguments[2] : null;
			className = (arguments.length > 3) ? arguments[3] : null;
			classNameLogic = (arguments.length > 4) ? arguments[4] : DEFAULT_LOGIC;
			attributes = (arguments.length > 5) ? arguments[5] : null;
			attributeLogic = (arguments.length > 6) ? arguments[6] : DEFAULT_LOGIC;
			outermost = (arguments.length > 7) ? arguments[7] : false;
		}
		// sanity checks
		if (!element) {
			throw new TypeError("element can not be null - getAncestorOrSelf");
		}
		if (!(tagName || className || attributes)) {
			throw new TypeError("getAncestorOrSelf with multi argument signature must declare at least one of tagName or className arguments.");
		}

		if (tagName) {
			tagName = tag[tagName.toUpperCase()] || tagName;
		}
		// Set default limit to BODY - DO NOT LIMIT TO HTML unless you want native classList to throw an error in some browsers.
		if (limitTagName) {
			limit = tag[limitTagName.toUpperCase()] || tag.BODY;
			if (limit === tag.HTML) {
				limit = tag.BODY;
			}
		}

		if ((next = element)) {
			do {
				nextTagName = next.tagName;
				if ((!tagName || nextTagName === tagName) &&
						(!className || classNamesMatch(next, className, classNameLogic)) &&
						(!attributes || attributesMatch(next, attributes, attributeLogic))) {
					result = next;
					if (!outermost) {
						break;
					}
				}
				else if (nextTagName === limit) {
					break;
				}
			}/*
				The test for nodeType is for the case where the element has been removed from the
				DOM and exists in a document fragment. This can occur unexpectedly in Internet Explorer
				where the element has been removed from the DOM and IE events fire at the wrong time.
			*/
			while ((next = next.parentNode) && next.nodeType === Node.ELEMENT_NODE);
		}
		return result;
	}

	return getAncestorOrSelf;

	/**
	 * Options for finding an ancestor (or self) based on aspects of the HTML. Of the optional properties at least
	 * one of tagName, className or attributes must be set otherwise we end up looking for nothing.
	 *
	 * @typedef {Object} module:wc/dom/getAncestorOrSelf~config
	 * @property {Element} element The element from which to start searching.
	 * @property {String} [tagName] The tagName we are searching for.
	 * @property {String} [limitTagName] Stop searching if we hit this tagName.
	 * @property {(String|String[])} [className] If present the element we are searching for must also have this
	 *    class (or these classes if an array).
	 * @property {String} [classNameLogic=every] Must be either "some" or "every" where "every" means the element
	 *    must have every class in the array; "some" means the element needs to have at least one match. Only has an
	 *    effect if the className is an Array.
	 * @property {Object} attributes  A map of attributes that must be on the element, each key is an attribute
	 *    name, each value is an attribute value. If a property has a null value then it is simply a test that the
	 *    attribute is present, otherwise it matches if the element has the attribute and the property's value
	 *    matches the element's attribute's value. For example:
	 *    <pre><code>{"type":"button", "checked": null}</code></pre>.
	 * @property {String} [attributeLogic=every] Must be either "some" or "every" where "every" means the element
	 *    must have every attribute in the collection; "some" means the element needs to have at least one match.
	 *    Only effective if attributes is set and has more than one property.
	 */
});
