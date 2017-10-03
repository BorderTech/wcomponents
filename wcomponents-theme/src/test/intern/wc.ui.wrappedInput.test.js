define(["intern!object", "intern/chai!assert", "wc/ui/wrappedInput", "wc/dom/shed", "wc/dom/wrappedInput" ,"wc/i18n/i18n", "./resources/test.utils!"],
	function (registerSuite, assert, controller, shed, domWrappedInput, i18n, testutils) {
		"use strict";
		/*
		 * Unit tests for wc/ui/wrappedInput
		 */
		var testHolder,
			testContent = "<div id='wrappedinputtestcontent'>\n\
				<span class='wc-input-wrapper' id='wrapper'><input id='wrappedinput' type='text'></span>\n\
				<span class='wc-input-wrapper' id='wrapper-with-placeholder'><input id='wrapper_input' type='text' placeholder='foo'></span>\n\
				<span class='wc-input-wrapper' id='wrapper-with-required-placeholder'><input id='wrapper_input' type='text' required></span>\n\
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
			testMandateDoesNotOverWritePlaceholder: function() {
				var wrapper = document.getElementById("wrapper-with-placeholder"),
					target = domWrappedInput.getInput(wrapper),
					expected = target.getAttribute("placeholder");
				return shed.mandatory(wrapper).then(function() {
					assert.strictEqual(target.getAttribute("placeholder"), expected, "Placeholder should not have changed");
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
			},
			testOptionalDoesNotUnsetExplicitPlaceholder: function () {
				var wrapper = document.getElementById("wrapper-with-placeholder"),
					target = domWrappedInput.getInput(wrapper),
					expected = target.getAttribute("placeholder");
				return shed.mandatory(wrapper).then(function() {
					return shed.optional(wrapper).then(function() {
						assert.strictEqual(target.getAttribute("placeholder"), expected, "Placeholder should not have changed");
					});
				});
			},
			testOptionalDoesUnsetExplicitPartialRequiredPlaceholder: function () {
				var wrapper = document.getElementById("wrapper-with-required-placeholder"),
					target = domWrappedInput.getInput(wrapper),
					expected = "foo";
				return i18n.translate("requiredPlaceholder").then(function(placeHolderText) {
					target.setAttribute("placeholder", placeHolderText + " foo");

					return shed.optional(wrapper).then(function() {
						assert.strictEqual(target.getAttribute("placeholder").trim(), expected, "Placeholder should have changed");
					});
				});
			},
			testOptionalDoesUnsetExplicitPartialRequiredPlaceholderAfter: function () {
				var wrapper = document.getElementById("wrapper-with-required-placeholder"),
					target = domWrappedInput.getInput(wrapper),
					expected = "foo";
				return i18n.translate("requiredPlaceholder").then(function(placeHolderText) {
					target.setAttribute("placeholder", "foo " + placeHolderText);

					return shed.optional(wrapper).then(function() {
						assert.strictEqual(target.getAttribute("placeholder").trim(), expected, "Placeholder should have changed");
					});
				});
			},
			testOptionalDoesUnsetExplicitPartialRequiredPlaceholderBetween: function () {
				var wrapper = document.getElementById("wrapper-with-required-placeholder"),
					target = domWrappedInput.getInput(wrapper),
					expected = "foo  bar";
				return i18n.translate("requiredPlaceholder").then(function(placeHolderText) {
					target.setAttribute("placeholder", "foo " + placeHolderText + " bar");

					return shed.optional(wrapper).then(function() {
						assert.strictEqual(target.getAttribute("placeholder").trim(), expected, "Placeholder should have changed");
					});
				});
			}
		});
	}
);

