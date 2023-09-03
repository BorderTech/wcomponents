/**
 * Allows a button to be a link by using its value as the href. The reason we do this instead of just styling a link to
 * look like a button is because we do not know how the button will appear - do we style to button to look like a button
 * in Gnome? KDE? Windows XP? Mac? So we need to use a real button and make it behave like a link.
 */

import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import popup from "wc/ui/popup";
import cancelUpdate from "wc/ui/cancelUpdate";
import shed from "wc/dom/shed";
import launchLink from "wc/ui/launchLink";
import redirect from "wc/ui/redirect";

const buttonQs = "button[data-wc-url]";
const cancelButtonQs = "button.wc_btn_cancel[data-wc-url]";

/**
 * Click listener which intercepts the button action and instigates navigation as the action.
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLButtonElement }} $event The click event.
 */
function clickEvent($event) {
	const { target, defaultPrevented } = $event;
	if (defaultPrevented) {
		return;
	}
	/** @type {HTMLButtonElement} */
	const element = target.closest(buttonQs);
	const url = element?.getAttribute("data-wc-url");

	if (url && !shed.isDisabled(element) && !popup.isOneOfMe(element) && !launchLink.isInlineLink(element)) {
		const form = element.matches(cancelButtonQs) ? element.closest("form") : null;
		if (form && cancelUpdate.cancelSubmission(form)) {
			$event.preventDefault();
		} else if (redirect.isLaunchUrl(url)) {
			$event.preventDefault();  // since we have prevented the link action we're going to have to do it ourselves
			redirect.register(url);  // redirect (poorly named) knows how to "do stuff" with URLs.
		} else {
			window.location.href = url;
			$event.preventDefault();
		}
	}
}

initialise.register({
	/**
	 * Initialise the functionality by adding a click listener.
	 * @function  module:wc/ui/navigationButton.initialise
	 * @param {HTMLBodyElement} element The element being initialised, usually document.body.
	 */
	initialise: function(element) {
		event.add(element, "click", clickEvent);
	}
});
