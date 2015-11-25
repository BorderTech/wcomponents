define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var testDate,
			day,
			result,
			SUNDAY,
			MONDAY,
			TUESDAY,
			WEDNESDAY,
			THURSDAY,
			FRIDAY,
			SATURDAY;
		registerSuite({
			name: "dayName",
			setup: function () {
				SUNDAY = 0;
				MONDAY = 1;
				TUESDAY = 2;
				WEDNESDAY = 3;
				THURSDAY = 4;
				FRIDAY = 5;
				SATURDAY = 6;
				testDate = new Date();
				return testutils.setupHelper(["wc/date/dayName"], function(dayName) {
					day = dayName;
				});
			},
			beforeEach: function () {
				result = day.get(testDate);
			},
			testDaySunday: function () {
				assert.strictEqual("Sunday", result[SUNDAY]);
			},
			testDayMonday: function () {
				assert.strictEqual("Monday", result[MONDAY]);
			},
			testDayTuesday: function () {
				assert.strictEqual("Tuesday", result[TUESDAY]);
			},
			testDayWednesday: function () {
				assert.strictEqual("Wednesday", result[WEDNESDAY]);
			},
			testDayThursday: function () {
				assert.strictEqual("Thursday", result[THURSDAY]);
			},
			testDayFriday: function () {
				assert.strictEqual("Friday", result[FRIDAY]);
			},
			testDaySaturday: function () {
				var result = day.get(testDate);
				assert.strictEqual("Saturday", result[SATURDAY]);
			}
		});
	});
