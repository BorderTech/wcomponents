define(["intern!object", "intern/chai!assert", "wc/dom/wrappedInput", "./resources/test.utils!"],
	function (registerSuite, assert, controller, testutils) {
		"use strict";
		/*
		 * Unit tests for wc/dom/wrappedInput
		 */
		var testHolder,
			testContent = "<div id='wrappedinputtestcontent'>\n\
				<span class='wc-input-wrapper' id='wrapper'><input id='wrappedinput' type='text'></span>\n\
				<span class='wc-ro-input' id='rowrapper'><span id='wrappedro'>value</span></span>\n\
				<input id='notwrapped' type='text'>\n\
				<span id='notwrapper'><input id='notwrappedinput' type='text'></span>\n\
				</div>";

		registerSuite({
			name: "wc/dom/wrappedInput",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testGetWidgetsisArray: function() {
				assert.isTrue(Array.isArray(controller.getWidgets()));
			},
			testIsOneOfMeWithWrapper: function() {
				assert.isTrue(controller.isOneOfMe(document.getElementById("wrapper")));
			},
			testIsNotOneOfMeWithROWrapper: function() {
				assert.isFalse(controller.isOneOfMe(document.getElementById("rowrapper")));
			},
			testIsOneOfMeWithROWrapper: function() {
				assert.isTrue(controller.isOneOfMe(document.getElementById("rowrapper"), true));
			},
			testIsNotOneOfMeWithROWrapperNotWrapper: function() {
				assert.isFalse(controller.isOneOfMe(document.getElementById("notwrapper")));
			},
			testIsReadOnlyTrue: function () {
				assert.isTrue(controller.isReadOnly(document.getElementById("rowrapper")));
			},
			testIsReadOnlyFalse: function () {
				assert.isFalse(controller.isReadOnly(document.getElementById("wrapper")));
			},
			testIsReadOnlyFalseNotAWrapper: function () {
				assert.isFalse(controller.isReadOnly(document.getElementById("notwrapper")));
			},
			testGetWidget: function () {
				var widget = controller.getWidget();
				assert.isNotNull(widget);
				assert.strictEqual(widget.className, "wc-input-wrapper");
			},
			testGetROWidget: function () {
				var widget = controller.getROWidget();
				assert.isNotNull(widget);
				assert.strictEqual(widget.className, "wc-ro-input");
			},
			testGetWidgetsOrder: function () {
				var widgets = controller.getWidgets();
				assert.equal(widgets[0], controller.getWidget());
				assert.equal(widgets[1], controller.getROWidget());
			},
			testGetInput: function() {
				var expected = document.getElementById("wrappedinput"),
					actual = controller.getInput(document.getElementById("wrapper"));
				assert.equal(actual, expected);
			},
			testGetInputRO: function() {
				assert.isNull(controller.getInput(document.getElementById("rowrapper")));
			},
			testGetInputNotWrapper: function() {
				assert.isNull(controller.getInput(document.getElementById("notwrapper")));
			},
			testGetWrapper: function () {
				var expected = document.getElementById("wrapper"),
					actual = controller.getWrapper(document.getElementById("wrappedinput"));
				assert.equal(actual, expected);
			},
			testGetWrapperRO: function () {
				assert.isNull(controller.getWrapper(document.getElementById("wrappedro")));
			},
			testGetWrapperNotWrapped: function () {
				assert.isNull(controller.getWrapper(document.getElementById("notwrappedinput")));
			},
			testGet: function () {
				assert.strictEqual(controller.get(document.getElementById("wrappedinputtestcontent")).length, 1);
			},
			testGetWithWrapper: function () {
				assert.strictEqual(controller.get(document.getElementById("wrapper")).length, 1);
			},
			testGetWithRO: function () {
				assert.strictEqual(controller.get(document.getElementById("wrappedinputtestcontent"), true).length, 2);
			},
			testGetWithROWrapperNotRO: function () {
				assert.strictEqual(controller.get(document.getElementById("rowrapper")).length, 0);
			},
			testGetWithROWrapper: function () {
				assert.strictEqual(controller.get(document.getElementById("rowrapper"), true).length, 1);
			},
			testGetNothing: function () {
				assert.strictEqual(controller.get(document.getElementById("notwrapper")).length, 0);
				assert.strictEqual(controller.get(document.getElementById("notwrapper"), true).length, 0);
			}
		});
	}
);

