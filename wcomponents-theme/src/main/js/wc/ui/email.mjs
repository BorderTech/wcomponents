import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import sprintf from "wc/string/sprintf.mjs";
import feedback from "wc/ui/feedback.mjs";
import wcconfig from "wc/config.mjs";

const emailSelector = "input[type='email']";
const message = "Did you mean '%s'?";
let mailchecker;

function getMailChecker(view) {
	if (mailchecker) {
		return Promise.resolve(mailchecker);
	}
	return new Promise((win, lose) => {
		try {
			view.define = globalThis.define = function(name, deps, cb) {
				if (name === "mailcheck" && deps.length === 0) {
					mailchecker = cb();
					view.define = globalThis.define = null;
					win(mailchecker);
				}
			};
			view.define.amd = true;
		} catch (ex) {
			lose(ex);
		}
		import("mailcheck/src/mailcheck.js");  // mailcheck should call fake AMD define above
	});
}

/**
 * Checks an email input and detects common typos.
 * @param {UIEvent & {target: HTMLInputElement}} $event A form element.
 */
function emailCheck({ target }) {
	const options = wcconfig.get("wc/ui/email", {});

	if (target.matches(emailSelector)) {
		options.email = target.value;
		options.suggested = (suggestion) => {
			const newEmail = suggestion["full"];
			if (newEmail) {
				/*
				 * Removing any success messages from any other validation actions.
				 * I think these should auto-disappear anyway, but currently they don't.
				 */
				feedback.remove(target, null, feedback.LEVEL.SUCCESS);
				feedback.flagInfo({
					element: target,
					message: sprintf(message, newEmail)
				});
			}
		};
		options.empty = () => feedback.remove(target, null, feedback.LEVEL.INFO);
		getMailChecker(target.ownerDocument.defaultView).then(mailcheck => mailcheck.run(options));
	}
}

initialise.register({
	initialise: element => {
		event.add(element, { type: "change", listener: emailCheck, capture: true })
	}
});
