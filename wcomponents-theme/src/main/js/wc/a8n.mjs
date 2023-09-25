/**
 * This module provides affordances for automated testing tools.
 */

import initialise from "wc/dom/initialise.mjs";
import ajax from "wc/ajax/ajax.mjs";
import Trigger from "wc/ajax/Trigger.mjs";
import timers from "wc/timers.mjs";
import Observer from "wc/Observer.mjs";
import fixes from "wc/fixes.mjs";

let observer,
	timer,
	globalPending = 0;

const instance = {
		subscribe: subscribe,
		unsubscribe: unsubscribe,
		clearSubscribers: clearSubscribers,
		isReady: isFlaggedReady,
		onReady: onReady,
		attr: "data-wc-domready"
	},
	flags = {
		AJAX: 1,
		TIMERS: 2,
		AJAX_TRIGGER: 4,
		DOM_READY: 8,
		FIXES: 16
	};

timers._subscribe(pendingTimers);

/*
 * Subscriber for pending AJAX requests.
 */
ajax.subscribe((pending) => pendingUpdated(pending, flags.AJAX));
Trigger.subscribe(pendingAjaxTrigger);
Trigger.subscribe(pendingAjaxTrigger, -1);


/*
 * Subscriber for pending AJAX requests that will update the DOM.
 */
function pendingAjaxTrigger(trigger, pending) {
	// console.log("TRIGGER ", pending);
	pendingUpdated(pending, flags.AJAX_TRIGGER);
}

/*
 * Subscriber for pending timeouts.
 */
function pendingTimers(pending) {
	// console.log("TIMER ", pending);
	pendingUpdated(pending, flags.TIMERS);
}

function waitForFixes() {
	const needsFixes = fixes.length;
	const fixesLoaded = () => pendingUpdated(false, flags.FIXES);
	if (needsFixes) {
		pendingUpdated(true, flags.FIXES);
		try {
			Promise.all(fixes.map(fix => import(fix))).then(fixesLoaded).catch(fixesLoaded);
		} catch (ex) {
			fixesLoaded();
		}
	}
}

/**
 * Called by subscribers when they are notified of a possible change in state.
 * @param {boolean} pending
 * @param {number} flag
 */
function pendingUpdated(pending, flag) {
	if (timer) {
		clearTimeout(timer);
	}
	if (flag !== null) {
		if (pending) {
			globalPending |= flag;
		} else {
			globalPending &= ~flag;
		}
	}
	checkNotify();
}

function checkNotify() {
	const element = document.body;
	if (element) {
		const isReady = !globalPending;  // When nothing is pwnding it will be zero
		const currentState = instance.isReady();
		if (isReady !== currentState) {
			if (timer) {
				clearTimeout(timer);
			}
			const notify = stateChangeFactory(element, instance.attr);
			if (!isReady) {  // If the DOM is busy we want to notify ASAP
				notify();
			} else {  // If the DOM is ready notify "soon" in case another action is about to start
				const delay = localStorage["wc.a8n.delay"] || 251;  // String should be ok without casting...
				timer = setTimeout(notify, delay);
			}
		}
	}
}

/*
 * Handle a change of state from ready to busy and vice versa.
 */
function stateChangeFactory(element, attr) {
	return function () {
		const isReady = !globalPending;
		element.setAttribute(attr, isReady);
		if (observer) {
			observer.notify(isReady);
			if (isReady) {
				observer.setFilter("onready");
				observer.notify();
				observer.reset("onready");  // this is a one-shot group
			}
		}
	};
}

/**
 * Determine if the page is "ready"
 * @returns {Boolean} true if the page is ready
 */
function isFlaggedReady() {
	const element = document.body;
	return (element && element.getAttribute(instance.attr) === "true");
}

/**
 * Remove ALL subscribers.
 */
function clearSubscribers() {
	if (observer) {
		observer.reset();
		observer.reset("onready");
	}
}

/**
 * Remove this specific subscriber.
 * @param {Function} subscriber
 */
function unsubscribe(subscriber) {
	if (!subscriber || !observer) {
		return;
	}
	return observer.unsubscribe(subscriber);
}

/**
 * This is a "one shot" subscribe - your callback will be called when a8n is next "ready" and then discarded.
 * If a8n is ready now the callback will be called  without waiting for any further state changes.
 * @param {Function} callback The function to call on ready.
 */
function onReady(callback) {
	if (callback) {
		if (instance.isReady()) {
			setTimeout(callback, 0);
		} else {
			if (!observer) {
				observer = new Observer();
			}
			observer.subscribe(callback, {group: "onready"});
		}
	}
}

/**
 * Subscribers will be called any time the global ready state changes.
 * They will be passed a boolean, true means ready, false means not ready.
 * @param {Function} subscriber
 */
function subscribe(subscriber) {
	if (!subscriber) {
		return;
	}
	if (!observer) {
		observer = new Observer();
	}
	return observer.subscribe(subscriber);
}

const initialiser = {
	preInit: function() {
		pendingUpdated(true, flags.DOM_READY);
	},
	postInit: function() {
		waitForFixes();
		pendingUpdated(false, flags.DOM_READY);
	}
};

instance._initialiser = initialiser;  // testing only

try {
	if (Object.freeze) {
		Object.freeze(instance);
	}
} catch (ex) {
	console.warn(ex);
}

initialise.register(initialiser);
export default instance;
