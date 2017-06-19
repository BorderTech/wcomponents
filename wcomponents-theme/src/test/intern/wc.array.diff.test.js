define(["intern!object", "intern/chai!assert", "./resources/test.utils"], function (registerSuite, assert, testutils) {
	"use strict";
	/*
	 * To create a new Intern unit test file:
	 *
	 * 1. Save this as a *.js file.
	 * 2. Set TEST_MODULE to the name of the module to test, e.g. "wc/ui/foo" and optionally set a suiteName.
	 * 3. If the test requires other dependencies create the deps array containing these.
	 * 4. If the controller needs specific UI to test set ONE of testContent (HTML) OR urlResource (from where the test UI will be loaded)
	 * 5. If there are other dependencies they can be named. This is optional as they _could_ be referenced as arg[n] but that is really ugly.
	 *    Map setup's then function's arg members to these names. arg[0] is thecontroller.
	 * 7. Optionally update (or remove if not needed) setup, beforeEach, afterEach and teardown
	 * 6. Write tests use `controller` as the var name for the module being tested. Review `testGotController` as a simple sample.
	 */

	var
		/**
		 * The module name of the module being tested eg "wc/ui/foo".
		 * @type String
		 */
		TEST_MODULE = "wc/array/diff",
		/**
		 * A human readable name for the suite. This could be as simpl as TEST_MODULE.
		 * @type String
		 */
		suiteName = TEST_MODULE,// .match(/\/([^\/]+)$/)[1],
		/**
		 * An options array of dependency names in addition to TEST_MODULE, Define a String Array here and setup will convert it to a module array.
		 * @type arr
		 */
		deps,
		// If you have extra dependencies you will want a way to reference them.
		//
		// END CONFIGURATION VARS
		//
		// the next two are not settable.
		controller; // This will hold any UI needed for the tests. It is left undefined if testContent & urlResource are both falsey.

	registerSuite({
		name: suiteName,

		setup: function() {
			var allDeps = (deps && deps.length) ? deps : [];
			allDeps.unshift(TEST_MODULE);
			return testutils.setupHelper(allDeps).then(function(arg) {
				controller = arg[0];
			});
		},
		testDiff: function() {
			var expected = [1, 2],
				actual = controller([1, 2, 3], [3, 4, 5]);
			assert.sameMembers(expected, actual);
		},
		testSame: function() {
			var actual = controller([1, 2, 3], [1, 2, 3]);
			assert.isTrue(Array.isArray(actual) && actual.length === 0);
		},
		testNothingToDiff: function() {
			var actual = controller();
			assert.isTrue(Array.isArray(actual) && actual.length === 0);
		},
		testNotArray: function() {
			var actual = controller([1, 2, 3], {});
			assert.isTrue(Array.isArray(actual) && actual.length === 0);
		},
		testSubset: function() {
			var actual = controller([1, 2, 3], [1, 2, 3, 4, 5]);
			assert.isTrue(Array.isArray(actual) && actual.length === 0);
		},
		testDifferentOrderStillSame: function() {
			var actual = controller([1, 2, 3], [3, 1, 2]);
			assert.isTrue(Array.isArray(actual) && actual.length === 0);
		}
	});
});