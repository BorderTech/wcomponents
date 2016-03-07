define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var controller;
		registerSuite({
			name: "daysinMonth",
			setup: function() {
				return testutils.setupHelper(["wc/date/daysInMonth"], function(obj) {
					controller = obj;
				});
			},
			testDaysInMonth: function() {
				assert.strictEqual(31, controller(1969, 1));
				assert.strictEqual(28, controller(1969, 2));
				assert.strictEqual(31, controller(1969, 3));
				assert.strictEqual(30, controller(1969, 4));
				assert.strictEqual(31, controller(1969, 5));
				assert.strictEqual(30, controller(1969, 6));
				assert.strictEqual(31, controller(1969, 7));
				assert.strictEqual(31, controller(1969, 8));
				assert.strictEqual(30, controller(1969, 9));
				assert.strictEqual(31, controller(1969, 10));
				assert.strictEqual(30, controller(1969, 11));
				assert.strictEqual(31, controller(1969, 12));
			},
			testDaysInMonthLeapYear: function() {
				assert.strictEqual(31, controller(1968, 1));
				assert.strictEqual(29, controller(1968, 2));
				assert.strictEqual(31, controller(1968, 3));
				assert.strictEqual(30, controller(1968, 4));
				assert.strictEqual(31, controller(1968, 5));
				assert.strictEqual(30, controller(1968, 6));
				assert.strictEqual(31, controller(1968, 7));
				assert.strictEqual(31, controller(1968, 8));
				assert.strictEqual(30, controller(1968, 9));
				assert.strictEqual(31, controller(1968, 10));
				assert.strictEqual(30, controller(1968, 11));
				assert.strictEqual(31, controller(1968, 12));
			}
		});
	});
