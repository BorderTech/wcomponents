define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var cookie,
			testProp = "babyfoot" + (new Date()).getTime(),
			testVal = "elephant";

		registerSuite({
			name: "domCookie",
			setup: function() {
				return testutils.setupHelper(["wc/dom/cookie"], function(obj) {
					cookie = obj;
				});
			},
			beforeEach: function() {
				cookie.erase(testProp);
				assert.isFalse(!!cookie.read(testProp), "Tests should start in clean state");
			},
			testPutGet: function() {
				cookie.create(testProp, testVal, 10);
				assert.strictEqual(testVal, cookie.read(testProp));
			},
			testPutGetErase: function() {
				cookie.create(testProp, testVal, 10);
				cookie.erase(testProp);
				assert.isFalse(!!cookie.read(testProp));
			},
			testPutGetSession: function() {
				cookie.create(testProp, testVal);
				assert.strictEqual(testVal, cookie.read(testProp));
			},
			testPutGetEraseSession: function() {
				cookie.create(testProp, testVal);
				cookie.erase(testProp);
				assert.isFalse(!!cookie.read(testProp));
			}
		});
	});
