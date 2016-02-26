define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var TEST_MODULE = "wc/dom/getStyle",
			controller, testHolder,
			urlResource = "@RESOURCES@/domGetStyle.html";


		function helpCompareResults(expectedResult, result) {
			assert.strictEqual(expectedResult.r, result.r);
			assert.strictEqual(expectedResult.g, result.g);
			assert.strictEqual(expectedResult.b, result.b);
		}

		registerSuite({
			name: "domGetStyle",
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
			testGetUnsetStyleRed: function() {
				var result,
					expectedResult = {r: 255, g: 255, b: 255},
					element;
				element = document.getElementById("noStyle");
				result = controller(element, "background-color");
				assert.strictEqual(expectedResult.r, result.r);
			},
			testGetUnsetStyleGreen: function() {
				var result,
					expectedResult = {r: 255, g: 255, b: 255},
					element;

				element = document.getElementById("noStyle");
				result = controller(element, "background-color");

				assert.strictEqual(expectedResult.g, result.g);
			},
			testGetUnsetStyleBlue: function() {
				var result,
					expectedResult = {r: 255, g: 255, b: 255},
					element;

				element = document.getElementById("noStyle");
				result = controller(element, "background-color");

				assert.strictEqual(expectedResult.b, result.b);
			},
			testGetSetStyleRed: function() {
				var result,
					expectedResult = {r: 255, g: 0, b: 0},
					element;

				element = document.getElementById("InlineStyledContainer");
				result = controller(element, "background-color");

				assert.strictEqual(expectedResult.r, result.r);
			},
			testGetSetStyleGreen: function() {
				var result,
					expectedResult = {r: 255, g: 0, b: 0},
					element;

				element = document.getElementById("InlineStyledContainer");
				result = controller(element, "background-color");

				assert.strictEqual(expectedResult.g, result.g);
			},
			testGetSetStyleBlue: function() {
				var result,
					expectedResult = {r: 255, g: 0, b: 0},
					element;

				element = document.getElementById("InlineStyledContainer");
				result = controller(element, "background-color");

				assert.strictEqual(expectedResult.b, result.b);
			},
			testGetStyleTxtboxWhite: function() {
				var result,
					element;

				element = document.getElementById("txt1");
				result = controller(element, "background-color");
				helpCompareResults({r: 255, g: 255, b: 255}, result);
			},
			testGetStyleTxtboxBlack: function() {
				var result,
					element;
				element = document.getElementById("txt2");
				result = controller(element, "background-color");
				helpCompareResults({r: 0, g: 0, b: 0}, result);
			},
			testGetStyleTxtboxColor: function() {
				var result,
					element;
				element = document.getElementById("txt3");
				result = controller(element, "background-color");
				helpCompareResults({r: 221, g: 221, b: 221}, result);
			}
		});
	});
