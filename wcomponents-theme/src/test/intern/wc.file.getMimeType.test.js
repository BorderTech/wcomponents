define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	var TEST_MODULE = "wc/file/getMimeType",
		controller;

	registerSuite({
		name: TEST_MODULE,
		setup: function() {
			return testutils.setupHelper([TEST_MODULE], function(obj) {
				controller = obj;
			});
		},
		testGetMimeType: function() {
			var element = new testutils.MockFileSelector("", "@HERE@\\resources\\note.txt", "text/plain", 16),
				result = controller(element)[0];
			assert.strictEqual(result.mime, "text/plain");
		},
		testGetExtension: function() {
			var element = new testutils.MockFileSelector("", "@HERE@\\resources\\note.txt", "text/plain", 16),
				result = controller(element)[0];
			assert.strictEqual(result.ext, "txt");
		}
	});
});
