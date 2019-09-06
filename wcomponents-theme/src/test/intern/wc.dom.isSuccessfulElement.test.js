define(["intern!object", "intern/chai!assert", "wc/dom/isSuccessfulElement", "intern/resources/test.utils!"],
	function(registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			FORM_START = "<form method='get' action='#' id='form1'>",
			FORM_END = "</form>",
			INPUT = FORM_START +
				"<input id='input1' name='i1' type='text'>" +
				FORM_END,
			INPUT_BUTTON = FORM_START +
				"<input id='input1' name='i1' type='button'>" +
				FORM_END,
			INPUT_SUBMIT = FORM_START +
				"<input id='input1' name='i1' type='submit'>" +
				FORM_END,
			INPUT_IMAGE = FORM_START +
				"<input id='input1' name='i1' type='image'>" +
				FORM_END,
			INPUT_RESET = FORM_START +
				"<input id='input1' name='i1' type='reset'>" +
				FORM_END,
			SELECT = FORM_START +
				"<select id='sel1' name='s1'><option value='0'>zero</option><option value='1'>one</option></select>" +
				FORM_END,
			BUTTON = FORM_START +
				"<button type='button' id='button1' name='b1' value='x'>button</button>" +
				FORM_END,
			BUTTON_SUBMIT = FORM_START +
				"<button type='submit' id='button1' name='b1' value='x'>button</button>" +
				FORM_END,
			HTML = FORM_START +
				"<input id='success0' name='i1' type='text' value='x'>\
		<input id='success1' name='i2' type='text'>\
		<input id='not0' name='i3' type='text' value='x' disabled>\
		<input id='success2' name='r1' type='radio' value='false' checked>\
		<input id='not1' name='r1' type='radio' value='true'>\
		<input id='success3' name='cb1' type='checkbox' value='x' checked>\
		<input id='not2' name='cb2' type='checkbox' value='y'>\n\
		<input id='not3' name='cb3' type='checkbox' value='y' checked disabled>\
		<select id='success4' name='sel1'><option value='0'>zero</option><option value='1' selected>one</option></select>\
		<select id='success5' name='sel2'><option value='0'>zero</option><option value='1'>one</option></select>\
		<select id='not4' name='sel3' multiple><option value='0'>zero</option><option value='1'>one</option></select>\
		<button type='button' name='b1' value='x'>button</button>" +
				FORM_END;

		registerSuite({
			name: "wc/dom/isSuccessfulElement",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				if (!testHolder) {
					assert.fail(true, undefined, "did not create testHolder.");
				}
				testHolder.innerHTML = "";
			},
			teardown: function() {
				testHolder.innerHTML = "";
			},
			testInputNoValue: function() {
				var element;
				testHolder.innerHTML = INPUT;
				element = document.getElementById("input1");
				assert.isTrue(controller(element));
			},
			testIsSuccessfulElementInput: function() {
				var element;
				testHolder.innerHTML = INPUT;
				element = document.getElementById("input1");
				element.value = "x";
				assert.isTrue(controller(element));
			},
			testDisabled: function() {
				var element;
				testHolder.innerHTML = INPUT;
				element = document.getElementById("input1");
				element.value = "x";
				element.disabled = true;
				assert.isFalse(controller(element));
			},
			testNoName: function() {
				var element;
				testHolder.innerHTML = INPUT;
				element = document.getElementById("input1");
				element.value = "x";
				element.name = "";
				assert.isFalse(controller(element));
			},
			testWithSingleSelect: function() {
				var element;
				testHolder.innerHTML = SELECT;
				element = document.getElementById("sel1");
				assert.isTrue(controller(element));
			},
			testWithSingleSelectSelected: function() {
				var element;
				testHolder.innerHTML = SELECT;
				element = document.getElementById("sel1");
				element.selectedIndex = 0;
				assert.isTrue(controller(element));
			},
			testWithSingleSelectNegSelectIndex: function() {
				var element;
				testHolder.innerHTML = SELECT;
				element = document.getElementById("sel1");
				element.selectedIndex = -1;
				assert.isFalse(controller(element));
			},
			testWithMultiSelect: function() {
				var i, element;
				testHolder.innerHTML = SELECT;
				element = document.getElementById("sel1");
				element.multiple = true;
				for (i = 0; i < element.options.length; ++i) {
					element.options[i].selected = false;
				}
				assert.isFalse(controller(element));
			},
			testWithMultiSelectWithSelection: function() {
				var element;
				testHolder.innerHTML = SELECT;
				element = document.getElementById("sel1");
				element.multiple = true;
				element.options[0].selected = true;
				assert.isTrue(controller(element));
			},
			testWithButtonNotAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = BUTTON;
				element = document.getElementById("button1");
				assert.isFalse(controller(element));
			},
			testWithButtonAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = BUTTON;
				element = document.getElementById("button1");
				assert.isTrue(controller(element, true));
			},
			testWithButtonSubmitNotAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = BUTTON_SUBMIT;
				element = document.getElementById("button1");
				assert.isFalse(controller(element));
			},
			testWithButtonSubmitAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = BUTTON_SUBMIT;
				element = document.getElementById("button1");
				assert.isFalse(controller(element, true)); // submit never succeeds.
			},
			testInputButtonNotSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_BUTTON;
				element = document.getElementById("input1");
				assert.isFalse(controller(element));
			},
			testInputButtonAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_BUTTON;
				element = document.getElementById("input1");
				assert.isTrue(controller(element, true));
			},
			testInputSubmitNotSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_SUBMIT;
				element = document.getElementById("input1");
				assert.isFalse(controller(element));
			},
			testInputSubmitAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_SUBMIT;
				element = document.getElementById("input1");
				assert.isFalse(controller(element, true));
			},
			testInputResetButtonsNotSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_RESET;
				element = document.getElementById("input1");
				assert.isFalse(controller(element));
			},
			testInputResetButtonsAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_RESET;
				element = document.getElementById("input1");
				assert.isFalse(controller(element, true));
			},
			testInputImageButtonsNotSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_IMAGE;
				element = document.getElementById("input1");
				assert.isFalse(controller(element));
			},
			testInputImageButtonsAlwaysSuccessful: function() {
				var element;
				testHolder.innerHTML = INPUT_IMAGE;
				element = document.getElementById("input1");
				assert.isFalse(controller(element, true));
			},
			// tests of get all
			testGetAllSimple: function() {
				var start;
				testHolder.innerHTML = BUTTON;
				start = document.getElementById("form1");
				assert.isTrue(Array.isArray(controller.getAll(start)));
			},
			testGetAllSimpleEmpty: function() {
				var expected = 0,
					start;
				testHolder.innerHTML = BUTTON;
				start = document.getElementById("form1");
				assert.strictEqual(expected, controller.getAll(start).length);
			},
			testGetAllSimpleWithButtons: function() {
				var expected = 1,
					start;
				testHolder.innerHTML = BUTTON;
				start = document.getElementById("form1");
				assert.strictEqual(expected, controller.getAll(start, true).length);
			},
			testGetAllNoButtons: function() {
				var expected = 6,
					start;
				testHolder.innerHTML = HTML;
				start = document.getElementById("form1");
				assert.strictEqual(expected, controller.getAll(start).length);
			},
			testGetAllWithButtons: function() {
				var expected = 7,
					start;
				testHolder.innerHTML = HTML;
				start = document.getElementById("form1");
				assert.strictEqual(expected, controller.getAll(start, true).length);
			}
		});
	});