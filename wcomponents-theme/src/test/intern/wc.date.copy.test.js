define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		var controller,
			FEBRUARY = 1,
			HOURS = 3,
			MINS = 4,
			SECS = 5,
			MILLIS = 6;

		registerSuite({
			name: "dateCopy",
			setup: function() {
				return testutils.setupHelper(["wc/date/copy"], function(obj) {
					controller = obj;
				});
			},
			testCopy: function() {
				var testDate = new Date(1968, FEBRUARY, 12, HOURS, MINS, SECS, MILLIS),
					result = controller(testDate);
				assert.strictEqual(testDate.getDate(), result.getDate());
				assert.strictEqual(testDate.getFullYear(), result.getFullYear());
				assert.strictEqual(testDate.getHours(), result.getHours());
				assert.strictEqual(testDate.getMilliseconds(), result.getMilliseconds());
				assert.strictEqual(testDate.getMinutes(), result.getMinutes());
				assert.strictEqual(testDate.getMonth(), result.getMonth());
				assert.strictEqual(testDate.getSeconds(), result.getSeconds());
			}});
	});
