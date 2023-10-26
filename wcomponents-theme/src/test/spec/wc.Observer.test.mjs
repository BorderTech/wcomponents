import Observer from "wc/Observer.mjs";
import timers from "wc/timers.mjs";

describe("wc/Observer", () => {
	var ns = "An_observed_nameSpace",
		ownerDocument,
		testHolder,
		containerId,
		observer;

	function getRandomInt(min, max) {
		min = Math.ceil(min);
		max = Math.floor(max);
		return Math.floor(Math.random() * (max - min)) + min;
	}

	beforeAll(function() {
		observer = new Observer();
		ownerDocument = document;
		testHolder = ownerDocument.body.appendChild(ownerDocument.createElement("div"));
		containerId = testHolder.id = `test-id-${Date.now()}`;
	});

	afterEach(function() {
		observer.reset();
		observer.reset(ns);
	});

	/* NOTE: The subscribe() tests rely on an assumption that notify() works as expected. */

	it("testObserverSubscribe", function() {
		let wasNotified = false;

		function subscriber() {
			wasNotified = true;
		}

		observer.subscribe(subscriber);
		observer.notify();
		expect(wasNotified).withContext("The subscriber should be notified if it was correctly subscribed.").toBeTrue();
	});

	it("testObserverSubscribePromise", function() {
		var wasNotified = false;

		function subscriber() {
			wasNotified = true;
			return Promise.resolve();
		}

		observer.subscribe(subscriber);
		observer.notify().then(function() {
			expect(wasNotified).withContext("The subscriber should be notified if it was correctly subscribed.").toBeTrue();
		});
	});

	it("testObserverSubscribeStagedPromise", function() {
		var wasNotified = false,
			myObserver = new Observer(true);

		function subscriber() {
			wasNotified = true;
			return Promise.resolve();
		}

		myObserver.subscribe(subscriber);
		myObserver.notify().then(function() {
			expect(wasNotified).withContext("The subscriber should be notified if it was correctly subscribed.").toBeTrue();
		});
	});

	it("testObserverSubscribeSubscriberNeedsArgs", function() {
		let wasNotified = false;
		function subscriber(arg1, arg2) {
			if (arg1 === "foo" && arg2 === "bar") {
				wasNotified = true;
			}
		}
		observer.subscribe(subscriber);
		observer.notify("foo", "bar");
		expect(wasNotified).withContext("Subscribe with args should honour notified args.").toBeTrue();
	});

	it("testObserverSubscribeTwice", function() {
		var count = 0;

		function subscriber() {
			count++;
		}

		observer.subscribe(subscriber);
		observer.subscribe(subscriber);
		observer.notify();
		expect(count).withContext("Should not be able to subscribe more than once.").toBe(1);
	});

	it("testObserverSubscribeNoParams", function() {
		const doBadThing = () => observer.subscribe();
		expect(doBadThing).withContext("Expected exception: Subscribing without a subscriber should have failed.").toThrowError();
	});

	/* Subscriber groups are really an issue for notify(). This just tests that  the subscriber gets subscribed (by returning itself) and does not throw an exception. */
	it("testObserverSubscribeWithGroup", function() {
		function subscriber() { }
		expect(observer.subscribe(subscriber, { group: ns })).withContext("Subscribe with a group should return something.").not.toBeNull();
	});

	/* Tests of Observer context applied to a subscriber. The context supplied by a call to subscribe should be the "this" of the subscriber when notified. */
	it("testObserverSubscribeWithContext", function() {
		var actualContext,
			expectedContext = ownerDocument.getElementById(containerId) || fail("Cannot get context container.");

		function subscriber() {
			actualContext = this;
		}
		observer.subscribe(subscriber, { context: expectedContext });
		observer.notify();
		expect(actualContext).withContext("Notify should have reset actualContext.").toBe(expectedContext);
	});

	it("testObserverSubscribeWithDifferentContexts", function() {
		var actualContext1,
			actualContext2,
			expectedContext1 = ownerDocument.getElementById(containerId),
			expectedContext2 = { foo: "bar" };

		function subscriber1() {
			actualContext1 = this;
		}

		function subscriber2() {
			actualContext2 = this;
		}

		observer.subscribe(subscriber1, { context: expectedContext1 });
		observer.subscribe(subscriber2, { context: expectedContext2 });
		observer.notify();

		expect(actualContext1).withContext("Notify should have reset actualContext1.").toBe(expectedContext1);
		expect(actualContext2).withContext("Notify should have reset actualContext2.").toBe(expectedContext2);
	});

	it("testObserverSubscribeWithNullContext", function() {
		var actualContext,
			expectedContext = window.self;

		function subscriber() {
			actualContext = this;
		}
		observer.subscribe(subscriber, { context: null });  // pass nothing and we should get global context
		observer.notify();
		expect(actualContext).withContext("Notify should have reset actualContext.").toBe(expectedContext);
	});

	it("testObserverSubscribeWithContextPassthru", function() {
		const expectedContext = { foo: "bar" };

		let actualContext;
		function subscriber() {
			actualContext = this;
		}

		observer.subscribe(subscriber);  // if using "call" or "apply" and no context specified context should pass through
		observer.notify.call(expectedContext);
		expect(actualContext).withContext("Notify should have reset actualContext.").toBe(expectedContext);
	});

	it("testObserverSubscribeWithMethodContext", function() {
		let actualContext;

		function Subscriber() {
			this.myMethod = function() {
				actualContext = this;
			};
		}

		const expectedContext = new Subscriber();
		observer.subscribe(expectedContext, { method: "myMethod" }); // calling a method the context should be the object to which the method belongs
		observer.notify();
		expect(actualContext).withContext("Notify should have reset actualContext.").toBe(expectedContext);
	});

	it("testObserverSubscribeWithMethodContextOverride", function() {
		let actualContext;
		const expectedContext = ownerDocument.getElementById(containerId);

		function Subscriber() {
			this.myMethod = function() {
				actualContext = this;
			};
		}
		observer.subscribe(new Subscriber(), { context: expectedContext, method: "myMethod" });
		observer.notify();
		expect(actualContext).withContext("Notify should have reset actualContext").toBe(expectedContext);
	});

	/* Testing the priority parameter. */
	it("testObserverSubscribeImportance", function() {
		let amIImportant = true;

		function iAmImportant() {
			amIImportant = true;
		}
		function iAmNotImportant() {
			amIImportant = false;
		}

		// two subscribers, the second is important so gets notified first.
		observer.subscribe(iAmNotImportant, { priority: Observer.priority.MED });
		observer.subscribe(iAmImportant, { priority: Observer.priority.HIGH });
		observer.notify();
		expect(amIImportant).withContext("The important subscriber should be notified first, therefore amIImportant should be reset by the first subscriber").toBeFalse();
	});

	it("testObserverSubscribeLowImportance", function() {
		let lastcaller = 1;

		observer.subscribe(function() {
			lastcaller = 2;
		}, { priority: Observer.priority.LOW });
		observer.subscribe(function() {
			lastcaller = 3;
		});
		observer.notify();
		expect(lastcaller).withContext("The low priority subscriber should be called last.").toBe(2);
	});

	it("testObserverSubscribeHighImportance", function() {
		let firstcaller;

		observer.subscribe(function() {
			firstcaller = firstcaller || 1;
		});
		observer.subscribe(function() {
			firstcaller = firstcaller || 2;
		}, { priority: Observer.priority.HIGH });
		observer.subscribe(function() {
			firstcaller = firstcaller || 3;
		});
		observer.notify();
		expect(firstcaller).withContext("The High priority subscriber should be called first.").toBe(2);
	});

	/* The important parameter is boolean and therefore should be able to be set by another function */
	it("testObserverSubscribeImportanceFunction", function() {
		var amIImportant = true;

		function iAmImportant() {
			amIImportant = true;
		}
		function iAmNotImportant() {
			amIImportant = false;
		}

		function setImportanceParameter(foo) {
			return typeof foo === "string";

		}
		observer.subscribe(iAmNotImportant, { priority: setImportanceParameter({ p1: "empty" }) });
		observer.subscribe(iAmImportant, { priority: setImportanceParameter("anything") });
		observer.notify();
		expect(amIImportant).withContext("The important subscriber should be notified first, therefore amIImportant should be reset by the first subscriber").toBeFalse();
	});


	it("testImportanceWithPromise", function() {
		const expected = ["high", "medium", "low"],
			order = [];
		observer.subscribe(function() {
			order.push("medium");
		});
		observer.subscribe(function() {
			order.push("low");
		}, { priority: Observer.priority.LOW });
		observer.subscribe(function() {
			order.push("high");
		}, { priority: Observer.priority.HIGH });
		return observer.notify().then(function() {
			expect(order.join()).toEqual(expected.join());
		});
	});

	it("testImportanceWithPromiseRejectsAndErrors", function() {
		const expected = ["high", "high", "medium", "medium", "low", "low"],
			order = [];
		observer.subscribe(function() {
			order.push("medium");
			return Promise.reject("Observer testing reject in medium subscriber");
		});
		observer.subscribe(function() {
			order.push("medium");
			throw new Error("Observer testing error in medium subscriber");
		});
		observer.subscribe(function() {
			order.push("low");
			throw new Error("Observer testing error in low subscriber");
		}, { priority: Observer.priority.LOW });
		observer.subscribe(function() {
			order.push("low");
			return Promise.reject("Observer testing reject in low subscriber");
		}, { priority: Observer.priority.LOW });
		observer.subscribe(function() {
			order.push("high");
			throw new Error("Observer testing error in high subscriber");
		}, { priority: Observer.priority.HIGH });
		observer.subscribe(function() {
			order.push("high");
			return Promise.reject("Observer testing reject in high subscriber");
		}, { priority: Observer.priority.HIGH });
		return observer.notify().then(function() {
			expect(order.join()).toEqual(expected.join());
		});
	});

	it("testStagedImportance", function() {
		const myObserver = new Observer(true),
			expected = ["high", "medium", "low"],
			order = [];
		myObserver.subscribe(function() {
			order.push("medium");
		});
		myObserver.subscribe(function() {
			order.push("low");
		}, { priority: Observer.priority.LOW });
		myObserver.subscribe(function() {
			order.push("high");
		}, { priority: Observer.priority.HIGH });
		return myObserver.notify().then(function() {
			expect(order.join()).toEqual(expected.join());
		});
	});

	it("testStagedImportanceSubscriberThrowsErrors", function() {
		const myObserver = new Observer(true),
			expected = ["high", "high", "medium", "medium", "low", "low"],
			order = [];
		myObserver.subscribe(function() {
			order.push("medium");
			throw new Error("Observer testing error in medium subscriber");
		});
		myObserver.subscribe(function() {
			order.push("medium");
			throw new Error("Observer testing error in medium subscriber");
		});
		myObserver.subscribe(function() {
			order.push("low");
		}, { priority: Observer.priority.LOW });
		myObserver.subscribe(function() {
			order.push("low");
		}, { priority: Observer.priority.LOW });
		myObserver.subscribe(function() {
			order.push("high");
			throw new Error("Observer testing error in high subscriber");
		}, { priority: Observer.priority.HIGH });
		myObserver.subscribe(function() {
			order.push("high");
			throw new Error("Observer testing error in high subscriber");
		}, { priority: Observer.priority.HIGH });
		return myObserver.notify().then(function() {
			expect(order.join()).toEqual(expected.join());
		});
	});

	it("testStagedImportanceWaits", function() {
		const myObserver = new Observer(true),
			expected = ["high", "medium", "low"],
			order = [];
		/*
			Even if the low subscribers are very fast and the high subscribers
			very slow, each stage will complete in order if "staged" is true.
		 */
		myObserver.subscribe(function() {
			return new Promise(function(win) {
				timers.setTimeout(function() {
					order.push("medium");
					win();
				}, 20);
			});
		});
		myObserver.subscribe(function() {
			order.push("low");
		}, { priority: Observer.priority.LOW });
		myObserver.subscribe(function() {
			return new Promise(function(win) {
				timers.setTimeout(function() {
					order.push("high");
					win();
				}, 40);
			});
		}, { priority: Observer.priority.HIGH });

		return myObserver.notify().then(function() {
			expect(order.join()).withContext("Subscribers should wait for the previous stage to complete").toEqual(expected.join());
		});
	});

	it("testStagedImportanceRandomWaits", function() {
		const myObserver = new Observer(true),
			expected = ["high", "medium", "low"],
			delays = [getRandomInt(0, 50), getRandomInt(0, 50), getRandomInt(0, 50)],
			order = [];
		/*
		 Same as test above but mix up the times
		 */
		myObserver.subscribe(function() {
			return new Promise(function(win) {
				timers.setTimeout(function() {
					order.push("medium");
					win();
				}, delays[0]);
			});
		});
		myObserver.subscribe(function() {
			return new Promise(function(win) {
				timers.setTimeout(function() {
					order.push("low");
					win();
				}, delays[1]);
			});
		}, { priority: Observer.priority.LOW });
		myObserver.subscribe(function() {
			return new Promise(function(win) {
				timers.setTimeout(function() {
					order.push("high");
					win();
				}, delays[2]);
			});
		}, { priority: Observer.priority.HIGH });

		return myObserver.notify().then(function() {
			expect(order.join()).withContext("Subscribers should wait for the previous stage to complete " + delays.join()).toEqual(expected.join());
		});
	});

	/* Testing the method parameter */
	it("testObserverSubscribeWithMethod", function() {
		var calledIn = null,
			objectSubscriber;

		function ObjectSubscriber() {
			this.doSubscribe = function() {
				calledIn = this;
			};
		}

		objectSubscriber = new ObjectSubscriber();
		observer.subscribe(objectSubscriber, { method: "doSubscribe" });
		observer.notify();
		expect(calledIn).withContext("Subscribed method should have been called.").not.toBeNull();
	});

	it("testObserverSubscribeWithMethodKeepsContext", function() {
		var calledIn,
			objectSubscriber;

		function ObjectSubscriber() {
			this.doSubscribe = function() {
				calledIn = this;
			};
		}

		objectSubscriber = new ObjectSubscriber();
		observer.subscribe(objectSubscriber, { method: "doSubscribe" });
		observer.notify();
		expect(calledIn).withContext("Context should have been kept when using a subscriber Object with method name.").toBe(objectSubscriber);
	});

	/* subscribe()'s method param does not come into play until notify() is called so it may contain pretty much
	 * anything but the various exceptions should be tested in notify() tests.*/
	it("testObserverSubscribeMethodUndefinedFunction", function() {
		var iHaveBeenCalled = false,
			hasSubscribed = false,
			objectSubscriber;

		function ObjectSubscriber() {
			iHaveBeenCalled = true;
			this.doSubscribe = function() {
				hasSubscribed = true;
			};
		}

		objectSubscriber = new ObjectSubscriber();
		observer.subscribe(objectSubscriber, { method: "listSubscribe" });
		expect(iHaveBeenCalled).withContext("Subscribe should have instantiated a subscriber object").toBeTrue();
		observer.notify();
		expect(hasSubscribed).withContext("I should not have been subscribed").toBeFalse();
	});

	/* unsubscribe tests */
	it("testObserverUnubscribeNoParamsMultiSubscribers", function() {
		let isSubscribed = false;

		observer.subscribe(() => isSubscribed = true);
		observer.subscribe(() => {});
		observer.unsubscribe();  // Call unsubscribe() with no args

		try {
			observer.unsubscribe();  // Call unsubscribe() with no params does not unsubscribe anything
			observer.notify();  // should reach here as no error thrown.
		} finally {
			expect(isSubscribed).withContext("Notify called and unsubscribe should have failed.").toBeTrue();
		}
	});

	it("testObserverUnubscribeNoParamsSingleSubscriber", function() {
		let isSubscribed = false;

		function subscriber() {
			isSubscribed = true;
		}

		observer.subscribe(subscriber);

		try {
			observer.unsubscribe();  // Call unsubscribe() with no params does not unsubscribe anything
			observer.notify();  // should reach here as no error thrown.
		} finally {
			expect(isSubscribed).withContext("Notify called and unsubscribe should have failed.").toBeTrue();
		}
	});

	it("testObserverUnubscribeNoGroup", function() {
		var isSubscribed = false;

		function subscriber() {
			isSubscribed = true;
		}

		observer.subscribe(subscriber);
		observer.unsubscribe(subscriber);

		// if we now call notify having unsubscribed then wasSubscribed should not be changed
		observer.notify();
		expect(isSubscribed).withContext("notify called after unsubscribe should not change value of isSubscribed").toBeFalse();
	});

	it("testObserverUnubscribeUsingRvalNoGroup", function() {
		var rval, isSubscribed = false;

		function subscriber() {
			isSubscribed = true;
		}

		rval = observer.subscribe(subscriber);
		observer.unsubscribe(rval);

		// if we now call notify having unsubscribed then wasSubscribed should not be changed
		observer.notify();
		expect(isSubscribed).withContext("notify called after unsubscribe should not change value of isSubscribed").toBeFalse();
	});

	it("testObserverUnsubscribeWithGroupMismatch", function() {
		/* note: this depends on working observer.filter (see below) */
		var isSubscribed = false;

		function subscriber() {
			isSubscribed = true;
		}

		observer.subscribe(subscriber, { group: ns });
		observer.unsubscribe(subscriber); // should be nothing
		observer.setFilter(ns);
		observer.notify();
		expect(isSubscribed).withContext("Subscriber should have been notified since have not unsubscribed with the correct group.").toBeTrue();
	});

	/* NOTIFY tests */
	it("testObserverNotify", function() {
		// NOTE: most subscribe/unsubscribe tests rely on a working notify() so there is some double-up.
		// this is same test as testObserverSubscribeSubscriberNeedsArgs() and is here for completeness.
		var rval, wasNotified = false;

		function subscriber(arg1, arg2) {
			if (arg1 === "foo" && arg2 === "bar") {
				wasNotified = true;
			}
		}

		rval = observer.subscribe(subscriber);
		observer.notify("foo", "bar");
		expect(wasNotified).toBeTrue();
		wasNotified = false;
		observer.unsubscribe(rval);
		expect(wasNotified).withContext("Should not be notified after unsubscribe").toBeFalse();
	});

	it("testObserverNotifyOrderWithMultipleImportantAndNotImportant", function() {
		const rval = [],
			expected = { 0: 0, 1: 1, 2: 2, 3: 3, 4: 4, 5: 5 };

		let idx = 0,
			result = {};

		rval.push(observer.subscribe(curriedSubscriber(2), { priority: Observer.priority.MED }));
		rval.push(observer.subscribe(curriedSubscriber(5), { priority: Observer.priority.LOW }));
		rval.push(observer.subscribe(curriedSubscriber(0), { priority: Observer.priority.HIGH }));
		rval.push(observer.subscribe(curriedSubscriber(3)));
		rval.push(observer.subscribe(curriedSubscriber(4)));
		rval.push(observer.subscribe(curriedSubscriber(1), { priority: Observer.priority.HIGH }));

		observer.notify();
		// @ts-ignore
		expect(expected).toEqual(result);

		function curriedSubscriber(sauce) {
			return function () {
				result[idx++] = sauce;
			};
		}

		result = {};
		observer.unsubscribe(rval);
		expect(0).withContext("Array should be emptied for me").toBe(rval.length);
		observer.notify();
		expect({}).withContext("Should be able to unsubscribe with array of rvals").toEqual(result);
	});

	/* Check that adding order is honored across groups. */
	it("testObserverNotifyOrderWithDifferentGroups", function() {
		var idx = 0,
			filter = observer.getGroupAsWildcardFilter("oscar.mike.foxtrot.golf"),
			ns1 = "oscar.mike.foxtrot.golf",
			ns2 = "oscar.mike.*.golf",
			ns3 = "whisky.tango.foxtrot",
			result = { },
			expected = {
				0: 0,
				1: 1,
				2: 3,
				3: 5 };

		function curriedSubscriber(sauce) {
			return function () {
				result[idx++] = sauce;
			};
		}
		/* The try/finally is only here to ensure proper cleanup of the various groups. */
		try {
			observer.subscribe(curriedSubscriber(0), { group: ns1 });
			observer.subscribe(curriedSubscriber(1), { group: ns2 });
			observer.subscribe(curriedSubscriber(2), { group: ns3 });
			observer.subscribe(curriedSubscriber(3), { group: ns2 });
			observer.subscribe(curriedSubscriber(4), { group: ns3 });
			observer.subscribe(curriedSubscriber(5), { group: ns1 });

			observer.setFilter(filter);
			observer.notify().then(function() {
				expect(result).toEqual(expected);
			});
		} finally {
			expect(result).toEqual(expected);
			observer.reset(ns1);
			observer.reset(ns2);
			observer.reset(ns3);
		}
	});

	/* This is an advanced and critically important test to ensure that the order in which subscribers are added is
	 * honored even when multiple groups are involved (by means of a wildcard subscriber). AS WELL AS ensuring
	 * subscriber priority is also be honored across groups.
	 *
	 * This is a complex matter and you are probably reading this because you changed observer and screwed it up.
	 * Don't change the test: go back and get it right. */
	it("testObserverNotifyOrderWithPriorityAndDifferentGroups", function() {
		var idx = 0,
			filter = observer.getGroupAsWildcardFilter("oscar.mike.foxtrot.golf"),
			ns1 = "oscar.mike.foxtrot.golf",
			ns2 = "oscar.mike.*.golf",
			ns3 = "whisky.tango.foxtrot",
			result = { },
			expected = {
				0: 1,
				1: 6,
				2: 3,
				3: 7,
				4: 0,
				5: 5 };

		function curriedSubscriber(sauce) {
			return function () {
				result[idx++] = sauce;
			};
		}
		try {
			observer.subscribe(curriedSubscriber(0), { group: ns1, priority: Observer.priority.LOW });
			observer.subscribe(curriedSubscriber(1), { group: ns2, priority: Observer.priority.HIGH });
			observer.subscribe(curriedSubscriber(2), { group: ns3, priority: Observer.priority.HIGH });
			observer.subscribe(curriedSubscriber(3), { group: ns2 });
			observer.subscribe(curriedSubscriber(4), { group: ns3 });
			observer.subscribe(curriedSubscriber(5), { group: ns1, priority: Observer.priority.LOW });
			observer.subscribe(curriedSubscriber(6), { group: ns1, priority: Observer.priority.HIGH });
			observer.subscribe(curriedSubscriber(7), { group: ns1, priority: Observer.priority.MED });
			observer.setFilter(filter);
			observer.notify().then(function() {
				expect(result).withContext("YOU HAVE BROKEN OBSERVER: GO AND FIX IT! _DO NOT_ CHANGE THIS TEST.").toEqual(expected);
			});
		} finally {
			expect(result).withContext("YOU HAVE BROKEN OBSERVER: GO AND FIX IT! _DO NOT_ CHANGE THIS TEST.").toEqual(expected);
			observer.reset(ns1);
			observer.reset(ns2);
			observer.reset(ns3);
		}
	});

	it("testObserverNotifySubscriberNotFunction", function() {
		// this is an extension of the subscriber test

		var someObject = {
				prop1: function() {
					aBoolean = true;
				}
			},
			aBoolean = false;

		observer.subscribe(someObject);
		observer.notify();
		expect(aBoolean).withContext("Notifying an object subscriber should not trigger the functions of that object.").toBeFalse();
	});

	it("testObserverFilterSimple", function() {
		var wasNotified;

		function subscriber() {
			wasNotified = false;
		}

		function filterSubscriber() {
			wasNotified = true;
		}

		observer.subscribe(filterSubscriber, { group: ns });
		// if filter does nothing then the next subscriber will be the last called.
		observer.subscribe(subscriber, { priority: Observer.priority.LOW });

		observer.setFilter(ns);
		observer.notify();
		expect(wasNotified).withContext("wasNotified should not have been set by subscriber not matching filter.").toBeTrue();
	});

	it("testObserverFilterWildcard", function() {

		var wasNotified = 0,
			otherNs = "ui.some.other.namespace",
			wildNs = "ui.*.namespace",
			filterFn = observer.getGroupAsWildcardFilter(otherNs);  // assume observer.getGroupAsWildcardFilter works as expected - tested later

		function subscriber() {
			wasNotified += 1;
		}

		function filterSubscriber() {
			wasNotified += 1;
		}

		function wildFilterSubscriber() {
			wasNotified += 1;
		}

		try {
			observer.subscribe(subscriber);
			observer.subscribe(filterSubscriber, { group: otherNs });
			observer.subscribe(wildFilterSubscriber, { group: wildNs });

			// wild card filter should result in both subscriberTwo and subscriberThree being called by notify
			observer.setFilter(filterFn);
			observer.notify();
		} finally {
			expect(wasNotified).withContext("wasNotified should only be incremented by each subscriber matching the wildcarded group name.").toBe(2);
			observer.reset(otherNs);
			observer.reset(wildNs);
		}
	});

	it("testObserverFilterFunction", function() {
		var wasNotified = 0,
			localns = "ui.some.namespace",
			otherNs = "ui.some.other.namespace",
			thirdNs = "ui.this.namespace";

		// example
		// instead of internal groups we want to filter for base groups in ui.some.*
		var filterFn = function(group) {
			var result = false,
				segments = group.split(".");
			if (segments[0] === "ui" && segments[1] === "some") {
				result = true;
			}
			return result;
		};


		function subscriber() {
			wasNotified += 1;
		}

		function filterSubscriber() {
			wasNotified += 2;
		}

		function wildFilterSubscriber() {
			wasNotified += 4;
		}


		observer.subscribe(subscriber, { group: localns });
		observer.subscribe(filterSubscriber, { group: otherNs });
		observer.subscribe(wildFilterSubscriber, { group: thirdNs });

		observer.setFilter(filterFn);
		observer.notify();

		expect(wasNotified).withContext("wasNotified should be adjusted only by functions in a group matching that set by the filter function").toBe(3);
		observer.reset(localns);
		observer.reset(otherNs);
		observer.reset(thirdNs);
	});

	it("testObserverFilterFunctionNotBoolean", function() {
		var wasNotified = 0,
			localns = "ui.some.namespace",
			otherNs = "ui.some.other.namespace",
			thirdNs = "ui.this.namespace",
			// instead of internal groups we want to filter for base groups in ui.some.*
			filterFn = function(group) {
				var result = "no",
					segments = group.split(".");
				if (segments[0] === "ui" && segments[1] === "some") {
					result = "yes";
				}
				return result;
			};

		function subscriber() {
			wasNotified += 1;
		}

		function filterSubscriber() {
			wasNotified += 2;
		}

		function wildFilterSubscriber() {
			wasNotified += 4;
		}

		observer.subscribe(subscriber, { group: localns });
		observer.subscribe(filterSubscriber, { group: otherNs });
		observer.subscribe(wildFilterSubscriber, { group: thirdNs });

		observer.setFilter(filterFn);

		// we expect JavaScript truthy/falsey not strictly boolean.
		observer.notify();

		expect(wasNotified).withContext("wasNotified should be set by everything which does not return a falsey value").toBe(7);
		observer.reset(localns);
		observer.reset(otherNs);
		observer.reset(thirdNs);
	});

	it("testObserverFilterNoParams", function() {
		var wasNotified = 0;

		function subscriber() {
			wasNotified += 1;
		}

		function filterSubscriber() {
			wasNotified += 2;
		}

		observer.subscribe(subscriber);
		observer.subscribe(filterSubscriber, { group: ns });

		try {
			observer.setFilter();  // try to call observer.setFilter with no filter defined should throw an error
		} catch (e) {
			observer.notify();
		} finally {
			expect(wasNotified).withContext("Call to notify should call subscribers in GLOBAL group since no filter was set").toBe(1);
		}
	});

	it("testObserverFilterNotStringOrFunction", function() {
		var wasNotified = 0;

		function subscriber() {
			wasNotified += 1;
		}

		function filterSubscriber() {
			wasNotified += 2;
		}

		observer.subscribe(subscriber);
		observer.subscribe(filterSubscriber, { group: ns });

		try {
			observer.setFilter(null);
		} catch (e) {
			observer.notify();
		} finally {
			expect(wasNotified).withContext("Call to notify should call subscribers in GLOBAL group since no filter was set").toBe(1);
			observer.reset(ns);
		}
	});

	it("testSetFilterNotFalsyStringOrFunctionThrowsError", function() {
		const doBadthing = () => observer.setFilter({});
		expect(doBadthing).toThrowError("arg must be a String or Function");
	});

	it("testGetGroupAsWildcardFilter", function() {
		/* What we are testing here is that foo.*.bar returns true from filter "foo.a.b.c....bar" and
		 * "foo.bar.somethingelse" does not. */
		var localns = "ui.*.name.space",
			otherNs = "ui.*.namespace",
			filter = "ui.long.sample.foo.bar.namespace",
			returnedValue = observer.getGroupAsWildcardFilter(filter);

		expect(returnedValue(localns)).toBeFalse();
		expect(returnedValue(otherNs)).toBeTrue();
	});

	it("testObserverGetGroupAsWildcardFilterNoFilter", function() {
		var hadError = false;

		// attempting to use a null filter throws an error
		try {
			observer.getGroupAsWildcardFilter(null);
		} catch (error) {
			hadError = true;
		} finally {
			expect(hadError).withContext("Calling getGroupAsWildcardFilter without a filter should throw an error.").toBeTrue();
		}
	});

	it("testObserverGetGroupAsWildcardFilterNonsenseFilter", function() {
		var filter = function() {
				return true;
			},
			hadError = false;

		try {
			observer.getGroupAsWildcardFilter(filter);  // nonsense filter, even if of the correct "type", will throw an error
		} catch (error) {
			hadError = true;
		} finally {
			expect(hadError).withContext("calling getGroupAsWildcardFilter without a valid filter should throw an error").toBeTrue();
		}
	});

	it("testObserverCallback", function() {
		var someCounter = 1;

		function mySubscriber() {
			return 2;
		}

		function callback(result) {
			someCounter = result;
		}

		observer.subscribe(mySubscriber);
		observer.setCallback(callback);
		observer.notify();
		expect(someCounter).withContext("The return value of the subscriber should be the value passed to callback.").toBe(2);
	});

	it("testObserverCallbackReturnStopsNotify", function() {
		var notified = null;

		function mySubscriber() {
			notified = true;
			return 2;
		}

		function mySubscriber2() {
			notified = false;
			return 0;
		}

		function callback(result) {
			return !!result;
		}

		observer.subscribe(mySubscriber);
		observer.subscribe(mySubscriber2);
		observer.setCallback(callback);
		observer.notify();
		expect(notified).withContext("Callback return true should prevent notify from continuing.").toBeTrue();
	});

	it("testObserverCallbackNoFunction", function() {
		const doBadThing = () => observer.setCallback();
		expect(doBadThing).toThrowError();
	});

	it("testObserverCallbackNotFunctionStillNotifies", function() {
		var wasNotified,
			callback = { };

		function subscriber() {
			wasNotified = true;
		}

		try {
			observer.subscribe(subscriber);
			observer.setCallback(callback);
			observer.notify();
		} finally {
			expect(wasNotified).withContext("setCallback without a valid callback function does not break notify.").toBeTrue();
		}
	});

	it("testObserverCallbackNullStillNotifies", function() {
		var wasNotified;

		function subscriber() {
			wasNotified = true;
		}

		try {
			observer.subscribe(subscriber);
			observer.setCallback(null);  // throws an error
		} catch (e) {
			observer.notify();
		} finally {
			expect(wasNotified).withContext("setCallback with a null callback function does not break notify.").toBeTrue();
		}
	});

	it("testObserverCallbackWrongFunction", function() {
		let wasNotified = 0;

		function subscriber() {
			wasNotified = 1;
			return true;
		}

		function callback() {
			/* set up a callback function with no formal params then use arguments to get params actually passed*/
			if (arguments[0] !== undefined) {
				wasNotified += 2;
			}
			if (arguments[1] !== undefined) {
				wasNotified += 4;
			}
			if (arguments[2] !== undefined) {
				wasNotified += 8;
			}
		}

		observer.subscribe(subscriber);

		// we know callback can be any function
		// we know notify() calls the callback function passing three params
		// context, subscriber, result
		observer.setCallback(callback);
		observer.notify();
		expect(wasNotified > 1).withContext("wasNotified should be changed if any params were passed to callback.").toBeTrue();
	});

	/* Reset tests */
	it("testObserverReset", function() {
		/* this is a modification of two tests we have already seen. We know from above that wasNotified will change if reset does not work as expected */
		let wasNotified = false;

		function subscriber(arg1, arg2) {
			if (arg1 === "foo" && arg2 === "bar") {
				wasNotified = true;
			}
		}
		observer.subscribe(subscriber);

		observer.reset();
		observer.notify("foo", "bar");
		expect(wasNotified).toBeFalse();
	});

	it("testObserverResetWithGroup", function() {
		var wasNotified = false;

		function subscriber() {
			wasNotified = true;
		}
		observer.subscribe(subscriber, { group: ns });

		observer.reset(ns);
		observer.setFilter(ns);
		observer.notify();
		expect(wasNotified).toBeFalse();
	});

	it("testObserverResetGlobalNotGroup", function() {
		let wasNotified = 0;

		function subscriber() {
			wasNotified = 1;
		}
		function subscriber2() {
			wasNotified = 2;
		}
		observer.subscribe(subscriber, { group: ns });
		observer.subscribe(subscriber2);

		observer.reset();
		observer.notify();  // no filter so notify global
		observer.setFilter(ns);
		observer.notify();  // group not resert
		expect(wasNotified).toEqual(1);
	});

	it("testObserverResetGroupNotGlobal", function() {
		let wasNotified = 0;

		function subscriber() {
			wasNotified = 1;
		}
		function subscriber2() {
			wasNotified = 2;
		}
		observer.subscribe(subscriber, { group: ns });
		observer.subscribe(subscriber2);

		observer.reset(ns);
		observer.notify();  // no filter so notify global
		observer.setFilter(ns);
		observer.notify();
		expect(wasNotified).toEqual(2);
	});
});
