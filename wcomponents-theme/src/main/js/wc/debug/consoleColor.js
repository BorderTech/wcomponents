/**
 * A debug mode only fake module to provide DRAMATIC recolouring of the document body when the console logs a warning or
 * error. The actual style applied is determined by the CSS, this module just adds a new className to the body.
 *
 * This is not actually an AMD module but can be "required" as if it was. This is done automatically when in debug
 * mode.
 *
 * @module
 * @param {Object} g global.
 */
(function(g) {
	"use strict";
	var noop = function() {
			debugger;
		},
		console = "console";  // allow the code to be minified a little better (pointless - we are not present at all in teh minified code!)

	/**
	 * Override a console method to add a flagging facility to it.
	 * @function logFactory
	 * @private
	 * @param {String} mthd The console method to apply "warn" or "error" (you could recolour on log or info but that
	 *    would be silly).
	 * @param {String} flag The className to add to body when this method is logged.
	 * @returns {unresolved} The return value of the original console method beforeit was overridden, should be
	 *    undefined.
	 */
	function logFactory(mthd, flag) {
		var func = g[console][mthd] || noop;
		return function() {
			var docBody,
				result = func.apply(this, arguments);
			if (flag && g.document && (docBody = g.document.body)) {
				/*
				 * Check for classList without calling it because otherwise if classList logs an error
				 * or warning we would get an infinite loop :)
				 *
				 * I do not anticipate supporting any browsers that do no implement classList AND
				 * which can not be pollyfilled to support it.
				 */
				if ("classList" in docBody) {
					// css selector specificity must ensure that error style trumps warn style
					docBody.classList.add(flag);
				}
			}
			return result;
		};
	}

	if (console in g) {
		g[console].warn = logFactory("warn", "wc_loggedwarn");
		g[console].error = logFactory("error", "wc_loggederror");
	}
})(this);
