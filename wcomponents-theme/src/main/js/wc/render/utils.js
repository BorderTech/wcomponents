define([], function() {

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
					if (renameMap[nextName] && renameMap.hasOwnProperty(nextName)) {
						nextName = renameMap[nextName];
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
					element.setAttribute(attrName, attrs[attrName]);
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
		}
		if (childNodes && childNodes.length) {
			childNodes.forEach(function(next) {
				element.appendChild(next);
			});
		}
		return element;
	}

	return {
		createElement: createElement,
		extractAttributes: extractAttributes
	};
});