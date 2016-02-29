define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";



	var controller, testProp = "babyfoot" + (new Date()).getTime(),
		testVal = "elephant";


	registerSuite({
		name: "storage",
		setup: function() {
			return testutils.setupHelper(["wc/dom/storage"], function(obj) {
				controller = obj;

			});
		},
		beforeEach: function() {
			controller.erase(testProp, false);
			controller.erase(testProp, true);

			assert.isFalse(!!controller.get(testProp, false), "Tests should start in clean state");
			assert.isFalse(!!controller.get(testProp, true, "Tests should start in clean state"));
		},
		teardown: function() {
			controller.erase(testProp, false);
			controller.erase(testProp, true);
		},
		testPutNotSessionGetNotSession: function() {
			controller.put(testProp, testVal, false);
			assert.strictEqual(controller.get(testProp, false), testVal);
		},

		testPutNotSessionGetWithSession: function() {
			controller.put(testProp, testVal, false);
			assert.notStrictEqual(controller.get(testProp, true), testVal);
		},

		testPutGetErase: function() {
			controller.put(testProp, testVal, false);
			controller.erase(testProp, false);
			assert.isFalse(!!controller.get(testProp, false));
		},

		testPutSessionGetSession: function() {
			controller.put(testProp, testVal, true);
			assert.strictEqual(controller.get(testProp, true), testVal);
		},
		testPutSessionGetNotSession: function() {
			controller.put(testProp, testVal, true);
			assert.notStrictEqual(controller.get(testProp, false), testVal);
		},

		testPutSessionEraseSession: function() {
			controller.put(testProp, testVal, true);
			controller.erase(testProp, true);
			assert.isFalse(!!controller.get(testProp, true));
		}
	});
});
