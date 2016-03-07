define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		/* eslint-disable no-new-wrappers */
		var isNumeric;

		registerSuite({
			name: "isNumeric",
			setup: function() {
				return testutils.setupHelper(["wc/isNumeric"], function(obj) {
					isNumeric = obj;
				});
			},
			testisNumericIntegerString: function () {
				var arg = "666",
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericFloatStringObject: function() {
				var arg = new String("666.666"),
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericFloatStringObjectNoMutation: function() {
				var arg = new String("666.666"),
					result = isNumeric(arg);
				assert.strictEqual(true, result);
				assert.strictEqual("666.666", arg.valueOf());  // not mutated
			},
			testisNumericInteger: function() {
				var arg = 666,
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericFloat: function() {
				var arg = 666.666,
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericNegativeInteger: function() {
				var arg = -666,
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericNegativeStringFloat: function() {
				var arg = "-666.666",
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericNothing: function() {
				var arg,
					result = isNumeric(arg);
				assert.strictEqual(false, result);
			},
			testisNumericNonNumericString: function() {
				var arg = "xyz333",
					result = isNumeric(arg);
				assert.strictEqual(false, result);
			},
			testisNumericNumber: function() {
				var arg = new Number(-666),
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericNumberNoMutation: function() {
				var arg = new Number(-666),
					result = isNumeric(arg);
				assert.strictEqual(true, result);
				assert.strictEqual(-666, arg.valueOf());  // not mutated
			},
			testisNumericNumber2: function() {
				var arg = new Number(666),
					result = isNumeric(arg);
				assert.strictEqual(true, result);
			},
			testisNumericNumber2NoMutation: function() {
				var arg = new Number(666),
					result = isNumeric(arg);
				assert.strictEqual(true, result);
				assert.strictEqual(666, arg.valueOf());  // not mutated
			}
		});
		/* eslint-enable no-new-wrappers */
	});
