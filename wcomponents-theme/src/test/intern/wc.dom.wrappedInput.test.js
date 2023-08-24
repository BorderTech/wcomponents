define(["intern!object", "intern/chai!assert", "wc/dom/wrappedInput", "intern/resources/test.utils!"],
	function (registerSuite, assert, wrappedInput, testutils) {
		"use strict";
		/*
		 * Unit tests for wc/dom/wrappedInput
		 * NOTE for IDs there is a convention for wrapped input IDs which is based on the XML for the Input components
		 * which are wrapped. The wrapper has ID "foo" and the input has ID "foo_input". The example below uses this convention.
		 */
		let testHolder;
		const testContent = `<div id='wrappedinputtestcontent'>
				<span class='wc-input-wrapper' id='wrapper'><input id='wrapper_input' type='text'></span>
				<span class='wc-ro-input' id='rowrapper'><span id='rowrapper_input'>value</span></span>
				<input id='notwrapped_input' type='text'>
				<span id='notwrapper'><input id='notwrapper_input' type='text'></span>
				</div>`;

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
			"testGetWidgetsisArray": function() {
				if (!wrappedInput.getWidgets) {
					/*
						This api appeared to be unused, so I removed it.
						Leaving the tests in for now in case I was wrong
						and it gets put back in.
					 */
					this.skip("API removed");
				}
				assert.isTrue(Array.isArray(wrappedInput.getWidgets()));
			},
			testGetWidget: function () {
				if (!wrappedInput.getWidget) {
					/*
						This api appeared to be unused, so I removed it.
						Leaving the tests in for now in case I was wrong
						and it gets put back in.
					 */
					this.skip("API removed");
				}
				const widget = wrappedInput.getWidget();
				assert.isNotNull(widget);
				assert.strictEqual(widget.className, "wc-input-wrapper");
			},
			"testGetROWidget": function () {
				if (!wrappedInput.getROWidget) {
					/*
						This api appeared to be unused, so I removed it.
						Leaving the tests in for now in case I was wrong
						and it gets put back in.
					 */
					this.skip("API removed");
				}
				const widget = wrappedInput.getROWidget();
				assert.isNotNull(widget);
				assert.strictEqual(widget.className, "wc-ro-input");
			},
			"testGetWidgetsOrder": function () {
				if (!wrappedInput.getWidgets) {
					/*
						This api appeared to be unused, so I removed it.
						Leaving the tests in for now in case I was wrong
						and it gets put back in.
					 */
					this.skip("API removed");
				}
				const widgets = wrappedInput.getWidgets();
				assert.equal(widgets[0], wrappedInput.getWidget());
				assert.equal(widgets[1], wrappedInput.getROWidget());
			},
			testIsOneOfMeWithWrapper: function() {
				assert.isTrue(wrappedInput.isOneOfMe(document.getElementById("wrapper")));
			},
			testIsNotOneOfMeWithROWrapper: function() {
				assert.isFalse(wrappedInput.isOneOfMe(document.getElementById("rowrapper")));
			},
			testIsOneOfMeWithROWrapper: function() {
				assert.isTrue(wrappedInput.isOneOfMe(document.getElementById("rowrapper"), true));
			},
			testIsNotOneOfMeWithROWrapperNotWrapper: function() {
				assert.isFalse(wrappedInput.isOneOfMe(document.getElementById("notwrapper")));
			},
			testIsReadOnlyTrue: function () {
				assert.isTrue(wrappedInput.isReadOnly(document.getElementById("rowrapper")));
			},
			testIsReadOnlyFalse: function () {
				assert.isFalse(wrappedInput.isReadOnly(document.getElementById("wrapper")));
			},
			testIsReadOnlyFalseNotAWrapper: function () {
				assert.isFalse(wrappedInput.isReadOnly(document.getElementById("notwrapper")));
			},
			testGetInput: function() {
				var expected = document.getElementById("wrapper_input"),
					actual = wrappedInput.getInput(document.getElementById("wrapper"));
				assert.equal(actual, expected);
			},
			testGetInputRO: function() {
				assert.isNull(wrappedInput.getInput(document.getElementById("rowrapper")));
			},
			testGetInputNotWrapper: function() {
				assert.isNull(wrappedInput.getInput(document.getElementById("notwrapper")));
			},
			testGetWrapper: function () {
				var expected = document.getElementById("wrapper"),
					actual = wrappedInput.getWrapper(document.getElementById("wrapper_input"));
				assert.equal(actual, expected);
			},
			testGetWrapperRO: function () {
				assert.isNull(wrappedInput.getWrapper(document.getElementById("rowrapper_input")));
			},
			testGetWrapperNotWrapped: function () {
				assert.isNull(wrappedInput.getWrapper(document.getElementById("notwrapper_input")));
			},
			testGet: function () {
				assert.strictEqual(wrappedInput.get(document.getElementById("wrappedinputtestcontent")).length, 1);
			},
			testGetWithWrapper: function () {
				assert.strictEqual(wrappedInput.get(document.getElementById("wrapper")).length, 1);
			},
			testGetWithRO: function () {
				assert.strictEqual(wrappedInput.get(document.getElementById("wrappedinputtestcontent"), true).length, 2);
			},
			testGetWithROWrapperNotRO: function () {
				assert.strictEqual(wrappedInput.get(document.getElementById("rowrapper")).length, 0);
			},
			testGetWithROWrapper: function () {
				assert.strictEqual(wrappedInput.get(document.getElementById("rowrapper"), true).length, 1);
			},
			testGetNothing: function () {
				assert.strictEqual(wrappedInput.get(document.getElementById("notwrapper")).length, 0);
				assert.strictEqual(wrappedInput.get(document.getElementById("notwrapper"), true).length, 0);
			},
			testGetWrappedId: function() {
				var expected = "wrapper_input",
					actual = wrappedInput.getWrappedId(document.getElementById("wrapper"));
				assert.equal(actual, expected);
			},
			testGetWrappedIdFromInput: function() {
				var expected = "wrapper_input",
					actual = wrappedInput.getWrappedId(document.getElementById("wrapper_input"));
				assert.equal(actual, expected);
			},
			testGetWrappedIdRO: function() {
				var expected = "rowrapper_input",
					actual = wrappedInput.getWrappedId(document.getElementById("rowrapper"));
				assert.equal(actual, expected);
			},
			testGetWrappedIdNotWrapper: function() {
				assert.isNull(wrappedInput.getWrappedId(document.getElementById("notwrapper_input")));
			}
		});
	}
);
