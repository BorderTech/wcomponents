define(["wc/dom/event"], function(eventManager) {

	var EVENT_NAME_RE = /^on([A-Z][a-z]+)/;

	/**
	 * Gets the properties (attributes) from an Element.
	 * These props could be fed into createElement.
	 * @param {Element} element The element for which we want properties.
	 * @returns {Object} A map of properties set on this Element.
	 */
	function getProps(element) {
		var result = {}, next, nextName, nextValue, attrs, i;
		if (element) {
			attrs = element.attributes;
			if (attrs) {
				for (i = 0; i < attrs.length; i++) {
					next = attrs[i];
					nextName = next.name;
					nextValue = next.value;
					if (nextName === "class") {
						nextName = "className";
					} else if (nextName === "for") {
						nextName = "htmlFor";
					}
					if (nextValue) {  // remember attributes are always strings - "0" and "false" are truthy (0 and false are falsey)
						if (nextValue === "true") {
							result[nextName] = true;
						} else if (nextValue === "false") {
							result[nextName] = false;
						} else {
							result[nextName] = nextValue;
						}
					} else {
						result[nextName] = true;  // The attribute is true because it is present
					}
				}
			}
		}
		return result;
	}

	/**
	 * Import all child nodes from a given element to an array (or to another element).
	 * Order will be maintained.
	 * Existing nodes in the target `to` will be untouched.
	 * @param {Element} from The element from which childNodes will be exported.
	 * @param {Element|Node[]} to The target into which the childNodes will be imported.
	 * @returns {Element|Node[]} An array of DOM nodes or a DOM element.
	 */
	function importKids(from, to) {
		if (from && to) {
			if (from === to) {
				// This would be an infinite loop - you shall not pass!
				throw new Error("Cannot import to self");
			}
			while (from.firstChild) {
				if (to.appendChild) {
					to.appendChild(from.firstChild);
				} else if (Array.isArray(to)) {
					to.push(from.removeChild(from.firstChild));
				} else {
					break;
				}
			}
		}
		return to;
	}

	/**
	 * Helper for createElement - sets attributes on an Element from a provided associative array.
	 * @param {Element} element The element on which the attributes are to be set.
	 * @param {Object} props key/value pairs of attributes to set.
	 */
	function setAttributes(element, props) {
		var attrName, attrVal;
		if (props) {
			for (attrName in props) {
				if (props.hasOwnProperty(attrName)) {
					attrVal = props[attrName];
					if (attrVal !== null) {
						if (attrName === "className") {
							attrName = "class";
						} else if (typeof attrVal === "function") {
							wireEvent(element, attrName, attrVal);
						} else if (attrName === "htmlFor") {
							attrName = "for";
						}
						element.setAttribute(attrName, attrVal);
					}
				}
			}
		}
	}

	/**
	 * Register an event handler.
	 * @param {Element} element The element on which to regsiter an event handler.
	 * @param {string} type The event type to register e.g. "onClick"
	 * @param {Function} handler The event handler.
	 */
	function wireEvent(element, type, handler) {
		var match = type.match(EVENT_NAME_RE);
		if (match) {
			match = match[1];
			match = match.toLowerCase();
			eventManager.add(element, match, handler);
		}
	}

	/**
	 * Creates a new Element.
	 * @param {string} name The node name.
	 * @param {Object} props additional element attributes, properties etc.
	 * @param {Element[]} childNodes Nodes to append to the newly created Element
	 * @returns {Element} The newly created element.
	 */
	function createElement(name, props, childNodes) {
		var element = document.createElement(name);
		if (props) {
			setAttributes(element, props);
		}
		appendKids(element, childNodes);
		return element;
	}

	/**
	 * Appends a collection of nodes to this element.
	 * @param {Element} element The container to which the nodes should be appended.
	 * @param {Array.<Element|String>} childNodes The nodes to append to the element;
	 *    strings are treated as text nodes.
	 */
	function appendKids(element, childNodes) {
		if (childNodes && childNodes.length) {
			childNodes.forEach(function(next) {
				if (next) {
					if (next.constructor === String) {
						next = document.createTextNode(next);
					}
					element.appendChild(next);
				}
			});
		}
	}

	return {
		createElement: createElement,
		getProps: getProps,
		appendKids: appendKids,
		importKids: importKids
	};
});
