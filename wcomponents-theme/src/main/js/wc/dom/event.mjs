/**
 * Provides a browser agnostic event wrapper.
 *
 * If the HTML5 "write once, run anywhere" dream comes true then this class can hopefully be deleted. The support for
 * DOM Level 2 events in Internet Explorer 9 is a major step in the right direction.
 *
 * Well actually what it still gives us into the 2020s is a way to easily unsubscribe events.
 * It is handy to have an event manager, everything still does - how often do you write "addEventListener" in React and Angular?

 * Features implemented:
 *
 * * this keyword applies correctly in listener functions (it is the element the event is attached to);
 * * this keyword can be overridden when adding event listener (only somewhat useful, the scope of the listener could
 * be bound anyway using currying or bind. The minor disadvantage to these methods is:
 * ** a small memory hit because you are creating new instances of those listeners each time;
 * ** the event manager can not tell if a listener is attached twice.
 * * implemented numerous polyfills to ensure events have standard properties in all browsers;
 * * event order is guaranteed, events will be fired in the order they are added except you can add an event at **different
 *   priorities**: HIGH, MED, LOW (see {@link module:wc/dom/event.add} for more detail);
 * * a listener is prevented from being attached to the same element for than particular event type more than once;
 * * can programmatically fire an event on an element even if that is a custom event.
 *
 * Historically this class had some other concerns, such as helping prevent memory leaks in IE. It was originally
 * loosely based on [this](http://therealcrisp.xs4all.nl/upload/addEvent_dean.html) but has since been reworked
 * and rewritten to the point that it is completely unique.
 *
 * @todo Fix the public member mechanism - add and remove should pretty much match addEventListener and should take the same args as each other.
 * @todo redo event.fire
 */

import Observer from "wc/Observer";
import uid from "wc/dom/uid";
import timers from "wc/timers";


let atTargetEvent;  // used to prevent eventListener firing twice in the target phase if attached using bubble and capture
const MAX_RECURSE = 3,
	BUBBLE_SUFFIX = ".bubble",
	CAPTURE_SUFFIX = ".capture",
	PRI = Observer.priority,
	ELID_ATTR = "elid",
	events = {},
	currentEvent = {};  // the type of the event currently being processed or null when no event being processed

/**
 * Provides a wrapper for an event listener. This listens for events then uses an instance of
 * {@link module:wc/Observer} to manage the calls the event "listeners" in the ui modules.
 *
 * Major gotcha here that you need to be careful of if you are stomping around in here breaking all of my
 * hard work. If an element has eventListener attached with both capture true and capture false AND an event
 * originates at that element (i.e. it is the target of the event) then BOTH the capture and bubble
 * eventListeners will be called (because that's what happens in the AT_TARGET phase). Furthermore, the
 * event listeners must be called in the order they were added regardless of whether they were added using
 * capture or bubble - in the target phase they effectively ignore capture and bubble and must be treated as
 * one whole group.
 *
 * @function
 * @private
 * @throws {Error} Throws a generic error if there is too much recursion which looks like an event calling itself
 *    for example by calling element.onXXXX().
 */
function eventListener(/* $event */) {
	const $event = arguments[0] || window.event,
		phase = $event.eventPhase,
		type = $event.type;

	if (!currentEvent[type]) {
		currentEvent[type] = 1;
	} else if (currentEvent[type] < MAX_RECURSE) {
		currentEvent[type]++;
	} else {
		throw new Error("eventListener calling itself? calling element.onXXXX() directly?");
	}
	try {
		let filter;
		if (phase === globalThis.Event.BUBBLING_PHASE) {  // if both are undefined or if it actually is bubbling phase
			filter = type + BUBBLE_SUFFIX;
			atTargetEvent = null;
		} else if (phase === globalThis.Event.CAPTURING_PHASE) {
			filter = type + CAPTURE_SUFFIX;
			atTargetEvent = null;
		} else if (phase === globalThis.Event.AT_TARGET && atTargetEvent !== $event) {
			filter = targetPhaseFilterFactory(type);
			atTargetEvent = $event;  // flag that this event has already been handled in the target phase
		}
		if (filter) {
			const elementElid = $event.currentTarget[ELID_ATTR];
			const observer = events[elementElid];
			observer.setCallback(function (result) {
				if ((result === false || $event.returnValue === false) && !$event.defaultPrevented) {
					$event.preventDefault();
				}
			});
			observer.setFilter(filter);
			observer.notify.call(this, $event);  // "call" so we pass through the scope
		}
		/*
		 * Returning ANYTHING from a beforeUnload event in IE will cause a confirmation dialog to
		 * be presented to the user every time they try to leave the page.
		 * I have decided to remove the return value mainly because of this. However, I can see no
		 * reason why we should return anything when the event should be cancelled using preventDefault
		 * not by returning false.
		 *
		 * If it must be re-instated please conditionally return undefined if the event type is beforeunload.
		 * When you are testing this note that some versions of IE are MUCH worse than others when it comes to
		 * displaying this annoying confirmation dialog. IE9 seems to be particularly annoying in this regard.
		 */
		// return !($event.defaultPrevented);
	} finally {
		currentEvent[type]--;
	}
}

/**
 * Used as a filter for Observer when we are in the AT_TARGET phase and therefore need to
 * notify listeners attached with and without capture.
 * Two choices here: cache these filter functions (and use more memory) or leave them
 * uncached (and use more CPU). There is no one true correct answer here.
 *
 * @function
 * @private
 * @param {String} type The event type.
 * @returns {Function} A function which is used to filter based on target phase.
 */
function targetPhaseFilterFactory(type) {
	const bubble = type + BUBBLE_SUFFIX,
		capture = type + CAPTURE_SUFFIX;
	return function(group) {
		return group === bubble || group === capture;
	};
}

/**
 * This little helper adapts the horrible old organically grown event API to the new one.
 * The problem with having three optional args in a row is you get this sort of thing:
 * `event.add(element, "click", handler, null, null, true)`
 * @param {IArguments} args The arguments from a call to event.add.
 * @returns {{ type: string, listener: function, pos: number, scope: Object, capture: boolean, passive: boolean }} An eventArgs object, no matter if it was called with the new or old API.
 */
function addApi(args) {
	const argMap = ["type", "listener", "pos", "scope", "capture", "passive"];
	let result = args[1];
	if (args.length > 2) {
		result = {};
		for (let i = 0; i < argMap.length; i++) {
			result[argMap[i]] = args[i + 1];
		}
	}
	return result;
}

const instance = {
	/**
	 * Add an event listener and subscribes a function to {@link module:wc/Observer} instance to handle the
	 * event. NOTE: we no longer support dom0 binding: get over it.
	 *
	 * @function module:wc/dom/event.add
	 * @param {Element|global} element The element to which the event listener will be associated.
	 * @param {string} eventArgs.type The type of event (eg 'click', 'focus' NOT 'onclick', 'onfocus')
	 * @param {Function} eventArgs.listener The event listener that will be called on the event
	 * @param {number} [eventArgs.pos] positive number = runs later, negative number = runs earlier
	 *    Note, the weird numbering convention is due to backwards compatibility support. Think of the
	 *    numbers as belonging to a timeline: |-ve ---- 0 ---- +ve|
	 * @param {object} [eventArgs.scope] The scope in which to call the listener (ie override the 'this')
	 * @param {boolean} [eventArgs.capture] If true the event will listen at the capture phase. Default is false
	 *    (listens at the bubble phase). If you set capture to true in a browser that does not support
	 *    capture an exception will be thrown.
	 * @returns {Object} A dto that can be used to "remove" if the listener was able to be added as an event subscriber.
	 * @throws {TypeError} Thrown if the capture parameter is set true and the browser is not dom2 compliant.
	 */
	add: function (element/* , args */) {
		let result;
		const args = addApi(arguments),
			priority = args.pos ? ((args.pos > 0) ? PRI.LOW : PRI.HIGH) : PRI.MED;
		const elementElid = element[ELID_ATTR] || (element[ELID_ATTR] = uid());
		const capture = !!args.capture;
		const passive = !!args.passive;
		let group = (capture) ? `${args.type}${CAPTURE_SUFFIX}` : `${args.type}${BUBBLE_SUFFIX}`;
		if (passive) {
			group = `${group}_passive`;
		}
		const observer = events[elementElid] || (events[elementElid] = new Observer());
		if (observer.isSubscribed(args.listener, group)) {
			console.warn("listener: ", args.listener, " already bound to: ", args.type, " on element: ", element);
			result = false;
		} else {
			if (observer.subscriberCount(group) < 0) {
				// if less than zero this is the first subscriber for this type on this element
				element.addEventListener(args.type, eventListener, { capture, passive });
				// could fall back to dom 0 binding but meh, get with the program
			}
			result = observer.subscribe(args.listener, { group: group, context: args.scope, priority: priority });
			result.elid = elementElid;
		}
		return result;
	},
	
	/**
	 * Remove an event subscription from a particular element.
	 *
	 * Note, I removed the constraint which prevented you from removing an event listener that was currently
	 * being fired (ie it removed itself) as I think the problem being 'solved' here is already solved in the
	 * eventListener() code where a static snapshot of event listeners is taken before any of them are notified.
	 *
	 * @function module:wc/dom/event.remove
	 * @param {Element|Object|Object[]} element The element from which the event is removed.
	 *    Alternatively simply pass the result from a call to the "add" method of this module.
	 *    You may also pass an array of these - note the array will be modified! It will be emptied.
	 * @param {string} [type] The type we are removing. Not used if called with return value of "add".
	 * @param {Function} [listener] The subscriber (listener) for the event. Not used if called with return value of "add".
	 * @param {boolean} [capture] True if the event is to be removed from the capture phase. Make sure this
	 *    matches where it was attached! Not used if called with return value of "add".
	 * @param {boolean} [passive] If you registered it as passive you need to remove it as passive.
	 */
	remove: function (element, type, listener, capture, passive) {
		const dto = arguments[0],
			unsub = function(elid, args) {
				const observer = events[elid];
				if (observer) {
					observer.unsubscribe.apply(observer, args);
				}
			};
		if (arguments.length === 1) {
			if (Array.isArray(dto)) {
				while (dto.length) {
					this.remove(dto.pop());
				}
			} else {
				unsub(dto.elid, [dto]);
			}
		} else {
			let group = capture ? `${type}${CAPTURE_SUFFIX}` : `${type}${BUBBLE_SUFFIX}`;
			if (passive) {
				group = `${group}_passive`;
			}
			unsub(element[ELID_ATTR], [listener, group]);
		}
	},

	/**
	 * Fire an event. **IMPORTANT** If you want to fire FOCUS then use
	 * {@link module:wc/dom/focus#setFocusRequest}.
	 *
	 * Note that text type controls are fired in a different way to other controls. This is necessary in some
	 * browsers but not in others; we'll keep it consistent unless there is a need not to.
	 *
	 * I have prevented events from firing while another event is currently firing to help prevent infinite
	 * loops (change call click which calls change). May be overly protective, could reduce it so that you can't
	 * fire the same event (e.g. click can't fire while click is firing). Ok we did that and it is fine.
	 * I still think it's over-protective - I NEARLY removed the currentEvent check completely but🐔chickened out.
	 * Now it has a recursion counter, and it will allow the first few through (simply setting it to 2 would cater
	 * for the vast majority of legitimate cases).
	 *
	 * @function module:wc/dom/event.fire
	 * @param {Element} element The element to fire the event on.
	 * @param {string} $event The event to fire (eg 'click')
	 * @param {Object} [options]
	 * @param {boolean} [options.bubbles]
	 * @param {boolean} [options.cancelable]
	 * @param {string} [options.detail] for custom events
	 * @returns {Boolean} Should probably be undefined: use defaultPrevented to check if an event has ceased.
	 */
	fire: function (element, $event, options) {
		let result;
		const conf = options || { bubbles: true, cancelable: false };
		if (!currentEvent[$event] || currentEvent[$event] < MAX_RECURSE) {
			if (element && $event) {
				if ($event !== "submit" && element[$event] &&
					!element.matches("[type='text'], [type='password'], textarea, select")) {
					element[$event]();
				} else {
					let evt;
					if (conf.detail) {
						evt = new CustomEvent($event, conf);
						result = !element.dispatchEvent(evt);
					} else {
						// won't fully simulate a click (ie navigate a link)
						evt = document.createEvent("HTMLEvents");
						evt.initEvent($event, conf.bubbles, conf.cancelable);
						result = !element.dispatchEvent(evt);
					}
				}
			} else {
				throw new TypeError("arguments can not be null");
			}
		} else {
			console.log("Too much recursion, queueing", element, $event, options);
			timers.setTimeout(this.fire, 0, element, $event, options);
		}
		return result;
	},

	/**
	 * Get a string that represents the state of this object for diagnostic purposes.
	 *
	 * @function module:wc/dom/event.toString
	 * @public
	 * @returns {String}
	 */
	toString: () => Object.keys(events).map(elid => `${elid}: ${events[elid].toString()}`).join("\n"),
	canCapture: true  // Legacy API
};

export default instance;
