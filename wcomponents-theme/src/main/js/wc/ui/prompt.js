/**
 * Present a message to the user.
 */
define(["wc/timers"], function(timers) {
	var messageTimer;

	function debounce(func) {
		if (messageTimer) {
			timers.clearTimeout(messageTimer);
		}
		messageTimer = timers.setTimeout(func, 250);
	}

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
		}
		else {
			msg = formatMessage(message);
		}
		return msg;
	}


	return {
		alert: function(message) {
			debounce(function() {
				var msg = formatMessages(message);
				window.alert(msg);
			});
		},
		confirm: function(message, callback) {
			debounce(function() {
				var msg = formatMessages(message),
					result = window.confirm(msg);
				if (callback) {
					try {
						callback(result);
					}
					catch (ex) {
						console.warn(ex);
					}
				}
			});
		}
	};
});
