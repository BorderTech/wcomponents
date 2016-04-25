/**
 * Module to provide HTML5 capabilities in IE8-. NOTE: there is a free-standing version of this in the XSLT to do
 * first-load fixes which is needed to even get the transformed XML to render properly.This module is needed though to
 * make AJAX work properly in IE8 (and earlier if desperate).
 *
 * @module
 * @private
 * @requires module:wc/has
 */
define(["wc/has", "module"],
	/** @param {Object} has @param {Object} module @ignore */
	function(has, module) {
		"use strict";
		/**
		 * @function
		 * @alias module:wc/fix/html5Fix_ie8
		 * @param {Node} doc Where we can create elements in JavaScript for the IE8 fix (window.document or maybe a
		 *    DocumentFragment).
		 * @ignore
		 */
		return function(doc) {
			var i, config = (window.System) ? window.System.config : module.config(),
				elements = (has("ie") < 9) ? config.elements : [];
			for (i = 0; i < elements.length; i++) {  // NOTE: do not be tempted by Array.forEach ... IE8 does not have it and it may not be loaded yet.
				doc.createElement(elements[i]);
			}
		};
	});
