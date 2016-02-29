define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";
		registerSuite({
			name: "stringTrim",
			setup: function() {
				return testutils.setupHelper([], function() {
					// no op
					return true;
				});
			},
			testTrimString: function() {
				var testString = "  how  many strawberries   grow in the sea? ",
					expected = "how  many strawberries   grow in the sea?",
					result = testString.trim();
				assert.strictEqual(expected, result);
			},
			testTrimStringTabs: function() {
				var testString = "\thow  many strawberries\tgrow in the sea?\t\t",
					expected = "how  many strawberries\tgrow in the sea?",
					result = testString.trim();
				assert.strictEqual(expected, result);
			}
		});
	});
