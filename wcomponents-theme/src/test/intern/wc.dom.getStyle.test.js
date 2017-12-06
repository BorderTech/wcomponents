define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var TEST_MODULE = "wc/dom/getStyle",
			controller, testHolder,
			testContent,
			testWidthNoUnits = "200",
			testWidthWithUnits = testWidthNoUnits + "px",
			testDisplay = "block",
			urlResource = "@RESOURCES@/domGetStyle.html";


		function helpCompareResults(expectedResult, result) {
			assert.strictEqual(expectedResult.r, result.r);
			assert.strictEqual(expectedResult.g, result.g);
			assert.strictEqual(expectedResult.b, result.b);
		}

		function getElementNotColour() {
			var testId = "testGetStyle-innerelement",
				HTML = "<span id='" + testId + "' style='display:" + testDisplay + ";width:" + testWidthWithUnits+ "'>content</span>";
			testHolder.insertAdjacentHTML("beforeend", HTML);
			return document.getElementById(testId);
		}

		registerSuite({
			name: "domGetStyle",
			setup: function() {
				var result = testutils.setupHelper([TEST_MODULE]).then(function(arr) {
					controller = arr[0];
					testHolder = testutils.getTestHolder();
					return testutils.setUpExternalHTML(urlResource, testHolder).then(function(response) {
						testContent = response;
						return Promise.resolve();
					});
				});
				return result;
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
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
			},
			testGetStyle_JSForm: function() {
				var result,
					element;
				element = document.getElementById("txt3");
				result = controller(element, "backgroundColor");
				helpCompareResults({r: 221, g: 221, b: 221}, result);
			},
			testGetStyle_withUnits: function() {
				var element = getElementNotColour(),
					result = controller(element, "width", true);
				assert.strictEqual(result, testWidthWithUnits);
			},
			testGetStyle_withoutUnits: function() {
				var element = getElementNotColour(),
					result = controller(element, "width");
				assert.strictEqual(result, testWidthNoUnits);
			},
			testGetStyle_NumberNotAColour: function() {
				var element = getElementNotColour(),
					result;
				element.style.opacity = "0.9";
				result = controller(element, "opacity", false, true);
				assert.strictEqual(Math.round(parseFloat(result) * 100), 90);
			}
		});
	});
