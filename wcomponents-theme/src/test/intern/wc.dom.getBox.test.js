define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";
	var controller, testHolder, TOP = 150,
		LEFT = 100,
		SCROLL = 60, // simpler to keep it less than LEFT
		MARGIN = 60, // doesn't really matter how much
		TEST_ID = "testgetBoxElement";


	function doSimpleTest(expected, dimension, scroll) {
		var element = document.getElementById(TEST_ID);

		document.body.scrollTop = 0;  // browsers
		document.documentElement.scrollTop = 0;  // IE
		document.body.scrollLeft = 0;  // browsers
		document.documentElement.scrollLeft = 0;  // IE
		if (scroll) {
			document.body[scroll] = SCROLL;  // browsers
			document.documentElement[scroll] = SCROLL;  // IE
		}
		assert.strictEqual(controller(element)[dimension], expected);
	}

	registerSuite({
		name: "wc/dom/getBox",
		setup: function() {
			return testutils.setupHelper(["wc/dom/getBox"], function(obj) {
				controller = obj;
				testHolder = testutils.getTestHolder();
			});
		},
		beforeEach: function() {
			testHolder.innerHTML = "<div id=\"" + TEST_ID + "\" style=\"position:absolute;left:" + LEFT + "px;top:" + TOP + "px;\">absolute position</div>";
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
			var element = document.getElementById(TEST_ID);
			element.style.marginTop = MARGIN + "px";
			doSimpleTest(TOP + MARGIN, "top");
		},
		testGetBoxTopWithMarginLeft: function() {
			var element = document.getElementById(TEST_ID);
			element.style.marginLeft = MARGIN + "px";
			doSimpleTest(LEFT + MARGIN, "left");
		},
		testGetBoxTopWithVerticalScroll: function() {
			testHolder.insertAdjacentHTML("beforeEnd", "<div style='height:10000px;'>spacer</div>");
			doSimpleTest(TOP - SCROLL, "top", "scrollTop");
		},
		testGetBoxLeftWithHorizontalScroll: function() {
			testHolder.insertAdjacentHTML("beforeEnd", "<div style='height:20px;width:10000px;'>spacer</div>");
			doSimpleTest(LEFT - SCROLL, "left", "scrollLeft");
		},
		testGetBox_width: function() {
			var element = document.getElementById(TEST_ID),
				box;
			element.style.width = "100.5px";
			element.style.height = "20.4px";
			box = controller(element);
			// actual width depends on browser rounding
			assert.isTrue(100.4 < box.width && 100.5 >= box.width);
		},
		testGetBox_height: function() {
			var element = document.getElementById(TEST_ID),
				box;
			element.style.width = "100.5px";
			element.style.height = "20.4px";
			box = controller(element);
			// actual height depends on browser rounding
			assert.isTrue(20.3 < box.height && 20.4 >= box.height);
		},
		testGetBox_width_withRounding: function() {
			var element = document.getElementById(TEST_ID),
				box;
			element.style.width = "100.5px";
			element.style.height = "20.4px";
			box = controller(element, true);
			assert.strictEqual(box.width, 101);
		},
		testGetBox_height_withRounding: function() {
			var element = document.getElementById(TEST_ID),
				box;
			element.style.width = "100.5px";
			element.style.height = "20.4px";
			box = controller(element, true);
			assert.strictEqual(box.height, 20);
		}
	});
});
