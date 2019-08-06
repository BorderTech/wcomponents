define(["intern!object", "intern/chai!assert", "wc/dom/dateFieldUtils", "wc/dom/fieldIndicatorUtils", "wc/date/parsers", "wc/has", "./resources/test.utils!"],
	function (registerSuite, assert, dateFieldUtils, fieldIndicatorUtils, parsers, has, testutils) {
		"use strict";
		var testHolder,
			widgets,
			values = {
				DATE_PARTIAL_WITH_PARTIAL: "JUL 2019",
				DATE_PARTIAL_WITH_FULL: "02 JUL 2019",
				DATE_PARTIAL_WITH_INVALID: "kung fu",
				DATE_PARTIAL_WITH_AMBIGUOUS: "111111",
				CUSTOM_WITH_PARTIAL_XFR: "2019-07-??",
				CUSTOM_WITH_FULL_XFR: "2019-07-02",
				DATE_FAKE_WITH_FULL: "02 JUL 2019",
				DATE_FAKE_WITH_AMBIGUOUS: "111111"
			},
			testSuite = {
				name: "wc/dom/dateFieldUtils",
				setup: function() {
					widgets = dateFieldUtils.getWidgets();
					testHolder = testutils.getTestHolder();

					testutils.renderWidget(widgets.DATE, testHolder);
					testutils.renderWidget(widgets.DATE_PARTIAL, testHolder);

					Object.keys(values).forEach(function(widgetKey) {
						var element, fieldIndicatorWidgets = fieldIndicatorUtils.getWidgets();
						if (widgetKey.indexOf("DATE_PARTIAL_") === 0) {
							widgets[widgetKey] = widgets.DATE_PARTIAL.extend("", { id: widgetKey });
							testutils.renderWidget(widgets[widgetKey], testHolder);
						} else if (widgetKey.indexOf("DATE_FAKE_") === 0) {
							widgets[widgetKey] = widgets.DATE_FAKE.extend("", { id: widgetKey });
							testutils.renderWidget(widgets[widgetKey], testHolder);
						} else if (widgetKey.indexOf("CUSTOM_") === 0) {
							widgets[widgetKey] = widgets.CUSTOM.extend("", { id: widgetKey });
							element = testutils.renderWidget(widgets[widgetKey], testHolder);
							element.appendChild(fieldIndicatorWidgets.FIELDINDICATOR.render());
						}
					});
				},
				beforeEach: function() {
					Object.keys(values).forEach(function(widgetKey) {
						var element = widgets[widgetKey].findDescendant(testHolder);
						if ("value" in element) {
							element.value = values[widgetKey];
						} else if (element.hasAttribute("data-wc-value")) {
							element.setAttribute("data-wc-value", values[widgetKey]);
						} else if (element.firstChild) {
							element.insertBefore(document.createTextNode(values[widgetKey]), element.firstChild);
						} else {
							element.appendChild(document.createTextNode(values[widgetKey]));
						}
					});
				},
				afterEach: function() {

				},
				testGetParserPartial: function() {
					var parser = parsers.get(parsers.type.PARTIAL),
						element = widgets.DATE_PARTIAL_WITH_PARTIAL.findDescendant(testHolder),
						actual = dateFieldUtils.getParser(element);
					assert.isTrue(parser.equals(actual));
				},
				testGetRawValuePartialWithPartial: function() {
					var key = "DATE_PARTIAL_WITH_PARTIAL",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values[key]);
				},
				testGetRawValuePartialWithInvalid: function() {
					var key = "DATE_PARTIAL_WITH_INVALID",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values[key]);
				},
				testGetRawValuePartialWithFull: function() {
					var key = "DATE_PARTIAL_WITH_FULL",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values[key]);
				},
				testGetRawValueCustomPartialXfr: function() {
					var key = "CUSTOM_WITH_PARTIAL_XFR",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values[key]);
				},
				testGetRawValueCustomFullXfr: function() {
					var key = "CUSTOM_WITH_FULL_XFR",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values[key]);
				},
				testGetValuePartialWithPartial: function() {
					var key = "DATE_PARTIAL_WITH_PARTIAL",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual.xfr, "2019-07-??");
					assert.equal(actual.raw, values[key]);
				},
				testGetValuePartialWithInvalid: function() {
					var key = "DATE_PARTIAL_WITH_INVALID",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual.xfr, "");
					assert.equal(actual.raw, values[key]);
				},
				testGetValuePartialWithFull: function() {
					var key = "DATE_PARTIAL_WITH_FULL",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual.xfr, "2019-07-02");
					assert.equal(actual.raw, values[key]);
				},
				testGetValueCustomPartialXfr: function() {
					var key = "CUSTOM_WITH_PARTIAL_XFR",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual.xfr, values[key]);
					assert.equal(actual.raw, values[key]);
				},
				testGetValueCustomFullXfr: function() {
					var key = "CUSTOM_WITH_FULL_XFR",
						element = widgets[key].findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual.xfr, values[key]);
					assert.equal(actual.raw, values[key]);
				},
				testHasPartialDateWithPartial: function() {
					var element = widgets.DATE_PARTIAL_WITH_PARTIAL.findDescendant(testHolder),
						actual = dateFieldUtils.hasPartialDate(element);
					assert.isTrue(actual);
				},
				testHasPartialDateWithInvalid: function() {
					var element = widgets.DATE_PARTIAL_WITH_INVALID.findDescendant(testHolder),
						actual = dateFieldUtils.hasPartialDate(element);
					assert.strictEqual(1, actual);
				},
				testHasPartialDateEmpty: function() {
					var element = widgets.DATE_PARTIAL.findDescendant(testHolder),
						actual = dateFieldUtils.hasPartialDate(element);
					assert.isFalse(actual);
				},
				testHasPartialDateWithFull: function() {
					var element = widgets.DATE_PARTIAL_WITH_FULL.findDescendant(testHolder),
						actual = dateFieldUtils.hasPartialDate(element);
					assert.isFalse(actual);
				},
				testHasPartialDateCustomPartialXfr: function() {
					var element = widgets.CUSTOM_WITH_PARTIAL_XFR.findDescendant(testHolder),
						actual = dateFieldUtils.hasPartialDate(element);
					assert.isTrue(actual);
				},
				testHasPartialDateCustomFullXfr: function() {
					var element = widgets.CUSTOM_WITH_FULL_XFR.findDescendant(testHolder),
						actual = dateFieldUtils.hasPartialDate(element);
					assert.isFalse(actual);
				},
				testGetMatches: function() {
					var partialElement = widgets.DATE_PARTIAL_WITH_AMBIGUOUS.findDescendant(testHolder),
						fakeElement = widgets.DATE_FAKE_WITH_AMBIGUOUS.findDescendant(testHolder),
						actualPartial = dateFieldUtils.getMatches(partialElement),
						actualFake = dateFieldUtils.getMatches(fakeElement);
					/*
					 * Note: this test could be more specifc about the matches however I did not want to bind it strongly to the specific date masks that
					 * have been configured via i18n.
					 *
					 * The main thing to ensure is that partial date fields use a different parser than full date fields AND that the partial parser matches
					 * a greater number of possible "dates".
					 */
					assert.isAbove(actualFake.length, 0, "There should be about two possible full date matches");
					assert.isAbove(actualPartial.length, actualFake.length, "Partial date field should have more matches than full date ");
				},
				testGetMatchesPartialWithFullDate: function() {
					var element = widgets.DATE_PARTIAL_WITH_FULL.findDescendant(testHolder),
						actual = dateFieldUtils.getMatches(element);
					assert.equal(1, actual.length, "There should be one date match for a full date");
				},
				testGetMatchesFakeWithFullDate: function() {
					var element = widgets.DATE_FAKE_WITH_FULL.findDescendant(testHolder),
						actual = dateFieldUtils.getMatches(element);
					assert.equal(1, actual.length, "There should be one date match for a full date");
				}
			};

		if (has("native-dateinput")) {

			values.DATE = "1660-11-28";

			testSuite.testGetParserNative = function() {
				var standardParser = parsers.get(parsers.type.STANDARD),
					nativeDate = widgets.DATE.findDescendant(testHolder),
					actual = dateFieldUtils.getParser(nativeDate);
				assert.isTrue(standardParser.equals(actual));
			};

			testSuite.testGetRawValueNative = function() {
				var key = "DATE",
					element = widgets[key].findDescendant(testHolder),
					actual = dateFieldUtils.getRawValue(element);
				assert.equal(actual, values[key]);
			};

			testSuite.testGetValueNative = function() {
				var key = "DATE", element = widgets[key].findDescendant(testHolder),
					actual = dateFieldUtils.getValue(element);
				assert.equal(actual.xfr, values[key]);
				assert.equal(actual.raw, values[key]);
			};

			testSuite.testHasPartialDateNative = function() {
				var element = widgets.DATE.findDescendant(testHolder),
					actual = dateFieldUtils.hasPartialDate(element);
				assert.isFalse(actual);
			};

			testSuite.testGetMatchesNative = function() {
				var element = widgets.DATE.findDescendant(testHolder),
					actual = dateFieldUtils.getMatches(element);
				assert.equal(1, actual.length, "There should be one date match for a full date");
			};
		}

		registerSuite(testSuite);
	}
);
