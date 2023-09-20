const ROLE_MAP = {
	"a": "link",
	"button": "button",
	"checkbox": "checkbox",
	"dialog": "dialog",
	"email": "textbox",
	"image": "button",
	"input": "textbox",
	"number": "spinbox",
	"option": "option",
	"password": "textbox",
	"progress": "progressbar",
	"radio": "radio",
	"range": "slider",
	"reset": "button",
	"select": "listbox",
	"submit": "button",
	"td": "gridcell",
	"tel": "textbox",
	"text": "textbox",
	"textarea": "textbox",
	"tr": "row"
};

/**
 * <p>Module to provide WAI-ARIA roles, state and properties implicit in HTML Elements. The WAI-ARIA guidelines are
 * extremely specific: the role attribute is used to change the control type or add a control type to an element which
 * is not a control and the aria-* attributes are an extension of native language attributes and should not be applied
 * where native semantics exist.</p>
 *
 * <p>This module allows us to use WAI-ARIA roles, properties and states interchangeably with native semantics by
 * supplying the implied role of an element with no "role" attribute and therefore the aria-* attribute states and
 * properties which may be applied to that element.</p>
 */

const instance = {
	/**
	 * Get the "role" implicit in an HTML element.
	 *
	 * @function module:wc/dom/impliedARIA.getImpliedRole
	 * @param {Element} element A DOM NODE but really should be an element.
	 * @returns {String} The implied role for the element.
	 */
	getImpliedRole: function (element) {
		// if something has an explicit role then implied roles should not be considered.
		if (element.nodeType === Node.ELEMENT_NODE && !element.hasAttribute("role")) {
			if (element.matches("table")) {
				return element.matches("table.wc_tbl_expansion") ? "treegrid" : "grid";
			}
			if (element.matches("th")) {
				return element.closest("thead") ? "columnheader" : "rowheader";
			}
			const useType = element.matches("input, button");
			const roleKey = useType ? element["type"] : element.nodeName.toLowerCase();
			return ROLE_MAP[roleKey];
		}
		return null;
	},

	/**
	 * Does element X natively support state Y? What's native stuff doing in an implied ARIA helper? Good question:
	 * I had to put it somewhere!
	 *
	 * @function module:wc/dom/impliedARIA.supportsNativeState
	 * @param {Element} element The element to test.
	 * @param {string} state an HTML attribute representing a state: required, selected, checked or disabled.
	 * @returns {boolean} true if element supports that attribute
	 */
	supportsNativeState: function (element, state) {
		let result = false;
		/*
		 * Cannot rely on this quick mechanism because IE (11 at least) supports "disabled" in elements which are not
		 * form controls. It does not seem to apply checked, selected or required to them though.
		 if (state in element || element.hasAttribute(state)) {
		 return true;
		 }
		 */
		/* NOTE: FIELDSET is natively disable-able, but we choose to not allow it for pragmatic reasons.*/
		const disableable = ["input", "button", "select", "textarea", "optgroup", "option"].join();
		switch (state) {
			case "disabled":
				result = element.matches(disableable);
				break;
			case "required":
				result = supportsRequired(element);
				break;
			case "any":
				result = supportsSelected(element) || supportsChecked(element);
				break;
			case "checked":
				result = supportsChecked(element);
				break;
			case "selected":
				result = supportsSelected(element);
				break;
		}
		return result;
	}
};

/**
 * Determine if an HTML element supports the native required attribute. See
 *
 * * https://html.spec.whatwg.org/#attr-input-required,
 * * https://html.spec.whatwg.org/#attr-select-required
 * * https://html.spec.whatwg.org/#attr-textarea-required
 *
 * @function
 * @private
 * @param {Element} element The element to test.
 * @returns {Boolean} true if the element supports the required attribute.
 */
function supportsRequired(element) {
	const notRequired = ["hidden", "range", "color", "submit", "image", "reset", "button"].map(type => `not([type='${type}'])`);
	const required = [`input:${notRequired.join(":")}`, "select", "textarea"].join();
	return element.matches(required);
}

/**
 * Determine if an HTML element supports the native checked attribute which is currently only supported by input
 * of type radio or checkbox event though all input elements report "checked" as a property.
 *
 * @function
 * @private
 * @param {Element} element The element to test.
 * @returns {Boolean} true if the element supports the checked attribute i.e. it is an input element and its
 *    type is radio or checkbox.
 */
function supportsChecked(element) {
	return element.matches("input[type='checkbox'],input[type='radio']");
}

/**
 * Determine if an HTML element supports the native selected attribute which is currently only supported by
 * option elements.
 *
 * @function
 * @private
 * @param {Element} element The element to test.
 * @returns {Boolean} true if the element supports the selected attribute i.e. it is an option element.
 */
function supportsSelected(element) {
	return element?.matches("option");
}

export default instance;
