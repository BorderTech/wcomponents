define(["intern!object", "intern/chai!assert", "wc/ui/disabledLink", "wc/dom/event", "./resources/test.utils"],
	function (registerSuite, assert, controller, event, testutils) {
		"use strict";

		var testHolder,
			linkId = "ui-disabledlink-1",
			testContent = "<a id='" + linkId + "' href='#' aria-disabled='true'>Click me</a>",
			called = false;

		function clickEvent($event) {
			if (!$event.defaultPrevented) {
				called = true;
			}
		}

		registerSuite({
			name: "wc/ui/disabledLink",
			setup: function() {
				testHolder = testutils.getTestHolder();
				testHolder.innerHTML = testContent;
				event.add(document.body, clickEvent, 1);
			},
			testDisabledLink: function() {
				var target = document.getElementById(linkId);
				event.fire(target, event.TYPE.click); // target.click();
				assert.isFalse(called, "Expected the click to be ignored.");
			}
		});
	});
