define(["wc/dom/Widget"], function (Widget) {
	"use strict";

	/**
	 * The descriptor of the icon element.
	 * @type module:wc/dom/Widget
	 */
	var ICON = new Widget("", "wc-fa", {"aria-hidden": "true"});

	function getHTML(icon) {
		return "<i class='wc-fa " + icon + "' aria-hidden='true'></i>";
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
	 * @param {String} icon the classes to change
	 * @param {boolean} [add] if `true` add the classes, otherwise remove them
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
			const classes = icon.split(" ").filter(Boolean);
			iconElement.classList[func](...classes);
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
	 * Swap icon classes for another. May be used to add or remove icon classes.
	 * @function
	 * @public
	 * @param {Element} element The element which may contain an icon. If there is no icon then this function does nothing.
	 * @param {String} add the icon classNames to add
	 * @param {String} remove the icon classNames to remove
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
			const classes = remove.split(" ").filter(Boolean);
			icon.classList.remove(...classes);
		}
		if (add) {
			const classes = add.split(" ").filter(Boolean);
			icon.classList.add(...classes);
		}
	};

	/**
	 * Remove classes from an icon.
	 * @function
	 * @public
	 * @param {Element} element the element which may contain an icon
	 * @param {String} remove the classes to remove
	 */
	Icon.prototype.remove = function(element, remove) {
		var icon;
		if (addRemoveIcon(element, remove)) {
			icon = getIcon(element);
			if (icon.classList.length === 1) {
				icon.parentNode.removeChild(icon);
			}
		}
	};

	/**
	 * Add classes to an existing icon _or_ add a new icon as the first child of an element
	 * @function
	 * @public
	 * @param {Element} element the icon element or an element to which we add an icon
	 * @param {String} add the icon classNames to add
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
	 * @requires module:wc/dom/Widget
	 */
	return new Icon();
});
