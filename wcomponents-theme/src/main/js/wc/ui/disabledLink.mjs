/**
 * Provides disable-ability to HTML `a` elements.
 */

import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";

/**
 * Click event listener: prevent navigation if the link is "disabled".
 *
 * @function
 * @private
 * @param {MouseEvent} $event A click event.
 */
function clickEvent($event) {
	const { target, defaultPrevented } = $event;
	if (defaultPrevented) {
		return;
	}
	const element = target.closest("a");
	if (element && shed.isDisabled(element)) {
		$event.preventDefault();
	}
}

initialise.register({
	initialise: (element) => event.add(element, "click", clickEvent, -1)
});
