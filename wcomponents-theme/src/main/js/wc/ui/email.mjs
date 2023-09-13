import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import sprintf from "lib/sprintf";
import feedback from "wc/ui/feedback.mjs";
import wcconfig from "wc/config.mjs";
import mailcheck from "mailcheck";

const emailSelector = "input[type='email']";
const message = "Did you mean '%s'?";

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
					message: sprintf.sprintf(message, newEmail)
				});
			}
		};
		options.empty = () => feedback.remove(target, null, feedback.LEVEL.INFO);

		mailcheck.run(options);
	}
}

export default initialise.register({
	initialise: (element) => event.add(element, { type: "change", listener: emailCheck, capture: true })
});
