define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	/* ACHTUNG!
	 * These tests rely on a file input element so we can't actually test the real thing in many of the tests (because
	 * you can't programatically populate a file input element). */

	var TEST_MODULE = "wc/file/getFileSize",
		controller;
	registerSuite({
		name: TEST_MODULE,
		setup: function() {
			return testutils.setupHelper([TEST_MODULE], function(obj) {
				controller = obj;
			});
		},
		testGetFileSize: function() {
			var element = new testutils.MockFileSelector("", "@HERE@\\resources\\note.txt", "text/plain", 16);
			assert.strictEqual(controller(element)[0], 16);
		}
	});
});
