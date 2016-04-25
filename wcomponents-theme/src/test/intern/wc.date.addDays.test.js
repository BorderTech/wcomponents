define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var controller,
			testDate,
			FEBRUARY = 1,
			HOURS = 3,
			MINS = 4,
			SECS = 5,
			MILLIS = 6;

		function setUp() {
			// new Date(year, month, day, hours, minutes, seconds, milliseconds)
			testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS);
		}

		registerSuite({
			name: "dateAddDays",
			setup: function() {
				return testutils.setupHelper(["wc/date/addDays"], function(obj) {
					controller = obj;
				});
			},
			testAddDays: function () {
				setUp();
				assert.strictEqual(12, testDate.getDate());
				controller(12, testDate);
				assert.strictEqual(24, testDate.getDate());
				assert.strictEqual(HOURS, testDate.getHours());
				assert.strictEqual(MINS, testDate.getMinutes());
				assert.strictEqual(SECS, testDate.getSeconds());
				assert.strictEqual(MILLIS, testDate.getMilliseconds());
			},
			testAddDaysLeapYear: function () {
				setUp();
				assert.strictEqual(12, testDate.getDate());
				controller(17, testDate);
				assert.strictEqual(29, testDate.getDate());
				assert.strictEqual(FEBRUARY, testDate.getMonth());
				assert.strictEqual(HOURS, testDate.getHours());
				assert.strictEqual(MINS, testDate.getMinutes());
				assert.strictEqual(SECS, testDate.getSeconds());
				assert.strictEqual(MILLIS, testDate.getMilliseconds());
			},
			testAddDaysNonLeapYear: function () {
				setUp();
				testDate.setFullYear(1969);
				assert.strictEqual(12, testDate.getDate());
				controller(17, testDate);
				assert.strictEqual(1, testDate.getDate());
				assert.strictEqual(FEBRUARY + 1, testDate.getMonth());
				assert.strictEqual(HOURS, testDate.getHours());
				assert.strictEqual(MINS, testDate.getMinutes());
				assert.strictEqual(SECS, testDate.getSeconds());
				assert.strictEqual(MILLIS, testDate.getMilliseconds());
			}
		});
	});
