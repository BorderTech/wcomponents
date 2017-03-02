define(["intern!object", "intern/chai!assert", "wc/dom/textContent", "./resources/test.utils!"],
	function(registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			CONTENT = "this is the content";

		registerSuite({
			name: "wc/dom/textContent",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				if (!testHolder) {
					assert.fail(true, undefined, "did not create testHolder.");
				}
				testHolder.innerHTML = "";
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGet: function() {
				testHolder.innerHTML = "<p id='p1'>" + CONTENT + "</p>";
				assert.strictEqual(CONTENT, controller.get(document.getElementById("p1")));
			},
			testSet: function() {
				var expected = "<p>" + CONTENT.toLowerCase() + "</p>",
					outer = document.createElement("div"),
					inner = document.createElement("p");
				outer.appendChild(inner);
				controller.set(inner, CONTENT);
				assert.strictEqual(expected, outer.innerHTML.toLowerCase());
			}
		});
	});