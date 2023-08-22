/**
 * A debug mode only fake module to provide DRAMATIC recolouring of the document body when the console logs a warning or
 * error. The actual style applied is determined by the CSS, this module just adds a new className to the body.
 *
 *
 * @module
 */

const noop = function() {
		// The point of this function is to invoke the debugger, so we should allow it.
		// eslint-disable-next-line no-debugger
		debugger;
	},
	console = "console";  // allow the code to be minified a little better (pointless - we are not present at all in teh minified code!)

/**
 * Override a console method to add a flagging facility to it.
 * @function logFactory
 * @private
 * @param {String} mthd The console method to apply "warn" or "error" (you could recolour on log or info but that would be silly).
 * @param {String} flag The className to add to body when this method is logged.
 * @returns {Function} A logger wrapper for console methods.
 */
function logFactory(mthd, flag) {
	const func = globalThis[console][mthd] || noop;
	return function() {
		let docBody,
			result = func.apply(this, arguments);
		if (flag && (docBody = globalThis.document?.body)) {
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

if (console in globalThis) {
	globalThis[console].warn = logFactory("warn", "wc_loggedwarn");
	globalThis[console].error = logFactory("error", "wc_loggederror");
}
export default {
	/**
	 * Clears any consoleColor flags.
	 */
	reset: function() {
		let docBody = globalThis.document?.body;
		if (docBody && ("classList" in docBody)) {
			docBody.classList.remove("wc_loggedwarn");
			docBody.classList.remove("wc_loggedwarn");
		}
	}
};
