define(["intern!object", "intern/chai!assert", "wc/dom/isAcceptableTarget", "./resources/test.utils!"],
	function (registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			sameElementTestId = "isacceptabletargettest-button",
			buttonHolderId = "isacceptabletargettest-buttonholder",
			target1id = "isacceptabletargettest-insidenofocus",
			element1id = "isacceptabletargettest-nofocuscontainer",
			target2id = "isacceptabletargettest-insidefocusable",
			element2id = "isacceptabletargettest-focusablefocus",
			testContent = "\
<div id='" + buttonHolderId + "'>potential event handler\n\
	<button type='button' id='" + sameElementTestId + "'>event target</button>\n\
</div>\n\
<div>\n\
	<span id='" + element1id + "'>potential event handler\n\
		<span id='" + target1id + "'>event target</span>\n\
	</span>\n\
</div>\n\
<div>\n\
	<span tabindex='0' role='row' id='" + element2id +  "'>potential event handler\n\
		<span id='" + target2id + "' tabindex='0' role='button'>event target</span><\n\
	</span>\n\
</div>";

		registerSuite({
			name: "wc/dom/isAcceptableTarget",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testIsAcceptableTarget_same: function() {
				var element = document.getElementById(sameElementTestId);
				assert.isTrue(controller(element, element));
			},
			testIsAcceptableTarget_notActiveTarget: function() {
				var testElement = document.getElementById(element1id),
					testEventTarget = document.getElementById(target1id);
				assert.isTrue(controller(testElement, testEventTarget));
			},
			testIsAcceptableTarget_activeTarget: function() {
				var testElement = document.getElementById(element2id),
					testEventTarget = document.getElementById(target2id);
				assert.isFalse(controller(testElement, testEventTarget));
			},
			testIsAcceptableTarget_SelfFirstFocusableElement: function() {
				var testElement = document.getElementById(sameElementTestId),
					testEventTarget = document.getElementById(buttonHolderId);
				assert.isTrue(controller(testElement, testEventTarget));
			}
		});
	}
);
