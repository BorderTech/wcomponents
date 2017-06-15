define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";



	var controller,
		testProp = "babyfoot" + (new Date()).getTime(),
		testVal = "elephant",
		canPut;

	// Safari does not allow put when running in private mode - which is how it runs
	function canPutInSafari() {
		if (typeof window.localStorage === 'object') {
			try {
				window.localStorage.setItem('localStorage', 1);
				window.localStorage.removeItem('localStorage');
				return true;
			} catch (e) {
				// we are in Safari in private mode.
				return false;
			}
		}
		return true;
	}

	registerSuite({
		name: "storage",
		setup: function() {
			return testutils.setupHelper(["wc/dom/storage"]).then(function(obj) {
				controller = obj[0];
				canPut = canPutInSafari();
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
			if (canPut) {
				controller.put(testProp, testVal, false);
				assert.strictEqual(controller.get(testProp, false), testVal);
			}
		},
		testPutNotSessionGetWithSession: function() {
			if (canPut) {
				controller.put(testProp, testVal, false);
				assert.notStrictEqual(controller.get(testProp, true), testVal);
			}
		},
		testPutGetErase: function() {
			if (canPut) {
				controller.put(testProp, testVal, false);
				controller.erase(testProp, false);
				assert.isFalse(!!controller.get(testProp, false));
			}
		},
		testPutSessionGetSession: function() {
			if (canPut) {
				controller.put(testProp, testVal, true);
				assert.strictEqual(controller.get(testProp, true), testVal);
			}
		},
		testPutSessionGetNotSession: function() {
			if (canPut) {
				controller.put(testProp, testVal, true);
				assert.notStrictEqual(controller.get(testProp, false), testVal);
			}
		},
		testPutSessionEraseSession: function() {
			if (canPut) {
				controller.put(testProp, testVal, true);
				controller.erase(testProp, true);
				assert.isFalse(!!controller.get(testProp, true));
			}
		}
	});
});
