define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
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
			}
		});
	}
);
