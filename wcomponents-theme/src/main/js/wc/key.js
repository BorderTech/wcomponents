/**
 * Module to provide key press information.
 * @module
 */
define(function() {
	"use strict";
	/**
	 * @constructor
	 * @alias module:wc/key~Key
	 * @private
	 */
	function Key() {
		var lookup;

		/**
		 * Helper for getLiteral which build a reverse lookup map.
		 * @function
		 * @private
		 * @returns {Array} An array of KeyEvent property names.
		 */
		function buildReverseLookup() {
			var next,
				result = [];
			for (next in KeyEvent) {
				result[KeyEvent[next]] = next;
			}
			return result;
		}

		/**
		 * Get the literal representing a code.
		 *
		 * @function module:wc/key.getLiteral
		 * @param {Number} code The keycode to look up.
		 * @returns {String} The literal string representing this keycode
		 */
		this.getLiteral = function (code) {
			return (lookup || (lookup = buildReverseLookup()))[code];
		};

		/**
		 * Get the keys pressed in an event.
		 *
		 * @function module:wc/key.getKeysPressed
		 * @param {Event} $event A DOM Event
		 * @returns {String} Key literals separated by '+' representing keys pressed in this event
		 *    modifier keys(alt, ctrl, meta, shift) are listed first but otherwise listed alphabetically,
		 *    e.g. 'CTRL+ARROW_UP', and 'ALT+CTRL+ARROW_UP' but not 'CTRL+ALT+ARROW_UP'
		 */
		this.getKeysPressed = function ($event) {
			var result,
				pressed = [],
				keyCode = $event.keyCode;

			if ($event.altKey) {
				pressed[pressed.length] = "DOM_VK_ALT";
			}
			if ($event.ctrlKey) {
				pressed[pressed.length] = "DOM_VK_CONTROL";
			}
			if ($event.metaKey) {
				pressed[pressed.length] = "DOM_VK_META";
			}
			if ($event.shiftKey) {
				pressed[pressed.length] = "DOM_VK_SHIFT";
			}

			if (!this.isMeta(keyCode)) {
				pressed[pressed.length] = (lookup || (lookup = buildReverseLookup()))[keyCode];
			}
			result = pressed.join("+");
			return result;
		};

		/**
		 * Determine if this is a "META" key.
		 * @param {number} keyCode The pressed key.
		 * @returns {Boolean} true if the key is either ALT, CTRL, META or SHIFT
		 */
		this.isMeta = function(keyCode) {
			return (keyCode === KeyEvent.DOM_VK_ALT || keyCode === KeyEvent.DOM_VK_CONTROL ||
						keyCode === KeyEvent.DOM_VK_META || keyCode === KeyEvent.DOM_VK_SHIFT);
		};
	}
	return /** @alias module:wc/key */ new Key();
});
