define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	var ns = "An_observed_nameSpace",
		testHolder,
		containerId,
		Observer,
		observer;

	registerSuite({
		name: "Observer",
		setup: function() {
			return testutils.setupHelper(["wc/Observer"]).then(function(arr) {
				Observer = arr[0];
				observer = new Observer();
				testHolder = testutils.getTestHolder();
				containerId = testHolder.id;
			});
		},
		afterEach: function() {
			observer.reset();
			observer.reset(ns);
		},
		/* NOTE: The subscribe() tests rely on an assumption that notify() works as expected. */

		/* Why does subscribe() return the subscriber? Who knows; but it does so it gets tested. */
		testObserverSubscribeReturnsSubscriber: function() {
			function subscriber() {
				return true;
			}
			assert.strictEqual(observer.subscribe(subscriber), subscriber, "Observer.subscribe should return the subscribed function.");
		},
		testObserverSubscribe: function() {
			var wasNotified = false;

			function subscriber() {
				wasNotified = true;
			}

			observer.subscribe(subscriber);
			observer.notify();
			assert.isTrue(wasNotified, "The observered subscriber should be notified if it was correctly subscribed.");
		},
		testObserverSubscribeSubscriberNeedsArgs: function() {
			var wasNotified = false;
			function subscriber(arg1, arg2) {
				if (arg1 === "foo" && arg2 === "bar") {
					wasNotified = true;
				}
			}
			observer.subscribe(subscriber);
			observer.notify("foo", "bar");
			assert.isTrue(wasNotified, "Subscribe with args should honour notified args.");
		},
		testObserverSubscribeTwice: function() {
			var count = 0;

			function subscriber() {
				count++;
			}

			observer.subscribe(subscriber);
			observer.subscribe(subscriber);
			observer.notify();
			assert.strictEqual(count, 1, "Should not be able to subscribe more than once.");
		},
		testObserverSubscribeNoParams: function() {
			try {
				observer.subscribe();
				assert.fail(!null, null, "Expected exception: Subscribing without a subscriber should have failed.");  // should not get here
			}
			catch (e) {
				assert.isTrue(true, "Error expected.");
			}
		},
		/* Subscribing an object allows late binding of the subscriber function. The intent of this is that a method
		 * name is always passed to Observer.subscribe but lack of overloading in JS makes this impractical to enforce.
		 * This tests that subscribing an Object works. Later we test subscribing with a method name to testnotify of
		 * that method.*/
		testObserverSubscribeToNotAFunction: function() {
			var someObject = {
				prop1: null,
				prop2: function() {
					return true;
				}};

			assert.strictEqual(observer.subscribe(someObject), someObject, "We should be able to subscribe anything and have it returned.");
		},
		/* Subscriber groups are really an issue for notify(). This just tests that  the subscriber gets subscribed (by returning itself) and does not throw an exception. */
		testObserverSubscribeWithGroup: function() {
			function subscriber() {}
			assert.strictEqual(observer.subscribe(subscriber, {group: ns}), subscriber, "Subscribe with a group should return the subscriber.");
		},
		/* Tests of Observer context applied to a subscriber. The context supplied by a call to subscribe should be the "this" of the subscriber when notified. */
		testObserverSubscribeWithContext: function() {
			var actualContext,
				expectedContext = document.getElementById(containerId) || assert.fail(null, !null, "Cannot get context container.");

			function subscriber() {
				actualContext = this;
			}
			observer.subscribe(subscriber, {context: expectedContext});
			observer.notify();
			assert.strictEqual(actualContext, expectedContext, "Notify should have reset actualContext.");
		},
		testObserverSubscribeWithDifferentContexts: function() {
			var actualContext1,
				actualContext2,
				expectedContext1 = document.getElementById(containerId),
				expectedContext2 = {foo: "bar"};

			function subscriber1() {
				actualContext1 = this;
			}

			function subscriber2() {
				actualContext2 = this;
			}

			observer.subscribe(subscriber1, {context: expectedContext1});
			observer.subscribe(subscriber2, {context: expectedContext2});
			observer.notify();

			assert.strictEqual(actualContext1, expectedContext1, "Notify should have reset actualContext1.");
			assert.strictEqual(actualContext2, expectedContext2, "Notify should have reset actualContext2.");
		},
		testObserverSubscribeWithNullContext: function() {
			var actualContext,
				expectedContext = window.self;

			function subscriber() {
				actualContext = this;
			}
			observer.subscribe(subscriber, {context: null});  // pass nothing and we should get global context
			observer.notify();
			assert.strictEqual(actualContext, expectedContext, "Notify should have reset actualContext.");
		},
		testObserverSubscribeWithContextPassthru: function() {
			var actualContext,
				expectedContext = {foo: "bar"};

			function subscriber() {
				actualContext = this;
			}

			observer.subscribe(subscriber);  // if using "call" or "apply" and no context specified context should pass through
			observer.notify.call(expectedContext);
			assert.strictEqual(actualContext, expectedContext, "Notify should have reset actualContext.");
		},
		testObserverSubscribeWithMethodContext: function() {
			var actualContext,
				expectedContext;

			function Subscriber() {
				this.myMethod = function() {
					actualContext = this;
				};
			}

			expectedContext = new Subscriber();
			observer.subscribe(expectedContext, {method: "myMethod"}); // calling a method the context should be the object to which the method belongs
			observer.notify();
			assert.strictEqual(actualContext, expectedContext, "Notify should have reset actualContext.");
		},
		testObserverSubscribeWithMethodContextOverride: function() {
			var actualContext,
				expectedContext = document.getElementById(containerId);

			function Subscriber() {
				this.myMethod = function() {
					actualContext = this;
				};
			}
			observer.subscribe(new Subscriber(), {context: expectedContext, method: "myMethod"});
			observer.notify();
			assert.strictEqual(actualContext, expectedContext, "Notify should have reset actualContext");
		},
		/* Testing the priority parameter. */
		testObserverSubscribeImportance: function() {
			var amIImportant = true;

			function iAmImportant() {
				amIImportant = true;
			}
			function iAmNotImportant() {
				amIImportant = false;
			}

			// two subscribers, the second is important so gets notified first.
			observer.subscribe(iAmNotImportant, {priority: Observer.priority.MED});
			observer.subscribe(iAmImportant, {priority: Observer.priority.HIGH});
			observer.notify();
			assert.isFalse(amIImportant, "The important subscriber should be notified first, therefore amIImportant should be reset by the first subscriber");
		},
		testObserverSubscribeLowImportance: function() {
			var lastcaller = 1;

			observer.subscribe(function() {
				lastcaller = 2;
			}, {priority: Observer.priority.LOW});
			observer.subscribe(function() {
				lastcaller = 3;
			});
			observer.notify();
			assert.strictEqual(lastcaller, 2, "The low priority subscriber should be called last.");
		},
		testObserverSubscribeHighImportance: function() {
			var firstcaller;

			observer.subscribe(function() {
				firstcaller = firstcaller || 1;
			});
			observer.subscribe(function() {
				firstcaller = firstcaller || 2;
			}, {priority: Observer.priority.HIGH});
			observer.subscribe(function() {
				firstcaller = firstcaller || 3;
			});
			observer.notify();
			assert.strictEqual(firstcaller, 2, "The High prioritey subscriber should be called first.");
		},/* The important parameter is boolean and therefore should be able to be set by another function */
		testObserverSubscribeImportanceFunction: function() {
			var amIImportant = true;

			function iAmImportant() {
				amIImportant = true;
			}
			function iAmNotImportant() {
				amIImportant = false;
			}

			function setImportanceParameter(foo) {
				if (typeof foo === "string") {
					 return true;
				}
				return false;
			}
			observer.subscribe(iAmNotImportant, {priority: setImportanceParameter({p1: "empty"})});
			observer.subscribe(iAmImportant, {priority: setImportanceParameter("anything")});
			observer.notify();
			assert.isFalse(amIImportant, "The important subscriber should be notified first, therefore amIImportant should be reset by the first subscriber");
		},
		/* Testing the method parameter */
		testObserverSubscribeWithMethod: function() {
			var calledIn = null,
				objectSubscriber;

			function ObjectSubscriber() {
				this.doSubscribe = function() {
					calledIn = this;
				};
			}

			objectSubscriber = new ObjectSubscriber();
			observer.subscribe(objectSubscriber, {method: "doSubscribe"});
			observer.notify();
			assert.isNotNull(calledIn, "Subscribed method should have been called.");
		},
		testObserverSubscribeWithMethodKeepsContext: function() {
			var calledIn,
				objectSubscriber;

			function ObjectSubscriber() {
				this.doSubscribe = function() {
					calledIn = this;
				};
			}

			objectSubscriber = new ObjectSubscriber();
			observer.subscribe(objectSubscriber, {method: "doSubscribe"});
			observer.notify();
			assert.strictEqual(calledIn, objectSubscriber, "Context should have been kept when using a subscriber Object with method name.");
		},
		/* subscribe()'s method param does not come into play until notify() is called so it may contain pretty much
		 * anything but the various exceptions should be tested in notify() tests.*/
		testObserverSubscribeMethodUndefinedFunction: function() {
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
			observer.subscribe(objectSubscriber, {method: "listSubscribe"});
			assert.isTrue(iHaveBeenCalled, "Subscribe should have instantiated a subscriber object");
			observer.notify();
			assert.isFalse(hasSubscribed, "I should not have been subscribed");
		},
		/* unsubscribe tests */
		/* If there is more than one subscriber (in a group) unsubscribe expects an arg. */
		testObserverUnubscribeNoParamsMultiSubscribers: function() {
			var isSubscribed = false;

			function subscriber() {
				isSubscribed = true;
			}
			function dummySubscriber() {}

			try {
				observer.subscribe(subscriber);
				observer.subscribe(dummySubscriber);
				observer.unsubscribe();  // Call unsubscribe() with no args
				assert.fail(!null, null, "Error expected when unsubscribe called withoput args and more than one subscriber in group");
			}
			catch (error) {  // expected error
				observer.notify();
			}
			finally {
				assert.isTrue(isSubscribed, "Notify called and unsubscribe should have failed.");
			}
		},
		/* TODO: this is inconsistent and MUST be fixed. unsubscribe with no args should either always throw an illegal
		 * argument exception (as above) or NEVER (as here). My preference is for the former.*/
		testObserverUnubscribeNoParamsSingleSubscriber: function() {
			var isSubscribed = false;

			function subscriber() {
				isSubscribed = true;
			}

			observer.subscribe(subscriber);
			try {
				observer.unsubscribe();  // Call unsubscribe() with no params does not unsubscribe anything
				observer.notify();  // should reach here as no error thrown.
			}
			catch (error) {
				assert.fail(error, null, "No error expected when unsubscribe called without params and exactly one subscriber in group");
			}
			finally {
				assert.isTrue(isSubscribed, "Notify called and unsubscribe should have failed.");
			}
		},
		testObserverUnubscribeNoGroupReturnsSubscriber: function() {
			var theSubscriber = null,
				unsubSubscriber = null;

			function subscriber() {
				return true;
			}

			theSubscriber = observer.subscribe(subscriber);
			unsubSubscriber = observer.unsubscribe(theSubscriber);
			assert.strictEqual(unsubSubscriber, theSubscriber, "unsubscribe() should return the subscriber.");
		},
		testObserverUnubscribeNoGroup: function() {
			var isSubscribed = false;

			function subscriber() {
				isSubscribed = true;
			}

			observer.subscribe(subscriber);
			observer.unsubscribe(subscriber);

			// if we now call notify having unsubscribed then wasSubscribed should not be changed
			observer.notify();
			assert.isFalse(isSubscribed, "notify called after unsubscribe should not change value of isSubscribed");
		},
		testObserverUnubscribeWithGroup: function() {
			var theSubscriber = null;

			function subscriber() {
				return true;
			}

			theSubscriber = observer.subscribe(subscriber, {group: ns});
			assert.strictEqual(observer.unsubscribe(subscriber, ns), theSubscriber, "unsubscribe with group should return the subscriber.");
		},
		testObserverUnsubscribeWithGroupMismatchNotSubscriber: function() {
			/* note: this depends on working observer.filter (see below) */
			var theSubscriber = null;

			function subscriber() {
				return true;
			}

			theSubscriber = observer.subscribe(subscriber, {group: ns});
			assert.notEqual(observer.unsubscribe(subscriber), theSubscriber, "unsubscribe should not return the subscriber if not using the same group.");
		},
		testObserverUnsubscribeWithGroupMismatchIsNull: function() {
			/* note: this depends on working observer.filter (see below) */
			function subscriber() {
				return true;
			}

			observer.subscribe(subscriber, {group: ns});
			assert.isNull(observer.unsubscribe(subscriber), "unsubscribe when subscribe used a different group should return null.");
		},
		testObserverUnsubscribeWithGroupMismatch: function() {
			/* note: this depends on working observer.filter (see below) */
			var isSubscribed = false;

			function subscriber() {
				isSubscribed = true;
			}

			observer.subscribe(subscriber, {group: ns});
			observer.unsubscribe(subscriber); // should be nothing
			observer.setFilter(ns);
			observer.notify();
			assert.isTrue(isSubscribed, "Subscriber should have been notified since have not unsubscribed with the correct group.");
		},
		/* NOTIFY tests */
		testObserverNotify: function() {
			// NOTE: most subscribe/unsubscribe tests rely on a working notify() so there is some double-up.
			// this is same test as testObserverSubscribeSubscriberNeedsArgs() and is here for completeness.
			var wasNotified = false;

			function subscriber(arg1, arg2) {
				if (arg1 === "foo" && arg2 === "bar") {
					wasNotified = true;
				}
			}

			observer.subscribe(subscriber);
			observer.notify("foo", "bar");
			assert.isTrue(wasNotified);
		},
		testObserverNotifyOrderWithMultipleImportantAndNotImportant: function() {
			var idx = 0,
				result = {},
				expected = {0: 0, 1: 1, 2: 2, 3: 3, 4: 4, 5: 5};

			observer.subscribe(curriedSubscriber(2), {priority: Observer.priority.MED});
			observer.subscribe(curriedSubscriber(5), {priority: Observer.priority.LOW});
			observer.subscribe(curriedSubscriber(0), {priority: Observer.priority.HIGH});
			observer.subscribe(curriedSubscriber(3));
			observer.subscribe(curriedSubscriber(4));
			observer.subscribe(curriedSubscriber(1), {priority: Observer.priority.HIGH});

			observer.notify();
			assert.deepEqual(expected, result);

			function curriedSubscriber(expected) {
				return function () {
					result[idx++] = expected;
				};
			}
		},
		/* Check that adding order is honored across groups. */
		testObserverNotifyOrderWithDifferentGroups: function() {
			var idx = 0,
				filter = observer.getGroupAsWildcardFilter("oscar.mike.foxtrot.golf"),
				ns1 = "oscar.mike.foxtrot.golf",
				ns2 = "oscar.mike.*.golf",
				ns3 = "whisky.tango.foxtrot",
				result = {},
				expected = {0: 0, 1: 1, 2: 3, 3: 5};

			function curriedSubscriber(expected) {
				return function () {
					result[idx++] = expected;
				};
			}
			/* The try/finally is only here to ensure proper cleanup of the various groups. */
			try {
				observer.subscribe(curriedSubscriber(0), {group: ns1});
				observer.subscribe(curriedSubscriber(1), {group: ns2});
				observer.subscribe(curriedSubscriber(2), {group: ns3});
				observer.subscribe(curriedSubscriber(3), {group: ns2});
				observer.subscribe(curriedSubscriber(4), {group: ns3});
				observer.subscribe(curriedSubscriber(5), {group: ns1});

				observer.setFilter(filter);
				observer.notify();
			}
			finally {
				assert.deepEqual(result, expected);
				observer.reset(ns1);
				observer.reset(ns2);
				observer.reset(ns3);
			}
		},
		/* This is an advanced and critically important test to ensure that the order in which subscribers are added is
		 * honored even when multiple groups are involved (by means of a wildcard subscriber). AS WELL AS ensuring
		 * subscriber priority is also be honored across groups.
		 *
		 * This is a complex matter and you are probably reading this because you changed observer and screwed it up.
		 * Don't change the test: go back and get it right. */
		testObserverNotifyOrderWithPriorityAndDifferentGroups: function() {
			var idx = 0,
				filter = observer.getGroupAsWildcardFilter("oscar.mike.foxtrot.golf"),
				ns1 = "oscar.mike.foxtrot.golf",
				ns2 = "oscar.mike.*.golf",
				ns3 = "whisky.tango.foxtrot",
				result = {},
				expected = {0: 1, 1: 6, 2: 3, 3: 7, 4: 0, 5: 5};

			function curriedSubscriber(expected) {
				return function () {
					result[idx++] = expected;
				};
			}
			try {
				observer.subscribe(curriedSubscriber(0), {group: ns1, priority: Observer.priority.LOW});
				observer.subscribe(curriedSubscriber(1), {group: ns2, priority: Observer.priority.HIGH});
				observer.subscribe(curriedSubscriber(2), {group: ns3, priority: Observer.priority.HIGH});
				observer.subscribe(curriedSubscriber(3), {group: ns2});
				observer.subscribe(curriedSubscriber(4), {group: ns3});
				observer.subscribe(curriedSubscriber(5), {group: ns1, priority: Observer.priority.LOW});
				observer.subscribe(curriedSubscriber(6), {group: ns1, priority: Observer.priority.HIGH});
				observer.subscribe(curriedSubscriber(7), {group: ns1, priority: Observer.priority.MED});
				observer.setFilter(filter);
				observer.notify();
			}
			finally {
				assert.deepEqual(result, expected, "YOU HAVE BROKEN OBSERVER: GO AND FIX IT! _DO NOT_ CHANGE THIS TEST.");
				observer.reset(ns1);
				observer.reset(ns2);
				observer.reset(ns3);
			}
		},
		testObserverNotifySubscriberNotFunction: function() {
			// this is an extension of the subscriber test

			var someObject = {
					prop1: function() {
						aBoolean = true;
					}
				},
				aBoolean = false;

			observer.subscribe(someObject);
			observer.notify();
			assert.isFalse(aBoolean, "Notifying an object subscriber should not trigger the functions of that object.");
		},
		testObserverFilterSimple: function() {
			var wasNotified;

			function subscriber() {
				wasNotified = false;
			}

			function filterSubscriber() {
				wasNotified = true;
			}

			observer.subscribe(filterSubscriber, {group: ns});
			// if filter does nothing then the next subscriber will be the last called.
			observer.subscribe(subscriber, {priority: Observer.priority.LOW});

			observer.setFilter(ns);
			observer.notify();
			assert.isTrue(wasNotified, "wasNotified should not have been set by subscriber not matching filter.");
		},
		testObserverFilterWildcard: function() {

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
				observer.subscribe(filterSubscriber, {group: otherNs});
				observer.subscribe(wildFilterSubscriber, {group: wildNs});

				// wild card filter should result in both subscriberTwo and subscriberThree being called by notify
				observer.setFilter(filterFn);
				observer.notify();
			}
			finally {
				assert.strictEqual(wasNotified, 2, "wasNotified should only be incremented by each subscriber matching the wildcarded group name.");
				observer.reset(otherNs);
				observer.reset(wildNs);
			}
		},
		testObserverFilterFunction: function() {
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


			observer.subscribe(subscriber, {group: localns});
			observer.subscribe(filterSubscriber, {group: otherNs});
			observer.subscribe(wildFilterSubscriber, {group: thirdNs});

			observer.setFilter(filterFn);
			observer.notify();

			assert.strictEqual(wasNotified, 3, "wasNotified should be adjusted only by functions in a group matching that set by the filter function");
			observer.reset(localns);
			observer.reset(otherNs);
			observer.reset(thirdNs);
		},
		testObserverFilterFunctionNotBoolean: function() {
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

			observer.subscribe(subscriber, {group: localns});
			observer.subscribe(filterSubscriber, {group: otherNs});
			observer.subscribe(wildFilterSubscriber, {group: thirdNs});

			observer.setFilter(filterFn);

			// we expect JavaScript truthy/falsey not strictly boolean.
			observer.notify();

			assert.strictEqual(wasNotified, 7, "wasNotified should be set by everything which does not return a falsey value");
			observer.reset(localns);
			observer.reset(otherNs);
			observer.reset(thirdNs);
		},
		testObserverFilterNoParams: function() {
			var wasNotified = 0;

			function subscriber() {
				wasNotified += 1;
			}

			function filterSubscriber() {
				wasNotified += 2;
			}

			observer.subscribe(subscriber);
			observer.subscribe(filterSubscriber, {group: ns});

			try {
				observer.setFilter();  // try to call observer.setFilter with no filter defined should throw an error
			}
			catch (e) {
				observer.notify();
			}
			finally {
				assert.strictEqual(wasNotified, 1, "Call to notify should call subscribers in GLOBAL group since no filter was set");
			}
		},
		testObserverFilterNotStringOrFunction: function() {
			var wasNotified = 0;

			function subscriber() {
				wasNotified += 1;
			}

			function filterSubscriber() {
				wasNotified += 2;
			}

			observer.subscribe(subscriber);
			observer.subscribe(filterSubscriber, {group: ns});

			try {
				observer.setFilter(null);
			}
			catch (e) {
				observer.notify();
			}
			finally {
				assert.strictEqual(wasNotified, 1, "Call to notify should call subscribers in GLOBAL group since no filter was set");
				observer.reset(ns);
			}
		},
		testGetGroupAsWildcardFilter: function() {
			/* What we are testing here is that foo.*.bar returns true from filter "foo.a.b.c....bar" and
			 * "foo.bar.somethingelse" does not. */
			var localns = "ui.*.name.space",
				otherNs = "ui.*.namespace",
				filter = "ui.long.sample.foo.bar.namespace",
				returnedValue = observer.getGroupAsWildcardFilter(filter);

			assert.isFalse(returnedValue(localns));
			assert.isTrue(returnedValue(otherNs));
		},
		testObserverGetGroupAsWildcardFilterNoFilter: function() {
			var hadError = false;

			// attempting to use a null filter throws an error
			try {
				observer.getGroupAsWildcardFilter(null);
			}
			catch (error) {
				hadError = true;
			}
			finally {
				assert.isTrue(hadError, "Calling getGroupAsWildcardFilter without a filter should throw an error.");
			}
		},
		testObserverGetGroupAsWildcardFilterNonsenseFilter: function() {
			var filter = function() {
					return true;
				},
				hadError = false;

			try {
				observer.getGroupAsWildcardFilter(filter);  // nonsense filter, even if of the correct "type", will throw an error
			}
			catch (error) {
				hadError = true;
			}
			finally {
				assert.isTrue(hadError, "calling getGroupAsWildcardFilter without a valid filter should throw an error");
			}
		},
		testObserverCallback: function() {
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
			assert.strictEqual(someCounter, 2, "The return value of the subscriber should be the value passed to callback.");
		},
		testObserverCallbackReturnStopsNotify: function() {
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
			assert.isTrue(notified, "Callback return true should prevent notify from continuing.");
		},
		testObserverCallbackNoFunction: function() {
			var hadError = false;
			observer.subscribe(function () {});
			try {
				observer.setCallback();  // this call should always result in an error
			}
			catch (error) {
				hadError = true;
			}
			finally {
				assert.isTrue(hadError);
			}
		},
		testObserverCallbackNotFunctionStillNotifies: function() {
			var wasNotified,
				callback = {};

			function subscriber() {
				wasNotified = true;
			}

			try {
				observer.subscribe(subscriber);
				observer.setCallback(callback);
				observer.notify();
			}
			finally {
				assert.isTrue(wasNotified, "setCallback without a valid callback function does not break notify.");
			}
		},
		testObserverCallbackNullStillNotifies: function() {
			var wasNotified;

			function subscriber() {
				wasNotified = true;
			}

			try {
				observer.subscribe(subscriber);
				observer.setCallback(null);  // throws an error
			}
			catch (e) {
				observer.notify();
			}
			finally {
				assert.isTrue(wasNotified, "setCallback with a null callback function does not break notify.");
			}
		},
		testObserverCallbackWrongFunction: function() {
			var wasNotified = 0;

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
			assert.isTrue(wasNotified > 1, "wasNotified should be changed if any params were passed to callback.");
		},
		/* Reset tests */
		testObserverReset: function() {
			/* this is a modification of two tests we have already seen. We know from above that wasNotified will change if reset does not work as expected */
			var wasNotified = false;

			function subscriber(arg1, arg2) {
				if (arg1 === "foo" && arg2 === "bar") {
					wasNotified = true;
				}
			}
			observer.subscribe(subscriber);

			observer.reset();
			observer.notify("foo", "bar");
			assert.isFalse(wasNotified);
		},
		testObserverResetWithGroup: function() {
			var wasNotified = false;

			function subscriber() {
				wasNotified = true;
			}
			observer.subscribe(subscriber, {group: ns});

			observer.reset(ns);
			observer.setFilter(ns);
			observer.notify();
			assert.isFalse(wasNotified);
		},
		testObserverResetGlobalNotGroup: function() {
			var wasNotified = 0;

			function subscriber() {
				wasNotified = 1;
			}
			function subscriber2() {
				wasNotified = 2;
			}
			observer.subscribe(subscriber, {group: ns});
			observer.subscribe(subscriber2);

			observer.reset();
			observer.notify();  // no filter so notify global
			observer.setFilter(ns);
			observer.notify();  // group not resert
			assert.strictEqual(wasNotified, 1);
		},
		testObserverResetGroupNotGlobal: function() {
			var wasNotified = 0;

			function subscriber() {
				wasNotified = 1;
			}
			function subscriber2() {
				wasNotified = 2;
			}
			observer.subscribe(subscriber, {group: ns});
			observer.subscribe(subscriber2);

			observer.reset(ns);
			observer.notify();  // no filter so notify global
			observer.setFilter(ns);
			observer.notify();
			assert.strictEqual(wasNotified, 2);
		}
	});
});
