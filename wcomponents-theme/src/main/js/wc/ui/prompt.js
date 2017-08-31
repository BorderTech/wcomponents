/**
 * Present a message to the user.
 */
define(["wc/debounce"], function(debounce) {

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
		var msg;
		if (Array.isArray(message)) {
			msg = message.map(formatMessage);
			msg = msg.join("\n");
		} else {
			msg = formatMessage(message);
		}
		return msg;
	}

	function confirm(message, callback) {
		var msg = formatMessages(message),
			result = window.confirm(msg);
		if (callback) {
			try {
				callback(result);
			} catch (ex) {
				console.warn(ex);
			}
		}
		return result;
	}


	var prompt = {
		alert: debounce(function(message) {
			var msg = formatMessages(message);
			window.alert(msg);
		}, 250),
		confirmAsync: debounce(confirm, 250),
		confirm: function(message, callback) {
			if (callback) {
				prompt.confirmAsync(message, callback);
			} else {
				return confirm(message);
			}
		}
	};
	return prompt;
});
