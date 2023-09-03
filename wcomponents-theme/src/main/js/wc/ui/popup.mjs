/**
 * Provides two separate but related pieces of behaviour:
 * <ol><li>Allows the creation of "popup links" ie elements that open a popup window
 *    from WLink with windowAttributes, WContentButton, WMenuItem etc.;</li>
 * <li>Open a popup window from WPopup which is a horrible mechanism to open one or more popup window(s) on page load.</li>
 * </ol>
 *
 * <p><strong>NOTE:</strong> popups can have terrible accessibility and usability consequences and should be used
 * with extreme caution. If a WButton action will result in a pop-up window being opened on page load that WButton
 * must have its popupTrigger property set to true (using `button.setpopupTrigger(true);` in Java)</p>
 */

import event from "wc/dom/event";
import initialise from "wc/dom/initialise";
import uid from "wc/dom/uid";
import timers from "wc/timers";

const processQueueDelay = 1000,
	URL_INDEX = 0,
	NAME_INDEX = 1,
	SPECS_INDEX = 2,
	SPACE = (/\W/g),
	popperQs = "button[aria-haspopup='true'][data-wc-url]";

const instance = {
	/**
	 * Indicates if an element is a pop-up trigger. This is required by {@link module:wc/ui/navigationButton}
	 * (amongst others) to prevent navigation if the navigation button is also a pop-up creator.
	 *
	 * @function module:wc/ui/popup.isOneOfMe
	 * @public
	 * @param {Element} element the element to test if it is a popup trigger.
	 * @returns {Boolean} true if element is a popup trigger.
	 */
	isOneOfMe: function(element) {
		return element.matches(popperQs);
	},

	/**
	 * Register all popups to open later.
	 * @function module:wc/ui/popup.register
	 * @public
	 * @param {Array} popupArray An array of popup description arrays (one description per popup). This is
	 *    basically an array of arrays and should be looked into.
	 */
	register: function (popupArray) {
		if (popupArray && popupArray.length) {
			initialise.addCallback(() => timers.setTimeout(processQueue, processQueueDelay, popupArray));
		}
		/**
		 * Process any queued calls to "open" and open the popups now.
		 * @function
		 * @private
		 * @param {Array} popupQueue An array of popup definition arrays.
		 * @todo This should now be anonymized into the register timout.
		 */
		function processQueue(popupQueue) {
			while (popupQueue.length) {
				_open(popupQueue.shift());
			}
		}
	}
};

/**
 * Does the popup, includes an IE sanity check on window name.
 * A popup can occur from a load-time (or ajax-received) JavaScript Array or from a direct button click
 * which is why there are a few steps between this function and the click event listener. Don't try to be
 * too clever and conflate to two.
 * @function
 * @private
 *
 * @param {String[]} infoArr the popup info as an array where the array elements are in the order as defined
 *    in the class variables above.
 * @todo This array in a particular order is a bit fragile, consider using an object instead.
 */
function _open(infoArr) {
	let name = infoArr[NAME_INDEX] || uid();  // we MUST have a name so generate one if it was set to ""
	if (SPACE.test(name)) {
		console.warn("Removing non-word-characters from window name");
		name = name.replace(SPACE, "");
		if (!name) {  // If the original name was only invalid characters we will need to generate a new one
			name = uid();
		}
	}
	const specs = infoArr[SPECS_INDEX];
	// NOTE: new issue found in IE8 after an update in March 2014!! window.open(url, name, null) no longer has the same effect as window.open(url, name);
	if (specs) {
		window.open(infoArr[URL_INDEX], name, specs);
	} else {
		window.open(infoArr[URL_INDEX], name);
	}
}

/**
 * Helper for the click event handler. We are still not doing the actual popping here - this is for clicking
 * on buttons so will only open the popup if the button has a URL stand-in `data-` attribute.
 * @function
 * @private
 * @param {HTMLElement} element The popup trigger which was clicked.
 */
function popupNow(element) {
	const url = element.getAttribute("data-wc-url");
	if (url) {
		_open([url, element.getAttribute("data-wc-window"), element.getAttribute("data-wc-specs")]);
	}
}

/**
 * A click event listener to open a popup window from specific buttons.
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event a click event.
 */
function clickEvent($event) {
	if ($event.defaultPrevented) {
		return;
	}
	/** @type HTMLButtonElement */
	const element = $event.target.closest(popperQs);
	if (element && !element.disabled) {
		popupNow(element);
		$event.preventDefault();
	}
}

initialise.register({
	/**
	 * Initialise for popups, add a click event handler
	 * @function module:wc/ui/popup.initialise
	 * @public
	 * @param {HTMLElement} element the HTML Element being initialised (document.body in practice).
	 */
	initialise: function(element) {
		event.add(element, "click", clickEvent);
	}
});
export default instance;
