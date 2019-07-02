define(["wc/dom/event"], function(eventManager) {

	var EVENT_NAME_RE = /^on([A-Z][a-z]+)/;

	/**
	 * Takes the attributes from an Element and turns them into a key/value map for easy lookup.
	 * @param {Element} element The element which may contain attributes to map.
	 * @paramn {Object} renameMap A map of old attribute names to new ones if they should be changed while exxtracting.
	 * @param {Object} [obj] Optionally the attributes will be mapped onto this object.
	 * @returns {Object} A map of attributes set on this Element.
	 */
	function extractAttributes(element, renameMap, obj) {
		var result = obj || {}, next, nextName, attrs, i;
		if (element) {
			attrs = element.attributes;
			if (attrs) {
				for (i = 0; i < attrs.length; i++) {
					next = attrs[i];
					nextName = next.name;  // .toLowerCase();
					if (renameMap.hasOwnProperty(nextName)) {
						nextName = renameMap[nextName];
						if (!nextName) {
							continue;  // mapping to null means do not copy this attribute
						}
					}
					if (nextName === "class") {
						nextName = "className";
					} else if (nextName === "for") {
						nextName = "htmlFor";
					}
					if (next.value) {
						if (next.value === "true") {
							result[nextName] = true;
						} else if (next.value === "false") {
							result[nextName] = false;
						} else {
							result[nextName] = next.value;
						}
					} else {
						result[nextName] = true;  // The attribute is true because it is present
					}
				}
			}
		}
		return result;
	}

	function importKids(from, to) {
		while (from.firstChild) {
			if (to.appendChild) {
				to.appendChild(from.firstChild);
			} else if (Array.isArray(to)) {
				to.push(from.removeChild(from.firstChild));
			} else {
				break;
			}
		}
		return to;
	}

	/**
	 * Helper for createElement - sets attributes on an Element from a provided associative array.
	 * @param {Element} element The element on which the attributes are to be set.
	 * @param {Object} attrs key/value pairs of attributes to set.
	 */
	function setAttributes(element, attrs) {
		var attrName;
		if (attrs) {
			for (attrName in attrs) {
				if (attrs.hasOwnProperty(attrName)) {
					if (attrName === "className") {
						attrName = "class";
					} else if (attrName === "htmlFor") {
						attrName = "for";
					}
					element.setAttribute(attrName, attrs[attrName]);
				}
			}
		}
	}

	function wireEvents(element, elementConfig) {
		var i, match, next, handler, configProps = Object.keys(elementConfig);
		for (i = 0; i < configProps.length; i++) {
			next = configProps[i];
			if (next) {
				handler = elementConfig[next];
				match = next.match(EVENT_NAME_RE);
				if (match && typeof handler === "function") {
					match = match[1];
					match = match.toLowerCase();
					eventManager.add(element, match, handler);
				}
			}
		}
	}


	/**
	 * Creates a new Element.
	 * @param {string} name The node name.
	 * @param {Object} elementConfig additional element attributes, properties etc.
	 * @param {Element[]} childNodes Nodes to append to the newly created Element
	 * @returns {Element} The newly created element.
	 */
	function createElement(name, elementConfig, childNodes) {
		var element = document.createElement(name);
		if (elementConfig) {
			setAttributes(element, elementConfig.attrs);
			wireEvents(element, elementConfig);
		}
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
		return element;
	}

	return {
		createElement: createElement,
		extractAttributes: extractAttributes,
		importKids: importKids
	};
});