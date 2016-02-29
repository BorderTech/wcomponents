define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var initialise, TIMEOUT = 3000;  // needs to be large...
		/*
		 * NOTE on async tests.
		 * We can call the deferred object resolver from the initialise callback and
		 * assume that all callbacks are called, even in the test of that
		 * assumption, because if the resolver is not called the test will timeout and
		 * therefore fail.
		 */
		registerSuite({
			name: "wc/dom/initialise",
			setup: function() {
				return testutils.setupHelper(["wc/dom/initialise"], function(obj) {
					initialise = obj;
				});
			},
			addInitRoutineInvalidListener: function() {
				assert.isTrue(initialise.addInitRoutine(7) === null, "addInitRoutine should return null when add fails");
			},
			addCallbackInvalidListener: function() {
				assert.isTrue(initialise.addCallback(7) === null, "addCallback should return null when add fails");
			},
			toStringReflectsState: function() {
				var after, before = initialise.toString();
				initialise.addBodyListener(function() {});  // adding a listener should change the toString
				after = initialise.toString();
				assert.notEqual(before, after, "toString not good enough");
			},
			allCallbacksCalled: function() {
				/*
				 * Check all subscribers are called.
				 */
				var initRoutine, bodyListener, postInit, ptr = 0,
					expected = 3,
					// expected is the number of callbacks we add to initialise
					deferred = this.async(TIMEOUT);

				function callbackFactory(/* expectedOrder, name */) {
					return function() {
						ptr++;
						if (ptr === expected) {
							deferred.resolve();
						}
					};
				}

				function resolver() {
					assert.strictEqual(expected, ptr);
				}

				deferred.callback(resolver);

				bodyListener = callbackFactory(1, "bodyListener");
				postInit = callbackFactory(2, "postInit");
				initRoutine = callbackFactory(0, "initRoutine");

				initialise.addBodyListener({
					initialise: bodyListener
				});
				initialise.addCallback(postInit);
				initialise.addInitRoutine(initRoutine);
			},
			callbackRunOrder: function() {
				/*
				 * Check subscribers are called in the correct order.
				 */
				var initRoutine, bodyListener, postInit, ptr = 0,
					expected = 3,
					// expected is the number of callbacks we add to initialise
					deferred = this.async(TIMEOUT);

				function callbackFactory(expectedOrder, name) {
					return function() {
						assert.strictEqual(expectedOrder, ptr, name + " not called in expected order");
						ptr++;
						if (ptr === expected) {
							deferred.resolve();
						}
					};
				}
				// this does not need to do anything
				function resolver() {
					return true;
				}
				deferred.callback(resolver);

				bodyListener = callbackFactory(1, "bodyListener");
				postInit = callbackFactory(2, "postInit");
				initRoutine = callbackFactory(0, "initRoutine");

				initialise.addBodyListener({
					initialise: bodyListener
				});
				initialise.addCallback(postInit);
				initialise.addInitRoutine(initRoutine);

				// return deferred;
			},
			addNewSubscriberDuringExecute: function() {
				/**
				 * Tests a scenario that caused real world problems, it is important to maintain
				 * this test.
				 * A subscriber to initialise causes a new subscriber to be added to initialise.
				 * We need to make sure that new subscriber is subsequently executed.
				 */
				var initRoutine, bodyListener, postInit, initRoutineTwo, ptr = 0,
					expected = 4,
					// we are adding a new subscriber during execute remember
					deferred = this.async(TIMEOUT);

				function callbackFactory(expectedOrder, name, func) {
					return function() {
						if (func) {
							initialise.addInitRoutine(func);
						}
						ptr++;
						if (ptr === expected) {
							deferred.resolve();
						}
					};
				}

				function resolver() {
					assert.strictEqual(expected, ptr, "Not all subscribers called");
				}

				deferred.callback(resolver);

				initRoutineTwo = callbackFactory(3, "initRoutineTwo");
				postInit = callbackFactory(2, "postInit");
				initRoutine = callbackFactory(0, "initRoutine");

				bodyListener = callbackFactory(1, "bodyListener", initRoutineTwo);
				initialise.addBodyListener({
					initialise: bodyListener
				});
				initialise.addCallback(postInit);
				initialise.addInitRoutine(initRoutine);

				// return deferred;
			}
		});
	});
