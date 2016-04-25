/**
 * Provides custom attributes without using expandos. Eventually we will be able to remove this and use
 * data-\* attributes.
 *
 * Why continue to use this in IE8 when memory leaks are no longer an issue? The reason is that it guarantees
 * instance specific attributes.  So if you clone a node the new node is guaranteed not to inherit custom
 * attributes. This exists solely for the benefit of Internet Explorer which seems to be the only major browser
 * that clones expandos.
 *
 * Custom attributes are bound to the actual instance of the DOM element that was used to set them. That
 * means if you clone the DOM element (either through element.cloneNode or element.innerHTML) the custom
 * attributes will not "exist" for the clone.
 *
 * The problem we are solving here is that Internet Explorer copies expandos when using cloneNode or
 * innerHTML and others don't.  If we rely on the expando alone then we get different behaviour in different
 * browsers.
 *
 *@example
 * var element = document.createElement("span");
 * element.foo = "bar";
 * var clone = element.cloneNode(true);
 * typeof element.foo; //string
 * typeof clone.foo; //undefined in FF and Chrome, string in IE
 * //HOWEVER in IE accessor properties won't be cloned:
 * var element = document.createElement("span");
 * Object.defineProperty(element, "foo", {get: function(){return "bar";}})
 * var clone = element.cloneNode(true);
 * typeof element.foo; //string
 * typeof clone.foo; //undefined in IE
 *
 * @module
 */
define(function() {
	"use strict";
	/**
	 * Custom attribute model.
	 * @constructor
	 * @private
	 * @alias module:wc/dom/attribute~CustomAttribute
	 */
	function CustomAttribute() {
		var EXPANDO_NAME = "wc.dom.attribute",
			UNDEFINED = "undefined";

		/**
		 * Get a function to get an attribute.
		 *
		 * @function
		 * @private
		 * @returns {Function} A getter.
		 */
		function attributeGetterFactory() {
			var data = {};
			return function () {
				return data;
			};
		}

		/**
		 * Set up an element to allow storage of custom attributes.
		 *
		 * @function
		 * @private
		 * @param {Element} element The element we are initialising.
		 */
		function initialise(element) {
			var getter;
			if (typeof element[EXPANDO_NAME] === UNDEFINED || element[EXPANDO_NAME] === null) {
				getter = attributeGetterFactory();
				if (Object.defineProperty) {
					/*
					 * Note, this will fail in Safari5's native implementation of Object.defineProperty.
					 * That's because Safari 5 has implemented Object.defineProperty but only for POJOs
					 * this is the EXACT opposite of IE8's implementation...
					 * This code assumes and depends on Object.defineProperty being patched in Safari5.
					 * See: wc/ecma5/Object.defineProperty
					 */
					Object.defineProperty(element, EXPANDO_NAME, {get: getter});
				}
				else {
					// element[EXPANDO_NAME] = {};  // could just as easily do this for FF
					element.__defineGetter__(EXPANDO_NAME, getter);
				}
			}
		}

		/**
		 * Set a custom attribute.
		 *
		 * @function
		 * @alias module:wc/dom/attribute.set
		 * @param {Element} element The Element to which we add the attribute.
		 * @param {string} name The lookup "key".
		 * @param {*} value The value to store against the "key".
		 * @returns {*} The value stored in the custom attribute.
		 */
		this.set = function(element, name, value) {
			var result = null,
				data;
			initialise(element);
			if (name) {
				data = element[EXPANDO_NAME];
				result = data[name] = value;
			}
			return result;
		};

		/**
		 * Tests whether an element has a custom attribute set.
		 *
		 * @function
		 * @alias module:wc/dom/attribute.has
		 * @param {Element} element The Element to test.
		 * @param {string} name The lookup "key".
		 * @returns {boolean} true if the custom attribute has been set to any value at all.
		 */
		this.has = function(element, name) {
			var result = false,
				data;
			if (name) {
				data = element[EXPANDO_NAME];
				if (data) {
					result = (name in data);
				}
			}
			return result;
		};

		/**
		 * Get the value of a custom attribute for a particular element.
		 *
		 * @function
		 * @alias module:wc/dom/attribute.get
		 * @param {Element} element The Element to test.
		 * @param {string} name The lookup "key".
		 * @returns {?*} The value of the custom attribute or null if not found.
		 */
		this.get = function(element, name) {
			var result = null,
				data;
			if (name) {
				data = element[EXPANDO_NAME];
				if (data) {
					result = (name in data) ? data[name] : null;
				}
			}
			return result;
		};

		/**
		 * Removes a custom attribute from a particular element.
		 *
		 * @function
		 * @alias module:wc/dom/attribute.remove
		 * @param {Element} element The Element to test.
		 * @param {string} name The lookup "key".
		 * @returns {boolean} true if the attribute was deleted.
		 */
		this.remove = function(element, name) {
			var result = false,
				data;
			data = element[EXPANDO_NAME];
			if (data && name && typeof data[name] !== UNDEFINED) {
				result = delete data[name];
			}
			return result;
		};
	}
	return /** @alias module:wc/dom/attribute */ new CustomAttribute();
});
