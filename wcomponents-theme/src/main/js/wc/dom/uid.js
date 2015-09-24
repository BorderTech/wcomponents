/**
 * @module
 */
define(function() {
	"use strict";

	var id = 0,  // counter required otherwise ids will not be unique if generated in the same millisecond
		PREFIX = "uid";

	/**
	 * Each call to uid() returns a unique id. Unique IDs are sortable, that is each subsequent ID is greater than
	 * any previously generated IDs.
	 *
	 * Many elements have no assigned ID but require one for associating the element with an object/array (to avoid
	 * direct pointers why may cause memory leaks in some browsers). This uses date to make the ID unique in time,
	 * so that if for example a page source is viewed and copied (along with its uids) they would not clash with any
	 * later generated values.
	 *
	 * @function
	 * @alias module:wc/dom/uid
	 * @returns {String} A unique identifier.
	 */
	function uid() {
		return (PREFIX + (++id)) + Date.now();
	}
	return uid;
});
