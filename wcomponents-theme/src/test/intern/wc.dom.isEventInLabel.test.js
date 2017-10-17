define(["intern!object", "intern/chai!assert", "wc/dom/isEventInLabel", "./resources/test.utils!"],
	function (registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			noLabelId = "iseventinlabeltest-nolabel",
			inlabelNoFocusId = "iseventinlabeltest-nofocus",
			inLabelWithFocusId = "iseventinlabeltest-focusable",
			testContent = "<div><button type='button' id='" + noLabelId + "'>button 1</button></div>\n\
<div><label>label content <span id='" + inlabelNoFocusId + "'>event target</span></label></div>\n\
<div><label>label content <span tabindex='0' role='button'>focusable span <span id='" + inLabelWithFocusId + "'>event target</span></span></label></div>";

		registerSuite({
			name: "wc/dom/isEventInLabel",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testIsEventInLabel_noLabel: function() {
				var target = document.getElementById(noLabelId);
				assert.isFalse(controller(target));
			},
			testIsInLabel_notFocusable: function() {
				var target = document.getElementById(inlabelNoFocusId);
				assert.isTrue(controller(target));
			},
			testIsInLabel_focusable: function() {
				var target = document.getElementById(inLabelWithFocusId);
				assert.isFalse(controller(target));
			}
		});
	}
);
