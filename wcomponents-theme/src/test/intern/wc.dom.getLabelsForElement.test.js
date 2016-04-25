define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var TEST_MODULE = "wc/dom/getLabelsForElement", controller, testHolder,
			urlResource = "@RESOURCES@/domGetLabelsForElement.html";

		registerSuite({
			name: "domGetLabelsForElement",
			setup: function() {
				var result = testutils.setupHelper([TEST_MODULE]).then(function(arr) {
					controller = arr[0];
					testHolder = testutils.getTestHolder();
					return testutils.setUpExternalHTML(urlResource, testHolder);
				});
				return result;
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGetLabel: function() {
				var element = document.getElementById("male"),
					labels = controller(element),
					expected = document.getElementById("maleLabel");

				assert.strictEqual(expected, labels[0]);
			},
			testGetLabelNested: function() {
				var element = document.getElementById("female"),
					labels = controller(element),
					expected = document.getElementById("femaleLabel");

				assert.strictEqual(expected, labels[0]);
			},
			testGetLabelForFieldset: function() {
				var element = document.getElementById("fs1"),
					labels = controller(element),
					expected = document.getElementById("leg1");

				assert.strictEqual(expected, labels[0]);
			},
			testGetLabelCountMoreThanOne: function() {
				var element = document.getElementById("male"),
					labels = controller(element),
					expected = 2;

				assert.strictEqual(expected, labels.length);
			}
		});
	});
