define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var subscribers = [],
			TEST_MODULE = "wc/ajax/Trigger",
			resourceUrl = "@RESOURCES@/",
			Controller, testHolder,
			urlResource = resourceUrl + "ajaxTrigger.html",
			xmlUrl = resourceUrl + "note.xml",
			simpleRequest = {
				id: "foobar",
				url: xmlUrl,
				loads: ["mrDiv0", "mrDiv2"],
				method: "get"
			};

		/*
		 * WARNING!
		 *
		 * THESE TESTS FAIL ON SAUCELABS TUNNELS IF YOU USE "POST".
		 * Make sure you use "GET".
		 *
		 */

		/*
		 * trigger callback for simple (synchronous) busy tests
		 */
		function dummyCallback() {
			return true;
		}

		/*
		 * Test that the elements referenced by each id in the array are flagged as busy (or not busy)
		 * @param ids An array of ids
		 * @param expectBusy true if you want to assert that the elements to be busy, otherwise will
		 * assert that they are not busy.
		 */
		function testBusy(ids, expectBusy) {
			var next, i;
			assert.isTrue(ids.length > 0, "nothing to test");
			for (i = 0; i < ids.length; i++) {
				next = document.getElementById(ids[i]);
				if (expectBusy) {
					assert.strictEqual("true", next.getAttribute("aria-busy"),"busy state not set on " + ids[i]);
				} else {
					assert.notStrictEqual("true", next.getAttribute("aria-busy"),"busy state not cleared on " + ids[i]);
				}

			}
		}

		function clearSubscribers() {
			var next;
			while (subscribers.length) {
				next = subscribers.pop();
				Controller.unsubscribe(next);
				Controller.unsubscribe(next, -1);
			}
		}

		/*
		 * Same as testBusy but returns a promise which is resolved when the test is complete.
		 */
		function testBusyPromise(ids, expectBusy) {
			var promise = new Promise(function(win, lose) {
				var subscriber = function() {
					try {
						window.setTimeout(function() {
							// give the DOM a chance to catch up
							testBusy(ids, expectBusy);
							win();
						}, 100);
					} catch (ex) {
						lose(ex);
					}
				};
				subscribers.push(subscriber);
				Controller.subscribe(subscriber);
			});
			return promise;
		}


		/*
		 * simple error callback
		 */
		function errCallback(err) {
			// console.error(err);
			assert.fail(err);
		}

		registerSuite({
			name: "AjaxTrigger",
			setup: function() {
				var result = testutils.setupHelper([TEST_MODULE]).then(function(arr) {
					Controller = arr[0];
					testHolder = testutils.getTestHolder();
					/* set up the page with the ajax targets. Make fresh content for each test */
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			afterEach: function() {
				clearSubscribers();
			},
			testSetAriaBusy: function() {
				var trigger = new Controller(simpleRequest, function() {
					// "Fire trigger makes target busy"
					testBusy(trigger.loads, true);
				}, errCallback);
				var promise = trigger.fire();
				return promise;
			},
			testOthersNotAriaBusy: function() {
				var trigger = new Controller(simpleRequest, function() {
					// "Fire trigger does not make non-target busy"
					testBusy(["mrDiv1"]);
				}, errCallback);
				var promise = trigger.fire();
				return promise;
			},
			testSingleResponse: function() {
				var hitCount = 0,
					trigger = new Controller(simpleRequest, function(payload) {
						assert.isDefined(payload, "response should contain a payload");
						assert.isDefined(payload.documentElement, "response should be an XML document");
						assert.strictEqual(payload.documentElement.nodeName, "note", "documentElement should be 'note'");
						// "Fire causes only one response"
						assert.strictEqual(0, hitCount++, "response received too many times");
					}, errCallback);
				var promise = trigger.fire();
				return promise;
			},
			testResponseClearsBusy: function() {
				var trigger = new Controller(simpleRequest, dummyCallback, errCallback);
				var promise = testBusyPromise(trigger.loads);
				trigger.fire();
				return promise;
			},
			testTriggerWithFormRegion:function() {
				var _request = {
						id: "foobar",
						url: xmlUrl,
						loads: ["mrDiv1", "mrDiv2"],
						formRegion:"mrDiv1",
						method: "get"
					},
					trigger = new Controller(_request, dummyCallback, errCallback);
				var promise = testBusyPromise(trigger.loads);
				trigger.fire();
				return promise;
			},
			testDebouncing: function() {
				var hitCount = 0, i = 10,
					promise,
					trigger = new Controller(simpleRequest, function() {
						// "Multiple fires produces single response"
						assert.strictEqual(0, hitCount++, "response received too many times");
					}, errCallback);

				while (i--) {
					promise = trigger.fire();  // TODO probably should Promise.All this
				}
				return promise;
			},
			testOneShot: function() {
				var request = {
						oneShot: true,
						id: "foobar",
						url: xmlUrl,
						method: "get",
						loads: ["mrDiv0", "mrDiv2"]},
					trigger = new Controller(request, dummyCallback);
				var promise = new Promise(function(win, lose) {
					trigger.fire().then(function() {
						trigger.fire().then(function() {
							try {
								assert.fail("one shot trigger should not fire twice");
							} finally {
								lose();
							}
						}, win);
					}, errCallback);
				});
				return promise;
			},
			testSubscribeBefore: function() {
				var trigger = new Controller(simpleRequest, dummyCallback, errCallback);
				var promise = new Promise(function(win, lose) {
					var subscriber = function(instance, pending) {
						try {
							assert.strictEqual(trigger.id, instance.id, "first argument to subscriber should be trigger");
							assert.isTrue(pending, "when firing a Trigger pending must be true");
							win();
						} catch (ex) {
							lose();
						}
					};
					subscribers.push(subscriber);
					Controller.subscribe(subscriber, -1);
				});
				trigger.fire();
				return promise;
			},
			testSubscribeAfter: function() {
				var trigger = new Controller(simpleRequest, dummyCallback, errCallback);
				var promise = new Promise(function(win, lose) {
					var subscriber = function(instance, pending) {
						try {
							assert.strictEqual(trigger.id, instance.id, "first argument to subscriber should be trigger");
							assert.isFalse(pending, "when the last Trigger has fired pending must be false");
							win();
						} catch (ex) {
							lose();
						}
					};
					subscribers.push(subscriber);
					Controller.subscribe(subscriber);
				});
				trigger.fire();
				return promise;
			},
			testSubscribeAfterCallback: function() {
				var callbackCalled = false,
					trigger = new Controller(simpleRequest, function() {
						callbackCalled = true;
						return "foobar";
					}, errCallback);
				var promise = new Promise(function(win, lose) {
					var subscriber = function(instance, pending) {
						try {
							assert.isTrue(callbackCalled, "The callback should have been called by now");
							assert.strictEqual(trigger.id, instance.id, "first argument to subscriber should be trigger");
							assert.isFalse(pending, "when the last Trigger has fired pending must be false");
							win();
						} catch (ex) {
							lose();
						}
					};
					subscribers.push(subscriber);
					Controller.subscribe(subscriber, 1);
				});
				trigger.fire();
				return promise;
			},
			testSubscribeReset: function() {
				var onerr = function() {
						errCallback("This should never be called");
					},
					trigger = new Controller(simpleRequest, dummyCallback, errCallback);
				subscribers.push(onerr);
				Controller.subscribe(onerr);
				Controller.subscribe(onerr, -1);
				clearSubscribers();
				var promise = trigger.fire();
				return promise;
			}
//			,
//			testTwoTriggersSameTarget: function() {
//				var promise = new Promise(function(win, lose) {
//					var pending = 0,
//						tested = false,
//						request0 = {
//							id: "foobar0",
//							url: xmlUrl,
//							loads: ["mrDiv0", "mrDiv2"]
//						},
//						trigger0 = new Controller(request0, callback0),
//						request1 = {
//							id: "foobar1",
//							url: xmlUrl,
//							loads: ["mrDiv1", "mrDiv2"]
//						},
//						trigger1 = new Controller(request1, callback1);
//
//					function callback0(response, trigger) {
//						responseReceived(trigger);
//					}
//
//					function callback1(response, trigger) {
//						responseReceived(trigger);
//					}
//
//					function responseReceived(trigger) {
//						pending--;
//						if (pending) {
//							testBusy([trigger.loads[0]]);
//							testBusy(["mrDiv2"], true);
//							tested = true;
//						}
//						else {
//							testBusy(["mrDiv2"]);
//							assert.isTrue(tested, "The test didn't work");
//							win();
//						}
//					}
//
//					trigger0.fire();
//					pending++;
//					trigger1.fire();
//					pending++;
//				});
//				return promise;
//			}
		});
	});