define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var Format;

		registerSuite({
			name: "dateFormat",
			setup: function() {
				return testutils.setupHelper(["wc/date/Format"], function(obj) {
					Format = obj;
				});
			},
			testFormat: function () {
				var mask = "dd MMM yyyy",
					formatter = new Format(mask),
					date = "2000-01-01",
					expected = "01 Jan 2000",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatGB: function () {
				var mask = "dd/MM/yyyy",
					formatter = new Format(mask),
					date = "2000-02-03",
					expected = "03/02/2000",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatUS: function () {
				var mask = "MM/dd/yyyy",
					formatter = new Format(mask),
					date = "2000-02-03",
					expected = "02/03/2000",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatMissingPart: function () {
				var mask = "dd/MMM/yyyy",
					formatter = new Format(mask),
					date = "2000-??-01",
					expected = "01/ /2000",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatFullMonth: function () {
				var mask = "dd MMMM yyyy",
					formatter = new Format(mask),
					date = "1999-12-31",
					expected = "31 December 1999",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatYearOnly: function () {
				var mask = "yyyy",
					formatter = new Format(mask),
					date = "1999-12-31",
					expected = "1999",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatMonthOnly: function () {
				var mask = "MM",
					formatter = new Format(mask),
					date = "1999-12-31",
					expected = "12",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatDayOnly: function () {
				var mask = "d",
					formatter = new Format(mask),
					date = "1999-12-09",
					expected = "9",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTime: function () {
				var mask = "dd/MM/yyyy HH:mm",
					formatter = new Format(mask),
					date = "2000-02-03T00:02:01",
					expected = "03/02/2000 00:02",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTimeWithSeconds: function () {
				var mask = "dd/MM/yyyy HH:mm:ss",
					formatter = new Format(mask),
					date = "2000-02-03T00:02:59",
					expected = "03/02/2000 00:02:59",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTime12HrAM: function () {
				var mask = "dd/MM/yyyy hh:mm",
					formatter = new Format(mask),
					date = "2000-02-03T00:02:01",
					expected = "03/02/2000 12:02",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTime12HrPM: function () {
				var mask = "dd/MM/yyyy hh:mm",
					formatter = new Format(mask),
					date = "2000-02-03T23:59:01",
					expected = "03/02/2000 11:59",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTime12HrShort: function () {
				var mask = "dd/MM/yyyy h:mm",
					formatter = new Format(mask),
					date = "2000-02-03T01:02:01",
					expected = "03/02/2000 1:02",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTime12HrLong: function () {
				var mask = "dd/MM/yyyy hh:mm",
					formatter = new Format(mask),
					date = "2000-02-03T01:02:01",
					expected = "03/02/2000 01:02",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTime12HrAAAM: function () {
				var mask = "dd/MM/yyyy hh:mm a",
					formatter = new Format(mask),
					date = "2000-02-03T00:02:01",
					expected = "03/02/2000 12:02 AM",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatTime12HrAAPM: function () {
				var mask = "dd/MM/yyyy hh:mm a",
					formatter = new Format(mask),
					date = "2000-02-03T12:00:01",
					expected = "03/02/2000 12:00 PM",
					actual = formatter.format(date);
				assert.strictEqual(expected, actual);
			},
			testFormatReverse: function() {
				var mask = "dd MMM yyyy",
					formatter = new Format(mask),
					date = "01 Jan 2000",
					expected = "2000-01-01",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatGBReverse: function () {
				var mask = "dd/MM/yyyy",
					formatter = new Format(mask),
					date = "03/02/2000",
					expected = "2000-02-03",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatUSReverse: function () {
				var mask = "MM/dd/yyyy",
					formatter = new Format(mask),
					date = "02/03/2000",
					expected = "2000-02-03",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatMissingPartReverse: function () {
				var mask = "dd/MMM/yyyy",
					formatter = new Format(mask),
					date = "01/ /2000",
					expected = "2000-??-01",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatReverseMonthOnly: function () {
				var mask = "MM",
					formatter = new Format(mask),
					date = "12",
					expected = "????-12-??",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatReverseMonthAbbrOnly: function () {
				var mask = "MMM",
					formatter = new Format(mask),
					date = "Oct",
					expected = "????-10-??",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatReverseMonthYearOnly: function () {
				var mask = "MM yyyy",
					formatter = new Format(mask),
					date = "12 1973",
					expected = "1973-12-??",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatReverseMonthYearOnlyWithDashes: function () {
				var mask = "MM-yyyy",
					formatter = new Format(mask),
					date = "12-1973",
					expected = "1973-12-??",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatReverseDayMonthOnlyWithSlashes: function () {
				var mask = "dd/MM",
					formatter = new Format(mask),
					date = "31/12",
					expected = "????-12-31",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormatReverseDayMonthUSAOnlyWithSlashes: function () {
				var mask = "MM/dd",
					formatter = new Format(mask),
					date = "12/31",
					expected = "????-12-31",
					actual = formatter.reverse(date);
				assert.strictEqual(expected, actual);
			},
			testFormattedDatesSameTrue: function () {
				var date1 = " 31 DECEMBER 1999",
					date2 = "31 December 1999  ",
					actual = Format.formattedDatesSame(date1, date2);
				assert.isTrue(actual);
			},
			testFormattedDatesSameFalse: function () {
				var date1 = "31 October 1999",
					date2 = "31 December 1999",
					actual = Format.formattedDatesSame(date1, date2);
				assert.isFalse(actual);
			},
			testGetDefaultFormatter: function () {
				var formatter = Format.getDefaultFormatter();
				assert.strictEqual(formatter.constructor, Format);
			}
		});
	}
);
