import event from "wc/dom/event";
import focus from "wc/dom/focus";
import formUpdateManager from "wc/dom/formUpdateManager";
import initialise from "wc/dom/initialise";
import shed from "wc/dom/shed";
import timers from "wc/timers";
import isEventInLabel from "wc/dom/isEventInLabel";
import isAcceptableEventTarget from "wc/dom/isAcceptableTarget";
import $role from "wc/dom/role";
import icon from "wc/ui/icon";

const containerSelector = "details";
const headerSelector = `${containerSelector} > summary`;

/**
 * Module to control collapsible sections. These use a DETAILS element which has some functionality in some modern
 * browsers. Eventually this module should become redundant.
 */
const instance = {
	/**
	 * Indicates if a given element is a collapsible.
	 *
	 * @param {Element} element The element to test.
	 * @param {Boolean} [onlyContainer] If true then we only want to know if the element is a collapsible
	 *    container element; if explicitly false it is the header/trigger element and if undefined whether
	 *    it is either of these.
	 * @returns {Boolean} true if element matches the required type.
	 */
	isOneOfMe: function (element, onlyContainer) {
		let result;
		if (onlyContainer) {
			result = element.matches(containerSelector);
		} else if (onlyContainer === false) {
			result = element.matches(headerSelector);
		} else {
			result = element.matches([headerSelector, containerSelector].join());
		}
		return result;
	},
	/**
	 * Get the trigger element from a container element.
	 *
	 * @param {Element} element The start element.
	 * @returns {HTMLElement} If the start element is a collapsible container return its header/trigger element.
	 */
	getActionElement: function (element) {
		if (element.matches(containerSelector)) {
			return element.querySelector(headerSelector);
		}
		return null;
	}
};

/**
 * Toggles the expanded/collapsed state of a collapsible. If native then publishes shed.actions.EXPAND or
 * shed.actions.COLLAPSE as required to ensure AJAX is invoked.
 *
 * @todo Use a state change to publish rather than having to wire up click etc here.
 *
 * @function
 * @private
 * @param {Element} element A collapsible trigger element.
 */
function toggle(element) {
	const container = element.closest(containerSelector);
	let isOpen;

	/*
	 * Do this in a timeout to allow us to use the details element and cater for browsers with no native
	 * support and browsers with native support which is not completely accessible (eg mouse only or no
	 * inbuilt arrow key triggering).
	 */
	function checkDoToggle() {
		const isStillOpen = shed.isExpanded(container);

		if (isOpen) {
			if (isStillOpen) {
				shed.collapse(container);
			} else {
				shed.publish(container, shed.actions.COLLAPSE);
			}
		} else if (!isStillOpen) {
			shed.expand(container);
		} else {
			shed.publish(container, shed.actions.EXPAND);
		}
	}

	if (container) {
		isOpen = shed.isExpanded(container);
		timers.setTimeout(checkDoToggle, 0);
	}
}

/**
 * Helper function for key and click initiated collapse toggling. Used to determine if the event is
 * expected to change the state of the WCollapsible or some other nested interactive control. If we are able
 * to act on element, or find a different focusable element before we get to element then return that
 * element, so we can use it to work out if we have to prevent default on SPACEBAR
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event The event which initiated the toggle.
 * @param {HTMLElement} element A collapsible header. Element must already have been determined to match
 *    headerSelector and since we have already extracted this from $event we may as well pass it in as
 *    an arg rather than re-testing.
 * @returns {HTMLElement} The first interactive ancestor element of the event target if any. This may or may
 *    not be the collapsible header.
 */
function toggleEventHelper($event, element) {
	const target = $event.target;
	let result;
	if (!isEventInLabel(target)) {
		let focusableAncestor;
		// do not toggle collapsible if the event is supposed to be for a label
		if (isAcceptableEventTarget(element, target)) {
			toggle(element);
			return element;
		} else if ((focusableAncestor = focus.getFocusableAncestor(target))) {
			if (focusableAncestor !== target) {
				$event.preventDefault();
				timers.setTimeout(event.fire, 0, focusableAncestor, "click");
			} else {
				result = focusableAncestor;
			}
		}
	}
	return result;
}

/**
 * Click event handler to toggle the state of a collapsible if the SUMMARY is clicked. NOTE: this is
 * required when native support is available as our toggle method is also responsible for triggering AJAX on
 * open.
 *
 * @function
 * @private
 * @param {MouseEvent & { target: HTMLElement }} $event A click event.
 */
function clickEvent($event) {
	/** @type {HTMLElement} */
	const element = $event.defaultPrevented ? null : $event.target.closest(headerSelector);
	if (element) {
		toggleEventHelper($event, element);
	}
}

/**
 * Listen for shed.actions. When DISABLE  re-enable the summary element (the content will get the disable call
 * from its ancestor if a WCollapsible is "disabled" by subordinate). The summary is not "disableable"
 * (WCollapsible does not implement disableable) but the SUMMARY element is exposed as a button for
 * non-HTML5 aware AT so get disabled due to its role of "button". We do not republish the enable action
 * though as we do not need to apply the same rule to nested disableable components.
 *
 * When the action is "expand" or "collapse" and the SUMMARY has a role then update the aria-expanded
 * attribute.
 *
 * @function
 * @private
 * @param {Element} element the element being selected.
 * @param {String} action The shed action being pne of "disable", "expand" or "collapse".
 */
function shedSubscriber(element, action) {
	let header;
	// Can't use optional chaining until we get off R.js
	if (element?.matches(containerSelector) && (header = element.querySelector(headerSelector))) {
		if (action === shed.actions.DISABLE) {
			if (shed.isDisabled(header)) {
				shed.enable(header, true);
			}
		} else if ($role.get(header) === "button") {
			header.setAttribute("aria-expanded", (action === shed.actions.EXPAND).toString());
		}

		if (action === shed.actions.EXPAND) {
			icon.change(header, "fa-caret-down", "fa-caret-right");
		} else if (action === shed.actions.COLLAPSE) {
			icon.change(header, "fa-caret-right", "fa-caret-down");
		}
	}
}

/**
 * Write the state of collapsible sections.
 * @function
 * @private
 * @param {Element} container The container of the components whose state we are writing.
 * @param {Element} stateContainer Where to put the state fields.
 */
function writeState(container, stateContainer) {
	/**
	 * @param {Element} $element
	 */
	function writeStateCollapsible($element) {
		const val = shed.isExpanded($element) ? "open" : "closed";
		formUpdateManager.writeStateField(stateContainer, $element.id, val, false, true);
	}

	Array.from(container.querySelectorAll(containerSelector)).forEach(writeStateCollapsible);
	if (container.matches(containerSelector)) {
		writeStateCollapsible(container);
	}
}

const initiliaser = {
	/**
	 * Helper for late initialising and de-initialising this module.
	 * @param {boolean} init true if initialising, otherwise deinitialising.
	 * @function
	 * @private
	 */
	_postInit: function (init) {
		const su = init ? "subscribe" : "unsubscribe";
		shed[su](shed.actions.DISABLE, shedSubscriber);
		shed[su](shed.actions.EXPAND, shedSubscriber);
		shed[su](shed.actions.COLLAPSE, shedSubscriber);
		formUpdateManager[su](writeState);
	},

	/**
	 * Helper for initialising and de-initialising this module.
	 * @param {boolean} init true if initialising, otherwise deinitialising.
	 * @param {Element} element The element being de/initialised, usually document.body.
	 * @function
	 * @private
	 */
	_initialiseHelper: function (init, element) {
		const func = init ? "add" : "remove";
		event[func](element, "click", clickEvent);
	},

	/**
	 * Initialisation: wire up focus and click listeners on the BODY.
	 *
	 * @param {HTMLBodyElement} element
	 */
	initialise: element => initiliaser._initialiseHelper(true, element),

	/**
	 * Late initialiser callback to set up the shed subscribers. This is required because we have to give the
	 * SUMMARY element a role of button to expose its behaviour to non-HTML5 aware AT but this role makes the
	 * collapsible disable-able which is not a facet of a collapsible we want to encourage.
	 *
	 */
	postInit: () => initiliaser._postInit(true),

	/**
	 * Unsubscribes event listeners etc.
	 * @param {HTMLBodyElement} element The element being deinitialised.
	 */
	deinit: function(element) {
		initiliaser._postInit(false);
		initiliaser._initialiseHelper(false, element);
	}
};

initialise.register(initiliaser);
export default instance;
