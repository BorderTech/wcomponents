import a8n from "wc/a8n.mjs";
import ajax from "wc/ajax/ajax.mjs";
import Trigger from "wc/ajax/Trigger.mjs";
import timers from "wc/timers.mjs";

describe("wc/a8n", () => {
	let testHolder;
	const xmlUrl = "test/resource/note.xml",
		noop = function() {
			console.log("noop");
		};

	/**
	 * Checks that the DOM attribute is flagged as ready and that the module agrees.
	 * @param {Function} [onReady] Will be called when ready is true
	 * @returns {Boolean} true if a8n says DOM is ready.
	 */
	function isReady(onReady) {
		const notify = function() {
				setTimeout(onReady, 50);  // give the DOM a chance to catch up
			},
			subscriber = function(readyArg) {
				if (readyArg) {  // this will always be true because we know we started false and only get state changes
					a8n.unsubscribe(subscriber);
					notify();
				}
			},
			ready = document.body.getAttribute(a8n.attr) === "true";
		expect(a8n.isReady()).withContext("The dom attribute and the isReady method should agree").toBe(ready);
		if (onReady) {
			if (ready) {
				notify();
			} else {
				a8n.subscribe(subscriber);
			}
		}
		return ready;
	}

	/*
	 * Returns a promise which:
	 * Calls your function which is expected to trigger a "busy" state.
	 * Waits for the busy state and records it.
	 * Waits for the return to ready state, ensuring that busy state happened.
	 * Resolves if it happened properly, otherwise rejects.
	 */
	function a8nTriggerTestHelper(triggerFunc) {
		return new Promise(function(resolve, reject) {
			let triggerFired = false,
				wentBusy = false,
				subscriber = function(readyArg) {
					if (!triggerFired) {
						reject("I haven't even fired the trigger yet!");
					}
					if (readyArg) {
						// it went ready so it must have gone busy
						if (!wentBusy) {
							reject("Page went ready but didn't see it go busy");
						} else {
							setTimeout(resolve, 50);
						}
						a8n.unsubscribe(subscriber);
					} else {
						wentBusy = true;
					}
				};
			try {
				if (!isReady()) {
					reject("must start in ready state for tests to work");
				} else {
					a8n.subscribe(subscriber);
					triggerFired = true;
					triggerFunc();
				}
			} catch (ex) {
				reject(ex);
			}
		});
	}

	beforeAll(function() {
		a8n._initialiser.postInit();
		testHolder = document.body;
		testHolder.innerHTML = "<form><div id='mrDiv0'>mrDiv0</div><input name='foo'/></form>";
		return new Promise(function(resolve) {
			a8n.onReady(resolve);
		});
	});

	beforeEach(function() {
		return new Promise(function(resolve, reject) {
			try {
				// We may need to wait for the ready state to update from a previous test, will either resolve or time out
				isReady(resolve);
			} catch (ex) {
				reject(ex);
			}
		});
	});

	afterEach(function() {
		a8n.clearSubscribers();
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	it("testSubscriberTimers", function() {
		const trigger = function() {
			timers.setTimeout(noop, 100);
		};
		return a8nTriggerTestHelper(trigger);
	});

	it("testSubscriberAjaxTrigger", function() {
		const trigger = function() {
			const simpleRequest = {
					id: "foobar",
					url: xmlUrl,
					loads: ["mrDiv0"],
					method: "get"
				},
				ajaxTrigger = new Trigger(simpleRequest, noop, noop);
			ajaxTrigger.fire();
		};
		return a8nTriggerTestHelper(trigger);
	});

	it("testSubscriberAjax", function() {
		const trigger = function() {
			const request = {
				url: xmlUrl,
				callback: noop,
				cache: false,
				async: true,
				method: "get"
			};
			ajax.simpleRequest(request);
		};
		return a8nTriggerTestHelper(trigger);
	});
});
