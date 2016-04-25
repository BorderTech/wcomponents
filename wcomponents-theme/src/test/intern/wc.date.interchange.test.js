define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
		function (registerSuite, assert, testutils) {
			"use strict";

			var interchange, today;
			registerSuite({
				name: "dateInterchange",
				setup: function() {
					return testutils.setupHelper(["wc/date/interchange", "wc/date/today"], function(i, t) {
						interchange = i;
						today = t;
					});
				},
				testFromDate: function () {
					var date = new Date(2000, 0, 1),
						expected = "2000-01-01",
						actual = interchange.fromDate(date);
					assert.strictEqual(expected, actual);
				},
				testToDate: function () {
					var date = "2000-12-31",
						actual = interchange.toDate(date);
					assert.strictEqual(2000, actual.getFullYear());
					assert.strictEqual(11, actual.getMonth());
					assert.strictEqual(31, actual.getDate());
				},
				testToDateWithNoSeparators: function () {
					var date = "20000101",
						actual = interchange.toDate(date);
					assert.strictEqual(2000, actual.getFullYear());
					assert.strictEqual(0, actual.getMonth());
					assert.strictEqual(1, actual.getDate());
				},
				testToDateNoYear: function () {
					var now = today.get(),
						date = "????-01-01",
						actual = interchange.toDate(date);
					assert.strictEqual(now.getFullYear(), actual.getFullYear());
					assert.strictEqual(0, actual.getMonth());
					assert.strictEqual(1, actual.getDate());
				},
				testToDateNoMonth: function () {
					var date = "1999-??-31",
						actual = interchange.toDate(date);
					assert.strictEqual(1999, actual.getFullYear());
					assert.strictEqual(0, actual.getMonth());
					assert.strictEqual(31, actual.getDate());
				},
				testToDateNoDay: function () {
					var date = "1999-12-??",
						actual = interchange.toDate(date);
					assert.strictEqual(1999, actual.getFullYear());
					assert.strictEqual(11, actual.getMonth());
					assert.strictEqual(1, actual.getDate());
				},
				testToDateNoMonthNoDay: function () {
					var date = "1999-??-??",
						actual = interchange.toDate(date);
					assert.strictEqual(1999, actual.getFullYear());
					assert.strictEqual(0, actual.getMonth());
					assert.strictEqual(1, actual.getDate());
				},
				testFromValuesWithNumbers: function () {
					var date = {year: 2000, month: 1, day: 1},
						expected = "2000-01-01",
						actual = interchange.fromValues(date);
					assert.strictEqual(expected, actual);
				},
				testFromValuesWithStrings: function () {
					var date = {year: "2000", month: "01", day: "01"},
						expected = "2000-01-01",
						actual = interchange.fromValues(date);
					assert.strictEqual(expected, actual);
				},
				testToValues: function () {
					var date = "1999-12-31",
						actual = interchange.toValues(date);
					assert.strictEqual("1999", actual.year);
					assert.strictEqual("12", actual.month);
					assert.strictEqual("31", actual.day);
				},
				testToValuesNoYear: function () {
					var date = "????-12-31",
						actual = interchange.toValues(date);
					assert.isNull(actual.year);
					assert.strictEqual("12", actual.month);
					assert.strictEqual("31", actual.day);
				},
				testToValuesNoMonth: function () {
					var date = "1999-??-31",
						actual = interchange.toValues(date);
					assert.strictEqual("1999", actual.year);
					assert.isNull(actual.month);
					assert.strictEqual("31", actual.day);
				},
				testToValuesNoDay: function () {
					var date = "1999-06-??",
						actual = interchange.toValues(date);
					assert.strictEqual("1999", actual.year);
					assert.strictEqual("06", actual.month);
					assert.isNull(actual.day);
				},
				testToValuesNoYearNoDay: function () {
					var date = "????-06-??",
						actual = interchange.toValues(date);
					assert.isNull(actual.year);
					assert.strictEqual("06", actual.month);
					assert.isNull(actual.day);
				},
				testToValuesWithNoSeparators: function () {
					var date = "19991231",
						actual = interchange.toValues(date);
					assert.strictEqual("1999", actual.year);
					assert.strictEqual("12", actual.month);
					assert.strictEqual("31", actual.day);
				},
				testIsComplete: function () {
					var date = "2000-01-01";
					assert.isTrue(interchange.isComplete(date), date);
				},
				testIsCompleteNoSeparators: function () {
					var date = "20000101";
					assert.isTrue(interchange.isComplete(date), date);
				},
				testIsCompleteNoYear: function () {
					var date = "????-01-01";
					assert.isFalse(interchange.isComplete(date), date);
				},
				testIsCompleteNoMonth: function () {
					var date = "2000-??-01";
					assert.isFalse(interchange.isComplete(date), date);
				},
				testIsCompleteNoMonthNoSeparators: function () {
					var date = "2000??01";
					assert.isFalse(interchange.isComplete(date), date);
				},
				testIsCompleteNoDate: function () {
					var date = "2000-01-??";
					assert.isFalse(interchange.isComplete(date), date);
				},
				testIsValid: function () {
					var date = "2000-01-01";
					assert.isTrue(interchange.isValid(date), date);
				},
				testIsValidNoSeparators: function () {
					var date = "20000101";
					assert.isTrue(interchange.isValid(date), date);
				},
				testIsValidPartial: function () {
					var date = "2000-??-01";
					assert.isTrue(interchange.isValid(date), date);
				},
				testIsValidPartialNoSeparators: function () {
					var date = "200001??";
					assert.isTrue(interchange.isValid(date), date);
				},
				testIsValidWithInvalidString: function () {
					var date = "2000--01";
					assert.isFalse(interchange.isValid(date), date);
				},
				testIsValidPartialWithInvalidString: function () {
					var date = "200001";
					assert.isFalse(interchange.isValid(date), date);
				},
				testIsValidWithEmptyString: function () {
					var date = "";
					assert.isFalse(interchange.isValid(date), "An empty string should not be a valid xfer date");
				}
			});
		});
