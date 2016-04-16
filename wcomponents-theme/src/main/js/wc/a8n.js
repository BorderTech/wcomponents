/**
 * This module provides affordances for automated testing tools.
 */
define(["wc/dom/initialise", "wc/ajax/ajax", "wc/ajax/Trigger", "wc/timers", "wc/Observer", "wc/dom/storage"],
	function(initialise, ajax, Trigger, timers, Observer, storage) {
		var observer,
			timer,
			instance = {
				preInit: preInit,
				postInit: postInit,
				subscribe: subscribe
			},
			flags = {
				AJAX: 1,
				TIMERS: 2,
				AJAX_TRIGGER: 4,
				DOM_READY: 8
			},
			globalPending = 0;

		timers._subscribe(pendingTimers);
		ajax.subscribe(pendingAjax);
		Trigger.subscribe(pendingAjaxTrigger);
		Trigger.subscribe(pendingAjaxTrigger, -1);

		/*
		 * Subscriber for pending AJAX requests.
		 */
		function pendingAjax(pending) {
			var element = document.body;
			// console.log("AJAX ", pending);
			if (element) {
				// TODO remove this attribute - it is not needed any more now we have data-wc-domready
				element.setAttribute("data-wc-ajaxp", pending);
			}
			pendingUpdated(pending, flags.AJAX);
		}

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
			var element = document.body;
			if (element) {
				// TODO remove this attribute - it is not needed any more now we have data-wc-domready
				element.setAttribute("data-wc-timers", pending);
			}
			pendingUpdated(pending, flags.TIMERS);
		}

		/*
		 * Called by subscribers when they are notified of a possible change in state.
		 */
		function pendingUpdated(pending, flag) {
			if (timer) {
				window.clearTimeout(timer);
			}
			if (flag !== null) {
				if (pending) {
					globalPending |= flag;
				}
				else {
					globalPending &= ~flag;
				}
			}
			checkNotify();
		}

		function checkNotify() {
			var attr = "data-wc-domready",
				delay, notify, currentState, isReady,
				element = document.body;
			if (element) {
				isReady = !globalPending;
				currentState = (element.getAttribute(attr) === "true");
				if (isReady !== currentState) {
					if (timer) {
						window.clearTimeout(timer);
					}
					notify = stateChangeFactory(element, attr);
					if (!isReady) {  // If the DOM is busy we want to notify ASAP
						notify();
					}
					else {  // If the DOM is ready notify "soon" in case another action is about to start
						delay = storage.get("wc.a8n.delay") || 501;  // String should be ok without casting...
						timer = window.setTimeout(notify, delay);
					}
				}
			}
		}

		/*
		 * Handle a change of state from ready to busy and vice versa.
		 */
		function stateChangeFactory(element, attr) {
			return function() {
				var isReady = !globalPending;
				element.setAttribute(attr, isReady);
				if (observer) {
					observer.notify(isReady);
				}
			};
		}

		/*
		 * Called before initialisation rotuines run.
		 */
		function preInit() {
			pendingUpdated(true, flags.DOM_READY);
		}

		/*
		 * Called after initialisation rotuines run.
		 */
		function postInit() {
			pendingUpdated(false, flags.DOM_READY);
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

		initialise.register(instance);
		return instance;
	});
