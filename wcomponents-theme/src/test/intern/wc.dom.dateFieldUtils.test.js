define(["intern!object", "intern/chai!assert", "wc/dom/dateFieldUtils", "wc/dom/fieldIndicatorUtils", "wc/date/parsers", "wc/has", "./resources/test.utils!"],
	function (registerSuite, assert, dateFieldUtils, fieldIndicatorUtils, parsers, has, testutils) {
		"use strict";
	//				testContent += "<span id='" + ids.PARTIAL_TEXT + "'>JUL 2019</span>";
	//				testContent += "<span id='" + ids.FULL_TEXT + "'>02 JUL 2019</span>";
	//				testContent += "<span id='" + ids.PARTIAL_TEXT_XFR + "'>2019-07-??</span>";
	//				testContent += "<span id='" + ids.FULL_TEXT_XFR + "'>2019-07-02</span>";
		var testHolder,
			widgets,
			values = {
				DATE_PARTIAL_WITH_PARTIAL: "JUL 2019",
				DATE_PARTIAL_WITH_FULL: "02 JUL 2019",
				DATE_PARTIAL_WITH_INVALID: "kung fu",
				CUSTOM_WITH_PARTIAL_XFR: "2019-07-??",
				CUSTOM_WITH_FULL_XFR: "2019-07-02"
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
					var element = widgets.DATE_PARTIAL_WITH_PARTIAL.findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values.DATE_PARTIAL_WITH_PARTIAL);
				},
				testGetRawValuePartialWithInvalid: function() {
					var element = widgets.DATE_PARTIAL_WITH_INVALID.findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values.DATE_PARTIAL_WITH_INVALID);
				},
				testGetRawValuePartialWithFull: function() {
					var element = widgets.DATE_PARTIAL_WITH_FULL.findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values.DATE_PARTIAL_WITH_FULL);
				},
				testGetRawValueCustomPartialXfr: function() {
					var element = widgets.CUSTOM_WITH_PARTIAL_XFR.findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values.CUSTOM_WITH_PARTIAL_XFR);
				},
				testGetRawValueCustomFullXfr: function() {
					var element = widgets.CUSTOM_WITH_FULL_XFR.findDescendant(testHolder),
						actual = dateFieldUtils.getRawValue(element);
					assert.equal(actual, values.CUSTOM_WITH_FULL_XFR);
				},
				testGetValuePartialWithPartial: function() {
					var element = widgets.DATE_PARTIAL_WITH_PARTIAL.findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual, "2019-07-??");
				},
				testGetValuePartialWithInvalid: function() {
					var element = widgets.DATE_PARTIAL_WITH_INVALID.findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual, "");
				},
				testGetValuePartialWithFull: function() {
					var element = widgets.DATE_PARTIAL_WITH_FULL.findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual, "2019-07-02");
				},
				testGetValueCustomPartialXfr: function() {
					var element = widgets.CUSTOM_WITH_PARTIAL_XFR.findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual, values.CUSTOM_WITH_PARTIAL_XFR);
				},
				testGetValueCustomFullXfr: function() {
					var element = widgets.CUSTOM_WITH_FULL_XFR.findDescendant(testHolder),
						actual = dateFieldUtils.getValue(element);
					assert.equal(actual, values.CUSTOM_WITH_FULL_XFR);
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
				var element = widgets.DATE.findDescendant(testHolder),
					actual = dateFieldUtils.getRawValue(element);
				assert.equal(actual, values.DATE);
			};

			testSuite.testGetValueNative = function() {
				var element = widgets.DATE.findDescendant(testHolder),
					actual = dateFieldUtils.getValue(element);
				assert.equal(actual, values.DATE);
			};

			testSuite.testHasPartialDateNative = function() {
				var element = widgets.DATE.findDescendant(testHolder),
					actual = dateFieldUtils.hasPartialDate(element);
				assert.isFalse(actual);
			};

		}

		registerSuite(testSuite);
	}
);
