import i18n from "wc/i18n/i18n.mjs";
import size from "wc/file/size.mjs";
import accepted from "wc/file/accepted.mjs";
import prompt from "wc/ui/prompt.mjs";

/**
 * Check a file upload complies with its constraints.
 * @param {module:wc/file/validate~args} args The DTO containing the file input to test and configuration.
 */
function check(args) {
	let message, result = [],
		selector = args.selector;
	try {
		const testObj = args.files ? { files: args.files } : null;
		if (selector) {
			if (!accepted(selector, testObj)) {
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

export default { check };

/**
 * @typedef {Object} module:wc/file/validate~args
 * @property {Element} selector The file input to validate.
 * @property {File[]} [files] Alternatively validate these files.
 * @property {boolean} notify If truthy then the user will be notified with validation messages.
 * @property {boolean} stopAtFirst If truthy then no more checks will be performed once a check has failed.
 * @property {Function} callback Will be called back with the file selector only if there are no validation issues.
 * @property {Function} errback Will be called back with the file selector and an array of validation messages (will not be called if no validation issue found).
 */
