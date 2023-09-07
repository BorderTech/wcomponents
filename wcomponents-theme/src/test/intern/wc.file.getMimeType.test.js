define(["intern!object", "intern/chai!assert", "intern/resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";

	const TEST_MODULE = "wc/file/getMimeType";
	let controller;

	registerSuite({
		name: TEST_MODULE,
		setup: function() {
			return testutils.setupHelper([TEST_MODULE]).then((arr) => {
				controller = arr[0];
			});
		},
		testGetMimeType: function() {
			const element = new testutils.MockFileSelector("", "@HERE@\\resources\\note.txt", "text/plain", 16),
				result = controller(element)[0];
			assert.strictEqual(result.mime, "text/plain");
		}
	});
});
