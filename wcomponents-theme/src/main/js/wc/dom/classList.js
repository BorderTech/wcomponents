/**
 * Provides a wrapper for Element.classList which works in IE8 but is transparent for browsers which have a native
 * implementation.
 *
 * @module
 * @requires module:wc/has
 * @param {Object} global Yes, we need to patch a global. I love IE.
 *
 * @todo This needs proper doco.
 */
(function(global) {
	"use strict";
	// based on code from here: https://developer.mozilla.org/en/DOM/element.classList (but heavily modified)

	var nativeClassList;

	define(["wc/has"], /** @param has wc/has @ignore */function(has) {
		/*
		 * WARNING! MEMORY LEAKS! IE8!
		 * IE8 has massive problems with memory leaks when using accessor properties.
		 * The scoping and variable nulling here is carefully crafted to avoid these leaks.
		 * Don't change anything unless you understand this and are prepared to tackle this yourself
		 * and test in IE8 to ensure you haven't reintrodcued memory leak conditions.
		 */
		var constructorProto,
			classListProp = "classList",
			trim = /^\s+|\s+$/g,
			whiteSpaceRe = /\s+/;

		// First off, add classList to Element.prototype if possible and necessary
		nativeClassList = (document && (classListProp in (document.createElement("div"))));

		try {
			if (!nativeClassList) {  // this is really answering the question "am i in ie8?"
				constructorProto = global.Element || global.HTMLElement;
				if (constructorProto && (constructorProto = constructorProto.prototype)) {
					/*
					 * NOTE: we assume Object.defineProperty is present and usable by this point.
					 * If this browser only supports the deprecated __defineGetter__ api then this
					 * must be handled with another pollyfill (e.g. at time of writing the code is
					 * contained in "wc/ecma5/Object.defineProperty").
					 */
					if (has("native-console")) {
						global.console.log("Adding support for classList API on Element.prototype");
					}
					global.Object.defineProperty(window.Element.prototype, classListProp, { get: classListGetter });
				}
			}
		}
		finally {
			constructorProto = null;  // IE8 memory cleanup. Yes IE8, not 6 or 7.
		}

		function setClasses(elem, classes) {
			elem.className = classes.join(" ");
		}

		function checkAndGetIndex(classes, token) {
			if (token === "") {
				throw new TypeError("SYNTAX_ERR");
			}
			if (whiteSpaceRe.test(token)) {
				throw new TypeError("INVALID_CHARACTER_ERR");
			}

			return classes.indexOf(token);
		}
		// it is best if we just ignore this for a while.
		/**
		 * @this {Element}
		 * @ignore
		 */
		function classListGetter() {
			var elem = this,
				classes = ((elem.className) ? elem.className.replace(trim, "").split(whiteSpaceRe) : []);
			return {
				length: classes.length,
				item: function(i) {
					return classes[i] || null;
				},
				contains: function(token) {
					return checkAndGetIndex(classes, token) !== -1;
				},/** @this {Object} @param token A token @ignore */
				add: function(token) {
					if (checkAndGetIndex(classes, token) === -1) {
						classes[classes.length] = token;
						this.length = classes.length;
						setClasses(elem, classes);
					}
				},/** @this {Object}  @param token A token @ignore */
				remove: function(token) {
					var index = checkAndGetIndex(classes, token);
					if (index !== -1) {
						classes.splice(index, 1);
						this.length = classes.length;
						setClasses(elem, classes);
					}
				},/** @this {Object}  @param token A token @ignore */
				toggle: function(token) {
					if (checkAndGetIndex(classes, token) === -1) {
						this.add(token);
					}
					else {
						this.remove(token);
					}
				},
				toString: function() {
					return elem.className;
				}
			};
		}
		return new ClassList(classListGetter);
	});

	/**
	 * A proxy class to call classList methods, only useful if supporting browsers that:
	 * <ol><li>Do not natively support classList AND</li>
	 * <li>Do not allow extending the DOM via DOM Prototype Interface Objects with Accessor Properties.</li></ol>
	 *
	 * <p>It's best to only use the prototype chain classList if it is native. This is mainly because IE8 does such an
	 * abysmal job of implementing the prototype chain on DOM objects. HTMLObjectElement for example does not inherit
	 * the classList from the prototype chain even though it should.</p>
	 *
	 * @alias module:wc/dom/classList~ClassList
	 * @constructor
	 * @private
	 * @todo Add the relevant JSDoc.
	 * @param {Function} $classListGetter A factory that returns classList polyfills for browsers that don't support it natively.
	 *
	 * @ignore
	 */
	function ClassList($classListGetter) {
		var $this = this;

		function proxyHelper(command, element, token) {
			if (nativeClassList) {
				/*
				 * classList will be undefined if we hit an XML node in an HTML page
				 * we could consider checking namespaceURI instead
				 */
				if (typeof element.classList !== "undefined") {
					return element.classList[command](token);
				}
				return false;
			}
			return $classListGetter.call(element, null)[command](token);
		}

		$this.getLength = function (element) {
			if (nativeClassList) {
				return element.classList.length;
			}
			return $classListGetter.call(element, null).length;
		};

		$this.item = function (element, index) {
			if (nativeClassList) {
				return element.classList.item(index);
			}
			return $classListGetter.call(element, null).item(index);
		};

		$this.contains = function (element, token) {
			return proxyHelper("contains", element, token);
		};

		$this.add = function (element, token) {
			return proxyHelper("add", element, token);
		};

		$this.remove = function (element, token) {
			return proxyHelper("remove", element, token);
		};

		$this.toggle = function (element, token) {
			return proxyHelper("toggle", element, token);
		};

		$this.toString = function (element) {
			if (nativeClassList) {
				return element.classList.toString();
			}
			return $classListGetter.call(element, null).toString();
		};
	}

})(this);
