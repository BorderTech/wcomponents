define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var asciify;
		registerSuite({
			name: "asciify",
			setup: function() {
				return testutils.setupHelper(["wc/i18n/asciify"], function(obj) {
					asciify = obj;
				});
			},
			testAsciifyWithUniChar: function() {
				var input = "\u00e9",
					expected = "e",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithAsciiChar: function() {
				var input = "e",
					expected = "e",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithNumericChar: function() {
				var input = "0",
					expected = "0",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithUnmappedUniChar: function() {
				var input = "\u5047",
					expected = "\u5047",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithEmptyString: function() {
				var input = "",
					expected = "",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithUniString: function() {
				var input = " \u00e0\u00e2\u00e4 \u00e8\u00e9\u00ea\u00eb \u00ee\u00ef \u00f4 \u00f9\u00fb\u00fc ",
					expected = " aaa eeee ii o uuu ",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithAsciiString: function() {
				var input = "aaa eeee ii o uuu",
					expected = "aaa eeee ii o uuu",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithNumericString: function() {
				var input = "0123456789",
					expected = "0123456789",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithUnmappedUniString: function() {
				var input = "\u5f62\u58f0\u5b57 / \u5f62\u8072\u5b57",
					expected = "\u5f62\u58f0\u5b57 / \u5f62\u8072\u5b57",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			},
			testAsciifyWithMixedString: function() {
				var input = "0b\u00e0\u00e2\u00e4b\u00e8\u00e9\u00ea\u00ebh\u00ee\u00efy\u00f4\u00f9\u00fb\u00fc\u5047",
					expected = "0baaabeeeehiiyouuu\u5047",
					actual = asciify(input);
				assert.strictEqual(expected, actual);
			}
		});
	});
