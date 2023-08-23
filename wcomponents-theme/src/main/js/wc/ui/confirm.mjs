/**
* Provides functionality for a confirmation button.
*/

import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import cancelButton from "wc/ui/cancelButton";
import focus from "wc/dom/focus";

const confirmSelector = "button[data-wc-btnmsg]";
const cancelButtonSelector = cancelButton.getWidget();

/**
 * Use a "confirm" to ensure the user really want to undertake an action.
 * @function
 * @private
 * @param {MouseEvent} $event The click event.
 */
function clickEvent($event) {
	const { target, defaultPrevented } = $event;
	if (defaultPrevented) {
		return;
	}
	const element = target.closest(confirmSelector);
	if (element && !element.matches(cancelButtonSelector) && focus.canFocus(element)) {
		const message = element.getAttribute("data-wc-btnmsg");
		if (message) {
			const doContinue = window.confirm(message);
			if (!doContinue) {
				$event.preventDefault();  // $event.cancel();
				// console.info("Cancelled event");
				focus.setFocusRequest(element);
			}
		} else {
			console.warn("No message found for element", element);
		}
	}
}

// rule of thumb - any event listener that has the potential to cancel the event should probably be high priority
initialise.register({ initialise: (element) => event.add(element, "click", clickEvent, -1) });
