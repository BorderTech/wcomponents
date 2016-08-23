/**
 * Present a message to the user.
 */
define(["wc/timers"], function(timers) {
	var messageTimer;

	function formatMessage(obj) {
		if (!obj) {
			return "";
		}
		if (obj.message) {
			return obj.message;
		}
		return obj;
	}

	function showAlert(message) {
		if (messageTimer) {
			timers.clearTimeout(messageTimer);
		}
		messageTimer = timers.setTimeout(function() {
			var msg;
			if (Array.isArray(message)) {
				msg = message.map(formatMessage);
				msg = msg.join("\n");
			}
			else {
				msg = formatMessage(message);
			}
			window.alert(msg);
		}, 250);
	};

	return {
		alert: showAlert
	};
});
