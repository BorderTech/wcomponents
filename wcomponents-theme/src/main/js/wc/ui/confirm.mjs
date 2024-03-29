/**
* Provides functionality for a confirmation button.
*/

import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import cancelButton from "wc/ui/cancelButton.mjs";
import focus from "wc/dom/focus.mjs";

const confirmSelector = "button[data-wc-btnmsg]";
const cancelButtonSelector = cancelButton.getWidget();

/**
 * Use a "confirm" to ensure the user really want to undertake an action.
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event The click event.
 */
function clickEvent($event) {
	const { target, defaultPrevented } = $event;
	if (defaultPrevented || target?.nodeType !== Node.ELEMENT_NODE) {
		return;
	}
	/** @type HTMLButtonElement */
	const element = target.closest(confirmSelector);
	if (element && !element.matches(cancelButtonSelector) && focus.canFocus(element)) {
		const message = element.getAttribute("data-wc-btnmsg");
		if (message) {
			const doContinue = window.confirm(message);
			if (!doContinue) {
				$event.preventDefault();
				focus.setFocusRequest(element);
			}
		} else {
			console.warn("No message found for element", element);
		}
	}
}

// rule of thumb - any event listener that has the potential to cancel the event should probably be high priority
initialise.register({ initialise: (element) => event.add(element, "click", clickEvent, -1) });
