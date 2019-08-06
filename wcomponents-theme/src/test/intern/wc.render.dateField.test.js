define(["intern!object", "intern/chai!assert", "wc/dom/dateFieldUtils", "wc/render/dateField",
	"wc/dom/shed", "wc/has", "wc/array/toArray"],
	function (registerSuite, assert, dateFieldUtils, dateField, shed, has, toArray) {
		"use strict";
		var widgets,
			hasNativeDate = has("native-dateinput"),
			testSuite = {
				name: "wc/render/dateField",
				setup: function() {
					widgets = dateFieldUtils.getWidgets();
				},
				beforeEach: function() {
				},
				afterEach: function() {

				},
				testRender: function() {
					var state = {
						id: "element-666"
					};

					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var invalid = results.input.hasAttribute("aria-invalid");
						assert.isFalse(invalid, "An empty date field should not be invalid");
						if (hasNativeDate) {
							assert.isNull(results.input.getAttribute("autocomplete"), "autocomplete should not, by default, be set on a native date field");
						}
					});
				},
				testRenderWithDate: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "1605-11-05"
					};
					return renderHelper(state, { expectNative: hasNativeDate}).then(function(results) {
						var invalid = results.input.hasAttribute("aria-invalid"),
							actual = dateFieldUtils.getValue(results.wrapper);
						assert.equal(state["data-wc-value"], actual.xfr);
						assert.isFalse(invalid, "A date field with a full date should not be invalid");
					});
				},
				testRenderPartialFalseWithFullDate: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "1605-11-05",
						"data-wc-allowpartial": "false"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var invalid = results.input.hasAttribute("aria-invalid"),
							actual = dateFieldUtils.getValue(results.wrapper);
						assert.equal(state["data-wc-value"], actual.xfr);
						assert.isFalse(invalid, "A date field with a full date should not be invalid");
						if (!hasNativeDate) {
							assert.isTrue(widgets.DATE_WRAPPER_PARTIAL.isOneOfMe(results.wrapper), "A full date value should default to a native date field");
						}
					});
				},
				testRenderPartialTrueWithFullDate: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "1605-11-05",
						"data-wc-allowpartial": "true"
					};
					return renderHelper(state).then(function(results) {
						var invalid = results.input.hasAttribute("aria-invalid"),
							actual = dateFieldUtils.getValue(results.wrapper);
						assert.equal(state["data-wc-value"], actual.xfr);
						assert.isFalse(invalid, "A date field with a full date should not be invalid");
						assert.isTrue(widgets.DATE_WRAPPER_PARTIAL.isOneOfMe(results.wrapper), "allow-partial true should force a partial date field");
						if (hasNativeDate) {
							assert.isFalse(shed.isDisabled(results.switcher), "Switcher should be available");
						}
					});
				},
				testRenderPartialTrueWithPartialDate: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "1605-11-??",
						"data-wc-allowpartial": "true"
					};
					return renderHelper(state).then(function(results) {
						var invalid = results.input.hasAttribute("aria-invalid"),
							actual = dateFieldUtils.getValue(results.wrapper);
						assert.equal(state["data-wc-value"], actual.xfr);
						assert.isFalse(invalid, "A partial date field with a partial date should not be invalid");
						assert.isTrue(widgets.DATE_WRAPPER_PARTIAL.isOneOfMe(results.wrapper), "allow-partial true should force a partial date field");
						if (hasNativeDate) {
							assert.isTrue(shed.isDisabled(results.switcher), "Switcher should be disabled with partial date");
						}
					});
				},
				testRenderPartialFalseWithPartialDate: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "1605-11-??",
						"data-wc-allowpartial": "false"
					};
					return renderHelper(state).then(function(results) {
						var invalid = results.input.hasAttribute("aria-invalid"),
							actual = dateFieldUtils.getValue(results.wrapper);
						assert.equal(state["data-wc-value"], actual.xfr);
						assert.isFalse(invalid, "A partial date field with a partial date should not be invalid");
						assert.isTrue(widgets.DATE_WRAPPER_PARTIAL.isOneOfMe(results.wrapper), "A partial date value should force a partial date field");
						if (hasNativeDate) {
							assert.isTrue(shed.isDisabled(results.switcher), "Switcher should be disabled with partial date");
						}
					});
				},
				testRenderNotPartialWithPartialDate: function() {
					/*
					 * This ensures that full date fields are not rendered as partial date fields if supplied with a partial date value.
					 * The ability to render as a partial date field is restricted to fields that have the partial attribute set to either "true" or "false".
					 */
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "1605-11-??"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						if (hasNativeDate) {
							assert.isTrue(results.input.hasAttribute("aria-invalid"), "A full date field with a partial date should be invalid");
						}
						assert.isFalse(widgets.DATE_WRAPPER_PARTIAL.isOneOfMe(results.wrapper), "A partial date value on a full date field should NOT force a partial date field");
					});
				},
				testRenderNotPartialWithNonsense: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "nonsense"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = dateFieldUtils.getValue(results.wrapper);
						assert.equal(state["data-wc-value"], actual.raw);
					});
				},
				testRenderWithAutocomplete: function() {
					var state = {
						id: "element-666",
						"data-wc-autocomplete": "one-time-code"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = results.input.getAttribute("autocomplete");

						if (hasNativeDate) {
							assert.equal(state["data-wc-autocomplete"], actual, "autocomplete should not, by default, be set on a native date field");
						} else {
							assert.equal("off", actual, "every input that implements combo should have autocomplete turned off");
						}
					});
				},
				testRenderWithPlaceholder: function() {
					var state = {
						id: "element-666",
						"data-wc-placeholder": "there's a dragon in my garage"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = results.input.getAttribute("placeholder");
						assert.equal(state["data-wc-placeholder"], actual, "placeholder text should be set");
					});
				},
				testRenderWithRequired: function() {
					var state = {
						id: "element-666",
						"data-wc-required": "true"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = results.input.hasAttribute("required");
						assert.isTrue(actual, "Required attribute should be set");
					});
				},
				testRenderWithDisabled: function() {
					var state = {
						id: "element-666",
						"data-wc-disabled": "true"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = results.input.hasAttribute("disabled");
						assert.isTrue(actual, "Disabled attribute should be set");
					});
				},
				testRenderWithAcessibleText: function() {
					var state = {
						id: "element-666",
						"data-wc-accessibletext": "Why The Face"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = results.input.getAttribute("aria-label");
						assert.equal(actual, state["data-wc-accessibletext"], "Accessible text should be set");
					});
				},
				testRenderWithButtonId: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-id"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = results.input.getAttribute("data-wc-submit");
						assert.equal(actual, state["data-wc-buttonid"], "Button ID should be set");
					});
				},
				testRenderWithSubmitOnChange: function() {
					var state = {
						id: "element-666",
						"data-wc-submitonchange": "true"
					};
					return renderHelper(state, { expectNative: hasNativeDate });
				},
				testRenderWithClass: function() {
					var classes = ["test", "icicles"],
						state = {
							id: "element-666",
							"data-wc-class": classes.join(" "),
							"data-wc-buttonid": "some-id"
						};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						classes.forEach(function(next) {
							assert.isTrue(results.wrapper.classList.contains(next), "Additional class should be added to the wrapper " + next);
							assert.isFalse(results.input.classList.contains(next), "Additional class should NOT be added to the input " + next);
						});
					});
				},
				testRenderWithButtonMinMax: function() {
					var state = {
						id: "element-666",
						"data-wc-min": "2017-04-01",
						"data-wc-max": "2017-04-30"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actualMin = results.input.getAttribute("min"),
							actualMax = results.input.getAttribute("max");
						if (hasNativeDate) {
							assert.equal(actualMin, state["data-wc-min"], "min should be set");
							assert.equal(actualMax, state["data-wc-max"], "max should be set");
						} else {

							assert.isNull(actualMin, "min should only ever be set on native date inputs");
							assert.isNull(actualMax, "max should only ever be set on native date inputs");
						}
					});
				},
				testSwitchNativeToCustomAndBack: function() {
					var state = {
						id: "element-666",
						"data-wc-buttonid": "some-fake-id",
						"data-wc-autocomplete": "one-time-code",
						"data-wc-placeholder": "there's a dragon in my garage",
						"data-wc-accessibletext": "Why The Face",
						"data-wc-required": "true",
						"data-wc-value": "1605-11-05",
						"data-wc-allowpartial": "false"
					};
					return renderHelper(state, { expectNative: hasNativeDate }).then(function(results) {
						var actual = dateFieldUtils.getValue(results.wrapper);
						assert.equal(state["data-wc-value"], actual.xfr);
						if (hasNativeDate) {
							results.wrapper.setAttribute("data-wc-allowpartial", "true");
							return renderHelper(state, { el: results.wrapper }).then(function(switchedResults) {
								var actualSwitched = dateFieldUtils.getValue(switchedResults.wrapper);
								assert.equal(state["data-wc-value"], actualSwitched.xfr);
								return renderHelper(state, { el: switchedResults.wrapper }).then(function(twiceSwitchedResults) {
									var actualTwiceSwitched = dateFieldUtils.getValue(twiceSwitchedResults.wrapper);
									assert.equal(state["data-wc-value"], actualTwiceSwitched.xfr);
								});
							});
						}
					});
				}
			};


		/**
		 * Checks the class attribute on the rendered element - runs assertions.
		 * This means that it has the classes expected for its widget AND no additional classes unless specified.
		 * @param {Element} element The element to check.
		 * @param {wc/dom/Widget} widget The widget that describes this element.
		 * @param {String|String[]} additionalClasses Additional classes to expect on the element (beyond the widget).
		 */
		function checkClasses(element, widget, additionalClasses) {
			var expectedElement = widget.render(),  // easiest way to generate the effective classes on a widget taking into account container widgets
				expectedClasses = toArray(expectedElement.classList),
				actualClasses = toArray(element.classList);
			if (additionalClasses) {
				if (!Array.isArray(additionalClasses)) {
					additionalClasses = additionalClasses.split(" ");
				}
				expectedClasses = expectedClasses.concat(additionalClasses);
			}
			assert.sameMembers(expectedClasses, actualClasses, "Actual: " + actualClasses.join());

		}

		/**
		 * Uses the dateField renderer to create an WComponents date field with the given state.
		 * Performs some generic tests where possible.
		 * @param state Map of attributes to add to the CUSTOM "wc-dateinput" element that is passed to the render function to create the actual date field.
		 * @param config Configuration options, as shown below
		 * @param {boolean} [config.expectNative] If true then assertions will check that  a native date input has been generated.
		 * @param {Element} [config.el] Optionally provide the source element.
		 * @returns An object with properties referencing the different parts of a date field: wrapper, input, switcher
		 */
		function renderHelper(state, config) {
			var conf = config || {},
				element = conf.el || widgets.CUSTOM.render({ state: state }),
				inputAttrMap = {
					"data-wc-tooltip": "title",
					"data-wc-required": "required",
					"data-wc-disabled": "disabled",
					"data-wc-accessibletext": "aria-label",
					"data-wc-buttonid": "data-wc-submit",
					"data-wc-placeholder": "placeholder",
					"data-wc-autocomplete": "autocomplete",
					"data-wc-min": "min",
					"data-wc-max": "max"
				};

			return dateField.render(element).then(function(actual) {
				var switcherElement = widgets.SWITCHER.findDescendant(actual),
					inputElement,
					inputWidget,
					containerWidget,
					allowPartial = state["data-wc-allowpartial"] || state["data-wc-allowpartial"] === false,
					additionalInputClasses = element.hasAttribute("data-wc-submitonchange") ? ["wc_soc"] : "";

				// Determine what widgets to use to verify this element
				if (conf.expectNative) {
					containerWidget = "DATE_FIELD";
					inputWidget = "DATE";
					inputElement = widgets[inputWidget].findDescendant(actual);
				} else {
					if (allowPartial) {
						containerWidget = "DATE_WRAPPER_PARTIAL";
						inputWidget = "DATE_PARTIAL";
					} else {
						containerWidget = "DATE_WRAPPER_FAKE";
						inputWidget = "DATE_FAKE";
					}
					inputElement = widgets[inputWidget].findDescendant(actual);
					assert.equal("off", inputElement.getAttribute("autocomplete"), "every input that implements combo should have autocomplete turned off");
				}

				assert.isTrue(widgets[containerWidget].isOneOfMe(actual), "Expecting " + containerWidget);
				checkClasses(actual, widgets[containerWidget], state["data-wc-class"]);
				checkClasses(inputElement, widgets[inputWidget], additionalInputClasses);

				/*
				 * Check that all the attributes have been set correctly on the rendered element.
				 * We have specific tests for these elsewhere but I decided to add some generic assertions here too.
				 */
				Object.keys(inputAttrMap).forEach(function(attrName) {
					var actualAttr,
						stateVal = state[attrName],
						attrVal = inputAttrMap[attrName];
					if (stateVal) {
						if (stateVal === "true" || attrVal === stateVal) {
							// if the state sets an attribute to "true" or to its own name (required=required) then we will only check for the presence of the attribute.
							actualAttr = inputElement.hasAttribute(attrVal);
							assert.isTrue(actualAttr, attrVal + " attribute should be set");
						} else if (!conf.expectNative && attrVal === "autocomplete") {
							// special rules for autocomplete on fake dates
							actualAttr = inputElement.getAttribute(attrVal);
							assert.equal("off", actualAttr, "every input that implements combo should have autocomplete turned off");
						} else if (attrVal === "min" || attrVal === "max") {
							// min and max only for native date inputs
							actualAttr = inputElement.getAttribute(attrVal);
							if (conf.expectNative) {
								assert.equal(actualAttr, stateVal, attrVal + " attribute should be "+ stateVal);
							} else {
								assert.isNull(actualAttr, "min and max  should only ever be set on native date inputs");
							}
						} else if (stateVal.constructor === String) {
							// just a regular attribute with a value, make sure it has been renamed and set accordingly
							actualAttr = inputElement.getAttribute(attrVal);
							assert.equal(actualAttr, stateVal, attrVal + " attribute should be "+ stateVal);
						}
					}
				});

				assert.equal(state.id, inputElement.name, "The date field ID should be the input name attribute");

				assert.equal(state.id, actual.id, "The date field ID should be the wrapper ID");

				if (!hasNativeDate) {
					assert.isNull(switcherElement, "Should never provide option to switch when there is no native date field.");
				} else if (allowPartial) {
					assert.isNotNull(switcherElement, "Partial date should provide option to switch between native and custom date field.");
				}

				return { wrapper: actual, input: inputElement, switcher: switcherElement };
			});
		}

		registerSuite(testSuite);
	}
);
