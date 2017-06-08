define(["intern!object", "intern/chai!assert", "wc/ui/wrappedInput", "wc/dom/shed", "wc/dom/wrappedInput" , "./resources/test.utils!"],
	function (registerSuite, assert, controller, shed, domWrappedInput, testutils) {
		"use strict";
		/*
		 * Unit tests for wc/ui/wrappedInput
		 */
		var testHolder,
			testContent = "<div id='wrappedinputtestcontent'>\n\
				<span class='wc-input-wrapper' id='wrapper'><input id='wrappedinput' type='text'></span>\n\
				</div>";

		registerSuite({
			name: "wc/ui/wrappedInput",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testMandate: function () {
				var wrapper = document.getElementById("wrapper"),
					target = domWrappedInput.getInput(wrapper);
				return shed.mandatory(wrapper).then(function() {
					assert.isTrue(shed.isMandatory(target));
				});
			},
			testOptional: function () {
				var wrapper = document.getElementById("wrapper"),
					target = domWrappedInput.getInput(wrapper);
				return shed.mandatory(wrapper).then(function() {
					assert.isTrue(shed.isMandatory(target));
				}).then(function() {
					return shed.optional(wrapper).then(function() {
						assert.isFalse(shed.isMandatory(target));
					});
				});
			},
			testNoDefaultPlaceHolder: function() {
				var wrapper = document.getElementById("wrapper"),
					target = domWrappedInput.getInput(wrapper);
				assert.isFalse(target.hasAttribute("placeholder"));
			},
			testMandateSetsPlaceholder: function () {
				var wrapper = document.getElementById("wrapper"),
					target = domWrappedInput.getInput(wrapper);
				return shed.mandatory(wrapper).then(function() {
					assert.isTrue(!!target.getAttribute("placeholder"));
				});
			},
			testOptionalUnsetsPlaceholder: function () {
				var wrapper = document.getElementById("wrapper"),
					target = domWrappedInput.getInput(wrapper);
				return shed.mandatory(wrapper).then(function() {
					return shed.optional(wrapper).then(function() {
						assert.isFalse(target.hasAttribute("placeholder"));
					});
				});
			}
		});
	}
);

