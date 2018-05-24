define(["wc/dom/classList",
	"wc/dom/Widget"],
	function (classList, Widget) {
		"use strict";

		/**
		 * The descriptor of the icon element.
		 * @type module:wc/dom/Widget
		 */
		var ICON = new Widget("", "fa", {"aria-hidden": "true"});

		function getHTML(icon) {
			return "<i class='fa " + icon + "' aria-hidden='true'></i>";
		}

		/**
		 * Type checker for public functions.
		 * @function
		 * @private
		 * @param {Eleement} element the element arg to test
		 * @returns {Boolean} `true` if element is an Element
		 * @throws {TypeError} if element is not an Element
		 */
		function testElementArg(element) {
			if (!(element && element.nodeType === Node.ELEMENT_NODE)) {
				throw new TypeError("element must be an HTML element");
			}
			return true;
		}

		/**
		 * Get an icon from an element which may contain one.
		 * @function
		 * @private
		 * @param {Element} element the element to test
		 * @returns {Element} the icon if found
		 */
		function getIcon(element) {
			testElementArg(element);
			return ICON.isOneOfMe(element) ? element : ICON.findDescendant(element);
		}

		/**
		 * Helper to add/remove classes from an icon.
		 * @function
		 * @private
		 * @param {Element} element the element which may be or contain an icon
		 * @param {String} icon the class to change
		 * @param {boolean} [add] if `true` add the class, otherwise remove it
		 * @returns {boolean} `true` if an icon element is found, otherwise `false`
		 * @throws {TypeError} if element is not a HTML element
		 * @throws {TypeError} if icon is not a non-empty String
		 */
		function addRemoveIcon(element, icon, add) {
			var func, iconElement;
			if (!(element && icon)) {
				throw new TypeError("arguments must be defined");
			}
			if (icon.constructor !== String) {
				throw new TypeError("icon to " + (add ? "add" : "remove") + " argument must be a String");
			}

			iconElement = getIcon(element);
			if (iconElement) {
				func = add ? "add" : "remove";
				classList[func](iconElement, icon);
				return true;
			}
			return false;
		}

		/**
		 * @constructor
		 * @private
		 * @alias module:wc/ui/icon~Icon
		 */
		function Icon() {
		}

		/**
		 * Swap one icon class for another. May be used to add or remove an icon class.
		 * @function
		 * @public
		 * @param {Element} element The element which may contain an icon. If there is no icon then this function does nothing.
		 * @param {String} add the icon className to add
		 * @param {String} remove the icon className to remove
		 */
		Icon.prototype.change = function(element, add, remove) {
			var icon;
			if (!(add || remove)) {
				return;
			}
			if (!(icon = getIcon(element))) {
				return;
			}
			if (remove) {
				classList.remove(icon, remove);
			}
			if (add) {
				classList.add(icon, add);
			}
		};

		/**
		 * Remove a class from an icon.
		 * @function
		 * @public
		 * @param {Element} element the element which may contain an icon
		 * @param {String} remove the class to remove
		 */
		Icon.prototype.remove = function(element, remove) {
			var icon;
			if (addRemoveIcon(element, remove)) {
				icon = getIcon(element);
				if (classList.getLength(icon) === 1) {
					// only `fa` left
					icon.parentNode.removeChild(icon);
				}
			}
		};

		/**
		 * Add a class to an existing icon _or_ add a new icon as the first child of an element
		 * @function
		 * @public
		 * @param {Element} element the icon element or an element to which we add an icon
		 * @param {String} add the icon className to add
		 */
		Icon.prototype.add = function(element, add) {
			if (!addRemoveIcon(element, add, true)) {
				element.insertAdjacentHTML("afterbegin", getHTML(add));
			}
		};

		/**
		 * Get the {@link module:wc/dom/Widget} that describes an icon.
		 * @returns {iconL#3.Widget|module:wc/dom/Widget}
		 */
		Icon.prototype.getWidget = function() {
			return ICON;
		};

		Icon.prototype.get = function(element) {
			return getIcon(element);
		};

		/**
		 * Allows for manipulation of icons.
		 * @module
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/Widget
		 */
		return new Icon();
	});
