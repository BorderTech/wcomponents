define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var getFirstLabelForElement, testHolder,
			urlResource = "../../target/test-classes/theme_default/intern/resources/domTest.html";

		registerSuite({
			name: "domGetFirstLabelForElement",
			setup: function() {
				return testutils.setupHelper(["wc/ui/getFirstLabelForElement"], function(obj) {
					getFirstLabelForElement = obj;
					testHolder = testutils.getTestHolder();
					testutils.setUpExternalHTML(urlResource, testHolder);
				});
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGetFirstLabel: function() {
				var element = document.getElementById('male'),
					label = getFirstLabelForElement(element),
					expected = document.getElementById('maleLabel');
				assert.strictEqual(expected, label);
			},
			testGetFirstLabelForFieldset: function() {
				var element = document.getElementById('fs1'),
					label = getFirstLabelForElement(element),
					expected = document.getElementById('leg1');
				assert.strictEqual(expected, label);
			},
			testGetFirstLabelContentOnly: function() {
				var element = document.getElementById('male'),
					label = getFirstLabelForElement(element, true),
					expected = "Male";
				assert.strictEqual(expected, label);
			}
		});
	});
