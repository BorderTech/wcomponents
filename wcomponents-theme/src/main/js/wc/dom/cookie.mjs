/**
 * A module for dealing with cookies. The methods that do all the work are based on
 * http://www.quirksmode.org/js/cookies.html
 * @constructor
 * @alias module:wc/dom/cookie~Cookie
 * @private
 **/
function Cookie() {
	let cookies;  // cache cookies, will only reload if cookies are set through this class

	/**
	 * Passing anything to "days" that equates to false will create a session cookie,
	 * otherwise you will get a persistent cookie for the duration specified.
	 *
	 * Passing anything greater than 1000 to days will be interpreted as milliseconds
	 * instead of "days".  Passing milliseconds will make this function execute significantly faster.
	 *
	 * @function module:wc/dom/cookie.create
	 * @param {String} name The key to set in the cookie
	 * @param {String} value The value to set in the cookie.
	 * @param {number} [days] How long to stroe the cookie (in days).
	 */
	this.create = function (name, value, days) {
		let expires;
		if (days) {
			if (days < 1001) {
				days = (days * 86400000);
			}
			const date = new Date();
			date.setTime(date.getTime() + days);
			expires = "; expires=" + date.toGMTString();
		} else {
			expires = "";
		}
		cookies = null;  // the cookie cache is no longer valid
		document.cookie = name + "=" + value + expires + "; path=/";
	};

	/**
	 * Gets the value associated with a given name from a cookie.
	 *
	 * @function module:wc/dom/cookie.read
	 * @param {String} name The key.
	 * @returns {String} The value associated wth the key.
	 */
	this.read = function(name) {
		const nameEQ = `${name}=`;
		if (!cookies) {
			cookies = document.cookie;
			if (cookies) {
				cookies = cookies.split(";");
			}
		}
		if (cookies) {
			for (let i = 0; i < cookies.length; i++) {
				let next = cookies[i];
				while (next.charAt(0) === " ") {
					next = next.substring(1, next.length);
				}
				if (next.indexOf(nameEQ) === 0) {
					return next.substring(nameEQ.length, next.length);
				}
			}
		}
		return null;
	};

	/**
	 * Removes a key from cookies.
	 *
	 * @function module:wc/dom/cookie.erase
	 * @public
	 * @param {String} name The key to remove.
	 */
	this.erase = function(name) {
		this.create(name, "", -1);
	};
}

export default new Cookie();
