define(["wc/dom/tag", "wc/dom/Widget"], function (tag, Widget) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/dom/impliedARIA~ImpliedAria
	 * @private
	 */
	function ImpliedAria() {
		var ROLE_MAP = {
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
			},
			DISABLEABLE,
			REQUIRED,
			INPUT_NOT_REQUIRED,
			THEAD;

		/**
		 * Determine if a HTML element supports the native required attribute. See
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
			var tagName = element.tagName;
			REQUIRED = REQUIRED || [tag.INPUT, tag.SELECT, tag.TEXTAREA];
			if (~REQUIRED.indexOf(tagName)) {
				INPUT_NOT_REQUIRED = INPUT_NOT_REQUIRED || ["hidden", "range", "color", "submit", "image", "reset", "button"];
				if (tagName === tag.INPUT && ~INPUT_NOT_REQUIRED.indexOf(element.getAttribute("type"))) { // do not use element.type because a lot of cruddy browsers still reply text to that.
					return false;
				}
				return true;
			}
			return false;
		}

		/**
		 * Determine if a HTML element supports the native checked attribute which is currently only supported by input
		 * of type radio or checkbox event though all input elements report "checked" as a property.
		 *
		 * @function
		 * @private
		 * @param {Element} element The element to test.
		 * @returns {Boolean} true if the element supports the checked attribute i.e. it is an input element and its
		 *    type is radio or checkbox.
		 */
		function supportsChecked(element) {
			return element.tagName === tag.INPUT && (element.type === "checkbox" || element.type === "radio");
		}

		/**
		 * Determine if a HTML element supports the native selected attribute which is currently only supported by
		 * option elements.
		 *
		 * @function
		 * @private
		 * @param {Element} element The element to test.
		 * @returns {Boolean} true if the element supports the selected attribute i.e. it is an option element.
		 */
		function supportsSelected(element) {
			return element.tagName === tag.OPTION;
		}

		/**
		 * Get the "role" implicit in a HTML element.
		 *
		 * @function module:wc/dom/impliedARIA.getImpliedRole
		 * @param {Node} element A DOM NODE but really should be an element.
		 * @returns {String} The implied role for the element.
		 */
		this.getImpliedRole = function (element) {
			var roleKey;
			if (element.nodeType === Node.ELEMENT_NODE && !element.hasAttribute("role")) { // if something has an explicit role then implied roles should not be considered.
				if (element.tagName === tag.TABLE) {
					return (element.className && element.className.indexOf("wc_tbl_expansion") > -1) ? "treegrid" : "grid";
				}
				if (element.tagName === tag.TH) {
					THEAD = THEAD || new Widget(tag.THEAD);
					return (THEAD.findAncestor(element)) ? "columnheader" : "rowheader";
				}
				roleKey = (element.tagName === tag.INPUT || element.tagName === tag.BUTTON) ? element.type : element.tagName.toLowerCase();
				return ROLE_MAP[roleKey];
			}
			return null;
		};

		/**
		 * Does element X natively support state Y? What's native stuff doing in an implied ARIA helper? Good question:
		 * I had to put it somewhere!
		 *
		 * @function module:wc/dom/impliedARIA.supportsNativeState
		 * @param {Element} element The element to test.
		 * @param {String} state an HTML attribute representing a state: required, selected, checked or disabled.
		 * @returns {Boolean} true if element supports that attribute
		 */
		this.supportsNativeState = function (element, state) {
			var result = false;
			/*
			 * Cannot rely on this quick mechanism because IE (11 at least) supports "disabled" in elements which are not
			 * form controls. It does not seem to apply checked, selected or required to them though.
			 if (state in element || element.hasAttribute(state)) {
			 return true;
			 }
			 */
			switch (state) {
				case "disabled":
					DISABLEABLE = DISABLEABLE || [tag.INPUT, tag.BUTTON, tag.SELECT, tag.TEXTAREA, tag.OPTGROUP, tag.OPTION];
					/* NOTE: FIELDSET is natively disable-able but we choose to not allow it for pragmatic reasons.*/
					result = !!~DISABLEABLE.indexOf(element.tagName);
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
		};
	}

	/**
	 * <p>Module to provide WAI-ARIA roles, state and properties implicit in HTML Elements. The WAI-ARIA guidelines are
	 * extremely specific: the role attribute is used to change the control type or add a control type to an element which
	 * is not a control and the aria-* attributes are an extension of native language attributes and should not be applied
	 * where native semantics exist.</p>
	 *
	 * <p>This module allows us to use WAI-ARIA roles, properties and states interchangeably with native semantics by
	 * supplying the implied role of an element with no "role" attribute and therefore the aria-* attribute states and
	 * properties which may be applied to that element.</p>
	 *
	 * @module
	 * @requires module:wc/dom/tag
	 * @requires module:wc/dom/Widget
	 */
	return new ImpliedAria();
});
