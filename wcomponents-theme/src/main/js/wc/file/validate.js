define(["wc/i18n/i18n", "wc/file/size", "wc/file/accepted", "wc/ui/prompt"], function(i18n, size, accepted, prompt) {

	/**
	 * Check a file upload complies with its constraints.
	 * @param {module:wc/file/validate~args} args The DTO containing the file input to test and configuration.
	 */
	function check(args) {
		var message, result = [],
			testObj,
			selector = args.selector;
		try {
			if (args.files) {
				testObj = {
					files: args.files
				};
			}
			if (selector) {
				if (!accepted(selector)) {
					message = i18n.get("file_wrongtype", selector.accept);
					result.push(message);
				}
				if (!args.stopAtFirst || result.length < 1) {
					message = size.check({
						element: selector,
						testObj: testObj
					});
					if (message) {
						result.push(message);
					}
				}
			}
		} catch (ex) {
			// If validation results in an error it's best to let it go to the server (where it will be validated anyway)
			console.error(ex);
		}
		if (result.length) {
			if (args.errback) {
				args.errback(selector, result);
			}
			if (args.notify) {
				message = result.join("\n");
				prompt.alert(message);
			}
		} else if (args.callback) {
			args.callback(selector);
		}
	}

	return {
		check: check
	};

	/**
	 * @typedef {Object} module:wc/file/validate~args
	 * @property {Element} selector The file input to validate.
	 * @property {File[]} [files] Alternatively validate these files.
	 * @property {boolean} notify If truthy then the user will be notified with validation messages.
	 * @property {boolean} stopAtFirst If truthy then no more checks will be performed once a check has failed.
	 * @property {Function} callback Will be called back with the file selector only if there are no validation issues.
	 * @property {Function} errback Will be called back with the file selector and an array of validation messages (will not be called if no validation issue found).
	 */
});
