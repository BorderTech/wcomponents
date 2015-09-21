/**
 * Utility class for dealing with WAI-ARIA role or implied role of elements.
 *
 * @module
 * @requires module:wc/dom/impliedARIA
 */
define(["wc/dom/impliedARIA"], /** @param {Object} impliedARIA @ignore */ function(impliedARIA) {
	"use strict";

	/**
	 * @constructor
	 * @alias wc/dom/role~Role
	 * @private
	 */
	function Role() {

		/**
		 * Get the role (or implied role) of an element.
		 *
		 * @function module:wc/dom/role.get
		 * @param {Element} element The element to test.
		 * @param {boolean} [implied] Include getting implied role if true.
		 * @returns {?String} The WAI-ARIA role of the element, including its implied role if required.
		 */
		this.get = function(element, implied) {
			var role;
			if (element && element.nodeType === Node.ELEMENT_NODE) {
				role = element.getAttribute("role");
				if (implied && !role) {
					role = impliedARIA.getImpliedRole(element);
				}
			}
			return role;
		};

		/**
		 * Test if an element has a WAI-ARIA role.
		 * @function module:wc/dom/role.has
		 * @param {Element} element The element to test.
		 * @param {boolean} implied Should we test if the element has an implied role?
		 * @returns {Boolean} true if the element has a role (or implied role if implied is true.
		 */
		this.has = function(element, implied) {
			var result = false;
			if (element && element.nodeType === Node.ELEMENT_NODE) {
				result = element.hasAttribute("role");
				if (implied && !result) {
					result = !!impliedARIA.getImpliedRole(element);
				}
			}
			return result;
		};
	}

	return /** @alias wc/dom/role */ new Role();
});
