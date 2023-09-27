/**
 * Module to provide key press information.
 * @module
 */
export default {
	/**
	 * Determine if this is a "META" key.
	 * @param {string|KeyboardEvent} key The pressed key (can be the  KeyboardEvent itself or its `code` or `key` property).
	 * @returns {Boolean} true if the key is either ALT, CTRL, META or SHIFT
	 */
	isMeta: function(key) {
		if (key) {
			const metas = ["Alt", "Control", "Meta", "Shift"];
			/** @type string */
			const keyName = key["key"] || key["code"] || key;
			return metas.some(meta => keyName.startsWith(meta));
		}
		return false;
	}
};
