/**
 * Present a message to the user.
 */

import debounce from "wc/debounce.mjs";

const prompt = {
	alert: debounce(message => alert(formatMessages(message)), 250),
	confirmAsync: debounce(confirm, 250),
	confirm: function(message, callback) {
		if (callback) {
			prompt.confirmAsync(message, callback);
		} else {
			return doConfirm(message);
		}
	}
};

function formatMessage(obj) {
	if (!obj) {
		return "";
	}
	if (obj.message) {
		return obj.message;
	}
	return obj;
}

/**
 * @param {string} message The message to format.
 * @return {string}
 */
function formatMessages(message) {
	let msg;
	if (Array.isArray(message)) {
		msg = message.map(formatMessage);
		msg = msg.join("\n");
	} else {
		msg = formatMessage(message);
	}
	return msg;
}

/**
 *
 * @param {string} message The message to present to the user.
 * @param {function(boolean): void} [callback] Called with the user choice.
 * @return {boolean}
 */
function doConfirm(message, callback) {
	const msg = formatMessages(message),
		result = confirm(msg);  // window.confirm
	if (callback) {
		try {
			callback(result);
		} catch (ex) {
			console.warn(ex);
		}
	}
	return result;
}

export default prompt;
