/**
 * Provides print button functionality. Almost completely worthless.
 */

import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";

/**
 * Click listener to invoke the print dialog. Kill me now!
 * @function
 * @private
 * @param {MouseEvent} $event The click event.
 */
function clickEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}
	const element = $event.target.closest("button.wc-printbutton");
	if (element && !shed.isDisabled(element)) {
		$event.preventDefault();
		print();
	}
}

initialise.register({
	/**
	 * Initialise print button functionality.
	 * @param {HTMLBodyElement} element The element being initialised: document.body.
	 */
	initialise: (element) => event.add(element, "click", clickEvent)
});
