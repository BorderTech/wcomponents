define(["intern!object", "intern/chai!assert", "wc/ui/errors", "./resources/test.utils!"],
	function (registerSuite, assert, controller, testutils) {
		"use strict";

		var testHolder,
			SIMPLE_ERROR_TEXT = "Simple input must be completed",
			WRAPPED_ERROR_TEXT = "Wrapped input must be completed",
			COMPLEX_ERROR_TEXT = "Complex input must have a selection",
			CUSTOM_ERROR = "hello, I am a custom error message W00t!!",
			SIMPLE_ID = "simple",
			WRAPPED_ID = "wrapped",
			WRAPPED_INPUT_ID = "wrapped-input",
			COMPLEX_ID = "complex",
			CUSTOM_SIMPLE_ID = "custom",
			CUSTOM_OTHER_ID = "custom-wrapper",
			testContent = "<section class='wc-validationerrors'>\n\
<div class='wc-error'><a href='#" + SIMPLE_ID + "'>" + SIMPLE_ERROR_TEXT + "</a></div>\n\
<div class='wc-error'><a href='#" + WRAPPED_ID + "'>" + WRAPPED_ERROR_TEXT + "</a></div>\n\
<div class='wc-error'><a href='#" + COMPLEX_ID + "'>" + COMPLEX_ERROR_TEXT + "</a></div>\n\
</section>\n\
<label for='" + SIMPLE_ID + "' id='simple-label'>required simple input missing</label><input id='" + SIMPLE_ID + "' type='text' value='' required>\n\
<label for='" + WRAPPED_INPUT_ID + "' id='wrapped-label'>required wrapped input missing</label><span id='" + WRAPPED_ID +
			"' class='wc-input-wrapper'><input id='" + WRAPPED_INPUT_ID + "' type='text' value='' required>\n\
<fieldset id='" + COMPLEX_ID + "' class='wc-checkboxselect'><legend>mandatory select missing</legend><ul>\n\
<li><input type='checkbox' name='complex' value='0' id='complex-0'><label for='complex-0' id=complex-0-l'>zero</label></li>\n\
<li><input type='checkbox' name='complex' value='1' id='complex-1'><label for='complex-1' id=complex-1-l'>one</label></li>\n\
</ul></fieldset>\n\
<label for='" + CUSTOM_SIMPLE_ID + "'>no auto error</label><input id='" + CUSTOM_SIMPLE_ID + "' type='text'>\n\
<span id='" + CUSTOM_OTHER_ID + "'> <ins>start</ins> <em>middle</em> <strong>end</strong> </span>";

		function doErrorTest(id, expected, where) {
			var element = document.getElementById(id),
				errorContainer,
				errorText,
				_where = where || "afterEnd";

			assert.isOk(element, "Could not find element to test id: " + id);
			if (_where === "afterEnd") {
				errorContainer = element.nextSibling;
				while (errorContainer && errorContainer.nodeType === Node.TEXT_NODE) {
					errorContainer = errorContainer.nextSibling;
				}
			}			else {
				errorContainer = element.lastChild;
				while (errorContainer && errorContainer.nodeType === Node.TEXT_NODE) {
					errorContainer = errorContainer.previousSibling;
				}
			}
			assert.isOk(errorContainer, "Could not find error container for id " + id);
			errorText = errorContainer.textContent || errorContainer.innerText;
			assert.strictEqual(errorText, expected, "Unexpected error text");
		}

		function doInvalidTest(id) {
			var element = document.getElementById(id);
			assert.isOk(element, "Could not find element to test id: " + id);
			assert.strictEqual(element.getAttribute("aria-invalid"), "true", "Did not find expected value for `aria-invalid` for id " + id);
		}

		registerSuite({
			name: "wc/ui/errors",
			setup: function() {
				testHolder = testutils.getTestHolder();
			},
			beforeEach: function() {
				testHolder.innerHTML = testContent;
				controller._writeErrors(testHolder);
			},
			afterEach: function() {
				testHolder.innerHTML = "";
			},
			testAutoErrors: function() {
				doErrorTest(SIMPLE_ID, SIMPLE_ERROR_TEXT);
				doErrorTest(WRAPPED_ID, WRAPPED_ERROR_TEXT);
				doErrorTest(COMPLEX_ID, COMPLEX_ERROR_TEXT, "beforeEnd");
			},
			testAutoInvalid: function() {
				doInvalidTest(SIMPLE_ID);
				doInvalidTest(WRAPPED_INPUT_ID);
				doInvalidTest(COMPLEX_ID);
			},
			testFlagErrorInput: function() {
				var element = document.getElementById(CUSTOM_SIMPLE_ID),
					props = {
						"element": element,
						"message": CUSTOM_ERROR
					};
				assert.isNotOk(element.getAttribute("aria-invalid"));
				assert.notInclude(testHolder.innerHTML, CUSTOM_ERROR, "hmmm, already written the custom error");
				controller.flagError(props);
				doErrorTest(CUSTOM_SIMPLE_ID, CUSTOM_ERROR);
			},
			testFlagErrorContainer: function() {
				var element = document.getElementById(CUSTOM_OTHER_ID),
					props = {
						"element": element,
						"message": CUSTOM_ERROR
					};
				assert.isNotOk(element.getAttribute("aria-invalid"));
				assert.notInclude(testHolder.innerHTML, CUSTOM_ERROR, "hmmm, already written the custom error");
				controller.flagError(props);
				doErrorTest(CUSTOM_OTHER_ID, CUSTOM_ERROR, "beforeEnd");
			},
			testFlagErrorContainerWithPosition: function() {
				var element = document.getElementById(CUSTOM_OTHER_ID),
					props = {
						"element": element,
						"message": CUSTOM_ERROR,
						"position": "afterEnd"
					};
				assert.isNotOk(element.getAttribute("aria-invalid"));
				assert.notInclude(testHolder.innerHTML, CUSTOM_ERROR, "hmmm, already written the custom error");
				controller.flagError(props);
				doErrorTest(CUSTOM_OTHER_ID, CUSTOM_ERROR);
			},
			testFlagWithExistingError: function() {
				var id = "test-id",
					errorContainerId = id + "_err",
					INITIAL_ERROR = "some existing error",
					testElementHTML = "<input id='" + id + "' aria-invalid='true' aria-described-by='" + errorContainerId + "'>\n\
<span class='wc-fieldindicator wc-fieldindicator-type-error' id='" + errorContainerId + "'><i aria-hidden='true' class='fa fa-times-circle'></i>\n\
<span class='wc-error'>" + INITIAL_ERROR + "</span></span>",
					element,
					errorContainer,
					props;
				testHolder.innerHTML =""; // why do I still write this IE 6 hack?
				testHolder.innerHTML = testElementHTML;
				controller._writeErrors(testHolder);
				errorContainer = document.getElementById(errorContainerId);
				assert.include(errorContainer.innerHTML, INITIAL_ERROR, "exected to find initial error");
				assert.notInclude(errorContainer.innerHTML, CUSTOM_ERROR, "did not expect the custom error");
				element = document.getElementById(id);
				props = {
					"element": element,
					"message": CUSTOM_ERROR
				};
				controller.flagError(props);
				assert.include(errorContainer.innerHTML, INITIAL_ERROR, "exected to still find initial error");
				assert.include(errorContainer.innerHTML, CUSTOM_ERROR, "expected to find the custom error");
				assert.strictEqual(errorContainer.lastChild.innerHTML, CUSTOM_ERROR, "expected custom error to be the content of the last child");
			}
		});
	}
);

