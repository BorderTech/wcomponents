/**
 * Module to provide the point at which a two digit year rolls forwards/backwards when converting to a four digit
 * year.
 *
 * When expanding a 2 digit year into a full 4 digit one a sliding window is calculated on a 'pivot' point. The
 * sliding window determines which century the 2 digit year belongs to. If the number falls within the window of
 * "this year plus pivot" then it is this century. Otherwise, it is last century. The 'sliding' nature comes from the
 * fact that it changes based on today's date. The pivot can be a number from 1 to 100, the default is 15, which reflects
 * the fact that two digit years tend to be used to reference past dates or dates in the relatively near future.
 *
 */

/**
 * The pivot window. Defaults to 15.
 *
 * @var {number}
 * @private
 * @type {number}
 */
let pivotWindow = 15;

/**
 * Set the pivot window to something other than the 15-year default.
 *
 * @function
 * @alias module:wc/date/pivot.set
 * @param {number} value The value of the pivot window, must be between 0 and 101 exclusive.
 * @throws {RangeError} Thrown if the value is outside the range 1 to 100 inclusive.
 */
function set(value) {
	if (value > 0 && value < 101) {
		pivotWindow = value;
	} else {
		throw new RangeError("value must be between 1 and 100 (inclusive)");
	}
}

/**
 * Get the current value of the pivot window.
 *
 * @function
 * @alias module:wc/date/pivot.get
 * @returns {number} The current pivot window.
 */
function get() {
	return pivotWindow;
}

export default { get, set };
