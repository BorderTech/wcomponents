define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";
	/* ACHTUNG!
	 * Because these tests rely on a file input element we can't actually test the real thing in many of the tests
	 * (because you can't programaticallypopulate a file input element). */

	var TEST_MODULE = "wc/file/accepted",
		controller,
		testHolder,
		urlResource = "@RESOURCES@/fileAccepted.html";
	registerSuite({
		name: TEST_MODULE,
		setup: function() {
			var result = testutils.setupHelper([TEST_MODULE]).then(function(arr) {
				controller = arr[0];
				testHolder = testutils.getTestHolder();
				return testutils.setUpExternalHTML(urlResource, testHolder);
			});
			return result;
		},
		teardown: function() {
			testHolder.innerHTML = "";
		},
		testAcceptedWithNoValueNoAccept: function() {
			var element = document.getElementById("file1");
			assert.isTrue(controller(element));
		},
		/* NOTE for next three if no value in the file input then they must be "accepted" as the test array is empty */
		testAcceptedWithNoValueSingleMimeType: function() {
			var element = document.getElementById("file2");
			assert.isTrue(controller(element));
		},
		testAcceptedWithNoValueWildCardMimeType: function() {
			var element = document.getElementById("file3");
			assert.isTrue(controller(element));
		},
		testAcceptedWithNoValueMultipleMimeTypes: function() {
			var element = document.getElementById("file4");
			assert.isTrue(controller(element));
		},
		testAcceptedWithNoAccept: function() {
			var element = new testutils.MockFileSelector(null, "foo.gif", "image/gif");
			assert.isTrue(controller(element));
		},
		testAccepted: function() {
			var element = new testutils.MockFileSelector("image/gif", "foo.gif", "image/gif");
			assert.isTrue(controller(element));
		},
		testAcceptedWithMultiple: function() {
			var element = new testutils.MockFileSelector("image/png, image/gif", "foo.gif", "image/gif");
			assert.isTrue(controller(element));
		},
		testAcceptedWithWildcard: function() {
			var element = new testutils.MockFileSelector("image/*", "foo.gif", "image/gif");
			assert.isTrue(controller(element));
		},
		testAcceptedWithMismatch: function() {
			var element = new testutils.MockFileSelector("text/plain", "foo.gif", "image/gif");
			assert.isFalse(controller(element));
		},
		testAcceptedWithMismatchAndWildcard: function() {
			var element = new testutils.MockFileSelector("image/*", "foo.txt", "text/plain");
			assert.isFalse(controller(element));
		}
	});
});
