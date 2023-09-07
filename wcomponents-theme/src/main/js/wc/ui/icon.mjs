/**
 * The descriptor of the icon element.
 * @type {string}
 */
const iconQs = ".fa[aria-hidden='true']";

/**
 * Allows for manipulation of icons.
 */
const instance = {
	/**
	 * Swap one icon class for another. May be used to add or remove an icon class.
	 * @function
	 * @public
	 * @param {Element} element The element which may contain an icon. If there is no icon then this function does nothing.
	 * @param {String} add the icon className to add
	 * @param {String} remove the icon className to remove
	 */
	change: function(element, add, remove) {
		if (!(add || remove)) {
			return;
		}
		const icon = getIcon(element);
		if (!icon) {
			return;
		}
		if (remove) {
			icon.classList.remove(remove);
		}
		if (add) {
			icon.classList.add(add);
		}
	},

	/**
	 * Remove a class from an icon.
	 * @function
	 * @public
	 * @param {HTMLElement} element the element which may contain an icon
	 * @param {string} remove the class to remove
	 */
	remove: function(element, remove) {
		if (addRemoveIcon(element, remove)) {
			const icon = getIcon(element);
			if (icon.classList.length === 1) {
				// only `fa` left
				icon.parentNode.removeChild(icon);
			}
		}
	},

	/**
	 * Add a class to an existing icon _or_ add a new icon as the first child of an element
	 * @function
	 * @public
	 * @param {HTMLElement} element the icon element or an element to which we add an icon
	 * @param {string} add the icon className to add
	 */
	add: function(element, add) {
		if (!addRemoveIcon(element, add, true)) {
			element.insertAdjacentHTML("afterbegin", getHTML(add));
		}
	},

	/**
	 * Get the selector that describes an icon.
	 * @returns {string}
	 */
	getWidget: () => iconQs,

	/**
	 *
	 * @param {HTMLElement} element
	 * @return {HTMLElement}
	 */
	get: (element) => getIcon(element)
};

/**
 * @param {string} icon
 * @return {string}
 */
function getHTML(icon) {
	return `<i class='fa ${icon}' aria-hidden='true'></i>`;
}

/**
 * Type checker for public functions.
 * @function
 * @private
 * @param {Element} element the element arg to test
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
 * @returns {HTMLElement} the icon if found
 */
function getIcon(element) {
	testElementArg(element);
	return /** @type HTMLElement */(element.matches(iconQs) ? element : element.querySelector(iconQs));
}

/**
 * Helper to add/remove classes from an icon.
 * @function
 * @private
 * @param {Element} element the element which may be or contain an icon
 * @param {string} icon the class to change
 * @param {boolean} [add] if `true` add the class, otherwise remove it
 * @returns {boolean} `true` if an icon element is found, otherwise `false`
 * @throws {TypeError} if element is not an HTML element
 * @throws {TypeError} if icon is not a non-empty String
 */
function addRemoveIcon(element, icon, add) {
	if (!(element && icon)) {
		throw new TypeError("arguments must be defined");
	}
	if (typeof icon !== "string") {
		throw new TypeError("icon to " + (add ? "add" : "remove") + " argument must be a String");
	}

	const iconElement = getIcon(element);
	if (iconElement) {
		const func = add ? "add" : "remove";
		iconElement.classList[func](icon);
		return true;
	}
	return false;
}

export default instance;
