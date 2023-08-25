/**
 * Provides HTML5 Storage wrapper.
 *
 * <p>Writing this class at a time when HTML5 storage API is just coming in.
 * Need this class ATM to hide the levels of compliance in different browsers.
 * For example, Chrome has implements localStorage but not sessionStorage.</p>
 *
 * TODO delete this module

 * @constructor
 * @alias module:wc/dom/storage~Storage
 * @private
 */
function Storage() {

	/**
	 * Put something into storage.
	 *
	 * @function module:wc/dom/storage.put
	 * @param {String} key The lookup key for this item.
	 * @param value The value to store against the lookup key.
	 * @param {boolean} [session] if true the item will only be stored for the life of the BROWSER session (not http session).
	 */
	this.put = function(key, value, session) {
		if (key) {
			if (session) {
				globalThis.sessionStorage[key] = value;
			} else {
				globalThis.localStorage[key] = value;
			}
		}
	};

	/**
	 * Get something out of storage.
	 * @function module:wc/dom/storage.get
	 * @param {String} key The lookup key
	 * @param {boolean} [session] if true the item will looked for in the session storage only, otherwise local storage.
	 * @returns {*} the value stored against the key if found.
	 */
	this.get = function(key, session) {
		let result;
		if (key) {
			if (session) {
				result = globalThis.sessionStorage[key];
			} else {
				result = globalThis.localStorage[key];
			}
		}
		return result;
	};

	/**
	 * Erase something from storage.
	 *
	 * <p>Internet Explorer 8 (the latest IE at the time of writing) has some issues with the
	 * new HTML5 storage API.</p>
	 *
	 * <p>If you try to delete a key from sessionStorage or localStorage but the
	 * key does not exist then IE8 throws an error.  Other browsers don't have
	 * this problem.</p>
	 *
	 * <p>We can either penalise ALL browsers and lookup the value before we delete it
	 * OR we can penalise IE8 only by putting the delete in a try/catch.</p>
	 *
	 * <p>Note that IE8 in IE7 mode (wrongly) provides support for HTML5 Storage API, however
	 * it does not allow you to delete items you have stored so it will ALWAYS hit the catch
	 * block which is why we set the value to "", it's the best we can do.</p>
	 *
	 * @todo revisit this now that IE8 has gone bye-byes.
	 *
	 * @function module:wc/dom/storage.erase
	 * @param {String} key THe storage key to erase.
	 * @param {Boolean} [session] Set true to erase from session storage, false for local.
	 */
	this.erase = function (key, session) {
		if (key) {
			if (session) {
				delete window.sessionStorage[key];
			} else {
				delete window.localStorage[key];
			}
		}
	};
}
export default new Storage();
