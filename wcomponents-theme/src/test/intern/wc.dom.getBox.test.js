define(["intern!object", "intern/chai!assert", "./resources/test.utils"], function(registerSuite, assert, testutils) {
	"use strict";
	var controller, testHolder, TOP = 150,
		LEFT = 100,
		TEST_ID = "testgetBoxElement";


	function doSimpleTest(expected, dimension, noReset) {
		var element = document.getElementById(TEST_ID);
		if (!noReset) {
			document.body.scrollTop = 0;  // browsers
			document.documentElement.scrollTop = 0;  // IE
			document.body.scrollLeft = 0;  // browsers
			document.documentElement.scrollLeft = 0;  // IE
		}
		assert.strictEqual(controller(element)[dimension], expected);
	}

	registerSuite({
		name: "getBox",
		setup: function() {
			return testutils.setupHelper(["wc/dom/getBox"], function(obj) {
				controller = obj;
				testHolder = testutils.getTestHolder();
			});
		},
		beforeEach: function() {
			testHolder.innerHTML = '<div id="' + TEST_ID + '" style="position:absolute;left:' + LEFT + 'px;top:' + TOP + 'px;">absolute position</div>';
			// reset scroll if the test reporting is longer than the viewport height it will cause the simple tests to fail
		},
		afterEach: function() {
			testHolder.innerHTML = "";
		},
		testGetBoxLeft: function() {
			doSimpleTest(LEFT, "left");
		},
		testGetBoxTop: function() {
			doSimpleTest(TOP, "top");
		},
		testGetBoxTopWithMarginTop: function() {
			var MARGIN = 60,
				element = document.getElementById(TEST_ID);
			element.style.marginTop = MARGIN + "px";
			doSimpleTest(TOP + MARGIN, "top");
		},
		testGetBoxTopWithMarginLeft: function() {
			var MARGIN = 60,
				element = document.getElementById(TEST_ID);
			element.style.marginLeft = MARGIN + "px";
			doSimpleTest(LEFT + MARGIN, "left");
		},
		testGetBoxTopWithVerticalScroll: function() {
			var SCROLL = 60;
			testHolder.insertAdjacentHTML("beforeEnd", "<div style='height:10000px;'>spacer</div>");

			document.body.scrollTop = SCROLL;  // broswers
			document.documentElement.scrollTop = SCROLL;  // IE
			doSimpleTest(TOP - SCROLL, "top", true);
		},
		testGetBoxTopWithHorizontalScroll: function() {
			var SCROLL = 60;
			testHolder.insertAdjacentHTML("beforeEnd", "<div style='height:20px;width:10000px;'>spacer</div>");

			document.body.scrollLeft = SCROLL;  // broswers
			document.documentElement.scrollLeft = SCROLL;  // IE
			doSimpleTest(LEFT - SCROLL, "left", true);
		}
	});
});
