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
			}
		});
	}
);
