define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var controller,
			mondayWeek,
			defaultWeek;
		registerSuite({
			name: "dayName",
			setup: function () {
				return testutils.setupHelper(["wc/date/dayName"], function(dayName) {
					controller = dayName;
				});
			},
			beforeEach: function () {
				defaultWeek = controller.get();
				mondayWeek = controller.get(true);
			},
			testDaySunday: function () {
				assert.strictEqual("Sunday", defaultWeek[0]);
				assert.strictEqual("Monday", mondayWeek[0]);
			},
			testDayMonday: function () {
				assert.strictEqual("Monday", defaultWeek[1]);
				assert.strictEqual("Tuesday", mondayWeek[1]);
			},
			testDayTuesday: function () {
				assert.strictEqual("Tuesday", defaultWeek[2]);
				assert.strictEqual("Wednesday", mondayWeek[2]);
			},
			testDayWednesday: function () {
				assert.strictEqual("Wednesday", defaultWeek[3]);
				assert.strictEqual("Thursday", mondayWeek[3]);
			},
			testDayThursday: function () {
				assert.strictEqual("Thursday", defaultWeek[4]);
				assert.strictEqual("Friday", mondayWeek[4]);
			},
			testDayFriday: function () {
				assert.strictEqual("Friday", defaultWeek[5]);
				assert.strictEqual("Saturday", mondayWeek[5]);
			},
			testDaySaturday: function () {
				assert.strictEqual("Saturday", defaultWeek[6]);
				assert.strictEqual("Sunday", mondayWeek[6]);
			}
		});
	});
