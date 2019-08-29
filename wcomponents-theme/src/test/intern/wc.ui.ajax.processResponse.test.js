define(["intern!object", "intern/chai!assert", "wc/dom/Widget", "wc/ui/ajax/processResponse", "intern/resources/test.utils!"],
	function (registerSuite, assert, Widget, processResponse, testutils) {
		"use strict";

		var testHolder,
			errorMessage = "Something is rotten in the state of Denmark",
			targetId = "process-response-target-id",
			widget = new Widget("div","", { id: targetId });

		registerSuite({
			name: "wc/ui/ajax/processResponse",
			beforeEach: function() {
				var element;
				testHolder = testutils.getTestHolder();
				element = testHolder.appendChild(widget.render());
				element.textContent = "I am an ajax target hoorah!";
			},
			testProcessError: function() {
				return processResponse.processError(errorMessage, { loads: [targetId] }).then(function() {
					var element = document.getElementById(targetId);
					assert.strictEqual(document.ELEMENT_NODE, element.firstChild.nodeType, "Error message should be presented to user with advanced feedback module");
					assert.equal(errorMessage, element.textContent || element.innerText);
				});
			},
			testProcessErrorNoFeedbackModule: function() {
				return processResponse.processError(errorMessage, { loads: [targetId] }, true).then(function() {
					var element = document.getElementById(targetId);
					assert.strictEqual(document.TEXT_NODE, element.firstChild.nodeType, "Error message should be presented to user with basic feedback module");
					assert.equal(errorMessage, element.textContent || element.innerText);
				});
			}
		});
	});
