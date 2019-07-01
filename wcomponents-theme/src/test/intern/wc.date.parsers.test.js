define(["intern!object", "intern/chai!assert", "./resources/test.utils!"], function(registerSuite, assert, testutils) {
	"use strict";
	var parsers;

	function parserCompare(p1, p2, methods) {
		var result = false,
			testMethods = methods || ["getMasks", "isRolling", "isExpandYearIntoPast"];
		if (p1) {
			result = testMethods.every(function(testMethod) {
				return p1[testMethod]() === p2[testMethod]();
			});
		}
		return result;

	}

	registerSuite({
		name: "date/parsers",
		setup: function() {
			return testutils.setupHelper(["wc/date/parsers"], function(p1) {
				parsers = p1;
			});
		},
		testParserStndDefault: function() {
			var p1 = parsers.get(parsers.type.STANDARD),
				p2 = parsers.get();
			assert.isNotNull(p1);
			assert.isTrue(p1.equals(p2));
		},
		testParserStndVariants: function() {
			var p1 = parsers.get(parsers.type.STANDARD),
				p2 = parsers.get(parsers.type.PAST);
			assert.isNotNull(p1);
			assert.isFalse(p1.equals(p2));
			assert.isTrue(parserCompare(p1, p2, ["getMasks", "isRolling"]));
		},
		testParserPartialVariants: function() {
			var p1 = parsers.get(parsers.type.PARTIAL),
				p2 = parsers.get(parsers.type.PARTIAL_PAST);
			assert.isNotNull(p1);
			assert.isFalse(p1.equals(p2));
			assert.isTrue(parserCompare(p1, p2, ["getMasks", "isRolling"]));
		},
		testParserStndToPartial: function() {
			var p1 = parsers.get(parsers.type.STANDARD),
				p2 = parsers.get(parsers.type.PARTIAL);
			assert.isNotNull(p1);
			assert.isFalse(p1.equals(p2));
			assert.isTrue(parserCompare(p1, p2, ["isRolling", "isExpandYearIntoPast"]));
		},
		testPastParsers: function() {
			var p1 = parsers.get(parsers.type.PAST),
				p2 = parsers.get(parsers.type.PARTIAL_PAST);
			assert.isNotNull(p1);
			assert.isFalse(p1.equals(p2));
			assert.isTrue(parserCompare(p1, p2, ["isRolling", "isExpandYearIntoPast"]));
		}
	});
});
