define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var TEST_MODULE = "wc/dom/getLabelsForElement", controller, testHolder,
			urlResource = "@RESOURCES@/domGetLabelsForElement.html";

		registerSuite({
			name: "wc/dom/getLabelsForElement",
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
			testGetLabelWrapped: function() {
				var element = document.getElementById("wrappedinput"),
					labels = controller(element),
					expected = document.getElementById("wrappedlabel");
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
					expected = 3;

				assert.strictEqual(expected, labels.length);
			},
			testGetReadOnly: function() {
				var element = document.getElementById("rofield"),
					labels = controller(element, true),
					expected = document.getElementById("rolabel");

				assert.strictEqual(expected, labels[0]);
			},
			testGetWrappedReadOnly: function() {
				var element = document.getElementById("wrappedroinput"),
					labels = controller(element, true),
					expected = document.getElementById("wrappedrolabel");

				assert.strictEqual(expected, labels[0]);
			},
			testUnlabelled: function() {
				var result = controller(document.getElementById("unlabelled"));
				assert.isTrue(Array.isArray(result));
				assert.strictEqual(0, result.length);
			},
			testAriaLabel: function() {
				var element = document.getElementById("aria-text"),
					labels = controller(element),
					expected = document.getElementById("aria-label");
				assert.strictEqual(expected, labels[0]);
			},
			testAriaLabelUnavailable: function() {
				var element = document.getElementById("aria-text2"),
					labels = controller(element);
				assert.strictEqual(0, labels.length);
			},
			testAriaMultipleLabel: function() {
				var element = document.getElementById("aria-text3"),
					labels = controller(element),
					expected1 = document.getElementById("aria-label3"),
					expected2 = document.getElementById("aria-label4");
				assert.strictEqual(2, labels.length);
				assert.strictEqual(expected1, labels[0]);
				assert.strictEqual(expected2, labels[1]);
			},
			testAriaMultipleLabel2: function() {
				var element = document.getElementById("aria-input"),
					labels = controller(element),
					expected1 = document.getElementById("billing"),
					expected2 = document.getElementById("name");
				assert.strictEqual(2, labels.length);
				assert.strictEqual(expected1, labels[0]);
				assert.strictEqual(expected2, labels[1]);
			},
			testDivElementAriaLabel: function() {
				var element = document.getElementById("main"),
					labels = controller(element),
					expected = document.getElementById("foo");
				assert.strictEqual(expected, labels[0]);
			},
			testNullElement: function() {
				var labels = controller(null);
				assert.isNotOk(labels);
			}
		});
	});
