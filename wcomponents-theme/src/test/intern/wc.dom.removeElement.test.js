define(["intern!object", "intern/chai!assert", "wc/dom/removeElement", "./resources/test.utils!"],
	function (registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			removeId = "removeelementtest-target",
			testContent = "<span id='" + removeId + "'>content</span>";

		registerSuite({
			name: "wc/dom/removeElement",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testRemoveElement_noId: function() {
				assert.isOk(document.getElementById(removeId));
				assert.isUndefined(controller());
				assert.isOk(document.getElementById(removeId));
			},
			testRemoveElement: function() {
				assert.isOk(document.getElementById(removeId));
				assert.isUndefined(controller(removeId));
				assert.isNotOk(document.getElementById(removeId));
			}

			// TODO: needs tests using timeout
		});
	}
);
