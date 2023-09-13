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
			return confirm(message);
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

function confirm(message, callback) {
	const msg = formatMessages(message),
		result = confirm(msg);
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
