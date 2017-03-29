define(["wc/dom/classList",
	"wc/dom/Widget"],
	function (classList, Widget) {
		"use strict";


		/**
		 * The descriptor of the icon element.
		 * @type module:wc/dom/Widget
		 */
		var ICON = new Widget("", "fa", {"aria-hidden": "true"});

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
				throw new TypeError("element must be defined");
			}
			return true;
		}

		/**
		 * Get an icon from an element which may contain one.
		 * @param {Element} element the element to test
		 * @param {boolean} [force] if `true` then only check inside the element for an icon
		 * @returns {?Element} the icon if found
		 */
		function getIcon(element, force) {
			testElementArg(element);
			return force ? ICON.findDescendant(element) : ICON.isOneOfMe(element) ? element : ICON.findDescendant(element);
		}

		/**
		 * Helper to add/remove classes from an icon.
		 * @param {Element} element the element which may be or contain an icon
		 * @param {String} icon the class to change
		 * @param {boolean} [add] if `true` add the class, otherwise remove it
		 */
		function addRemoveIcon(element, icon, add) {
			var func, iconElement;
			if (!icon) {
				return;
			}
			iconElement = getIcon(element);
			if (iconElement) {
				func = add ? "add" : "remove";
				classList[func](iconElement, icon);
			}
		}

		/**
		 * @constructor
		 * @private
		 * @alias module:wc/ui/icon~Icon
		 */
		function Icon() {
			/*
			this.create = function(element, name) {
				testElementArg(element);
				if (!name) {
					throw new TypeError("Icon name must not be defined and not falsey");
				}
				var icon = ICON.findDescendant(element, true);
				if (icon) {
					icon.className = "fa"; // clear out icon types
				}
				else {
					element.addAjacentHTML("afterbegin", "<span aria-hidden='true' class='fa'></span>");
					icon = ICON.findDescendant(element, true);
				}
				if (icon) {
					classList.add(icon, name);
				}
			};
			*/
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
			if (!(icon = getIcon(element, true))) {
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
		 * @throws {TypeError} if element is not a HTML element
		 */
		Icon.prototype.remove = function(element, remove) {
			addRemoveIcon(element, remove, false);
		};

		/**
		 * Add a class to an existing icon.
		 * @function
		 * @public
		 * @param {Element} element the element which may contain an icon
		 * @param {String} add the class to add
		 * @throws {TypeError} if element is not a HTML element
		 */
		Icon.prototype.add = function (element, add) {
			addRemoveIcon(element, add, true);
		};

		/**
		 * Allows for manipulation of icons.
		 * @module
		 * @requires module:wc/dom/classList
		 * @requires module:wc/dom/Widget
		 */
		return new Icon();
	});
