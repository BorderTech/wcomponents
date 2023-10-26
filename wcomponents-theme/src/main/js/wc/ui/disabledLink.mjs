/**
 * Provides disable-ability to HTML `a` elements.
 */

import event from "wc/dom/event.mjs";
import initialise from "wc/dom/initialise.mjs";
import shed from "wc/dom/shed.mjs";

/**
 * Click event listener: prevent navigation if the link is "disabled".
 *
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event A click event.
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
