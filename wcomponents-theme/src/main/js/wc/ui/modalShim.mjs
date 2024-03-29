import uid from "wc/dom/uid.mjs";
import event from "wc/dom/event.mjs";
import focus from "wc/dom/focus.mjs";
import timers from "wc/timers.mjs";
import Observer from "wc/Observer.mjs";

export const MODAL_BACKGROUND_ID = "wc-shim";
const UNIT = "px";
const accessKeySelector = "[accesskey]";
const AKEY = "accesskey";
const ALTERNATE_OBSERVER_GROUP = "shimshow";

let SHIFTKEY_ON = false;
let accessKeyMap = {};
let activeElement;
let observer;

/**
 * Used to overlay the page with a shim which blocks mouse access to the components on the page and steals focus back
 *    if it loses it (e.g. due to TAB key or access keys).
 */
const instance = {
	/**
	 * Prevents interaction with the current browser window except for the active region.
	 * @function module:wc/ui/modalShim.setModal
	 * @param {Element} [activeRegion] The region the user is allowed to interact with. Defaults to the shim
	 *    (i.e. no interaction allowed).
	 * @param {String} [className] Additional class to add to the shim.
	 */
	setModal: function(activeRegion, className) {
		const shimElement = getShim(true);
		shimElement.style.height = document.documentElement.scrollHeight + UNIT;
		shimElement.removeAttribute("hidden");
		activeElement = activeRegion || shimElement;
		addRemoveEvents(true);
		if (className) {
			shimElement.classList.add(className);
		}

		// remove the access key attribute from controls with access keys which are not in the activeRegion
		Array.from(document.querySelectorAll(accessKeySelector)).forEach(next => {
			const nextId = next.id || (next.id = uid());
			if (activeRegion && !elementContains(activeRegion, next)) {
				accessKeyMap[nextId] = next.getAttribute(AKEY);
				next.removeAttribute(AKEY);
			}
		});

		if (observer) {
			observer.setFilter(ALTERNATE_OBSERVER_GROUP);
			observer.notify(activeElement);
		}
	},

	/**
	 * Removes the modal shim.
	 * @function module:wc/ui/modalShim.clearModal
	 */
	clearModal: function() {
		let notify;
		try {
			const shimElement = getShim(false);
			if (shimElement && !shimElement.hidden) {
				addRemoveEvents();
				shimElement.className = "";
				for (let key in accessKeyMap) {
					const aKeyElement = document.getElementById(key);
					if (aKeyElement) {
						aKeyElement.setAttribute(AKEY, accessKeyMap[key]);
					}
				}
				shimElement.hidden = true;
			}
			notify = true;
		} finally {
			activeElement = null;
			accessKeyMap = {};
			if (notify && observer) {
				observer.notify();
			}
		}
	},

	/**
	 * Allow external module to subscribe to this module's Observer instance to be informed when a modal shim is removed.
	 *
	 * If subscribing to `show` then the notification will include an arg of the activeElement. This is wither the active region passed in to
	 * `showModal` or the shim element if `showModal` is called without an `activeRegion` arg.
	 *
	 * If subscribing to `clear` (the default) then the notification will have no arguments.
	 *
	 * @function module:wc/ui/modalShim.subscribe
	 * @public
	 * @param {Function} subscriber the function to subscribe
	 * @param {boolean} [onshow] if true notify when the modalShim is shown, otherwise notify when the shim is removed
	 * @returns {Function} the subscribed function
	 */
	subscribe: function(subscriber, onshow) {
		let group = null;
		if (!observer) {
			observer = new Observer();
		}
		if (onshow) {
			group = { group: ALTERNATE_OBSERVER_GROUP };
		}
		return observer.subscribe(subscriber, group);
	},

	/**
	 * Unsubscribe from this observer instance.
	 * @function module:wc/ui/modalShim.unsubscribe
	 * @public
	 * @param {Function} subscriber the function to unsubscribe
	 * @param {boolean} [onshow] if true unsubscribe from the group notified when the modalShim is shown. The 'unsubscribe' will only succeed if
	 * the group is the same as when the subscriber was subscribed.
	 * @returns {Function} the unsubscribed function
	 */
	unsubscribe: function(subscriber, onshow) {
		if (observer) {
			const group = onshow ? ALTERNATE_OBSERVER_GROUP : null;
			return observer.unsubscribe(subscriber, group);
		}
		return null;
	}
};

/**
 * If the user is shift-tabbing their way back through the dialog we want to wrap focus around to the last
 * tabstop, not the first. To do this we need to track the state of the shift key because focus event does
 * not report shift key flag.
 *
 * @function
 * @private
 * @param {KeyboardEvent} $event The keydown event.
 */
function keyEvent($event) {
	SHIFTKEY_ON = $event.shiftKey;
}

/**
 * Returns truthy if the container contains, or is identical to, the element.
 * @param container The candidate for container.
 * @param element The candidate for contained.
 * @returns truthy if container contains, or is, element.
 */
function elementContains(container, element) {
	let result = container === element;
	// noinspection JSBitwiseOperatorUsage
	return result || (element.compareDocumentPosition(container) & Node.DOCUMENT_POSITION_CONTAINS);
}

/**
 * The activeElement (or something within it) has received focus. Cancel any outstanding calls to refocus it.
 * @function
 * @private
 * @param {KeyboardEvent} $event The focus event.
 */
function focusEvent($event) {
	if (!$event.defaultPrevented && activeElement) {
		if (!elementContains(activeElement, $event.target)) {
			timers.setTimeout(focus.focusFirstTabstop, 0, activeElement, null, SHIFTKEY_ON);
		}
	}
}

/**
 * Use the shim to prevent touch events by cancelling touchstart.
 * @function
 * @private
 * @param {TouchEvent} $event The touch start event.
 */
function touchstartEvent($event) {
	if (!$event.defaultPrevented && activeElement &&
		!elementContains(activeElement, $event.target)) {
		$event.preventDefault();
	}
}

/**
 * Attaches or detaches the events required by the modal shim.
 * @param {Boolean} [add] true to add the events, otherwise remove them.
 */
function addRemoveEvents(add) {
	const HAS_EVENTS = "wc/ui/modalShim.wired";
	const element = document.body;
	if (!(add && element[HAS_EVENTS])) {
		const action = add ? "add" : "remove";
		element[HAS_EVENTS] = add;
		event[action](element, "keydown", keyEvent, false);
		event[action](element, "keyup", keyEvent, false);
		if (add) {
			event[action](element, "focus", focusEvent, null, null, true);
			event[action](document.body, "touchstart", touchstartEvent, null, null, true);
		} else {
			event[action](element, "focus", focusEvent, true);
			event[action](document.body, "touchstart", touchstartEvent, true);
		}
	}
}

/**
 * @param {boolean} doCreate if true, will create the shim when necessary
 * @return {HTMLElement}
 */
function getShim(doCreate) {
	const result = document.getElementById(MODAL_BACKGROUND_ID);
	if (doCreate && !result) {
		return create();
	}
	return result;
}

/**
 * create a new modalShim if required.
 * @function
 * @private
 * @returns {HTMLElement} The shim element.
 */
function create() {
	const d = document,
		b = d.body,
		result = d.createElement("div");
	result.id = result.dataset["testid"] = MODAL_BACKGROUND_ID;
	result.hidden = true;
	if (b.firstChild) {
		b.insertBefore(result, b.firstChild);
	} else {
		b.appendChild(result);
	}
	return result;
}

export default instance;
