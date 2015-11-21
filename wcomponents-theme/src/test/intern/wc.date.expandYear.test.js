define(["intern!object", "intern/chai!assert", "./resources/test.utils"],
	function(registerSuite, assert, testutils) {
		/*
		 * Tests expandYear
		 * Also tests wc/date/pivot because that should really be part of expandYear.
		 * Also inadvertently tests wc/date/today.
		 */
		"use strict";
		var expandYear,
			pivot,
			today,
			pivotVal = null,
			todayVal;
		registerSuite({
			name: "dateExpandYear",
			setup: function() {
				return testutils.setupHelper(["wc/date/expandYear", "wc/date/pivot", "wc/date/today"], function(e, p, t) {
					expandYear = e;
					pivot = p;
					today = t;
					pivotVal = pivot.get();
					todayVal = today.get();
				});
			},
			teardown: function() {  /* IMPORTANT! clean up after yourself! */
				today.set(todayVal);  // reset the date in "today"
				pivot.set(pivotVal);  // reset the pivot value.
			},
			beforeEach: function() {
				pivot.set(pivotVal);
				today.set(new Date(1903, 9, 28));  // fake the date so these tests never go stale
			},
			testExpandYear: function() {
				assert.strictEqual(1918, expandYear("18"));
				assert.strictEqual(1902, expandYear("02"));
				assert.strictEqual(1899, expandYear("99"));
				assert.strictEqual(1918, expandYear(18));
				assert.strictEqual(1902, expandYear(2));
				assert.strictEqual(1899, expandYear(99));
			},
			testExpandYearPivotOne: function() {
				pivot.set(1);
				assert.strictEqual(1818, expandYear("18"));
				assert.strictEqual(1902, expandYear("02"));
				assert.strictEqual(1899, expandYear("99"));
				assert.strictEqual(1818, expandYear(18));
				assert.strictEqual(1902, expandYear(2));
				assert.strictEqual(1899, expandYear(99));
			},
			testExpandYearPivotOneHundred: function() {
				pivot.set(100);
				assert.strictEqual(1918, expandYear("18"));
				assert.strictEqual(2002, expandYear("02"));
				assert.strictEqual(1999, expandYear("99"));
				assert.strictEqual(1918, expandYear(18));
				assert.strictEqual(2002, expandYear(2));
				assert.strictEqual(1999, expandYear(99));
			},
			testPivotLBoundOutOfRange: function() {
				try {
					pivot.set(0);
					assert.fail("should have got an exception setting pivot too low");
				}
				catch (ignore) {
					// good
				}
			},
			testPivotUBoundOutOfRange: function() {
				try {
					pivot.set(101);
					assert.fail("should have got an exception setting pivot too high");
				}
				catch (ignore) {
					// good
				}
			}
		});
	});
