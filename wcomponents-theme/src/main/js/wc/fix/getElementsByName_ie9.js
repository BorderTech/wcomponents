/**
 * @module
 * @private
 * @requires module:wc/has
 */
define(["wc/has"], /** @param has @ignore */ function(has) {
	"use strict";
	/**
	 * All versions of IE up to IE9 have an imprecise implementation of document.getElementsbyName. The problem is that
	 * it will include elements with a matching ID in the results even if the name does not match. They should call it
	 * "getElementsByNameOrId".
	 *
	 * This fix ensures that elements are only matched on their name.
	 *
	 * @function
	 * @alias module:wc/fix/getElementsByName_ie9
	 * @param {String} name The value of the name attribute we want to match.
	 * @returns {NodeList} All elements with a name attribute the value of which is an exact match for name.
	 * @ignore
	 */
	function getElementsByNameSelectorsApi(name) {
		return this.querySelectorAll("*[name='" + name + "']");
	}

	if (has("bug-getelementsbyname")) {
		var documentConstructor = window.HTMLDocument || window.Document;
		documentConstructor.prototype.getElementsByName = getElementsByNameSelectorsApi;
	}

	return getElementsByNameSelectorsApi;
});
