import Trigger from "wc/ajax/Trigger.mjs";

describe("wc/ajax/Trigger", () => {
	const subscribers = [],
		xmlUrl = "test/resource/note.xml",
		simpleRequest = {
			id: "foobar",
			url: xmlUrl,
			loads: ["mrDiv0", "mrDiv2"],
			method: "get"
		};

	let testHolder;

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
		expect(ids.length).withContext("nothing to test").toBeGreaterThan(0);
		for (let i = 0; i < ids.length; i++) {
			let next = document.getElementById(ids[i]);
			if (expectBusy) {
				expect(next.getAttribute("aria-busy")).withContext("busy state not set on " + ids[i]).toBe("true");
			} else {
				expect(next.getAttribute("aria-busy")).withContext("busy state not cleared on " + ids[i]).not.toBe("true");
			}

		}
	}

	function clearSubscribers() {
		while (subscribers.length) {
			let next = subscribers.pop();
			Trigger.unsubscribe(next);
			Trigger.unsubscribe(next, -1);
		}
	}

	/*
	 * Same as testBusy but returns a promise which is resolved when the test is complete.
	 */
	function testBusyPromise(ids, expectBusy) {
		return new Promise(function(win, lose) {
			const subscriber = function() {
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
			Trigger.subscribe(subscriber);
		});
	}


	/*
	 * simple error callback
	 */
	function errCallback(err) {
		// console.error(err);
		fail(err);
	}

	beforeAll(() => {
		testHolder = document.body;
		testHolder.innerHTML = `
			<div id="mrDiv0">hi</div>
			<div id="mrDiv1">howdy</div>
			<div id="mrDiv2">yo</div>`;
	});

	afterAll(function() {
		testHolder.innerHTML = "";
	});

	afterEach(function() {
		clearSubscribers();
	});

	it("testSetAriaBusy", function() {
		const trigger = new Trigger(simpleRequest, function() {
			// "Fire trigger makes target busy"
			testBusy(trigger.loads, true);
		}, errCallback);
		return trigger.fire();
	});

	it("testOthersNotAriaBusy", function() {
		const trigger = new Trigger(simpleRequest, function() {
			// "Fire trigger does not make non-target busy"
			testBusy(["mrDiv1"]);
		}, errCallback);
		return trigger.fire();
	});

	it("testSingleResponse", function() {
		let hitCount = 0;
		const trigger = new Trigger(simpleRequest, function(payload) {
				expect(payload).withContext("response should contain a payload").toBeDefined();
				expect(payload.documentElement).withContext("response should be an XML document").toBeDefined();
				expect(payload.documentElement.nodeName).withContext("documentElement should be 'note'").toBe("note");
				// "Fire causes only one response"
				expect(hitCount++).withContext("response received too many times").toBe(0);
			}, errCallback);
		return trigger.fire();
	});

	it("testResponseClearsBusy", function() {
		const trigger = new Trigger(simpleRequest, dummyCallback, errCallback);
		const promise = testBusyPromise(trigger.loads);
		trigger.fire();
		return promise;
	});

	it("testTriggerWithFormRegion", function() {
		const _request = {
				id: "foobar",
				url: xmlUrl,
				loads: ["mrDiv1", "mrDiv2"],
				formRegion:"mrDiv1",
				method: "get"
			},
			trigger = new Trigger(_request, dummyCallback, errCallback);
		const promise = testBusyPromise(trigger.loads);
		trigger.fire();
		return promise;
	});

	it("testDebouncing", function() {
		let hitCount = 0, i = 10;
		const trigger = new Trigger(simpleRequest, function() {
				// "Multiple fires produces single response"
				expect(hitCount++).withContext("response received too many times").toBe(0);
			}, errCallback);
		let promise;
		while (i--) {
			promise = trigger.fire();  // TODO probably should Promise.All this
		}
		return promise;
	});

	it("testOneShot", function() {
		const request = {
				oneShot: true,
				id: "foobar",
				url: xmlUrl,
				method: "get",
				loads: ["mrDiv0", "mrDiv2"]},
			trigger = new Trigger(request, dummyCallback);
		return new Promise(function (win, lose) {
			trigger.fire().then(function () {
				trigger.fire().then(function () {
					try {
						fail("one shot trigger should not fire twice");
					} finally {
						lose();
					}
				}, win);
			}, errCallback);
		});
	});

	it("testSubscribeBefore", function() {
		const trigger = new Trigger(simpleRequest, dummyCallback, errCallback);
		const promise = new Promise(function(win, lose) {
			const subscriber = function(instance, pending) {
				try {
					expect(instance.id).withContext("first argument to subscriber should be trigger").toBe(trigger.id);
					expect(pending).withContext("when firing a Trigger pending must be true").toBeTrue();
					win();
				} catch (ex) {
					lose();
				}
			};
			subscribers.push(subscriber);
			Trigger.subscribe(subscriber, -1);
		});
		trigger.fire();
		return promise;
	});

	it("testSubscribeAfter", function() {
		const trigger = new Trigger(simpleRequest, dummyCallback, errCallback);
		const promise = new Promise(function(win, lose) {
			const subscriber = function(instance, pending) {
				try {
					expect(instance.id).withContext("first argument to subscriber should be trigger").toBe(trigger.id);
					expect(pending).withContext("when the last Trigger has fired pending must be false").toBeFalse();
					win();
				} catch (ex) {
					lose();
				}
			};
			subscribers.push(subscriber);
			Trigger.subscribe(subscriber);
		});
		trigger.fire();
		return promise;
	});

	it("testSubscribeAfterCallback", function() {
		let callbackCalled = false;
		const trigger = new Trigger(simpleRequest, function() {
				callbackCalled = true;
				return "foobar";
			}, errCallback);
		const promise = new Promise(function(win, lose) {
			const subscriber = function(instance, pending) {
				try {
					expect(callbackCalled).withContext("The callback should have been called by now").toBeTrue();
					expect(instance.id).withContext("first argument to subscriber should be trigger").toBe(trigger.id);
					expect(pending).withContext("when the last Trigger has fired pending must be false").toBeFalse();
					win();
				} catch (ex) {
					lose();
				}
			};
			subscribers.push(subscriber);
			Trigger.subscribe(subscriber, 1);
		});
		trigger.fire();
		return promise;
	});

	it("testSubscribeReset", function() {
		const onerr = function() {
				errCallback("This should never be called");
			},
			trigger = new Trigger(simpleRequest, dummyCallback, errCallback);
		subscribers.push(onerr);
		Trigger.subscribe(onerr);
		Trigger.subscribe(onerr, -1);
		clearSubscribers();
		return trigger.fire();
	});

	//			,
	//			testTwoTriggersSameTarget: function() {
	//				const promise = new Promise(function(win, lose) {
	//					const pending = 0,
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
	//							expect(tested).withContext("The test didn't work").toBeTrue();
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
