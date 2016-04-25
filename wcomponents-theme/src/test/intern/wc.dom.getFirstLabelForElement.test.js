define(["intern!object", "intern/chai!assert", "../intern/resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var getFirstLabelForElement, testHolder,
			urlResource = "@RESOURCES@/domTest.html";

		registerSuite({
			name: "domGetFirstLabelForElement",
			setup: function() {
				var result = testutils.setupHelper(["wc/ui/getFirstLabelForElement"]).then(function(arr) {
					getFirstLabelForElement = arr[0];
					testHolder = testutils.getTestHolder();
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGetFirstLabel: function() {
				var element = document.getElementById("male"),
					label = getFirstLabelForElement(element),
					expected = document.getElementById("maleLabel");
				assert.strictEqual(expected, label);
			},
			testGetFirstLabelForFieldset: function() {
				var element = document.getElementById("fs1"),
					label = getFirstLabelForElement(element),
					expected = document.getElementById("leg1");
				assert.strictEqual(expected, label);
			},
			testGetFirstLabelContentOnly: function() {
				var element = document.getElementById("male"),
					label = getFirstLabelForElement(element, true),
					expected = "Male";
				assert.strictEqual(expected, label);
			}
		});
	});
