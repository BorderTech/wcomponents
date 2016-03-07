define(["intern!object", "intern/chai!assert", "./resources/test.utils!"],
	function (registerSuite, assert, testutils) {
		"use strict";

		var escapeRe;

		registerSuite({
			name: "escapeRe",
			setup: function() {
				return testutils.setupHelper(["wc/string/escapeRe"], function(obj) {
					escapeRe = obj;
				});
			},
			testEscapeReString: function() {
				var testString = "a.b|c*d?e+f(g)h{i}j[k]l^m$n\\o",  // have to double escape backslashes,
					expected = "a\\.b\\|c\\*d\\?e\\+f\\(g\\)h\\{i\\}j\\[k\\]l\\^m\\$n\\\\o",  // have to double escape backslashes,
					result = escapeRe(testString);
				assert.strictEqual(expected, result);
			},
			testEscapeReStringWithWildcard: function() {
				var testString = "a.b|c*d?e+f(g)h{i}j[k]l^m$n\\o",  // have to double escape backslashes,
					expected = "a\\.b\\|c.*d\\?e\\+f\\(g\\)h\\{i\\}j\\[k\\]l\\^m\\$n\\\\o",  // have to double escape backslashes,
					result = escapeRe(testString, true);
				assert.strictEqual(expected, result);
			}
		});
	});
