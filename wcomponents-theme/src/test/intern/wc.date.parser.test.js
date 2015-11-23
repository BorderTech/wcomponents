define(["intern!object", "intern/chai!assert", "./resources/test.utils"], function(registerSuite, assert, testutils) {
	"use strict";
	/*
	 * Note, some of these tests will break in the future, like 20 years from the time of
	 * writing (2010).  If you are still using these tests in 20 years something went horribly
	 * wrong with the IT revolution.
	 */

	var Parser, pivot, today, now,
		standardMasks = ["ytm", "+-", "d M yy", "d M yyyy", "d MON yy", "d MON yyyy", "ddMMyy", "ddMMyyyy", "dMONyy", "dMONyyyy", "yyyy-MM-dd", "yyyyMMdd"],
		partialMasks = standardMasks.concat([" M y", " MON y", "M y", "MON y", "MONy", "MMyy", "Myyyy", "y", "ddMM"]),
		extendedPartialMasks = partialMasks.concat(["d M", "M", "d MON", "d M", "MON", "d", "dd yyyy", "ddyyyy", "ddyy", "dd yy"]),
		pivotVal, realToday;


	function doPlusMinusTest(days, rolling) {
		var parser = getParser(standardMasks, false, rolling),
			dayString = (days >= 0 ? "+" : "") + days.toString(),
			result = parser.parse(dayString);
		now.setDate(now.getDate() + days);
		assert.strictEqual(result.length, 1);
		assert.strictEqual(result[0].day, now.getDate());
		assert.strictEqual(result[0].month, (now.getMonth() + 1));
		assert.strictEqual(result[0].year, now.getFullYear());
	}

	function containsMatch(arr, match) {
		for (var i = 0; i < arr.length; i++) {
			if (arr[i].toXfer() === match) {
				return true;
			}
		}
		return false;
	}

	function getParser(masks, past, rolling) {
		var parser = new Parser();
		parser.setRolling(!!rolling);
		parser.setMasks(masks);
		parser.setExpandYearIntoPast(!!past);
		return parser;
	}

	registerSuite({
		name: "date/Parser",
		setup: function() {
			return testutils.setupHelper(["wc/date/Parser", "wc/date/pivot", "wc/date/today"], function(p1, p2, t) {
				Parser = p1;
				pivot = p2;
				today = t;
				pivotVal = pivot.get();
				realToday = today.get();
			});
		},
		beforeEach: function() {
			now = new Date();
		},
		afterEach: function() {
			pivot.set(pivotVal);
			today.set(realToday);
		},
		teardown: function() {
			pivot.set(pivotVal);
			today.set(realToday);
		},
		testParserStnd: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("28101973");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1973);
		},
		testParserDayOfWeek: function() {
			var parser = getParser(["E MON dd yyyy"], false, false);
			var result = parser.parse("Tue Nov 04 2003");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 4);
			assert.strictEqual(result[0].month, 11);
			assert.strictEqual(result[0].year, 2003);
		},
		testParserStndIsoDate: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("1973-10-28");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1973);
		},
		testParserCompressedIsoDate: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("19731028");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1973);
		},
		testParserStndFwdSlashes: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("28/10/1973");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1973);
		},
		testParserStndHyphens: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("28-10-1973");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1973);
		},
		testParserStndExpandYear: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("281025");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 2025);
		},
		testParserStndExpandYearPast: function() {
			var parser = getParser(standardMasks, true, false);
			var result = parser.parse("281025");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1925);
		},
		testParserStndMonthAbbr: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("28 OCT 1973");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1973);
		},
		testParserStndMonthFull: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("28 OCTOBER 1973");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 28);
			assert.strictEqual(result[0].month, 10);
			assert.strictEqual(result[0].year, 1973);
		},
		testParserPartial: function() {
			today.set(new Date(2000, 0, 1));
			pivot.set(15);
			var parser = getParser(partialMasks, false, false);
			var result = parser.parse("1111");
			assert.strictEqual(result.length, 2);
			assert.isTrue(containsMatch(result, "????-11-11"), "????-11-11");
			assert.isTrue(containsMatch(result, "2011-11-??"), "2011-11-??");
		},
		testParserXtnd: function() {
			today.set(new Date(2000, 0, 1));
			pivot.set(15);
			var parser = getParser(extendedPartialMasks, false, false);
			var result = parser.parse("1111");
			assert.strictEqual(result.length, 3);
			// debugger;
			assert.isTrue(containsMatch(result, "????-11-11"), "????-11-11");
			assert.isTrue(containsMatch(result, "2011-??-11"), "2011-??-11");
			assert.isTrue(containsMatch(result, "2011-11-??"), "2011-11-??");
		},
		testParserToday: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("t");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, now.getDate());
			assert.strictEqual(result[0].month, (now.getMonth() + 1));
			assert.strictEqual(result[0].year, now.getFullYear());
		},
		testParserYesterday: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("y");
			now.setDate(now.getDate() - 1);
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, now.getDate());
			assert.strictEqual(result[0].month, (now.getMonth() + 1));
			assert.strictEqual(result[0].year, now.getFullYear());
		},
		testParserTomorrow: function() {
			var parser = getParser(standardMasks, false, false);
			var result = parser.parse("m");
			now.setDate(now.getDate() + 1);
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, now.getDate());
			assert.strictEqual(result[0].month, (now.getMonth() + 1));
			assert.strictEqual(result[0].year, now.getFullYear());
		},
		testParserTomorrowPastDate: function() {
			var parser = getParser(standardMasks, true, false);
			var result = parser.parse("m");
			now.setDate(now.getDate() + 1);
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, now.getDate());
			assert.strictEqual(result[0].month, (now.getMonth() + 1));
			assert.strictEqual(result[0].year, (now.getFullYear() - 100));
		},
		testRollingDate: function() {
			var parser = getParser(standardMasks, false, true),
				result = parser.parse("30 Feb 2011");
			assert.strictEqual(result.length, 1);
			assert.strictEqual(result[0].day, 2);
			assert.strictEqual(result[0].month, 3);
			assert.strictEqual(result[0].year, 2011);
		},
		testInvalidDateNoRolling: function() {
			var parser = getParser(standardMasks, false, false),
				result = parser.parse("30 Feb 2011");
			assert.strictEqual(result.length, 0);
		},
		testParserTodayPlus100: function() {
			doPlusMinusTest(100, false);
		},
		testParserTodayMinus100: function() {
			doPlusMinusTest(-100, false);
		},
		testParserTodayPlus100Rolling: function() {
			doPlusMinusTest(100, true);
		},
		testParserTodayMinus100Rolling: function() {
			doPlusMinusTest(-100, true);
		}
	});
});
