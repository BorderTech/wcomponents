define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var resourceUrl = "@RESOURCES@/",
			a8n, Trigger, ajax, timers, testHolder,
			xmlUrl = resourceUrl + "note.xml",
			noop = function() {};

		/**
		 * Checks that the DOM attribute is flagged as ready and that the module agrees.
		 * @param {Function} onReady Will be called when ready is true
		 * @returns {Boolean} true if a8n says DOM is ready.
		 */
		function isReady(onReady) {
			var subscriber = function(readyArg) {
					if (readyArg) {  // this will always be true because we know we started false and only get state changes
						a8n.unsubscribe(subscriber);
						notify();
					}
				},
				notify = function() {
					window.setTimeout(onReady, 50);  // give the DOM a chance to catch up
				},
				ready = document.body.getAttribute(a8n.attr) === "true";
			assert.strictEqual(ready, a8n.isReady(), "The dom attribute and the isReady method should agree");
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
		 * Returns a prmise which:
		 * Calls your function which is expected to trigger a "busy" state.
		 * Waits for the busy state and records it.
		 * Waits for the return to ready state, esnuring that busy state happened.
		 * Resolves if it happened properly, otherwise rejects.
		 */
		function a8nTriggerTestHelper(triggerFunc) {
			var result = new Promise(function(resolve, reject) {
				var triggerFired = false,
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
								window.setTimeout(resolve, 50);
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
			return result;
		}

		registerSuite({
			name: "Automation",
			setup: function() {
				var result = testutils.setupHelper(["wc/a8n", "wc/ajax/ajax", "wc/ajax/Trigger", "wc/timers"]).then(function(arr) {
					a8n = arr[0];
					ajax = arr[1];
					Trigger = arr[2];
					timers = arr[3];
					testHolder = testutils.getTestHolder();
					testHolder.innerHTML = "<div id='mrDiv0'>mrDiv0</div>";
				}).then(function() {
					a8n.postInit();
					return new Promise(function(resolve) {
						a8n.onReady(resolve);
					});
				});
				return result;
			},
			beforeEach: function() {
				return new Promise(function(resolve, reject) {
					try {
						// We may need to wait for the ready state to update from a previous test, will either resolve or time out
						isReady(resolve);
					} catch (ex) {
						reject(ex);
					}
				});
			},
			afterEach: function() {
				a8n.clearSubscribers();
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testSubscriberTimers: function() {
				var trigger = function() {
					timers.setTimeout(noop, 100);
				};
				return a8nTriggerTestHelper(trigger);
			},
			testSubscriberAjaxTrigger: function() {
				var trigger = function() {
					var simpleRequest = {
							id: "foobar",
							url: xmlUrl,
							loads: ["mrDiv0"],
							method: "get"
						},
						ajaxTrigger = new Trigger(simpleRequest, noop, noop);
					ajaxTrigger.fire();
				};
				return a8nTriggerTestHelper(trigger);
			},
			testSubscriberAjax: function() {
				var trigger = function() {
					var request = {
						url: xmlUrl,
						callback: noop,
						cache: false,
						async: true,
						method: "get"
					};
					ajax.simpleRequest(request);
				};
				return a8nTriggerTestHelper(trigger);
			}
		});
	});