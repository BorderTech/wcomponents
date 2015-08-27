define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var getLabelsForElement, testHolder,
			urlResource = "../../target/test-classes/wcomponents_theme/intern/resources/domGetLabelsForElement.html";

		registerSuite({
			name: "domGetLabelsForElement",
			setup: function() {
				return testutils.setupHelper(["wc/dom/getLabelsForElement"], function(obj) {
					getLabelsForElement = obj;
					testHolder = testutils.getTestHolder();
					testutils.setUpExternalHTML(urlResource, testHolder);
				});
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testGetLabel: function() {
				var element = document.getElementById('male'),
					labels = getLabelsForElement(element),
					expected = document.getElementById('maleLabel');

				assert.strictEqual(expected, labels[0]);
			},
			testGetLabelNested: function() {
				var element = document.getElementById('female'),
					labels = getLabelsForElement(element),
					expected = document.getElementById('femaleLabel');

				assert.strictEqual(expected, labels[0]);
			},
			testGetLabelForFieldset: function() {
				var element = document.getElementById('fs1'),
					labels = getLabelsForElement(element),
					expected = document.getElementById('leg1');

				assert.strictEqual(expected, labels[0]);
			},
			testGetLabelCountMoreThanOne: function() {
				var element = document.getElementById('male'),
					labels = getLabelsForElement(element),
					expected = 2;

				assert.strictEqual(expected, labels.length);
			}
		});
	});
