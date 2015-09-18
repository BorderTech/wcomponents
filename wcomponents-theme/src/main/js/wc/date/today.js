/**
 * A module to wrap getting "today"  so that testing can occur on days other than the date the users computer is set to.
 * Whilst this module was designed to aid testing it is the preferred way to get "today" as a Date because it is able to
 * reliably deal with fake "todays".
 *
 * @module
 * @requires module:wc/date/copy
 */
define(["wc/date/copy"], /** @param copy wc/date/copy @ignore */ function(copy) {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/date/today.Today
	 * @private
	 */
	function Today() {
		var date = new Date();

		/**
		 * Set a fake (or not, your choice) "today". Also used to reset the real today if you have changed today
		 * at any point. NOTE: calling this function will not make you into Dr Who.
		 * @function
		 * @alias module:wc/date/today.set
		 * @static
		 * @param {Date} arg The date to set as "today".
		 */
		this.set = function(arg) {
			date = arg;
		};

		/**
		 * Get the value of "today" (which may have been set to a date other than the actual today).
		 * @function
		 * @alias module:wc/date/today.get
		 * @static
		 * @returns {Date} A new Date object equal to the set value of "today". We return a copy of today so it can
		 *    be manipulated at leisure.
		 */
		this.get = function() {
			return copy(date);
		};
	}
	return /** @alias module:wc/date/today */ new Today();
});
